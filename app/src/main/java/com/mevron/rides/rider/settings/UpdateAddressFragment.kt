package com.mevron.rides.rider.settings

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.mevron.rides.rider.R
import android.app.Activity
import com.mevron.rides.rider.databinding.UpdateAddressFragmentBinding
import com.mevron.rides.rider.localdb.SavedAddress
import dagger.hilt.android.AndroidEntryPoint
import com.mevron.rides.rider.util.getString
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.lang.Exception

@AndroidEntryPoint
class UpdateAddressFragment : Fragment() {

    companion object {
        fun newInstance() = UpdateAddressFragment()
    }

    private val viewModel: UpdateAddressViewModel by viewModels()
    private lateinit var binding: UpdateAddressFragmentBinding
    private lateinit var add: SavedAddress
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.update_address_fragment, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add = UpdateAddressFragmentArgs.fromBundle(arguments!!).address
        binding.addressName.setText(add.name)
        binding.address.setText(add.address)
        context?.let {
            Places.initialize(it.applicationContext, "AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE")

        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.editAddress.setOnClickListener {
            val fields = listOf(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS)

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(context)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        }

        binding.saveAddress.setOnClickListener {
            if (binding.addressName.text.toString().isEmpty()) {
                binding.addressName.setBackgroundResource(R.drawable.rounded_corner_field_red)
                Toast.makeText(context, "Enter the name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else{
                binding.addressName.setBackgroundResource(R.drawable.rounded_border_update)
            }

            if (binding.address.text.toString().isEmpty()) {
                binding.address.setBackgroundResource(R.drawable.rounded_corner_field_red)
                Toast.makeText(context, "Enter the address", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else{
            binding.address.setBackgroundResource(R.drawable.rounded_border_update)
        }
        }

        binding.address.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                activateButton()
            }

        })

        binding.addressName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                activateButton()
            }

        })


    }

    fun activateButton(){
        if(binding.addressName.getString().isNotEmpty() && binding.address.getString().isNotEmpty()){
            binding.saveAddress.setTextColor(Color.parseColor("#ffffff"))
         //   binding.saveAddress.setBackgroundColor(Color.parseColor("#FF9B04"))
          //  binding.saveAddress.setBackgroundDrawable()
            binding.saveAddress.setBackgroundColor(Color.parseColor("#FF9B04"))
            binding.address.setBackgroundResource(R.drawable.rounded_border_update)
            binding.addressName.setBackgroundResource(R.drawable.rounded_border_update)
        }else{
            binding.saveAddress.setTextColor(Color.parseColor("#999999"))
           // binding.saveAddress.setBackgroundColor(Color.parseColor("#E6E6E6"))
            binding.saveAddress.setBackgroundColor(Color.parseColor("#E6E6E6"))
        }
    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                AutocompleteActivity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        binding.address.setText(place.add)

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                   
                   binding.address.setText("")
                }
                AutocompleteActivity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }*/

     override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        //super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AUTOCOMPLETE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {

                val place: Place = Autocomplete.getPlaceFromIntent(data)

                try {
                    val lat = place.getLatLng().latitude
                    val lng = place.getLatLng().longitude
                    val add = place.getAddress()
                } catch (e: Exception) {
                }
                //returns list of addresses, take first one and send info to result receiver

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                binding.address.setText("")
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


}