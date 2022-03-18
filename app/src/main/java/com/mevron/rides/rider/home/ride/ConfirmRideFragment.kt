package com.mevron.rides.rider.home.ride

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.ConfirmRideFragmentBinding
import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.geolocation.GeoAPIClient
import com.mevron.rides.rider.remote.geolocation.GeoAPIInterface
import com.mevron.rides.rider.remote.model.RideRequest
import com.mevron.rides.rider.remote.socket.SocketHandler
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.bitmapFromVector
import com.mevron.rides.rider.util.displayLocationSettingsRequest
import com.mevron.rides.rider.util.getGeoLocation
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class ConfirmRideFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: ConfirmRideFragmentBinding
    private lateinit var mapView: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var geoDirections: GeoDirectionsResponse
    private lateinit var apiInterface: GeoAPIInterface
    private lateinit var location:Array<LocationModel>
    private var isCard = false
    private var uiid = ""

    companion object {
        fun newInstance() = ConfirmRideFragment()
    }

    private val viewModel: ConfirmRideViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.confirm_ride_fragment, container, false)

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        if (this::gMap.isInitialized) {
            gMap.clear()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        apiInterface = GeoAPIClient().getClient()?.create(GeoAPIInterface::class.java)!!

        location = arguments?.let { ConfirmRideFragmentArgs.fromBundle(it).location }!!
        isCard = arguments?.let { ConfirmRideFragmentArgs.fromBundle(it).isCard }!!
        uiid = arguments?.let { ConfirmRideFragmentArgs.fromBundle(it).uiid }!!
        sendAddressFromApi()
        binding.bqckButton.setOnClickListener {
            activity?.onBackPressed()
        }
        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment


        mapView.getMapAsync(this)
        binding.cancelButton.setOnClickListener {
            showDialog()
        }
        displayLocationSettingsRequest(binding)
    }




    private fun addMarkerToPolyLines() {

        val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
        val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation

        val marker3 =  MarkerOptions()
            .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
            .icon(bitmapFromVector( R.drawable.ic_driver_pick))

        val marker4 =  MarkerOptions()
            .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_dest))
        gMap.addMarker(marker3)
        gMap.addMarker(marker4)



    }



    override fun onStop() {
        super.onStop()
        //SocketHandler.closeConnection()
    }


    fun sendAddressFromApi(){
        //  toggleBusyDialog(true,"Please Wait...")
        val method = if (isCard) "card" else "cash"

        val data = RideRequest(cardId = uiid, paymentMethod = method, destinationLongitude = location[1].lng.toString(), destinationLatitude = location[1].lat.toString(),
        destinationAddress = location[1].address, pickupAddress = location[0].address, pickupLongitude = location[0].lng.toString(), pickupLatitude = location[0].lat.toString(), vehicleType = "standard")

        viewModel.confirmRider(data).observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it.let {  res ->
                when(res){

                    is  GenericStatus.Success ->{
                     //   Toast.makeText(context, "55", Toast.LENGTH_SHORT).show()

                       // SocketHandler.setSocket(uiid = "0e66aea8-569f-4adc-953e-27f65eec4e7e", lng = location[0].lng.toString(), lat = location[0].lat.toString())
                       // SocketHandler.establishConnection()
                        val s = SocketHandler.getSocket()
                        s?.on("search_drivers"){it1 ->
                            Log.i("getAddressFromApi", "getAddressFromApi: ${it1[0]}")
                            activity?.runOnUiThread {

                                binding.findingFoundDriver.visibility = View.VISIBLE
                                binding.confirmingDriver.visibility = View.GONE

                            }
                           // Toast.makeText(context, "${it1[0]}", Toast.LENGTH_LONG).show()

                           // val dat = Gson().fromJson(dt.toString(), TripManagementDataClass::class.java)

                        }




                        s?.on("trip_status"){it1 ->
                            Log.i("getAddressFromApi 33", "getAddressFromApi 33: ${it1[0]}")
                            activity?.runOnUiThread {

                                val dt = it1[0] as? JSONObject
                                val trip = dt?.get("trip") as? JSONObject
                                val status = trip?.get("status") as? String
                                if (status == "accepted"){
                                    val action = ConfirmRideFragmentDirections.actionGlobalBookedFragment(location)
                                    findNavController().navigate(action)
                                }
                            }
                            // Toast.makeText(context, "${it1[0]}", Toast.LENGTH_LONG).show()

                            // val dat = Gson().fromJson(dt.toString(), TripManagementDataClass::class.java)

                        }

                    }

                    is  GenericStatus.Error ->{
                       Toast.makeText(context, "Ride Booking Failed", Toast.LENGTH_LONG).show()
                    }

                    is GenericStatus.Unaunthenticated -> {
                        // toggleBusyDialog(false)
                        Toast.makeText(context, "Ride Booking Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

    }




    private fun showDialog() {
        val dialog = activity?.let { Dialog(it) }!!
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cancel_dialog)
       // val body = dialog.findViewById(R.id.body) as TextView
      //  body.text = title
        val yesBtn = dialog.findViewById(R.id.do_cancel) as MaterialButton
        val noBtn = dialog.findViewById(R.id.dont) as MaterialButton
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }





    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            gMap = p0
        }
        MapsInitializer.initialize(context?.applicationContext)

        location = arguments?.let { ConfirmRideFragmentArgs.fromBundle(it).location }!!

        if (location.isNotEmpty()){

            val builder = LatLngBounds.Builder()
            builder.include(LatLng(location[0].lat, location[0].lng))
            builder.include(LatLng(location[1].lat, location[1].lng))
            val bounds = builder.build()
            val width = resources.displayMetrics.widthPixels;
            val  height = resources.displayMetrics.heightPixels;
            val padding =(width * 0.40).toInt()
          //  val cu = CameraUpdateFactory.newLatLngBounds(bounds, 20)
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 300)

           // gMap.setPadding(50,50,50,50)
            //  gMap.setPadding(20,20,20,20)
          //  gMap.animateCamera(cu)
            //  gMap.setPadding(20,20,20,20)
          //  gMap.animateCamera(cu)
            gMap.moveCamera(cu)

            val currentLocation = LatLng(location[0].lat, location[0].lng)
            val cameraPosition = CameraPosition.Builder()
                .bearing(0.toFloat())
                .target(currentLocation)
                .zoom(15.5.toFloat())
                .build()
            // gMap.animateCamera(cu)
        }


        getGeoLocation(location, gMap) {
            geoDirections = it
            addMarkerToPolyLines()
        }

     /*   if (location.isNotEmpty()){
            val currentLocation = LatLng(location[0].lat, location[0].lng)
            val cameraPosition = CameraPosition.Builder()
                .bearing(0.toFloat())
                .target(currentLocation)
                .zoom(15.5.toFloat())
                .build()
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }*/

        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
            != PackageManager.PERMISSION_GRANTED && context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_REQUEST_CODE)
            return
        }


        p0?.isMyLocationEnabled = true




    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(this)
        }
    }


}