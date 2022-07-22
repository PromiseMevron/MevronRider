package com.mevron.rides.rider.home.search

import android.Manifest
import android.app.Dialog
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.SearchLocationFragmentBinding
import com.mevron.rides.rider.home.HomeAdapter
import com.mevron.rides.rider.home.OnAddressSelectedListener
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.home.search.ui.SearchLocationEvent
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData
import com.mevron.rides.rider.shared.ui.services.LocationProcessor
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.LauncherUtil
import com.mevron.rides.rider.util.displayLocationSettingsRequest
import com.mevron.rides.rider.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchLocationFragment : Fragment(), PlaceAdapter.OnPlaceSelectedListener,
    OnAddressSelectedListener {

    companion object {
        fun newInstance() = SearchLocationFragment()
    }

    private val locationProcessor = LocationProcessor()

    private val viewModel: SearchLocationViewModel by viewModels()
    private lateinit var binding: SearchLocationFragmentBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: PlaceAdapter
    private var locations = arrayListOf<LocationModel>()
    var itIsStart = false

    // TODO fetch saved addresses from api
    private lateinit var homeAdapter: HomeAdapter
    private var mDialog: Dialog? = null

    private var selectedField: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.search_location_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setEvent(SearchLocationEvent.GetAddress)

        val sessionToken = AutocompleteSessionToken.newInstance()

        lifecycleScope.launch {
            viewModel.uiState.collect {
                toggleBusyDialog(busy = it.isLoading)
                if (it.isAddressEntered) {
                    showAddressList()
                    initSearchConfig(it.destination, sessionToken)
                } else {
                    hideAddressList()
                }

                if (it.isSetLocationOnTheMapClicked) {
                    findNavController().navigate(R.id.action_searchLocationFragment_to_selectOnMapFragment)
                    viewModel.clearState()
                }
            }
        }

        context?.let {
            Places.initialize(it.applicationContext, "AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE")
            placesClient = Places.createClient(it)
        }

        getCurrentLocation(start = true)
        binding.startAddressField.setOnClickListener {
            itIsStart = false
        }
        binding.startAddressField.onFocusChangeListener =
            View.OnFocusChangeListener { _, p1 ->
                if (p1) {
                    itIsStart = false
                }
            }

        // TODO Refactor this!!!
        binding.addHome.setOnClickListener {
            val title = "Add Home"
            val holder = "Enter your home address"
            val type = "home"
            val action = SearchLocationFragmentDirections.actionGlobalSaveAddressFragment(
                title,
                holder,
                type
            )
            findNavController().navigate(action)
        }

        // TODO Refactor this!!!
        binding.addWork.setOnClickListener {
            val title = "Add Work"
            val holder = "Enter your work address"
            val type = "work"
            val action = SearchLocationFragmentDirections.actionGlobalSaveAddressFragment(
                title,
                holder,
                type
            )
            findNavController().navigate(action)
        }

        binding.startAddressField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectedField = binding.startAddressField

                viewModel.setState {
                    copy(
                        origin = binding.startAddressField.text.toString().trim()
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.destAddressField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectedField = binding.stopAddressField
                viewModel.setState {
                    copy(destination = binding.destAddressField.text.toString().trim())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.stopAddressField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectedField = binding.stopAddressField
                viewModel.setState {
                    copy(destination = binding.stopAddressField.text.toString().trim())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.close.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.selectOnMap.setOnClickListener {
            viewModel.setState { copy(isSetLocationOnTheMapClicked = true) }
        }

        binding.addStop.setOnClickListener {
            binding.addStop.visibility = View.GONE
            binding.destAddressLayout.visibility = View.VISIBLE
            binding.addDest.visibility = View.VISIBLE
        }
        binding.addDest.setOnClickListener {
            binding.addStop.visibility = View.VISIBLE
            binding.destAddressLayout.visibility = View.GONE
            binding.addDest.visibility = View.GONE
        }

        binding.currentLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation(start: Boolean = false) {
        itIsStart = start
        locationProcessor.checkLocationPermission(context, onSuccess = { loadLocation() }) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), Constants.LOCATION_REQUEST_CODE
            )
        }
    }

    private fun loadLocation() {
        getLocationProvider()?.lastLocation?.addOnSuccessListener {
            //  Toast.makeText(context, "22", Toast.LENGTH_LONG).show()
            val location = it
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                getAddressFromLocation(currentLocation)
            } else {
                displayLocationSettingsRequest(binding)
            }
        }?.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun getLocationProvider(): FusedLocationProviderClient? {
        return activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    }

    private fun getAddressFromLocation(location: LatLng?) {

        try {
            if (location != null) {
                val geoCoder = Geocoder(context, Locale.getDefault())

                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                val address = if (addresses.isNotEmpty())
                    addresses[0].getAddressLine(0)
                else ""
                binding.startAddressField.setText(address)
                if (locations.isEmpty()) {
                    locations.add(
                        LocationModel(
                            lat = location.latitude,
                            lng = location.longitude,
                            address
                        )
                    )
                } else {
                    locations.add(
                        0,
                        LocationModel(lat = location.latitude, lng = location.longitude, address)
                    )
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun initSearchConfig(query: String, sessionToken: AutocompleteSessionToken) {
        val placeRequest = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(placeRequest)
            .addOnSuccessListener {
                val response = it
                if (response.autocompletePredictions.isNotEmpty()) {

                    binding.placesRecyclerView.layoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(
                            context,
                            RecyclerView.VERTICAL, false
                        )
                    context?.let { _ ->
                        PlaceAdapter(this)
                    }?.let { theAdapter ->
                        adapter = theAdapter
                        binding.placesRecyclerView.adapter = adapter
                        adapter.submitList(response.autocompletePredictions)
                    }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun showAddressList() {
        binding.savedPlacesRecyclerView.visibility = View.GONE
        binding.placesRecyclerView.visibility = View.VISIBLE
        binding.addHome.visibility = View.GONE
        binding.addWork.visibility = View.GONE
        binding.currentLocation.visibility = View.GONE
    }

    private fun hideAddressList() {
        binding.savedPlacesRecyclerView.visibility = View.VISIBLE
        binding.placesRecyclerView.visibility = View.GONE
        binding.addHome.visibility = View.VISIBLE
        binding.addWork.visibility = View.VISIBLE
        binding.currentLocation.visibility = View.VISIBLE
    }

    private fun processEventLocation(prediction: AutocompletePrediction, view: EditText) {

        val placeFilters =
            listOf(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        val fetchRequest = FetchPlaceRequest.builder(prediction.placeId, placeFilters).build()
        placesClient.fetchPlace(fetchRequest)
            .addOnSuccessListener {
                val coordinates = it.place.latLng!!
                var address = ""
                address = if (it.place.name == it.place.address) {
                    it.place.name ?: ""
                } else {
                    it.place.name ?: "" + ", " + it.place.address
                }
                view.setText(it.place.address)
                if (view == binding.startAddressField) {

                    if (locations.isEmpty()) {
                        locations.add(
                            LocationModel(
                                lat = coordinates.latitude,
                                lng = coordinates.longitude,
                                address
                            )
                        )
                    } else {
                        locations.add(
                            0,
                            LocationModel(
                                lat = coordinates.latitude,
                                lng = coordinates.longitude,
                                address
                            )
                        )
                    }
                } else {
                    if (locations.size == 1) {
                        locations.add(
                            LocationModel(
                                lat = coordinates.latitude,
                                lng = coordinates.longitude,
                                address
                            )
                        )

                    } else {
                        locations.add(
                            1,
                            LocationModel(
                                lat = coordinates.latitude,
                                lng = coordinates.longitude,
                                address
                            )
                        )
                    }
                    viewModel.updateOrderStatus(locations)
                    var ads = arrayOf<LocationModel>()
                    for (a in locations) {
                        ads += a
                    }
                    val action =
                        SearchLocationFragmentDirections.actionSearchLocationFragmentToSelectRideFragment2(
                            ads
                        )
                    findNavController().navigate(action)
                }

                view.hideKeyboard()
            }
            .addOnFailureListener {
                //   Toast.makeText(activity, "Error in fetching address details", Toast.LENGTH_SHORT).show()
                it.printStackTrace()
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

    override fun onPlaceSelected(place: AutocompletePrediction) {
        selectedField?.let { processEventLocation(place, it) }
        adapter.submitList(ArrayList())
    }

    override fun onAddressSelected(data: LocationModel, dt: GetSavedAddressData) {
        if (locations.isEmpty()) {
            Toast.makeText(context, "Try again", Toast.LENGTH_LONG).show()
        } else {
            locations.add(data)
            viewModel.updateOrderStatus(locations)
            var ads = arrayOf<LocationModel>()
            for (a in locations) {
                ads += a
            }

            val action =
                SearchLocationFragmentDirections.actionSearchLocationFragmentToSelectRideFragment(
                    ads
                )
            findNavController().navigate(action)
        }
    }
}