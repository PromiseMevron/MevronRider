package com.mevron.rides.rider.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.AddSavedPlaceFragmentBinding
import com.mevron.rides.rider.home.AddressSelected
import com.mevron.rides.rider.localdb.SavedAddress
import com.mevron.rides.rider.home.HomeAdapter
import com.mevron.rides.rider.home.HomeViewModel
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddSavedPlaceFragment : Fragment(), AddressSelected {

    companion object {
        fun newInstance() = AddSavedPlaceFragment()
    }

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: AddSavedPlaceFragmentBinding
    private var mDialog: Dialog? = null
    private lateinit var adapter: HomeAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_saved_place_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAddress()

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.addNewPlace.setOnClickListener {
            val title = "Save a place"
            val holder = "Enter any address"
            val type = "others"
            val action = AddSavedPlaceFragmentDirections.actionGlobalSaveAddressFragment(title, holder, type)
            findNavController().navigate(action)
        }
    }


    private fun getAddress(){
        viewModel.getAddressFromDB().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter = HomeAdapter(it, this)
            binding.placesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
                RecyclerView.VERTICAL, false)
            binding.placesRecyclerView.adapter = adapter
        })
    }

    fun getAddressFromApi(){
        toggleBusyDialog(true,"Please Wait...")

        viewModel.getAddresses().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it.let {  res ->
                when(res){

                    is  GenericStatus.Success ->{
                        toggleBusyDialog(false)

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

    override fun selectedAddress(data: LocationModel, dt: SavedAddress) {
        val action = AddSavedPlaceFragmentDirections.actionAddSavedPlaceFragmentToUpdateSavedPlaceFragment(dt)
        findNavController().navigate(action)
    }

}