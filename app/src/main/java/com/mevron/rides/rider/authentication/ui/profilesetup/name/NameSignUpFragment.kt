package com.mevron.rides.rider.authentication.ui.profilesetup.name

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
import com.mevron.rides.rider.R
import com.mevron.rides.rider.auth.AuthUtil
import com.mevron.rides.rider.authentication.ui.profilesetup.name.event.NameSignUpEvent
import com.mevron.rides.rider.databinding.NameSignUpFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges

@AndroidEntryPoint
class NameSignUpFragment : Fragment() {

    companion object {
        fun newInstance() = NameSignUpFragment()
    }
    private lateinit var binding: NameSignUpFragmentBinding
    private val registerNameViewModel by viewModels<NameSignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         binding = DataBindingUtil.inflate(inflater, R.layout.name_sign_up_fragment, container, false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
                registerNameViewModel.state.collect { state ->
                    if (state.openNextScreen){
                        handleSuccess(state.name)
                    }
                    binding.nextButton.isEnabled = state.isSuccess
                    buttonCheck(state = state.isSuccess)
                }
        }

        binding.nextButton.clicks().take(1).onEach{
            registerNameViewModel.onEvent(NameSignUpEvent.OnBottonClicked)
        }.launchIn(lifecycleScope)

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.riderFirstName.textChanges().skipInitialValue().onEach {

            validateText()
        }.launchIn(lifecycleScope)

        binding.riderLastName.textChanges().skipInitialValue().onEach {
            validateText()
        }.launchIn(lifecycleScope)
    }

    fun validateText(){
       val name = binding.riderFirstName.text.toString()
        val lname = binding.riderLastName.text.toString()
        registerNameViewModel.updateState(lastName = lname)
        registerNameViewModel.updateState(firstName = name)
        registerNameViewModel.updateState(isSuccess = AuthUtil.validateFullName(name, lname))
    }

    private fun buttonCheck(state: Boolean){
        if (state){
            binding.nextButton.setImageResource(R.drawable.next_enabled)
         //   binding.riderFirstName.background = resources.getDrawable(R.drawable.rounded_corner_field,)
        //    binding.riderFirstName.setTextColor(resources.getColor(R.color.field_color ))
            //binding.riderLastName.background = resources.getDrawable(R.drawable.rounded_corner_field,)
           // binding.riderLastName.setTextColor(resources.getColor(R.color.field_color ))
            binding.nextButton.setImageResource(R.drawable.next_enabled)
        }else{
            binding.nextButton.setImageResource(R.drawable.next_enabled)
           // binding.riderFirstName.background = resources.getDrawable(R.drawable.rounded_corner_field_red, )
          //  binding.riderFirstName.setTextColor(resources.getColor(R.color.red ))
          //  binding.riderLastName.background = resources.getDrawable(R.drawable.rounded_corner_field_red,)
         //   binding.riderLastName.setTextColor(resources.getColor(R.color.field_color ))
        }
    }

    private fun handleSuccess(name: String){
        val action =
            NameSignUpFragmentDirections.actionNameSignUpFragmentToEmailLoginFragment(
                name
            )
        findNavController().navigate(action)
    }
}