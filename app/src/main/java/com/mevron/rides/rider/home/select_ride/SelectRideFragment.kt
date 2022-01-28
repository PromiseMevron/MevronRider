package com.mevron.rides.rider.home.select_ride

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.SelectOnMapFragmentBinding
import com.mevron.rides.rider.databinding.SelectRideFragmentBinding
import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.remote.geolocation.GeoAPIClient
import com.mevron.rides.rider.remote.geolocation.GeoAPIInterface
import com.mevron.rides.rider.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectRideFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = SelectRideFragment()
    }

    private lateinit var viewModel: SelectRideViewModel
    private lateinit var mapView: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var geoDirections: GeoDirectionsResponse
    private lateinit var binding: SelectRideFragmentBinding
    private lateinit var apiInterface: GeoAPIInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.select_ride_fragment, container, false)
        return binding.root
      //  return inflater.inflate(R.layout.select_ride_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (this::gMap.isInitialized) {
            gMap.clear()
        }
        getGeoLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiInterface = GeoAPIClient().getClient()?.create(GeoAPIInterface::class.java)!!

        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapView.getMapAsync(this)
        displayLocationSettingsRequest()
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
                    val snackbar = Snackbar
                        .make(binding.root, "Please enable location setting to use your current address.", Snackbar.LENGTH_LONG)
                        .setAction("Retry") {
                            displayLocationSettingsRequest()
                        }

                    snackbar.show()
                }
            }
        }
    }


    private fun plotPolyLines() {

        val steps: ArrayList<LatLng> = ArrayList()
        if (geoDirections.routes.isNullOrEmpty()) {
            return
        }
        val geoBounds = geoDirections.routes?.get(0)?.bounds
        val geoSteps = geoDirections.routes?.get(0)?.legs?.get(0)?.steps
        geoSteps?.forEach { geoStep ->
            steps.addAll(decodePolyline(geoStep.polyline?.points!!))
        }
        val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation
        steps.add(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))

        val builder = LatLngBounds.Builder()
        builder.include(LatLng(geoBounds?.northeast?.lat ?: 0.0, geoBounds?.northeast?.lng ?: 0.0))
        builder.include(LatLng(geoBounds?.southwest?.lat ?: 0.0, geoBounds?.southwest?.lng ?: 0.0))

        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.3).toInt()

        val boundsUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        gMap.animateCamera(boundsUpdate)
        val rectLine = PolylineOptions().width(10f).color(ContextCompat.getColor(context!!, R.color.secondary))
        for (step in steps) { rectLine.add(step) }
        gMap.clear()
        gMap.addPolyline(rectLine)
        gMap.addMarker(
            MarkerOptions()
                .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
                .icon(BitmapFromVector(context!!, R.drawable.ic_marker_drop))).showInfoWindow()

        val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
        gMap.addMarker(
            MarkerOptions()
            .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
            .icon(BitmapFromVector(context!!, R.drawable.ic_marker_pick))).showInfoWindow()

    }

    private fun BitmapFromVector(context: Context, id: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.

        val vectorDrawable = ContextCompat.getDrawable(context, id)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun decodePolyline(encoded: String): ArrayList<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val position = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(position)
        }
        return poly
    }


    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            gMap = p0
        }
        MapsInitializer.initialize(context?.applicationContext)

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


        getLocationProvider()?.lastLocation?.addOnSuccessListener {
            val location = it
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                val cameraPosition = CameraPosition.Builder()
                    .bearing(0.toFloat())
                    .target(currentLocation)
                    .zoom(15.toFloat())
                    .build()
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            } else { displayLocationSettingsRequest() }
        }
            ?.addOnFailureListener {
                it.printStackTrace()
            }

    }

    fun getLocationProvider(): FusedLocationProviderClient? {
        return activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(this)
        }
    }

    fun getGeoLocation(){
        val directionsEndpoint = "json?origin=" + "6.6064391" + "," + "3.2065786"+
                "&destination=" + "6.5561258" + "," + "3.3858765" +
                "&sensor=false&units=metric&mode=driving"+ "&key=" + "AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE"
        val call: Call<GeoDirectionsResponse> = apiInterface.getGeoDirections(directionsEndpoint)
        call.enqueue(object : Callback<GeoDirectionsResponse?> {
            override fun onResponse(call: Call<GeoDirectionsResponse?>?, response: Response<GeoDirectionsResponse?>) {
                if (response.isSuccessful) {
                    response.body().let {
                        val directionsPayload = it
                        if (directionsPayload != null) {
                            geoDirections = directionsPayload
                            plotPolyLines()
                        }
                        else {

                        }
                    }
                }
                else {

                }

            }

            override fun onFailure(call: Call<GeoDirectionsResponse?>, t: Throwable?) {
                call.cancel()
            }
        })
    }


}