package com.mevron.rides.rider.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import com.mevron.rides.rider.App
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.HomeFragmentBinding
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.localdb.SavedAddress
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.socket.SocketHandler
import com.mevron.rides.rider.util.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.math.ln


@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback, LocationListener, AddressSelected {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawer: ImageButton
    private  var gMap: GoogleMap? = null
    private lateinit var mapView: SupportMapFragment

    private lateinit var adapter: HomeAdapter
    private var add: ArrayList<LocationModel> = arrayListOf()
    private var mDialog: Dialog? = null
    private var loca: MutableLiveData<LatLng> = MutableLiveData()

    var LOCATION_REFRESH_TIME = 4000 // 4 seconds. The Minimum Time to get location update
    var LOCATION_REFRESH_DISTANCE = 0 // 0 meters. The Minimum Distance to be changed to get location update


    private var mCircle: Circle? = null
    var radiusInMeters = 100.0
    var strokeColor = -0x10000 //Color Code you want

    var shadeColor = 0x44ff0000 //opaque red fill


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        return  binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  val props = JSONObject()
     //   props.put("Home Screen", true)
      //  mixpanel().track("Android Home Screen", props)


        add = arrayListOf()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.mevronHomeBottom.bottomSheet)
        getAddressFromApi()
        getAddress()




        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
                binding.mevronHomeBottom.destAddressField.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchLocationFragment)
        }

        binding.mevronHomeBottom.allSavedLayout.setOnClickListener {
            findNavController().navigate(R.id.action_global_addSavedPlaceFragment)
        }

        binding.mevronHomeBottom.scheduleButton.setOnClickListener {
            binding.mevronHomeBottom.bottomSheet.visibility = View.GONE
            binding.mevronScheduleBottom.scheduleLayout.visibility = View.VISIBLE
        }

        binding.mevronScheduleBottom.scheduleTheRide.setOnClickListener {
            binding.mevronHomeBottom.bottomSheet.visibility = View.VISIBLE
            binding.mevronScheduleBottom.scheduleLayout.visibility = View.GONE
        }

        binding.mevronHomeBottom.myLocation.setOnClickListener {
           // animateToCurrentPosi(gMap)

          //  checkPermission()
            if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
                != PackageManager.PERMISSION_GRANTED && context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                } != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_REQUEST_CODE)
                return@setOnClickListener
            }
            getLocationProvider()?.lastLocation?.addOnSuccessListener {
                //  Toast.makeText(context, "22", Toast.LENGTH_LONG).show()
                val location = it
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    loca.postValue(currentLocation)
                    /* */

                    getAddressFromLocation(currentLocation)
                    val cameraPosition = CameraPosition.Builder()
                        .bearing(0.toFloat())
                        .target(currentLocation)
                        .zoom(18.5.toFloat())
                        .build()
                 //   gMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    gMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    //  getAddress(context, location.latitude, location.longitude, pickupAddressTextField)
                } else { displayLocationSettingsRequest(binding) }
            }
                ?.addOnFailureListener {
                    it.printStackTrace()
                }

        }
        loca.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            SocketHandler.setSocket(uiid = "0e66aea8-569f-4adc-953e-27f65eec4e7e", lng = it.longitude.toString(), lat = it.latitude.toString())
              SocketHandler.establishConnection()
              val s = SocketHandler.getSocket()
            s?.on("nearby_drivers"){
                 Log.i("socket io", it.toString())
                 Log.i("socket io 2", it[0].toString())
                 activity?.runOnUiThread {
                      //  Toast.makeText(context, it[0].toString(), Toast.LENGTH_LONG).show()
                      val data = it[0] as JSONObject
                      val locations = data["locations"] as JSONArray
                      gMap?.clear()
                         for (l in 0 until locations.length()){
                           val latLng = locations[l] as JSONObject
                           val lat = (latLng["latitude"] as String).toDouble()
                           val lng = (latLng["longitude"] as String).toDouble()
                              // addMarkerToMap(lat, lng)
                       }

                   }

             }
        })


      //  bottomSheetBehavior.peekHeight


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }



            override fun onStateChanged(bottomSheet: View, newState: Int) {
               when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.mevronHomeBottom.allSavedLayout.visibility = View.VISIBLE
                        binding.mevronHomeBottom.scheduleButton.visibility = View.GONE
                        binding.mevronHomeBottom.myLocation.visibility = View.GONE
                    }



                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.mevronHomeBottom.allSavedLayout.visibility = View.GONE
                        binding.mevronHomeBottom.scheduleButton.visibility = View.VISIBLE
                        binding.mevronHomeBottom.myLocation.visibility = View.VISIBLE
                    }
                    else ->{
                        binding.mevronHomeBottom.allSavedLayout.visibility = View.GONE
                        binding.mevronHomeBottom.scheduleButton.visibility = View.VISIBLE
                        binding.mevronHomeBottom.myLocation.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun addMarkerToMap(lat: Double, lng: Double){
        val lg = LatLng(lat, lng)

        val marker =  MarkerOptions()
            .position(LatLng(lat, lng))
            .icon(bitmapFromVector(R.drawable.group))
           // .rotation()
        gMap?.addMarker(marker)
    }



    override fun onStart() {
        super.onStart()
        drawerLayout = activity?.findViewById(R.id.drawer_layout)!!
        drawer = binding.drawerButton
        drawer.setOnClickListener {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                drawerLayout.closeDrawer(GravityCompat.START)
            } else {

                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        mapView.getMapAsync(this)
    }

   // @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
   //     Toast.makeText(context, "44444", Toast.LENGTH_SHORT).show()
        if (googleMap != null) {
       //     Toast.makeText(context, "5555", Toast.LENGTH_SHORT).show()
            gMap = googleMap
           // return

        }
       // Toast.makeText(context, "66666", Toast.LENGTH_SHORT).show()

        MapsInitializer.initialize(activity?.applicationContext)
       if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
           != PackageManager.PERMISSION_GRANTED && context?.let {
               ContextCompat.checkSelfPermission(
                   it,
                   Manifest.permission.ACCESS_COARSE_LOCATION)
           } != PackageManager.PERMISSION_GRANTED) {
           requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_REQUEST_CODE)
           return
       }

        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
      //  googleMap?.isMyLocationEnabled = true

        /*   val mLocationManager = context?.let {
              it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
          }


         mLocationManager?.requestLocationUpdates(
              LocationManager.GPS_PROVIDER,
              LOCATION_REFRESH_TIME.toLong(),
              LOCATION_REFRESH_DISTANCE.toFloat(),
              this
          )*/
        animateToCurrentPosi(googleMap)

       /// Toast.makeText(context, "11", Toast.LENGTH_LONG).show()
      //  val currentShipment = context.viewModel.currentShipment
      //  if (currentShipment.senderAddress.isNullOrEmpty() && currentShipment.receiverAddress.isNullOrEmpty()) {

      //  }

    }




   // @SuppressLint("MissingPermission")
    fun animateToCurrentPosi(googleMap: GoogleMap?){
        //checkPermission()
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
            != PackageManager.PERMISSION_GRANTED && context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_REQUEST_CODE)
            return
        }

       // return

      //  Toast.makeText(context, "12121", Toast.LENGTH_SHORT).show()
        getLocationProvider()?.lastLocation?.addOnSuccessListener {
            //  Toast.makeText(context, "22", Toast.LENGTH_LONG).show()
            val location = it
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                loca.postValue(currentLocation)
               /* */

                getAddressFromLocation(currentLocation)
                val cameraPosition = CameraPosition.Builder()
                    .bearing(0.toFloat())
                    .target(currentLocation)
                    .zoom(18.5.toFloat())
                    .build()
                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                //  getAddress(context, location.latitude, location.longitude, pickupAddressTextField)
            } else { displayLocationSettingsRequest(binding) }
        }
            ?.addOnFailureListener {
                it.printStackTrace()
            }
    }



    fun getLocationProvider(): FusedLocationProviderClient? {
        return activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    }

    override fun onLocationChanged(p0: Location) {
        val currentLocation = LatLng(p0.latitude, p0.longitude)
        getAddressFromLocation(currentLocation)

       // val addCircle = CircleOptions().center(LatLng(p0.latitude, p0.longitude)).radius(radiusInMeters).fillColor(shadeColor)
        //    .strokeColor(strokeColor).strokeWidth(8f)
       // mCircle = gMap.addCircle(addCircle)

        //move map camera

        //move map camera
        gMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(p0.latitude, p0.longitude)))
        gMap?.animateCamera(CameraUpdateFactory.zoomTo(18.5f))
    }

    override fun onStop() {
        super.onStop()
    //    SocketHandler.closeConnection()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(this)
        }
    }

    fun getAddressFromLocation(location: LatLng?) {


        try {
            if (location != null) {
                val geoCoder = Geocoder(context, Locale.getDefault())

                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                val address = if (addresses.isNotEmpty())
                    addresses[0].getAddressLine(0)
                else ""
                add.add(LocationModel(location.latitude, location.longitude, address))

            }
        } catch (ex: Exception){
            ex.printStackTrace()

        }
    }

    fun getAddressFromApi(){
      //  toggleBusyDialog(true,"Please Wait...")

        viewModel.getAddresses().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it.let {  res ->
                when(res){

                    is  GenericStatus.Success ->{
                      //  toggleBusyDialog(false)

                    }

                    is  GenericStatus.Error ->{
                       // toggleBusyDialog(false)
                    }

                    is GenericStatus.Unaunthenticated -> {
                       // toggleBusyDialog(false)
                    }
                }
            }
        })

    }

    private fun getAddress(){
        viewModel.getAddressFromDB().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.isEmpty()){
              //  Toast.makeText(context, "1", Toast.LENGTH_LONG).show()
                bottomSheetBehavior.peekHeight = 500
            }

            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            adapter = HomeAdapter(it, this)
            binding.mevronHomeBottom.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
                RecyclerView.VERTICAL, false)
            binding.mevronHomeBottom.recyclerView.adapter = adapter
        })
    }

    private fun toggleBusyDialog(busy: Boolean, desc: String? = null){
        if(busy){
            if(mDialog == null){
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_busy_layout,null)
                mDialog = LauncherUtil.showPopUp(requireContext(),view,desc)
            }else{
                if(!desc.isNullOrBlank()){
                    val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_busy_layout,null)
                    mDialog = LauncherUtil.showPopUp(requireContext(),view,desc)
                }
            }
            mDialog?.show()
        }else{
            mDialog?.dismiss()
        }
    }

    override fun selectedAddress(data: LocationModel, dt: SavedAddress) {
      if (add.isEmpty()){
          Toast.makeText(context, "Try again", Toast.LENGTH_LONG).show()
      }else{
          add.add(data)
          var ads = arrayOf<LocationModel>()
          for (a in add){
              ads += a
          }
          val action = HomeFragmentDirections.actionHomeFragmentToSelectRideFragment(ads)
          findNavController().navigate(action)
      }
    }

}