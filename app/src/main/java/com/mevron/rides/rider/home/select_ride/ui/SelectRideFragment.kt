package com.mevron.rides.rider.home.select_ride.ui

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.SelectRideFragmentBinding
import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.home.select_ride.CarsAdapter
import com.mevron.rides.rider.home.select_ride.OnCarSelectedListener
import com.mevron.rides.rider.home.select_ride.domain.MobilityTypeDomainModel
import com.mevron.rides.rider.remote.geolocation.GeoAPIClient
import com.mevron.rides.rider.remote.geolocation.GeoAPIInterface
import com.mevron.rides.rider.shared.ui.services.LocationProcessor
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.LauncherUtil
import com.mevron.rides.rider.util.bitmapFromVector
import com.mevron.rides.rider.util.displayLocationSettingsRequest
import com.mevron.rides.rider.util.getGeoLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectRideFragment : Fragment(), OnMapReadyCallback, OnCarSelectedListener {

    companion object {
        fun newInstance() = SelectRideFragment()
    }

    private val viewModel: SelectRideViewModel by viewModels()
    private lateinit var mapView: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var binding: SelectRideFragmentBinding
    private lateinit var apiInterface: GeoAPIInterface
    private var mDialog: Dialog? = null
    private lateinit var adapter: CarsAdapter
    private lateinit var cars: List<MobilityTypeDomainModel>
    var pos = 0

    private val locationProcessor = LocationProcessor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.select_ride_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->

//                toggleBusyDialog(uiState.isLoading, "Loading...")
                if (uiState.isMobilityTypeAvailable) {
                    binding.mevronRideBottom.destAddres.text = "Confirm ${cars[pos].name}"
                }

                if(!uiState.isMobilityTypesFetched && uiState.locationWrapper.model.isNotEmpty()) {
                    getCars()
                }

                if (uiState.isOpenPaymentClicked) {
                    val action =
                        SelectRideFragmentDirections.actionSelectRideFragmentToPaymentFragment2(
                            uiState.locationWrapper.model
                        )
                    findNavController().navigate(action)
                    viewModel.setState { copy(isOpenPaymentClicked = false) }
                }

                if (!uiState.isListLoaded && uiState.mobilityTypes.isNotEmpty())
                    context?.let {
                        adapter = CarsAdapter(
                            uiState.mobilityTypes,
                            pos,
                            this@SelectRideFragment
                        )
                        binding.mevronRideBottom.recyclerView.adapter = adapter
                        viewModel.setState { copy(isListLoaded = true) }
                    }
            }
        }

        arguments?.let { SelectRideFragmentArgs.fromBundle(it).location }?.let {
            viewModel.setEvent(SelectRideEvent.OnLocationAvailable(LocationWrapper(it)))
        }

        GeoAPIClient().getClient()?.create(GeoAPIInterface::class.java)?.let {
            apiInterface = it
        }

        binding.mevronRideBottom.destAddres.setOnClickListener {
            viewModel.setEvent(SelectRideEvent.OpenPaymentFragmentEvent)
        }
        binding.bqckButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // name = arguments?.let { EmailLoginFragmentArgs.fromBundle(it).name }!!
        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        binding.mevronRideBottom.codeApplied.setOnClickListener {
            if (binding.mevronRideBottom.codeSave.visibility == View.VISIBLE) {
                binding.mevronRideBottom.codeSave.visibility = View.GONE
                binding.mevronRideBottom.codeDirection.setImageResource(R.drawable.ic_code_up)
            } else {
                binding.mevronRideBottom.codeSave.visibility = View.VISIBLE
                binding.mevronRideBottom.codeDirection.setImageResource(R.drawable.ic_code_down)
            }
        }
        mapView.getMapAsync(this)
        displayLocationSettingsRequest(binding)
    }

    private fun getCars() {
        viewModel.setEvent(SelectRideEvent.RequestRideEvent)
    }

    private fun toggleBusyDialog(busy: Boolean, desc: String? = null) {
        if (busy) {
            if (mDialog == null) {
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_busy_layout, null)
                mDialog = LauncherUtil.showPopUp(requireContext(), view, desc)
            } else {
                if (!desc.isNullOrBlank()) {
                    val view = LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_busy_layout, null)
                    mDialog = LauncherUtil.showPopUp(requireContext(), view, desc)
                }
            }
            mDialog?.show()
        } else {
            mDialog?.dismiss()
        }
    }

    private fun addMarkerToPolyLines(
        geoDirections: GeoDirectionsResponse,
        location: Array<LocationModel>,
        onMarkerAdded: () -> Unit = {}
    ) {
        val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation
        val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
        var loc1 = location[0].address
        if (loc1.length > 20) {
            loc1 = location[0].address.substring(0..20)
        }

        var loc2 = location[1].address
        if (loc2.length > 20) {
            loc2 = location[1].address.substring(0..20)
        }

        val sLl = (startLocation?.lat ?: 0.0)
        val sLlg = (startLocation?.lng ?: 0.0)

        val sLl2 = (endLocation?.lat ?: 0.0)
        val sLlg2 = (endLocation?.lng ?: 0.0)


        val marker1 = MarkerOptions()
            .position(LatLng(sLl, sLlg))
            .anchor(1.05f, 1.05f)
            .icon(
                BitmapDescriptorFactory.fromBitmap(
                    createClusterBitmap(
                        add = loc1,
                        loc = "Start",
                        color = "#F57519"
                    )
                )
            )

        val marker2 = MarkerOptions()
            .position(LatLng(sLl2, sLlg2))
            .anchor(1.05f, 1.05f)
            .icon(
                BitmapDescriptorFactory.fromBitmap(
                    createClusterBitmap(
                        add = loc2,
                        loc = "To",
                        color = "#F9170F"
                    )
                )
            )

        val marker3 = MarkerOptions()
            .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_pick))

        val marker4 = MarkerOptions()
            .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_dest))

        gMap.addMarker(marker1)
        gMap.addMarker(marker2)
        gMap.addMarker(marker3)
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
        val padding = (width * 0.1).toInt()

        val boundsUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        //  gMap.animateCamera(boundsUpdate)
        Toast.makeText(context, "22", Toast.LENGTH_LONG).show()
        onMarkerAdded()
    }

    private fun createClusterBitmap(add: String, loc: String, color: String): Bitmap {
        val cluster: View = LayoutInflater.from(context).inflate(
            R.layout.choose_rider_marker,
            null
        )
        val clusterSizeText = cluster.findViewById<View>(R.id.address) as TextView
        clusterSizeText.text = add
        val clusterSizeText2 = cluster.findViewById<View>(R.id.state) as TextView
        clusterSizeText2.text = loc
        clusterSizeText2.setTextColor(Color.parseColor(color))

        //  clusterSizeText.text = clusterSize.toString()
        cluster.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
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

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            gMap = p0
            Toast.makeText(context, "33", Toast.LENGTH_LONG).show()
        }

        MapsInitializer.initialize(context?.applicationContext)

        arguments?.let { theArguments -> SelectRideFragmentArgs.fromBundle(theArguments).location }
            ?.let { locationModel ->
                viewModel.setEvent(SelectRideEvent.OnLocationAvailable(LocationWrapper(locationModel)))
            }

        lifecycleScope.launch {
            viewModel.uiState.collect {

                if (!it.isEmptyDirection && !it.isMarkerRendered) {
                    addMarkerToPolyLines(it.geoDirectionsResponse, it.locationWrapper.model) {
                        viewModel.setState { copy(isMarkerRendered = true) }
                    }
                }

                if (it.locationWrapper.model.isNotEmpty() && !it.isLocationLoaded) {
                    getGeoLocation(it.locationWrapper.model, gMap) { geoDirectionsResponse ->
                        viewModel.setEvent(
                            SelectRideEvent.GeoResponseAvailable(
                                geoDirectionsResponse
                            )
                        )
                    }
                }

                if (it.locationWrapper.model.isNotEmpty()) {
                    val location = it.locationWrapper.model
                    val builder = LatLngBounds.Builder()
                    builder.include(LatLng(location[0].lat, location[0].lng))
                    builder.include(LatLng(location[1].lat, location[1].lng))
                    val bounds = builder.build()
                    val width = resources.displayMetrics.widthPixels;
                    val height = resources.displayMetrics.heightPixels;
                    val padding = (width * 0.40).toInt()
                    val cu = CameraUpdateFactory.newLatLngBounds(bounds, 100)

                    //  gMap.setPadding(50,50,50,50)
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
            }
        }

        locationProcessor.checkLocationPermission(context) {
            locationProcessor.requestLocationPermission(this)
        }
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

    private fun initRecyclerView() {
        binding.mevronRideBottom.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                context,
                RecyclerView.VERTICAL, false
            )
    }

    override fun onCarSelected(pos: Int, car: String) {
        context?.let { _ ->
            adapter = CarsAdapter(cars, pos, this)
            binding.mevronRideBottom.recyclerView.adapter = adapter
            binding.mevronRideBottom.destAddres.text = "Confirm ${car}"
        }
    }
}