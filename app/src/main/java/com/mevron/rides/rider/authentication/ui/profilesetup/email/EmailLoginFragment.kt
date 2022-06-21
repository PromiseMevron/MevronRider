package com.mevron.rides.rider.authentication.ui.profilesetup.email

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R
import com.mevron.rides.rider.auth.AuthUtil
import com.mevron.rides.rider.authentication.ui.profilesetup.email.event.RegisterEmailEvent
import com.mevron.rides.rider.databinding.EmailLoginFragmentBinding
import com.mevron.rides.rider.home.ui.HomeActivity
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges

@AndroidEntryPoint
class EmailLoginFragment : Fragment() {

    companion object {
        fun newInstance() = EmailLoginFragment()
    }
    private var mDialog: Dialog? = null

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
            repeatOnLifecycle(Lifecycle.State.STARTED){
                registerEmailViewModel.state.collect { state ->
                    if (state.isSuccess){
                        moveToHome()
                    }

                    if (state.error.isNotEmpty()){
                        moveToFailure(state.error)
                    }

                    toggleBusyDialog(state.isLoading,
                             desc = if (state.isLoading) "Submitting Data..." else null)
                    checkButton(state = state.isCorrect)
                }
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

    private fun moveToHome(){
        startActivity(Intent(activity, HomeActivity::class.java))
        activity?.finish()
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
            binding.nextButton.isEnabled = state
            binding.nextButton.setImageResource(R.drawable.ic_done_enabled)
            binding.incorrectNumber.visibility = View.INVISIBLE
            binding.nextButton.setImageResource(R.drawable.next_enabled)
            binding.riderEmail.setBackground(resources.getDrawable(R.drawable.rounded_corner_field,))
            binding.riderEmail.setTextColor(resources.getColor(R.color.field_color ))
        }else{
            binding.nextButton.setImageResource(R.drawable.ic_done_enabled)
             binding.incorrectNumber.visibility = View.VISIBLE
            binding.nextButton.isEnabled = state
            binding.riderEmail.setBackground(resources.getDrawable(R.drawable.rounded_corner_field_red,))
            binding.riderEmail.setTextColor(resources.getColor(R.color.red ))
        }
    }

    private fun validateEmail(){
       val email = binding.riderEmail.text.toString()
        registerEmailViewModel.updateState(email = email, isCorrect = AuthUtil.validateEmail(email))
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