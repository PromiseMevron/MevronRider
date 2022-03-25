package com.mevron.rides.ridertest.home.selectmap

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import com.mevron.rides.ridertest.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.ridertest.databinding.SelectOnMapFragmentBinding
import com.mevron.rides.ridertest.home.model.LocationModel
import com.mevron.rides.ridertest.util.bitmapFromVector
import com.mevron.rides.ridertest.util.Constants
import java.util.*


class SelectOnMapFragment : Fragment(), OnMapReadyCallback, LocationListener, OnMapClickListener, GoogleMap.OnMapLongClickListener {

    companion object {
        fun newInstance() = SelectOnMapFragment()
    }

    private lateinit var viewModel: SelectOnMapViewModel
    private lateinit var binding: SelectOnMapFragmentBinding
    private var gMap: GoogleMap? = null
    private lateinit var mapView: SupportMapFragment
    private var marker: Marker? = null
    private lateinit var locationField: EditText
    private var locations : Array<LocationModel> = arrayOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.select_on_map_fragment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment

        locationField = binding.startAddressField



        binding.bqckButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.myLocation.setOnClickListener {
            myLocation(gMap)
        }

        binding.pickMe.setOnClickListener {
            binding.pickMeLqyout.visibility = View.GONE
            binding.dropMeLqyout.visibility = View.VISIBLE
            locationField = binding.endAddressField
            marker?.remove()
        }

        binding.dropMe.setOnClickListener {
            if (binding.endAddressField.text.toString().isNotEmpty() && locations.isNotEmpty() && locations.size > 1){
                val action = SelectOnMapFragmentDirections.actionSelectOnMapFragmentToSelectRideFragment(locations)
                findNavController().navigate(action)
            }else{
                Toast.makeText(context, "Select Location", Toast.LENGTH_LONG).show()
            }

        }

        getPresentLocation()
    }

    private fun getPresentLocation(){
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
            != PackageManager.PERMISSION_GRANTED && context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_REQUEST_CODE)
            return
        }

        getLocationProvider()?.lastLocation?.addOnSuccessListener {
            //  Toast.makeText(context, "22", Toast.LENGTH_LONG).show()
            val location = it
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                getAddressFromLocation(currentLocation)
                mapClicked(currentLocation)
            } else { displayLocationSettingsRequest() }
        }
            ?.addOnFailureListener {
                it.printStackTrace()
            }
    }

    override fun onStart() {
        super.onStart()
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        if (googleMap != null) {
            gMap = googleMap

            gMap?.setOnMapClickListener(this)
            gMap?.setOnMapLongClickListener(this)
        }


        MapsInitializer.initialize(activity?.applicationContext)

     /*   googleMap?.setOnMapClickListener {
            Toast.makeText(context, "ww", Toast.LENGTH_LONG).show()
            getAddressFromLocation(it)
        }*/
    /*    val gMapView = mapView.view
        if (gMapView?.findViewById<View>("1".toInt()) != null) {
            val locationButton = (gMapView.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 850)
        }*/
      //  checkPermission()
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
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false

        myLocation(googleMap)


    }


    fun myLocation(googleMap: GoogleMap?){
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
        getLocationProvider()?.lastLocation?.addOnSuccessListener {

            val location = it
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                val cameraPosition = CameraPosition.Builder()
                    .bearing(0.toFloat())
                    .target(currentLocation)
                    .zoom(15.5.toFloat())
                    .build()
                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                mapClicked(currentLocation)
                //  getAddress(context, location.latitude, location.longitude, pickupAddressTextField)
            } else { displayLocationSettingsRequest() }
        }
            ?.addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun displayLocationSettingsRequest() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 500
        locationRequest.fastestInterval = 100
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
                if (locationField == binding.startAddressField){
                    if (locations.isEmpty()){
                        locations += LocationModel(location.latitude, location.longitude, address)
                    }else{
                        locations[0] = LocationModel(location.latitude, location.longitude, address)
                    }
                }else{
                    if (locations.size == 1){
                        locations += LocationModel(location.latitude, location.longitude, address)
                    }else{
                        locations[1] = LocationModel(location.latitude, location.longitude, address)
                    }
                }
                locationField.setText(address)

            }
        } catch (ex: Exception){
            ex.printStackTrace()

        }
    }

    override fun onMapClick(p0: LatLng?) {
        mapClicked(p0)
        Log.i("onMapClick Long", p0?.longitude.toString())
        Log.i("onMapClick Lat", p0?.latitude.toString())
    }

    override fun onMapLongClick(p0: LatLng?) {
     //   Toast.makeText(context, "sds 2222 333", Toast.LENGTH_LONG).show()
     mapClicked(p0)
        Log.i("onMapClick Long", p0?.longitude.toString())
        Log.i("onMapClick Lat", p0?.latitude.toString())
    }

    fun mapClicked(p0: LatLng?){
        getAddressFromLocation(p0)

        //remove previously placed Marker
        marker?.remove()

        var id = 0
        id = if (locationField == binding.startAddressField){
            R.drawable.ic_marker_pick
        }else{
            R.drawable.ic_marker_drop
        }

        //place marker where user just clicked
        marker = gMap?.addMarker(
            p0?.let {
                MarkerOptions().position(it).title("")
                    .icon(context?.let { bitmapFromVector(id) })
            }
        )
    }




}