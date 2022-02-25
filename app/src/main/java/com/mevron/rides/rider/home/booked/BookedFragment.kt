package com.mevron.rides.rider.home.booked

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.BookedFragmentBinding
import com.mevron.rides.rider.util.Constants
import java.util.*

class BookedFragment : Fragment(), OnMapReadyCallback, LocationListener {

    companion object {
        fun newInstance() = BookedFragment()
    }

    private lateinit var viewModel: BookedViewModel
    private lateinit var binding: BookedFragmentBinding
    private lateinit var driverAllBottomSheetBehavior: BottomSheetBehavior<ScrollView>
    private lateinit var onrideBottomSheetBehavior: BottomSheetBehavior<ScrollView>
    private lateinit var emergBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var rechedBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawer: ImageButton
    private lateinit var gMap: GoogleMap
    private lateinit var mapView: SupportMapFragment
    private var mCircle: Circle? = null
    var stateee = 0

    var timer: Timer? = null



    var radiusInMeters = 100.0
    var strokeColor = -0x10000 //Color Code you want

    var shadeColor = 0x44ff0000 //opaque red fill

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.booked_fragment, container, false)

        return  binding.root
    }


    override fun onStop() {
        super.onStop()
        // binding.player.player = null
        timer?.cancel()

    }


    private fun setRepeatingAsyncTask(){
        val handler = Handler()
        timer = Timer()

        val task: TimerTask = object : TimerTask() {
            override fun run() {
                handler.post { stimulateWebsocket()}
            }
        }

        timer?.schedule(task, 0, 30 * 1000.toLong()) // interval of one minute

    }


    fun stimulateWebsocket(){

        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        if (stateee == 1){
            binding.bookedHomeBottom.text1.visibility = View.GONE
            binding.bookedHomeBottom.text2.visibility = View.GONE
            binding.bookedHomeBottom.text11.visibility = View.VISIBLE
            binding.bookedHomeBottom.text22.visibility = View.VISIBLE
            binding.bookedHomeBottom.driverLinera.visibility = View.GONE

        }
        if (stateee == 2){
           driverAllBottomSheetBehavior.isHideable = true
            onrideBottomSheetBehavior.isHideable = false
            driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            onrideBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        if (stateee == 3){
            onrideBottomSheetBehavior.isHideable = true
            rechedBottomSheetBehavior.isHideable = false
            onrideBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            rechedBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        if (stateee == 4){
            timer?.cancel()
        }
        stateee += 1


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        driverAllBottomSheetBehavior = BottomSheetBehavior.from(binding.bookedHomeBottom.bottomSheet)
        onrideBottomSheetBehavior = BottomSheetBehavior.from(binding.bookedOnrideBottom.bottomSheet)
        emergBottomSheetBehavior = BottomSheetBehavior.from(binding.bookedEmergencyBottom.bottomSheet)
        rechedBottomSheetBehavior = BottomSheetBehavior.from(binding.bookedArrivedBottom.bottomSheet)

        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        onrideBottomSheetBehavior.isHideable = true
        emergBottomSheetBehavior.isHideable = true
        rechedBottomSheetBehavior.isHideable = true
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        onrideBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        emergBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        rechedBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        binding.scheduleButton.setOnClickListener {
            emergBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        setRepeatingAsyncTask()

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


    override fun onMapReady(googleMap: GoogleMap?) {

        if (googleMap != null) {
            gMap = googleMap
        }
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
        val gMapView = mapView.view
        if (gMapView?.findViewById<View>("1".toInt()) != null) {
            val locationButton = (gMapView.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 850)
        }

        googleMap?.isMyLocationEnabled = true
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        /// Toast.makeText(context, "11", Toast.LENGTH_LONG).show()
        //  val currentShipment = context.viewModel.currentShipment
        //  if (currentShipment.senderAddress.isNullOrEmpty() && currentShipment.receiverAddress.isNullOrEmpty()) {
        getLocationProvider()?.lastLocation?.addOnSuccessListener {
            //  Toast.makeText(context, "22", Toast.LENGTH_LONG).show()
            val location = it
            if (location != null) {

                val currentLocation = LatLng(location.latitude, location.longitude)
                getAddressFromLocation(currentLocation)
                val cameraPosition = CameraPosition.Builder()
                    .bearing(0.toFloat())
                    .target(currentLocation)
                    .zoom(15.toFloat())
                    .build()
                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                //  getAddress(context, location.latitude, location.longitude, pickupAddressTextField)
            } else { displayLocationSettingsRequest() }
        }
            ?.addOnFailureListener {
                it.printStackTrace()
            }
        //  }

    }

    private fun displayLocationSettingsRequest() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 1000
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = context?.let { LocationServices.getSettingsClient(it) }
        val task = client?.checkLocationSettings(builder.build())
        task?.addOnFailureListener { locationException: java.lang.Exception? ->
            if (locationException is ResolvableApiException) {
                try {
                    activity?.let { locationException.startResolutionForResult(it, Constants.LOCATION_REQUEST_CODE) }
                } catch (senderException: IntentSender.SendIntentException) {
                    senderException.printStackTrace()
                    /*  Snackbar.make(context, binding.root,"Please enable location setting to use your current address.",

                          View.OnClickListener { displayLocationSettingsRequest() }, "Retry", Snackbar.LENGTH_LONG

                          )*/

                    val snackbar = Snackbar
                        .make(binding.root, "Please enable location setting to use your current address.", Snackbar.LENGTH_LONG)
                        .setAction("Retry") {
                            displayLocationSettingsRequest()
                        }

                    snackbar.show()
                    // showErrorMessage(context, constraintLayout, "Please enable location setting to use your current address.",
                    //  View.OnClickListener { displayLocationSettingsRequest() }, getString(com.google.android.gms.maps.R.string.retry_text))
                }
            }
        }
    }

    fun getLocationProvider(): FusedLocationProviderClient? {
        return activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    }

    override fun onLocationChanged(p0: Location) {
        val currentLocation = LatLng(p0.latitude, p0.longitude)
        getAddressFromLocation(currentLocation)

        val addCircle = CircleOptions().center(LatLng(p0.latitude, p0.longitude)).radius(radiusInMeters).fillColor(shadeColor)
            .strokeColor(strokeColor).strokeWidth(8f)
        mCircle = gMap.addCircle(addCircle)

        //move map camera

        //move map camera
        gMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(p0.latitude, p0.longitude)))
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
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


            }
        } catch (ex: Exception){
            ex.printStackTrace()

        }
    }


}