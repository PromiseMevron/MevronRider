package com.mevron.rides.rider.authentication.ui.verifyotp

import android.Manifest
import android.app.Dialog
import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R
import com.mevron.rides.rider.authentication.ui.verifyotp.event.VerifyOTPEvent
import com.mevron.rides.rider.databinding.OTFragmentBinding
import com.mevron.rides.rider.home.ui.HomeActivity
import com.mevron.rides.rider.util.LauncherUtil
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OTPFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        fun newInstance() = OTPFragment()
    }

    // private val viewModel: OTViewModel by viewModels()
    private val MY_PERMISSIONS_REQUEST_LOCATION = 10000
    private val verifyOTPViewModel by viewModels<VerifyOTPViewModel>()
    private lateinit var binding: OTFragmentBinding
    private var phoneNumber = ""
    var phoneWrite = ""
    private var mDialog: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.o_t_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.otpView.itemCount = 6
        phoneNumber = arguments?.let { OTPFragmentArgs.fromBundle(it).phone }!!
        phoneWrite = arguments?.let { OTPFragmentArgs.fromBundle(it).phone }!!
        phoneWrite =
            "${context?.getString(R.string.we_have_sent_you_a_six_digit_code_on_your)} $phoneNumber"
        binding.text2.text = phoneWrite
        verifyOTPViewModel.updateState(phoneNumber = phoneNumber)
        binding.otpView.setOtpCompletionListener {
            verifyOTPViewModel.updateState(code = it)
            verifyOTPViewModel.onEvent(VerifyOTPEvent.OnOTPComplete)
        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        lifecycleScope.launchWhenResumed {
                verifyOTPViewModel.state.collect { state ->
                    toggleBusyDialog(
                        state.isLoading,
                        desc = if (state.isLoading) "Submitting Data..." else null
                    )

                    if (state.error.isNotEmpty()) {
                        handleError(state.error)
                    }

                    if (state.isRequestSuccess) {
                        handleSuccess(state.isNew)
                    }


            }
        }
    }

    private fun hasPermission(): Boolean{
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) || EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun requestPermission(){
        EasyPermissions.requestPermissions(this, "We need access to your location to be able to serve you properly", MY_PERMISSIONS_REQUEST_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
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

    private fun handleError(message: String) {
        binding.incorrectNumber.visibility = View.VISIBLE
        binding.nextButton.setImageResource(R.drawable.next_unenabled)
        binding.nextButton.isEnabled = false
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                verifyOTPViewModel.onEvent(VerifyOTPEvent.OnOTPComplete)
            }.show()
    }

    private fun handleSuccess(isNew: Boolean) {
        binding.incorrectNumber.visibility = View.INVISIBLE
        if (isNew) {
            val action =
                OTPFragmentDirections.actionOTPFragmentToNameSignUpFragment()
            findNavController().navigate(action)
        } else {
            openHomeActivity()
        }
    }

    private fun openHomeActivity(){
        if (hasPermission()){
            startActivity(Intent(activity, HomeActivity::class.java))
            activity?.finish()
        }else{
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            SettingsDialog.Builder(requireContext()).build().show()
        }else{
            requestPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
      openHomeActivity()

    }

}