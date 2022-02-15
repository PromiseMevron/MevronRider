package com.mevron.rides.rider.auth

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.App
import com.mevron.rides.rider.R
import com.mevron.rides.rider.auth.model.otp.ValidateOTPRequest
import com.mevron.rides.rider.databinding.OTFragmentBinding
import com.mevron.rides.rider.home.HomeActivity
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.TOKEN
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OTPFragment : Fragment() {

    companion object {
        fun newInstance() = OTPFragment()
    }

    private val viewModel: OTViewModel by viewModels()
    private lateinit var binding:OTFragmentBinding
    private var phoneNumber = ""
    var phoneWrite = ""
    private var mDialog: Dialog? = null
    private var isNew  = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.o_t_fragment, container, false)
        return binding.root
      //  return inflater.inflate(R.layout.o_t_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.otpView.itemCount = 6
        phoneNumber = arguments?.let { OTPFragmentArgs.fromBundle(it).phone }!!
        phoneWrite = arguments?.let { OTPFragmentArgs.fromBundle(it).phone }!!
        phoneWrite = "${context?.getString(R.string.we_have_sent_you_a_six_digit_code_on_your)} $phoneNumber"
        binding.text2.text = phoneWrite
        binding.otpView.setOtpCompletionListener {
            val data = ValidateOTPRequest(code = it, phoneNumber =phoneNumber)
            validateOTP(data)
        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.nextButton.setOnClickListener {
            if (isNew){
                findNavController().navigate(R.id.action_OTPFragment_to_nameSignUpFragment)
            }else{
                startActivity(Intent(activity, HomeActivity::class.java))
                activity?.finish()
            }

        }

    }

    fun validateOTP(data: ValidateOTPRequest){
        toggleBusyDialog(true,"Submitting Data...")
        viewModel.validateOTP(data).observe(viewLifecycleOwner, Observer {
            it.let { res ->
                when(res){
                    is GenericStatus.Error -> {
                        toggleBusyDialog(false)
                        val snackbar = res.error?.error?.message?.let { it1 ->
                            Snackbar
                                .make(binding.root, it1, Snackbar.LENGTH_LONG).setAction("Retry", View.OnClickListener {
                                    ::validateOTP
                                })
                        }
                        snackbar?.show()
                        binding.incorrectNumber.visibility = View.VISIBLE
                        binding.nextButton.setImageResource(R.drawable.next_unenabled)
                        binding.nextButton.isEnabled = true
                      //  binding.otpView.setBackgroundResource(R.drawable.rounded_corner_field_red)
                        binding.otpView.setItemBackgroundResources(R.drawable.rounded_corner_field_red)
                        binding.otpView.setItemBackground(resources.getDrawable(R.drawable.rounded_corner_field_red))
                      //  binding.ccpLayout.setBackgroundResource(R.drawable.rounded_corner_field_red)
                        binding.otpView.setTextColor(resources.getColor(R.color.red ))
                    }
                    is  GenericStatus.Success ->{
                        toggleBusyDialog(false)
                        val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                        val editor = sPref.edit()
                        editor.putString(TOKEN, res.data?.success?.data?.accessToken)
                        val type = (res.data?.success?.data?.riderType ?: "").lowercase()
                        isNew = type == "new"
                        //TOKENhhh
                      //  editor.putString("TOKENhhh", resource.data?.data?.token)
                        editor.apply()
                        binding.incorrectNumber.visibility = View.INVISIBLE
                        binding.nextButton.setImageResource(R.drawable.next_enabled)
                        binding.nextButton.isEnabled = true
                      //  binding.otpView.setItemBackgroundResources(R.drawable.rounded_corner_field)
                        binding.otpView.setItemBackground(resources.getDrawable(R.drawable.rounded_corner_field,))
                      //  binding.otpView.itemBackground.setBackgroundResource(R.drawable.rounded_corner_field)
                        binding.otpView.setTextColor(resources.getColor(R.color.field_color ))


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