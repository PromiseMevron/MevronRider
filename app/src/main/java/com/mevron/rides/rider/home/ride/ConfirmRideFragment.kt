package com.mevron.rides.rider.home.ride

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.mevron.rides.rider.home.ride.ui.ConfirmRideEvent
import com.mevron.rides.rider.home.ride.ui.ConfirmRideViewModel
import com.mevron.rides.rider.payment.ui.CustomCancelEventListener
import com.mevron.rides.rider.payment.ui.CustomCancelLayout
import com.mevron.rides.rider.payment.ui.CustomRatingEventListener
import com.mevron.rides.rider.remote.geolocation.GeoAPIClient
import com.mevron.rides.rider.remote.geolocation.GeoAPIInterface
import com.mevron.rides.rider.shared.ui.services.LocationProcessor
import com.mevron.rides.rider.socket.domain.CONNECTED
import com.mevron.rides.rider.socket.domain.Connected
import com.mevron.rides.rider.util.*
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException


@AndroidEntryPoint
class ConfirmRideFragment : Fragment(), OnMapReadyCallback, CustomCancelEventListener {

    private lateinit var binding: ConfirmRideFragmentBinding
    private lateinit var mapView: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var apiInterface: GeoAPIInterface
    private lateinit var location: Array<LocationModel>
    private var isCard = false
    private var mDialog: Dialog? = null
    private lateinit var cancelView: CustomCancelLayout

    companion object {
        fun newInstance() = ConfirmRideFragment()
    }

    private val viewModel: ConfirmRideViewModel by viewModels()

    private val locationProcessor = LocationProcessor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.confirm_ride_fragment, container, false)
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
        viewModel.updateOrderParamStatus()
        cancelView = binding.cancelReasonLayout.ratingCustom
        cancelView.setEventListener(this)
        cancelView.setUp(requireContext())

        // TODO Refactor this to use the PaymentOptionsRepository
       // isCard = arguments?.let { ConfirmRideFragmentArgs.fromBundle(it).isCard }!!

        binding.bqckButton.setOnClickListener {
            activity?.onBackPressed()
        }

        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment

        mapView.getMapAsync(this)
        binding.cancelButton.setOnClickListener {
            showRideCancellationDialog()
        }
        binding.cancelReasonLayout.close.setOnClickListener {
            binding.cancelReasonLayout.cancelBottom.visibility = View.GONE
        }

        binding.cancelReasonLayout.submitFeedback.setOnClickListener {
            if (viewModel.uiState.value.reasonForCancel.isEmpty()){
                Toast.makeText(requireContext(), "Please select a reason for cancellation", Toast.LENGTH_LONG).show()
            }else{
                binding.cancelReasonLayout.cancelBottom.visibility = View.GONE
                viewModel.cancelARide()
            }
        }

        displayLocationSettingsRequest(binding)

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                if (uiState.isBookingConfirmed) {
                    val action =
                        ConfirmRideFragmentDirections.actionGlobalBookedFragment(
                            location
                        )
                    findNavController().navigate(action)
                    viewModel.setState { copy(isBookingConfirmed = false) }
                    viewModel.updateOpenBooked()
                }

                if (uiState.isRideConfirmed) {
                    binding.findingFoundDriver.visibility = View.VISIBLE
                    binding.confirmingDriver.visibility = View.GONE
                    binding.findingDriverFailed.visibility = View.GONE
                }

                if (uiState.isRideConfirmedFailed) {
                    binding.findingFoundDriver.visibility = View.GONE
                    binding.confirmingDriver.visibility = View.GONE
                    binding.findingDriverFailed.visibility = View.VISIBLE
                }

                if (!uiState.isRideConfirmedFailed && !uiState.isRideConfirmed) {
                    binding.findingFoundDriver.visibility = View.GONE
                    binding.confirmingDriver.visibility = View.VISIBLE
                    binding.findingDriverFailed.visibility = View.GONE
                }


                if (uiState.isRideCancelled){
                    binding.cancelReasonSuccess.baseLayout.visibility = View.VISIBLE
                }

                toggleBusyDialog(
                    uiState.isLoading,
                    desc = if (uiState.isLoading) "Submitting Data..." else null
                )
            }
        }
        binding.homeButton.setOnClickListener {
            val  action = ConfirmRideFragmentDirections.actionGlobalHomeFragment().apply {
                showLoader = false
            }
            findNavController().navigate(action)
        }

        binding.cancelReasonSuccess.doneButton.setOnClickListener {
            val  action = ConfirmRideFragmentDirections.actionGlobalHomeFragment().apply {
                showLoader = false
            }
            findNavController().navigate(action)
        }

        viewModel.setEvent(ConfirmRideEvent.ConfirmRideRequest)
        viewModel.setEvent(ConfirmRideEvent.CollectSocketData)

        binding.cancelReasonLayout.other.setOnClickListener {
            cancelView.setUp(requireContext())
            cancelView.visibility = View.VISIBLE
            binding.cancelReasonLayout.submitFeedback.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.GONE
            binding.cancelReasonLayout.other1.visibility = View.VISIBLE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
        }

        binding.cancelReasonLayout.other1.setOnClickListener {
            cancelView.visibility = View.GONE
            binding.cancelReasonLayout.other1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.submitFeedback.visibility = View.VISIBLE
            viewModel.updateCancelValue("")

            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
        }


        binding.cancelReasonLayout.inefficientRoute.setOnClickListener {
            binding.cancelReasonLayout.inefficientRoute.visibility = View.GONE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.VISIBLE

            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaForButtons("inefficient route")
        }

        binding.cancelReasonLayout.inefficientRoute1.setOnClickListener {
            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE

            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaForButtons("")
        }

        binding.cancelReasonLayout.bookedByMistake.setOnClickListener {
            binding.cancelReasonLayout.bookedByMistake.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.VISIBLE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaForButtons("Booked by mistake")
        }

        binding.cancelReasonLayout.bookedByMistake1.setOnClickListener {
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaForButtons("")
        }

        binding.cancelReasonLayout.changeInPlan.setOnClickListener {
            binding.cancelReasonLayout.changeInPlan.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.VISIBLE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaForButtons("Change in plan")
        }

        binding.cancelReasonLayout.changeInPlan1.setOnClickListener {
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaForButtons("")
        }

    /*    binding.cancelReasonLayout.other.setOnClickListener {
            binding.cancelReasonLayout.other.visibility = View.GONE
            binding.cancelReasonLayout.other1.visibility = View.VISIBLE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE

            setAlphaForButtons("other")
        }

        binding.cancelReasonLayout.other1.setOnClickListener {
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE

            setAlphaForButtons("")
        }*/



    }

    private fun setAlphaForButtons(value: String){
        viewModel.updateCancelValue(value)
    }
    private fun addMarkerToPolyLines(geoDirections: GeoDirectionsResponse) {
        val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
        val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation

        val marker3 = MarkerOptions()
            .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_pick))

        val marker4 = MarkerOptions()
            .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_dest))
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
        val padding = (width * 0.3).toInt()

        val boundsUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
      //  gMap.moveCamera(boundsUpdate)
    }

    private fun showRideCancellationDialog() {
        val dialog = activity?.let { Dialog(it) }!!
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cancel_dialog)
        val yesBtn = dialog.findViewById(R.id.do_cancel) as MaterialButton
        val noBtn = dialog.findViewById(R.id.dont) as MaterialButton
        yesBtn.setOnClickListener {
            dialog.dismiss()
            binding.cancelReasonLayout.cancelBottom.visibility = View.VISIBLE
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onMapReady(p0: GoogleMap) {
        if (p0 != null) {
            gMap = p0
        }
        context?.applicationContext?.let { MapsInitializer.initialize(it) }

        locationProcessor.checkLocationPermission(
            context,
            onSuccess = {
                p0.isMyLocationEnabled = true
                startObservingViewModelOnMapReady()
            },
            onPermissionRequired = { locationProcessor.requestLocationPermission(this) }
        )
    }

    private fun startObservingViewModelOnMapReady() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                if (uiState.isValidCoordinate && !uiState.isCoordinateRendered) {
                    val startLocation = LocationModel(
                        uiState.startLocationLat,
                        uiState.startLocationLng,
                        uiState.startLocationAddress
                    )

                    val endLocation = LocationModel(
                        uiState.endLocationLat,
                        uiState.endLocationLng,
                        uiState.destinationAddress
                    )

                    val locations = arrayOf(startLocation, endLocation)
                    this@ConfirmRideFragment.location = locations
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


                    getGeoLocation(locations, gMap) {
                        addMarkerToPolyLines(it)
                        viewModel.resolveCoordinateRendered()
                    }
                }
            }
        }
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

    override fun closeCustomCancelButton() {
        cancelView.visibility = View.GONE
        binding.cancelReasonLayout.other1.visibility = View.GONE
        binding.cancelReasonLayout.other.visibility = View.VISIBLE
        binding.cancelReasonLayout.submitFeedback.visibility = View.VISIBLE
        viewModel.updateCancelValue("")

        binding.cancelReasonLayout.other.visibility = View.VISIBLE
        binding.cancelReasonLayout.other1.visibility = View.GONE

        binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
        binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
        binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
        binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
        binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
        binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE

    }

    override fun ratingCancelDone() {
        cancelView.visibility = View.GONE
        binding.cancelReasonLayout.submitFeedback.visibility = View.VISIBLE
        binding.cancelReasonLayout.other.visibility = View.GONE
        binding.cancelReasonLayout.other1.visibility = View.VISIBLE

        binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
        binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
        binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
        binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
        binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
        binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
    }

    override fun addRCancelRating(rating: String) {
        viewModel.updateCancelValue(rating)
    }
}