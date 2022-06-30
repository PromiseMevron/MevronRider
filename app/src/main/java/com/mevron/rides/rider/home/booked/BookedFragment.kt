package com.mevron.rides.rider.home.booked

import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.BookedFragmentBinding
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.shared.ui.services.LocationProcessor
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.bitmapFromVector
import com.mevron.rides.rider.util.getGeoLocation
import java.util.Locale
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BookedFragment : Fragment(), OnMapReadyCallback, LocationListener {

    companion object {
        fun newInstance() = BookedFragment()
    }

    private lateinit var viewModel: BookedViewModel
    private lateinit var binding: BookedFragmentBinding
    private lateinit var driverAllBottomSheetBehavior: BottomSheetBehavior<ScrollView>
    private lateinit var onRideBottomSheetBehavior: BottomSheetBehavior<ScrollView>
    private lateinit var emergedBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var reachedBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawer: ImageButton
    private lateinit var gMap: GoogleMap
    private lateinit var mapView: SupportMapFragment
    private var mCircle: Circle? = null
    private val locationProcessor = LocationProcessor()

    var radiusInMeters = 100.0
    var strokeColor = -0x10000 //Color Code you want

    var shadeColor = 0x44ff0000 //opaque red fill

    private lateinit var location: Array<LocationModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.booked_fragment, container, false)

        return binding.root
    }

    private fun bindDriverArrived() {
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.bookedHomeBottom.text1.visibility = View.GONE
        binding.bookedHomeBottom.text2.visibility = View.GONE
        binding.bookedHomeBottom.text11.visibility = View.VISIBLE
        binding.bookedHomeBottom.text22.visibility = View.VISIBLE
        binding.verifiedCode.visibility = View.GONE
        binding.bookedHomeBottom.driverLinera.visibility = View.GONE
    }

    private fun bindTripStarted() {
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        driverAllBottomSheetBehavior.isHideable = true
        onRideBottomSheetBehavior.isHideable = false
        binding.verifiedCode.visibility = View.GONE
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        onRideBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun bindTripCompleted() {
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.verifiedCode.visibility = View.GONE
        onRideBottomSheetBehavior.isHideable = true
        reachedBottomSheetBehavior.isHideable = false
        onRideBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        reachedBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun bindStartRide() {
        binding.verifiedCode.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        driverAllBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bookedHomeBottom.bottomSheet)
        onRideBottomSheetBehavior = BottomSheetBehavior.from(binding.bookedOnrideBottom.bottomSheet)
        emergedBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bookedEmergencyBottom.bottomSheet)
        reachedBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bookedArrivedBottom.bottomSheet)

        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        onRideBottomSheetBehavior.isHideable = true
        emergedBottomSheetBehavior.isHideable = true
        reachedBottomSheetBehavior.isHideable = true
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        onRideBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        emergedBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        reachedBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState.currentStatus) {
                    TripStatus.DRIVER_ARRIVED -> bindDriverArrived()
                    TripStatus.TRIP_STARTED -> bindTripStarted()
                    TripStatus.TRIP_COMPLETED -> bindTripCompleted()
                    TripStatus.START_RIDE -> bindStartRide()
                    TripStatus.ACCEPTED -> {
                        /** do nothing **/
                    }
                    TripStatus.UNKNOWN -> {
                        /** Do nothing **/
                    }
                }
            }
        }

        binding.okayRide.setOnClickListener {
            binding.verifiedCode.visibility = View.GONE
        }

        binding.scheduleButton.setOnClickListener {
            emergedBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
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

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                if (!uiState.isLocationProcessed && uiState.hasValidCoordinates) {
                    var locationModels = arrayOf<LocationModel>()
                    locationModels += LocationModel(
                        lat = uiState.pickupLatitude,
                        lng = uiState.pickupLongitude,
                        address = uiState.pickupAddress
                    )
                    locationModels += LocationModel(
                        lat = uiState.dropOffLatitude,
                        lng = uiState.dropOffLongitude,
                        address = uiState.destinationAddress
                    )
                    getGeoLocation(locationModels, gMap, true) {
                        gMap.clear()
                        addMarkerToPolyLines(it)
                    }
                }
            }
        }

        // what is driver_pickup_eta
        // trip_began

        locationProcessor.checkLocationPermission(context) {
            locationProcessor.requestLocationPermission(this)
        }

        val gMapView = mapView.view
        if (gMapView?.findViewById<View>("1".toInt()) != null) {
            val locationButton =
                (gMapView.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 850)
        }

        googleMap?.isMyLocationEnabled = true
    }

    private fun addMarkerToPolyLines(geoDirections: GeoDirectionsResponse) {

        val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
        val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation

        var loc2 = location[1].address
        if (loc2.length > 20) {
            loc2 = location[1].address.substring(0..20)
        }

        val sLl = (startLocation?.lat ?: 0.0)
        val sLlg = (startLocation?.lng ?: 0.0)

        val sLl2 = (endLocation?.lat ?: 0.0)
        val sLlg2 = (endLocation?.lng ?: 0.0)

        var sec = 0L
        geoDirections.routes?.forEach {
            it.legs?.forEach { it1 ->
                val sec2 = it1.duration?.value ?: 0L
                sec += sec2
            }
        }
        val mSec = sec.toInt()
        var tim = ""
        var min = mSec / 60
        tim = min.toString() + "min"

        if (min > 60) {
            min /= 60
            tim = min.toString() + "hr"
        }


        val marker2 = MarkerOptions()
            .position(LatLng(sLl2, sLlg2))
            .anchor(1.05f, 1.05f)
            .icon(BitmapDescriptorFactory.fromBitmap(createClusterBitmap(add = loc2, tim)))


        val marker4 = MarkerOptions()
            .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_drop_blue))


        gMap.addMarker(marker2)

        gMap.addMarker(marker4)

        val builder = LatLngBounds.Builder()
        builder.include(
            LatLng(
                geoDirections.routes?.get(0)?.bounds?.northeast?.lat ?: 0.0,
                geoDirections.routes?.get(0)?.bounds?.northeast?.lng ?: 0.0
            )
        )
        builder.include(
            LatLng(
                geoDirections.routes?.get(0)?.bounds?.southwest?.lat ?: 0.0,
                geoDirections.routes?.get(0)?.bounds?.southwest?.lat ?: 0.0
            )
        )

        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.3).toInt()

        val boundsUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        gMap.moveCamera(boundsUpdate)
    }

    private fun createClusterBitmap(add: String, min: String): Bitmap {
        val cluster: View = LayoutInflater.from(context).inflate(
            R.layout.on_trip_marker,
            null
        )
        val clusterSizeText = cluster.findViewById<View>(R.id.address) as TextView
        clusterSizeText.text = add

        val clusterSizeText2 = cluster.findViewById<View>(R.id.min) as TextView
        clusterSizeText2.text = min


        //  clusterSizeText.text = clusterSize.toString()
        cluster.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        cluster.layout(0, 0, cluster.measuredWidth, cluster.measuredHeight)
        val clusterBitmap = Bitmap.createBitmap(
            cluster.measuredWidth,
            cluster.measuredHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(clusterBitmap)
        cluster.draw(canvas)
        return clusterBitmap
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
                    activity?.let {
                        locationException.startResolutionForResult(
                            it,
                            Constants.LOCATION_REQUEST_CODE
                        )
                    }
                } catch (senderException: IntentSender.SendIntentException) {
                    senderException.printStackTrace()
                    /*  Snackbar.make(context, binding.root,"Please enable location setting to use your current address.",

                          View.OnClickListener { displayLocationSettingsRequest() }, "Retry", Snackbar.LENGTH_LONG

                          )*/

                    val snackbar = Snackbar
                        .make(
                            binding.root,
                            "Please enable location setting to use your current address.",
                            Snackbar.LENGTH_LONG
                        )
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

        val addCircle =
            CircleOptions().center(LatLng(p0.latitude, p0.longitude)).radius(radiusInMeters)
                .fillColor(shadeColor)
                .strokeColor(strokeColor).strokeWidth(8f)
        mCircle = gMap.addCircle(addCircle)

        //move map camera

        //move map camera
        gMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(p0.latitude, p0.longitude)))
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}