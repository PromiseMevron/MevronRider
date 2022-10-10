package com.mevron.rides.rider.authentication.ui.profilesetup.email

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.IntroScreenActivity
import com.mevron.rides.rider.R
import com.mevron.rides.rider.auth.AuthUtil
import com.mevron.rides.rider.authentication.ui.profilesetup.email.event.RegisterEmailEvent
import com.mevron.rides.rider.databinding.EmailLoginFragmentBinding
import com.mevron.rides.rider.home.ui.HomeActivity
import com.mevron.rides.rider.util.LauncherUtil
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges

@AndroidEntryPoint
class EmailLoginFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        fun newInstance() = EmailLoginFragment()
    }
    private var mDialog: Dialog? = null
    private val MY_PERMISSIONS_REQUEST_LOCATION = 10000

    private val registerEmailViewModel by viewModels<EmailSignViewModel>()
    private lateinit var binding: EmailLoginFragmentBinding
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.email_login_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        name = arguments?.let { EmailLoginFragmentArgs.fromBundle(it).name }!!

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        lifecycleScope.launchWhenResumed {

                registerEmailViewModel.state.collect { state ->
                    if (state.isSuccess){
                        openHomeActivity()
                    }

                    if (state.error.isNotEmpty()){
                        moveToFailure(state.error)
                    }

                    toggleBusyDialog(state.isLoading,
                             desc = if (state.isLoading) "Submitting Data..." else null)
                    checkButton(state = state.isCorrect)
                }

        }

        binding.nextButton.clicks().take(1).onEach {
            registerEmailViewModel.onEvent(RegisterEmailEvent.NextButtonClick)
        }.launchIn(lifecycleScope)

        binding.riderEmail.textChanges().skipInitialValue().onEach {
            registerEmailViewModel.updateState(email = binding.riderEmail.toString().trim())
            validateEmail()
        }.launchIn(lifecycleScope)
    }



    private fun moveToFailure(message: String){
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                registerEmailViewModel.onEvent(RegisterEmailEvent.NextButtonClick)
            }.show()
    }

    private fun checkButton(state: Boolean){
        if (state){
            print("state is $state")
            binding.nextButton.isEnabled = true
            binding.nextButton.setImageResource(R.drawable.ic_done_enabled)
            binding.incorrectNumber.visibility = View.INVISIBLE
            binding.nextButton.setImageResource(R.drawable.next_enabled)
            binding.riderEmail.setBackground(resources.getDrawable(R.drawable.rounded_corner_field,))
            binding.riderEmail.setTextColor(resources.getColor(R.color.field_color ))
        }else{
            binding.nextButton.setImageResource(R.drawable.ic_done_enabled)
             binding.incorrectNumber.visibility = View.INVISIBLE
            binding.nextButton.isEnabled = true
            binding.riderEmail.setBackground(resources.getDrawable(R.drawable.rounded_corner_field,))
            binding.riderEmail.setTextColor(resources.getColor(R.color.field_color ))
        }
    }

    private fun hasPermission(): Boolean{
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) || EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun requestPermission(){
        EasyPermissions.requestPermissions(this, "We need access to the location to be able to serve you properly", MY_PERMISSIONS_REQUEST_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun validateEmail(){
       val email = binding.riderEmail.text.toString()
        registerEmailViewModel.updateState(email = email, isCorrect = AuthUtil.validateEmail(email))
    }

    private fun openHomeActivity(){
        if (hasPermission()){

            val mLocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (mGPS) {
                startActivity(Intent(activity, HomeActivity::class.java))
                activity?.finish()
            }
            else {
                Toast.makeText(requireContext(), "Enable Location and try again", Toast.LENGTH_LONG).show()
                activity?.startActivity(Intent(requireActivity(), IntroScreenActivity::class.java))
            }
        }else{
            requestPermission()
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