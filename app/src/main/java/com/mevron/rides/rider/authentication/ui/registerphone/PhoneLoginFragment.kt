package com.mevron.rides.rider.authentication.ui.registerphone

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
import com.google.android.material.snackbar.Snackbar
import com.hbb20.CountryCodePicker
import com.mevron.rides.rider.R
import com.mevron.rides.rider.authentication.ui.registerphone.event.RegisterPhoneEvent
import com.mevron.rides.rider.authentication.ui.registerphone.state.RegisterPhoneState
import com.mevron.rides.rider.databinding.PhoneLoginFragmentBinding
import com.mevron.rides.rider.util.Constants.isNewNumberType
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges


@AndroidEntryPoint
class PhoneLoginFragment : Fragment() {

    companion object {
        fun newInstance() = PhoneLoginFragment()
    }

    private val registerPhoneViewModel by viewModels<RegisterPhoneViewModel>()
    private lateinit var binding: PhoneLoginFragmentBinding
    private var mDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.phone_login_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.countryPicker.registerCarrierNumberEditText(binding.phoneNumber)
        lifecycleScope.launchWhenResumed {

                registerPhoneViewModel.state.collect { state ->
                    updateNextButtonImageResource(state.isValidNumber)
                    toggleBusyDialog(
                        state.loading,
                        desc = if (state.loading) "Submitting Data..." else null
                    )

                    if (state.error.isNotEmpty()) {
                        handleError(state.error)
                    }

                    if (state.requestSuccess) {
                        handleSuccess(state.countryCodeAndPhoneNumber)
                    }
                    startCheckOfNumber(state.canCheckNumber, state)
                }

        }

        binding.phoneNumber.textChanges().skipInitialValue().onEach {
            if (it.isNotEmpty())
            registerPhoneViewModel.updateState(
                phoneNumber = if (it.toString().first().toString() == "0") it.toString().trim().drop(1).replace(" ", "").replace("-", "") else it.toString().trim().replace(" ", "").replace("-", "")
            )
            checkNumberValidity(binding.countryPicker)
        }.launchIn(lifecycleScope)

        registerPhoneViewModel.updateState(
            country = binding.countryPicker.selectedCountryName,
            countryCode = binding.countryPicker.selectedCountryCode
        )

        binding.countryPicker.setOnCountryChangeListener {
            registerPhoneViewModel.updateState(
                country = binding.countryPicker.selectedCountryName,
                countryCode = binding.countryPicker.selectedCountryCode
            )
        }

        binding.nextButton.clicks().take(1).onEach {
            registerPhoneViewModel.onEvent(RegisterPhoneEvent.NextButtonClick)
        }.launchIn(lifecycleScope)
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

    private fun startCheckOfNumber(checkNumber: Boolean, state: RegisterPhoneState){
        if (checkNumber)
            handleIncorrectNumber(state.isCorrectNumber)
    }

    private fun updateNextButtonImageResource(isValidNumber: Boolean) {
        if (isValidNumber) {
            binding.incorrectNumber.visibility = View.INVISIBLE
            binding.nextButton.setImageResource(R.drawable.next_unenabled)
        } else {
            binding.incorrectNumber.visibility = View.GONE
            binding.nextButton.setImageResource(R.drawable.next_enabled)
        }
    }

    private fun handleIncorrectNumber(isCorrectNumber: Boolean) {
        if (isCorrectNumber) {
            binding.incorrectNumber.visibility = View.INVISIBLE
            binding.nextButton.setImageResource(R.drawable.next_enabled)
            binding.nextButton.isEnabled = true
            binding.phoneNumber.setBackgroundResource(R.drawable.rounded_corner_field)
            binding.ccpLayout.setBackgroundResource(R.drawable.rounded_corner_field)
            binding.phoneNumber.setTextColor(resources.getColor(R.color.field_color))

        } else {
            binding.incorrectNumber.visibility = View.VISIBLE
            binding.nextButton.setImageResource(R.drawable.next_unenabled)
            binding.nextButton.isEnabled = false
            binding.phoneNumber.setBackgroundResource(R.drawable.rounded_corner_field_red)
            binding.ccpLayout.setBackgroundResource(R.drawable.rounded_corner_field_red)
            binding.phoneNumber.setTextColor(resources.getColor(R.color.red))
        }
    }

    private fun checkNumberValidity(ccPicker: CountryCodePicker?) {
        ccPicker?.setPhoneNumberValidityChangeListener {
            if (!it && !isNewNumberType(binding.phoneNumber.text.toString().trim())) {
                registerPhoneViewModel.updateState(
                    isCorrectNumber = false
                )
            } else {
                registerPhoneViewModel.updateState(isCorrectNumber = true)
            }
        } ?: binding.phoneNumber.textChanges().onEach {
            val number = binding.phoneNumber.text.toString().trim()
            registerPhoneViewModel.updateState(number = number)
        }.launchIn(lifecycleScope)
    }

    private fun handleError(message: String) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                registerPhoneViewModel.onEvent(RegisterPhoneEvent.NextButtonClick)
            }.show()
    }

    private fun handleSuccess(phoneAndCountryCode: String) {
        val action =
            PhoneLoginFragmentDirections.actionPhoneLoginFragmentToOTPFragment(phoneAndCountryCode)
        binding.phoneNumber.setText("")
        findNavController().navigate(action)
        registerPhoneViewModel.updateState(requestSuccess = false)
    }
}