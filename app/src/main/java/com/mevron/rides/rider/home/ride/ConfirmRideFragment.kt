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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.*
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
import com.mevron.rides.rider.remote.geolocation.GeoAPIClient
import com.mevron.rides.rider.remote.geolocation.GeoAPIInterface
import com.mevron.rides.rider.shared.ui.services.LocationProcessor
import com.mevron.rides.rider.socket.domain.CONNECTED
import com.mevron.rides.rider.socket.domain.Connected
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.bitmapFromVector
import com.mevron.rides.rider.util.displayLocationSettingsRequest
import com.mevron.rides.rider.util.getGeoLocation
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException


@AndroidEntryPoint
class ConfirmRideFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: ConfirmRideFragmentBinding
    private lateinit var mapView: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var apiInterface: GeoAPIInterface
    private lateinit var location: Array<LocationModel>
    private var isCard = false

    companion object {
        fun newInstance() = ConfirmRideFragment()
    }

    private val viewModel: ConfirmRideViewModel by viewModels()

    private val locationProcessor = LocationProcessor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       /* var mSocket: Socket? = null

        try {
            mSocket = IO.socket("http://staging.mevron.com:8086")
            Log.d("TAG", "Connecting socket")

        } catch (e: URISyntaxException) {
            Log.d("TAG", "Error Connecting socket $e")
        }
        mSocket?.on("event") {
            Log.d("TAG", "abcd $it")
            val type = "rider"
            val json = JSONObject(Connected.toData(arrayOf("uuid", "type"), arrayOf("c83be6ac-9b7e-4033-b0d7-1e7ea2e21a9c", type))).toString()
            mSocket.emit(CONNECTED, "{\"uuid\":\"c83be6ac-9b7e-4033-b0d7-1e7ea2e21a9c\", \"type\": \"rider\"}")
        } ?:  Log.d("TAG", "abcd 333")
        //search_drivers
        mSocket?.on("search_drivers") {
            Log.d("TAG", "abcd 2 $it")
        } ?:  Log.d("TAG", "abcd 4444")
        //  mSocket?.connect()
        mSocket?.open()*/

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
                } else {
                    binding.findingFoundDriver.visibility = View.GONE
                    binding.confirmingDriver.visibility = View.VISIBLE
                }
            }
        }
        viewModel.setEvent(ConfirmRideEvent.ConfirmRideRequest)
        viewModel.setEvent(ConfirmRideEvent.CollectSocketData)
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
        gMap.moveCamera(boundsUpdate)
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
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            gMap = googleMap
        }
        MapsInitializer.initialize(context?.applicationContext)

        locationProcessor.checkLocationPermission(
            context,
            onSuccess = {
                googleMap?.isMyLocationEnabled = true
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
                    getGeoLocation(locations, gMap) {
                        addMarkerToPolyLines(it)
                        viewModel.resolveCoordinateRendered()
                    }
                }
            }
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
}