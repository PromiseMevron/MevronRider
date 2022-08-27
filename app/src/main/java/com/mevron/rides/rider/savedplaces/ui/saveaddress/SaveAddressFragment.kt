package com.mevron.rides.rider.savedplaces.ui.saveaddress

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.SaveAddressFragmentBinding
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.home.search.PlaceAdapter
import com.mevron.rides.rider.savedplaces.ui.saveaddress.event.SaveAddressEvent
import com.mevron.rides.rider.savedplaces.ui.saveaddress.state.SaveAddressState
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.textChanges


@AndroidEntryPoint
class SaveAddressFragment : Fragment(), PlaceAdapter.OnPlaceSelectedListener {

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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.save_address_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = arguments?.let {
            SaveAddressFragmentArgs.fromBundle(
                it
            ).type
        }!!
        placeHolder = arguments?.let {
            SaveAddressFragmentArgs.fromBundle(
                it
            ).placeholder
        }!!
        title = arguments?.let {
            SaveAddressFragmentArgs.fromBundle(
                it
            ).title
        }!!

        context?.let {
            Places.initialize(it.applicationContext, "AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE")
            placesClient = Places.createClient(it)
        }
        viewModel.updateState(type = type, placeHolder = placeHolder, title = title)

        val sessionToken = AutocompleteSessionToken.newInstance()
        context?.let {
            Places.initialize(it.applicationContext, "AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE")
            placesClient = Places.createClient(it)
        }

        lifecycleScope.launchWhenResumed {

                viewModel.state.collect { state ->
                    binding.stateOfLocation.text = state.title
                    binding.addressField.hint = state.placeHolder
                    toggleBusyDialog(
                        state.isLoading,
                        desc = if (state.isLoading) "Submitting Data..." else null
                    )
                    if (state.autoCompletePredictions.isNotEmpty()) {
                        setUpRecyclerView(state)
                        binding.emptyData.visibility = View.GONE
                    }else{
                        binding.emptyData.visibility = View.VISIBLE
                    }
                    if (state.backPressed) {
                        activity?.onBackPressed()
                    }
                    if (state.isSuccess) {
                        Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
                        activity?.onBackPressed()
                    }
                    if (state.error.isNotEmpty()) {
                        Toast.makeText(context, "Failure to save address", Toast.LENGTH_LONG).show()
                    }
                    if (state.queryChanged) {
                        viewModel.initSearchConfig(
                            sessionToken = sessionToken,
                            placesClient = placesClient
                        )
                    }
                    if (state.addressForEditText.isNotEmpty()) {
                        binding.addressField.setText(state.addressForEditText)
                    }
                    if (state.openNextPage) {
                        val data =
                            LocationModel(lat = state.lat, lng = state.lng, address = state.address)
                        val action =
                            SaveAddressFragmentDirections.actionGlobalSaveAddressDetailsFragment(
                                data
                            )
                        findNavController().navigate(action)
                    }

            }
        }

        binding.close.setOnClickListener {
            activity?.onBackPressed()
         //   viewModel.handleEvent(SaveAddressEvent.OnBackButtonPressed)
        }

        binding.addressField.textChanges().skipInitialValue().onEach {
            viewModel.updateState(queryString = it.toString())
            viewModel.handleEvent(SaveAddressEvent.OnTextChanged)
        }.launchIn(lifecycleScope)

    }

    private fun setUpRecyclerView(state: SaveAddressState) {
        binding.placesRecyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL, false
        )
        adapter = context?.let { PlaceAdapter(this) }!!
        binding.placesRecyclerView.adapter = adapter
        binding.placesRecyclerView.visibility = View.VISIBLE
        adapter.submitList(state.autoCompletePredictions)
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

    override fun onPlaceSelected(pred: AutocompletePrediction) {
        viewModel.processEventLocation(prediction = pred, placesClient = placesClient)
    }


}