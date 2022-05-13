package com.mevron.rides.rider.savedplaces.ui.addaddress

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.AddSavedPlaceFragmentBinding
import com.mevron.rides.rider.home.AddressSelected
import com.mevron.rides.rider.home.HomeAdapter
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.localdb.SavedAddress
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData
import com.mevron.rides.rider.savedplaces.ui.addaddress.event.AddSavedAddressEvent
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AddSavedPlaceFragment : Fragment(), AddressSelected {
    companion object {
        fun newInstance() = AddSavedPlaceFragment()
    }

    private val viewModel: AddSavedPlaceViewModel by viewModels()
    private lateinit var binding: AddSavedPlaceFragmentBinding
    private var mDialog: Dialog? = null
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_saved_place_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.backButton) {
                        activity?.onBackPressed()
                    }

                    if (state.openNextPage) {
                        migrateToUpdate()
                    }

                    if (state.data.isNotEmpty()) {
                        setUpAdapter(state.data)
                    }
                }
            }
        }

        viewModel.handleEvent(AddSavedAddressEvent.GetNewAddress)

        binding.backButton.setOnClickListener {
            viewModel.handleEvent(AddSavedAddressEvent.OnBackButtonClicked)
        }

        binding.addNewPlace.setOnClickListener {
            viewModel.handleEvent(AddSavedAddressEvent.OnAddNewClicked)
        }
    }


    private fun setUpAdapter(data: MutableList<GetSavedAddressData>) {
        adapter = HomeAdapter(this, requireContext())
        binding.placesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context,
            RecyclerView.VERTICAL, false
        )
        binding.placesRecyclerView.adapter = adapter
        val dt = (data.map {
            SavedAddress(
                type = it.type,
                name = it.name,
                lat = it.lat,
                lng = it.lng,
                address = it.address,
                uiid = it.uuid,
                id = null
            )
        }).toMutableList()
        adapter.submitList(dt)
    }

    private fun migrateToUpdate() {
        val title = resources.getString(R.string.save_a_place)
        val holder = resources.getString(R.string.enter_any_address)
        val type = resources.getString(R.string.others)
        val action =
            AddSavedPlaceFragmentDirections.actionGlobalSaveAddressFragment(
                title,
                holder,
                type
            )
        findNavController().navigate(action)
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

    override fun selectedAddress(data: LocationModel, dt: SavedAddress) {
        val action =
            AddSavedPlaceFragmentDirections.actionAddSavedPlaceFragmentToUpdateSavedPlaceFragment(
                dt
            )
        findNavController().navigate(action)
    }

}