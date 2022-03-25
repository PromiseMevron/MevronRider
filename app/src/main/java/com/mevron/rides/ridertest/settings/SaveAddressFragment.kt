package com.mevron.rides.ridertest.settings

import android.app.Dialog
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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mevron.rides.ridertest.R

import com.mevron.rides.ridertest.databinding.SaveAddressFragmentBinding
import com.mevron.rides.ridertest.home.model.LocationModel
import com.mevron.rides.ridertest.home.model.getAddress.SaveAddressRequest
import com.mevron.rides.ridertest.home.search.PlaceAdapter
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.util.LauncherUtil
import com.mevron.rides.ridertest.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SaveAddressFragment : Fragment(), PlaceAdapter.OnItemClicked {

    companion object {
        fun newInstance() = SaveAddressFragment()
    }

    private val viewModel: SaveAddressViewModel by viewModels()
    private lateinit var binding: SaveAddressFragmentBinding
    private var title = ""
    private var placeHolder = ""
    private var type = ""
    private var mDialog: Dialog? = null

    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.save_address_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = arguments?.let { SaveAddressFragmentArgs.fromBundle(it).type }!!
        placeHolder = arguments?.let { SaveAddressFragmentArgs.fromBundle(it).placeholder }!!
        title = arguments?.let { SaveAddressFragmentArgs.fromBundle(it).title }!!

        binding.stateOfLocation.text = title
        binding.addressField.hint = placeHolder

        val sessionToken = AutocompleteSessionToken.newInstance()


        context?.let {
            Places.initialize(it.applicationContext, "AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE")
            placesClient = Places.createClient(it)
        }

        binding.close.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.addressField.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (binding.addressField.text.toString().trim().isNotEmpty()) {
                    initSearchConfig(binding.addressField.text.toString().trim(), sessionToken)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


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

                    binding.placesRecyclerView.visibility = View.VISIBLE

                }
                //   Toast.makeText(context, "44444", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                it.printStackTrace()
                //  Toast.makeText(context, "55555", Toast.LENGTH_LONG).show()
            }
    }

    fun saveAdddress(add: SaveAddressRequest){
        toggleBusyDialog(true, "Saving Address")

        viewModel.saveAddresses(add).observe(viewLifecycleOwner, Observer {
            it.let {  res ->
                when(res){
                    is  GenericStatus.Success ->{
                        toggleBusyDialog(false)
                        Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
                        activity?.onBackPressed()
                    }

                    is  GenericStatus.Error ->{
                        toggleBusyDialog(false)
                        Toast.makeText(context, "Failure to save address", Toast.LENGTH_LONG).show()
                    }

                    is GenericStatus.Unaunthenticated -> {
                        toggleBusyDialog(false)
                        Toast.makeText(context, "Failure to save address", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
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
                view.hideKeyboard()
               // home, work or others
                if (type == "others"){
                    if (coordinates != null && it != null){
                        val data = LocationModel(lat = coordinates.latitude, lng = coordinates.longitude, address = it.place.address!!)
                        val action = SaveAddressFragmentDirections.actionGlobalSaveAddressDetailsFragment(data)
                        findNavController().navigate(action)
                    }
                }else{
                    val data = SaveAddressRequest(address = address, latitude = coordinates.latitude.toString(),coordinates.longitude.toString(),
                        name = type, type = type
                        )
                    saveAdddress(data)
                }

                /*    if (isSender) {
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




            }
            .addOnFailureListener {
                //   Toast.makeText(activity, "Error in fetching address details", Toast.LENGTH_SHORT).show()
                it.printStackTrace()
            }

    }

    override fun returnedPred(pred: AutocompletePrediction) {
        processEventLocation(pred, binding.addressField)
        adapter.setData()
    }


}