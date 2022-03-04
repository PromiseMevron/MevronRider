package com.mevron.rides.rider.home.search

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mevron.rides.rider.localdb.SavedAddress
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.SearchLocationFragmentBinding
import com.mevron.rides.rider.home.AddressSelected
import com.mevron.rides.rider.home.HomeAdapter
import com.mevron.rides.rider.home.HomeFragmentDirections
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.settings.SettingsFragmentDirections
import com.mevron.rides.rider.util.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class SearchLocationFragment : Fragment(), PlaceAdapter.OnItemClicked, AddressSelected {

    companion object {
        fun newInstance() = SearchLocationFragment()
    }

    private val viewModel: SearchLocationViewModel by viewModels()
    private lateinit var binding: SearchLocationFragmentBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var selectedField: EditText
    private lateinit var adapter: PlaceAdapter
    private var locations = arrayListOf<LocationModel>()
    var itIsStart = false

    private lateinit var homeAdapter: HomeAdapter
    private var mDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_location_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val props = JSONObject()
        props.put("Search Screen", true)
        mixpanel().track("Android Search Screen", props)
        getAddress()
        val sessionToken = AutocompleteSessionToken.newInstance()
        selectedField = binding.startAddressField

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
                if (p1){
                    itIsStart = false
                }
            }

        binding.addHome.setOnClickListener {
            val title = "Add Home"
            val holder = "Enter your home address"
            val type = "home"
            val action = SearchLocationFragmentDirections.actionGlobalSaveAddressFragment(title, holder, type)
            findNavController().navigate(action)
        }

        binding.addWork.setOnClickListener {
            val title = "Add Work"
            val holder = "Enter your work address"
            val type = "work"
            val action = SearchLocationFragmentDirections.actionGlobalSaveAddressFragment(title, holder, type)
            findNavController().navigate(action)
        }

        binding.startAddressField.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectedField = binding.startAddressField
                if (binding.startAddressField.text.toString().trim().isNotEmpty()) {
                    if (!itIsStart){
                        initSearchConfig(binding.startAddressField.text.toString().trim(), sessionToken)
                    }

                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.destAddressField.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectedField = binding.destAddressField
                if (binding.startAddressField.text.toString().trim().isNotEmpty()) {
                    initSearchConfig(binding.destAddressField.text.toString().trim(), sessionToken)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.stopAddressField.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectedField = binding.stopAddressField
                if (binding.startAddressField.text.toString().trim().isNotEmpty()) {
                    initSearchConfig(binding.stopAddressField.text.toString().trim(), sessionToken)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.close.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.selectOnMap.setOnClickListener {
            findNavController().navigate(R.id.action_searchLocationFragment_to_selectOnMapFragment)
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

    private fun getAddress(){
        viewModel.getAddressFromDB().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            for (i in it){
                if (i.type == "home"){
                    binding.addHome.visibility = View.GONE
                }
                if (i.type == "work"){
                    binding.addWork.visibility = View.GONE
                }
            }
            homeAdapter = HomeAdapter(it, this)
            binding.savedPlacesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
                RecyclerView.VERTICAL, false)
            binding.savedPlacesRecyclerView.adapter = homeAdapter
        })
    }


   /* fun getAddress(){
        toggleBusyDialog(true,"Please Wait...")

        viewModel.getAddresses().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it.let {  res ->
                when(res){
                    is  GenericStatus.Success ->{
                        toggleBusyDialog(false)
                        for (i in res.data?.success?.data!!){
                            if (i.type == "home"){
                                binding.addHome.visibility = View.GONE
                            }
                            if (i.type == "work"){
                                binding.addWork.visibility = View.GONE
                            }
                        }
                        homeAdapter = HomeAdapter(res.data.success.data, this)
                        binding.savedPlacesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
                            RecyclerView.VERTICAL, false)
                        binding.savedPlacesRecyclerView.adapter = homeAdapter
                    }

                    is  GenericStatus.Error ->{
                        toggleBusyDialog(false)
                    }

                    is GenericStatus.Unaunthenticated -> {
                        toggleBusyDialog(false)
                    }
                }
            }
        })




    }*/

    fun getCurrentLocation(start: Boolean = false){
        itIsStart = start
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
            } else { displayLocationSettingsRequest(binding) }
        }
            ?.addOnFailureListener {
                it.printStackTrace()
            }
    }
    fun getLocationProvider(): FusedLocationProviderClient? {
        return activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    }

    //  }

    fun getAddressFromLocation(location: LatLng?) {

         try {
            if (location != null) {
                val geoCoder = Geocoder(context, Locale.getDefault())

                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                val address = if (addresses.isNotEmpty())
                    addresses[0].getAddressLine(0)
                else ""
                binding.startAddressField.setText(address)
                if (locations.isEmpty()){
                    locations.add(LocationModel(lat = location.latitude, lng = location.longitude, address))
                }else{
                    locations.add(0, LocationModel(lat = location.latitude, lng = location.longitude, address))
                }
                /**

                 */


            }
        } catch (ex: Exception){
            ex.printStackTrace()

        }
    }


    private fun initSearchConfig( query: String, sessionToken: AutocompleteSessionToken) {
        // var mutableListCountry = mutableListOf("ng","gh")
        val placeRequest = FindAutocompletePredictionsRequest.builder()
            // .setCountry("ng")
            //.setCountries()

            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(placeRequest)
            .addOnSuccessListener {
                val response = it
                if (response.autocompletePredictions.isNotEmpty()) {
                   // Toast.makeText(context, "3333", Toast.LENGTH_LONG).show()
                    print("res is ${response.autocompletePredictions}")

                    binding.placesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
                        RecyclerView.VERTICAL, false)
                    adapter = context?.let { it1 -> PlaceAdapter(it1, response.autocompletePredictions, this) }!!
                    binding.placesRecyclerView.adapter = adapter
                  //  context?.let { it1 -> PlaceAdapter(it1).setData(response.autocompletePredictions) }

                    binding.savedPlacesRecyclerView.visibility = View.GONE
                    binding.placesRecyclerView.visibility = View.VISIBLE
                    binding.addHome.visibility = View.GONE
                    binding.addWork.visibility = View.GONE
                    binding.currentLocation.visibility = View.GONE
                }
             //   Toast.makeText(context, "44444", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                it.printStackTrace()
              //  Toast.makeText(context, "55555", Toast.LENGTH_LONG).show()
            }
    }

    private fun processEventLocation(prediction: AutocompletePrediction, view: EditText) {

        val placeFilters = listOf(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        val fetchRequest = FetchPlaceRequest.builder(prediction.placeId, placeFilters).build()
        placesClient.fetchPlace(fetchRequest)
            .addOnSuccessListener {
                val coordinates = it.place.latLng!!
              //  val locationObject = LocationObject(coordinates?.latitude, coordinates?.longitude, it.place.name, it.place.address)

                var address = ""
                address = if (it.place.name == it.place.address) {
                    it.place.name ?: ""
                } else {
                    it.place.name ?: "" + ", " + it.place.address
                }
                view.setText(it.place.address)
                if (view == binding.startAddressField){

                    if (locations.isEmpty()){
                        locations.add(LocationModel(lat = coordinates.latitude, lng = coordinates.longitude, address))
                    }else{
                        locations.add(0, LocationModel(lat = coordinates.latitude, lng = coordinates.longitude, address))
                    }
                }else{
                    if (locations.size == 1){
                        locations.add(LocationModel(lat = coordinates.latitude, lng = coordinates.longitude, address))

                    }else{
                        locations.add(1, LocationModel(lat = coordinates.latitude, lng = coordinates.longitude, address))

                    }
                    var ads = arrayOf<LocationModel>()
                    for (a in locations){
                        ads += a
                    }
                    val action = SearchLocationFragmentDirections.actionSearchLocationFragmentToSelectRideFragment2(ads)
                    findNavController().navigate(action)

                }



            /*    if (isSender) {


                 *     var ads = arrayOf<LocationModel>()
                for (a in add){
                ads += a
                }
                val action = HomeFragmentDirections.actionHomeFragmentToSelectRideFragment(ads)
                findNavController().navigate(action)


                    activity.viewModel.currentShipment.senderLocation = locationObject
                    activity.viewModel.currentShipment.senderAddress =  address
                    try {
                        activity.viewModel.currentShipment.country = activity.geocoder.getFromLocation(coordinates?.latitude ?: 0.0,
                            coordinates?.longitude ?: 0.0, 1)[0].countryName.toUpperCase()
                        activity.viewModel.currentShipmentSubAdmin = activity.geocoder.getFromLocation(coordinates?.latitude ?: 0.0,
                            coordinates?.longitude ?: 0.0, 1)[0].subAdminArea
                        activity.viewModel.currentShipmentLocality = activity.geocoder.getFromLocation(coordinates?.latitude ?: 0.0,
                            coordinates?.longitude ?: 0.0, 1)[0].adminArea

                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                else {
                    activity.viewModel.currentShipment.receiverLocation = locationObject
                    activity.viewModel.currentShipment.receiverAddress = address
                    try {
                        activity.viewModel.currentReceiverLocality = activity.geocoder.getFromLocation(coordinates?.latitude ?: 0.0,
                            coordinates?.longitude ?: 0.0, 1)[0].adminArea
                        activity.viewModel.currentReceiverSubAdmin = activity.geocoder.getFromLocation(coordinates?.latitude ?: 0.0,
                            coordinates?.longitude ?: 0.0, 1)[0].subAdminArea
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }
                }*/
                view.hideKeyboard()



            }
            .addOnFailureListener {
             //   Toast.makeText(activity, "Error in fetching address details", Toast.LENGTH_SHORT).show()
                it.printStackTrace()
            }

    }

    private fun toggleBusyDialog(busy: Boolean, desc: String? = null){
        if(busy){
            if(mDialog == null){
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_busy_layout,null)
                mDialog = LauncherUtil.showPopUp(requireContext(),view,desc)
            }else{
                if(!desc.isNullOrBlank()){
                    val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_busy_layout,null)
                    mDialog = LauncherUtil.showPopUp(requireContext(),view,desc)
                }
            }
            mDialog?.show()
        }else{
            mDialog?.dismiss()
        }
    }

    override fun returnedPred(pred: AutocompletePrediction) {
        processEventLocation(pred, selectedField)
        adapter.setData()
    }

    override fun selectedAddress(data: LocationModel, dt: SavedAddress) {
        if (locations.isEmpty()){
            Toast.makeText(context, "Try again", Toast.LENGTH_LONG).show()
        }else{
            locations.add(data)
            var ads = arrayOf<LocationModel>()
            for (a in locations){
                ads += a
            }
            val action = SearchLocationFragmentDirections.actionSearchLocationFragmentToSelectRideFragment(ads)
            findNavController().navigate(action)
        }
    }


}


