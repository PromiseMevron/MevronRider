package com.mevron.rides.rider.home.ui

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.HomeFragmentBinding
import com.mevron.rides.rider.home.HomeAdapter
import com.mevron.rides.rider.home.OnAddressSelectedListener
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData
import com.mevron.rides.rider.shared.ui.services.LocationProcessor
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.LauncherUtil
import com.mevron.rides.rider.util.LocationSingleton.getAddressFromLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback, LocationListener, OnAddressSelectedListener {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val locationProcessor = LocationProcessor()
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawer: ImageButton
    private var googleMap: GoogleMap? = null
    private lateinit var mapView: SupportMapFragment

    private lateinit var adapter: HomeAdapter
    private var add: ArrayList<LocationModel> = arrayListOf()
    private var mDialog: Dialog? = null
    private var loca: MutableLiveData<LatLng> = MutableLiveData()

    var LOCATION_REFRESH_TIME = 4000 // 4 seconds. The Minimum Time to get location update
    var LOCATION_REFRESH_DISTANCE =
        0 // 0 meters. The Minimum Distance to be changed to get location update


    private var mCircle: Circle? = null
    var radiusInMeters = 100.0
    var strokeColor = -0x10000 //Color Code you want

    var shadeColor = 0x44ff0000 //opaque red fill


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add = arrayListOf()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.mevronHomeBottom.bottomSheet)
        setUpAdapter()
        getAddress()
        viewModel.setEvent(HomeEvent.ObserveTripState)

        lifecycleScope.launch {
            viewModel.uiState.collect {

                it.shouldOpenTipView.get {
                    findNavController().navigate(R.id.action_global_addTipFragment)
                }

                if (it.shouldOpenConfirmRide) {
                    findNavController().navigate(R.id.action_paymentFragment2_to_confirmRideFragment)
                    viewModel.resolveConfirmRide()
                }

                if (it.shouldOpenBookedRide) {
                    Log.d("sdsdd", "sdsdss 2")
                    findNavController().navigate(R.id.action_global_bookedFragment)
                    viewModel.resolveOpenBookedRide()
                }

                if (it.isOpenSearchClicked) {
                    findNavController().navigate(R.id.action_homeFragment_to_searchLocationFragment)
                    viewModel.resolveSearchClicked()
                }

                if (it.isAddSavePlaceClicked) {
                    findNavController().navigate(R.id.action_global_addSavedPlaceFragment)
                    viewModel.resolveAddSavePlaceClicked()
                }

                if (it.isScheduleClicked) {
                    binding.mevronHomeBottom.bottomSheet.visibility = View.GONE
                    binding.mevronScheduleBottom.scheduleLayout.visibility = View.VISIBLE
                }

                if (it.isScheduleTheRideClicked) {
                    binding.mevronHomeBottom.bottomSheet.visibility = View.VISIBLE
                    binding.mevronScheduleBottom.scheduleLayout.visibility = View.GONE
                }

                if (it.isMyLocationButtonClicked) {
                    gotToMyLocation()
                    viewModel.setState { copy(isMyLocationButtonClicked = false) }
                }

                if (it.isLocationAdded) {
                    if (add.isEmpty()) {
                        Toast.makeText(context, "Try again", Toast.LENGTH_LONG).show()
                    } else {
                        add.add(it.locationModel)
                        //  val ads = arrayListOf<LocationModel>()
                        var ads: Array<LocationModel> = arrayOf()
                        for (a in add) {
                            ads += a
                        }
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToSelectRideFragment(ads)
                        findNavController().navigate(action)
                    }

                    viewModel.setState { copy(isLocationAdded = false) }
                }
            }
        }

        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        binding.mevronHomeBottom.destAddressField.setOnClickListener {
            viewModel.setEvent(HomeEvent.OnSearchClicked)
        }

        binding.mevronHomeBottom.allSavedLayout.setOnClickListener {
            viewModel.setEvent(HomeEvent.OnAllSavedAddressClicked)
        }

        binding.mevronHomeBottom.scheduleButton.setOnClickListener {
            viewModel.setEvent(HomeEvent.ScheduleButtonClicked)
        }

        binding.mevronScheduleBottom.scheduleTheRide.setOnClickListener {
            viewModel.setEvent(HomeEvent.ScheduleTheRideClicked)
        }

        binding.mevronHomeBottom.myLocation.setOnClickListener {
            requestLocationPermissionIfnNotEnabled()
        }

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
                    else -> {
                        binding.mevronHomeBottom.allSavedLayout.visibility = View.GONE
                        binding.mevronHomeBottom.scheduleButton.visibility = View.VISIBLE
                        binding.mevronHomeBottom.myLocation.visibility = View.VISIBLE
                    }
                }
            }
        })

        gotToMyLocation()
    }

    private fun gotToMyLocation() {
        activity?.let {
            locationProcessor.animateToCurrentPosition(
                (activity as AppCompatActivity),
                googleMap,
                {
                    val coordinate = LatLng(
                        it.lat,
                        it.lng
                    ) //Store these lat lng values somewhere. These should be constant.

                    val location = CameraUpdateFactory.newLatLngZoom(coordinate, 15f)
                    googleMap?.animateCamera(location)
                },
                {
                    requestLocationPermissionIfnNotEnabled()
                }
            )
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

    // @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            this.googleMap = googleMap
        }

        MapsInitializer.initialize(activity?.applicationContext)
        requestLocationPermissionIfnNotEnabled {
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.isMyLocationButtonEnabled = false
            gotToMyLocation()
        }
    }

    private fun HomeFragment.requestLocationPermissionIfnNotEnabled(callback: () -> Unit = {}) {
        locationProcessor.checkLocationPermission(context, onSuccess = callback) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), Constants.LOCATION_REQUEST_CODE
            )
        }
    }

    override fun onLocationChanged(p0: Location) {
        context?.let { getAddressFromLocation(context!!, p0) }

        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(p0.latitude, p0.longitude)))
        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(18.5f))
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

    private fun getAddress() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                if (it.savedAddresses.isEmpty()) {
                    bottomSheetBehavior.peekHeight = 500
                }
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                adapter.submitList(it.savedAddresses)

            }
        }
    }

    private fun setUpAdapter() {
        adapter = HomeAdapter(this, requireContext())
        binding.mevronHomeBottom.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                context,
                RecyclerView.VERTICAL, false
            )
        binding.mevronHomeBottom.recyclerView.adapter = adapter
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

    override fun onAddressSelected(data: LocationModel, dt: GetSavedAddressData) {
        // what's the plan for dt variable?
        viewModel.setState { (copy(locationModel = data, isLocationAdded = true)) }
    }
}