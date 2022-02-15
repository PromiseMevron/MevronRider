package com.mevron.rides.rider.payment

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PaymentFragmentBinding
import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.remote.geolocation.GeoAPIClient
import com.mevron.rides.rider.remote.geolocation.GeoAPIInterface
import com.mevron.rides.rider.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentFragment : Fragment(), OnMapReadyCallback, PaySelected {

    companion object {
        fun newInstance() = PaymentFragment()
    }

    private lateinit var viewModel: PaymentViewModel
    private lateinit var binding:PaymentFragmentBinding
    private lateinit var mapView: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var geoDirections: GeoDirectionsResponse
    private lateinit var apiInterface: GeoAPIInterface
    private lateinit var location:Array<LocationModel>
    private lateinit var adapter: PaymentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.payment_fragment, container, false)

        return binding.root
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
        adapter = PaymentAdapter(this)
        binding.mevronPayBottom.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
            RecyclerView.VERTICAL, false)
      binding.mevronPayBottom.recyclerView.adapter = adapter
        location = arguments?.let { PaymentFragmentArgs.fromBundle(it).location }!!
        apiInterface = GeoAPIClient().getClient()?.create(GeoAPIInterface::class.java)!!

        binding.payCash.setOnClickListener {
            val action = PaymentFragmentDirections.actionPaymentFragment2ToConfirmRideFragment(location)
            findNavController().navigate(action)
        }
        binding.bqckButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // name = arguments?.let { EmailLoginFragmentArgs.fromBundle(it).name }!!
        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment

        mapView.getMapAsync(this)
        displayLocationSettingsRequest()
        binding.addVoucher.setOnClickListener {
            binding.mevronPayBottom.payTypeLayout.visibility = View.GONE
            binding.mevronPayBottom.voucherAddLayout.visibility = View.VISIBLE
        }
        binding.mevronPayBottom.doneButton.setOnClickListener {
            if (binding.mevronPayBottom.riderCode.text.toString().isEmpty()){
                Toast.makeText(context, "Enter code", Toast.LENGTH_LONG).show()
            }else{
                binding.mevronPayBottom.payTypeLayout.visibility = View.VISIBLE
                binding.mevronPayBottom.voucherAddLayout.visibility = View.GONE
                binding.addVoucher.visibility = View.GONE
                binding.voucherAdded.visibility = View.VISIBLE
                binding.voucherCode.text = binding.mevronPayBottom.riderCode.text.toString()
            }
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
        val rectLine = PolylineOptions().width(10f).color(ContextCompat.getColor(context!!, R.color.primary))
        for (step in steps) { rectLine.add(step) }
        // gMap.clear()
        gMap.addPolyline(rectLine)


        val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
        val marker1 =  MarkerOptions()
            .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
            .title("From")
            .snippet(location[0].address.substring(0..20))

        val marker2 =  MarkerOptions()
            .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
            .title("To")
            .snippet(location[1].address.substring(0..20))
        // .icon(BitmapFromVector(context!!, R.drawable.ic_marker_pick))


        gMap.addMarker(marker1).showInfoWindow()
        gMap.addMarker(marker2).showInfoWindow()


        /*  gMap.addMarker(
              MarkerOptions()
                  .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
                  .title("1111")
                  .snippet("33333")
              // .icon(BitmapFromVector(context!!, R.drawable.ic_marker_drop))
          ).showInfoWindow()*/

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
        val directionsEndpoint = "json?origin=" + "${location[0].lat}" + "," + "${location[0].lng}"+
                "&destination=" + "${location[1].lat}" + "," + "${location[1].lng}" +
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

    override fun selected() {
        findNavController().navigate(R.id.action_paymentFragment2_to_paymentMethodFragment)
    }
}