package com.mevron.rides.rider.auth

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputLayout
import com.hbb20.CountryCodePicker
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PhoneLoginFragmentBinding
import com.mevron.rides.rider.util.Constants.isNewNumberType
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import com.mevron.rides.rider.auth.model.register.RegisterBody
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.snackbar.Snackbar




@AndroidEntryPoint
class PhoneLoginFragment : Fragment() {

    companion object {
        fun newInstance() = PhoneLoginFragment()
    }

    //private lateinit var viewModel: PhoneLoginViewModel
    private val viewModel: PhoneLoginViewModel by viewModels()
    private lateinit var binding: PhoneLoginFragmentBinding
    private  var country = ""
    private  var countryCode = ""
    private var phone = ""
    private var mDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.phone_login_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPhoneListener(binding.phoneNumber, binding.incorrectNumber, binding.countryPicker)
        binding.countryPicker.registerCarrierNumberEditText(binding.phoneNumber)

        binding.countryPicker.setOnCountryChangeListener(OnCountryChangeListener {
            country = binding.countryPicker.selectedCountryName
            countryCode = binding.countryPicker.selectedCountryCode
        })


        binding.phoneNumber.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    initPhoneListener(binding.phoneNumber, binding.incorrectNumber, binding.countryPicker)
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {

                }

            }
        }

        binding.nextButton.setOnClickListener {
            loginUser()
        }
    }


    fun loginUser(){
        phone = binding.phoneNumber.text.toString().trim().drop(1)
        phone = "${countryCode}${phone}"
        val data = RegisterBody(country= country, phoneNumber = phone)
        toggleBusyDialog(true,"Submitting Data...")
        viewModel.phoneLogin(data).observe(viewLifecycleOwner, Observer {
            it.let { res ->
                when(res){
                    is GenericStatus.Error -> {
                        toggleBusyDialog(false)
                        val snackbar = res.error?.error?.message?.let { it1 ->
                            Snackbar
                                .make(binding.root, it1, Snackbar.LENGTH_LONG).setAction("Retry", View.OnClickListener {
                                    ::loginUser
                                })
                        }
                        snackbar?.show()




                    }

                    is  GenericStatus.Success ->{
                        toggleBusyDialog(false)

                        val action = PhoneLoginFragmentDirections.actionPhoneLoginFragmentToOTPFragment(phone)
                        binding.phoneNumber.setText("")
                        findNavController().navigate(action)
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

    fun initPhoneListener(
        phoneField: EditText,
        imageView: ImageView,
        ccPicker: CountryCodePicker?
    ) {

        if (ccPicker != null) {
            ccPicker.setPhoneNumberValidityChangeListener {
                if (!it && !isNewNumberType(phoneField.text.toString().trim())) {
                    imageView.visibility = View.VISIBLE
                    binding.nextButton.setImageResource(R.drawable.next_unenabled)
                    binding.nextButton.isEnabled = false
                }
                else {
                    imageView.visibility = View.INVISIBLE
                    binding.nextButton.setImageResource(R.drawable.next_enabled)
                    binding.nextButton.isEnabled = true
                }
            }
        }
        else {
            phoneField.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val number = phoneField.text.toString().trim()
                    if (number.length !in 11..14) {
                        imageView.visibility = View.INVISIBLE
                        binding.nextButton.setImageResource(R.drawable.next_enabled)
                    } else {
                        imageView.visibility = View.GONE
                        binding.nextButton.setImageResource(R.drawable.next_unenabled)
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
    }

}