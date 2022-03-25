package com.mevron.rides.ridertest.settings

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.SaveAddressDetailsFragmentBinding
import com.mevron.rides.ridertest.home.model.LocationModel
import com.mevron.rides.ridertest.home.model.getAddress.SaveAddressRequest
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveAddressDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = SaveAddressDetailsFragment()
    }

    private val viewModel: SaveAddressDetailsViewModel by viewModels()
    private lateinit var binding: SaveAddressDetailsFragmentBinding
    private var mDialog: Dialog? = null
    private lateinit var location: LocationModel
    private lateinit var data: SaveAddressRequest


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.save_address_details_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        location = SaveAddressDetailsFragmentArgs.fromBundle(arguments!!).addressModel
        binding.address.setText(location.address)


        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.editAddress.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.saveAddress.setOnClickListener {
            if (binding.addressName.text.toString().isEmpty()){
                Toast.makeText(context, "Enter the name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            data = SaveAddressRequest(address = location.address, latitude = location.lat.toString(),
                longitude = location.lng.toString(), name = binding.addressName.text.toString(),
            type = "others")
            saveAdddress(data)
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

}