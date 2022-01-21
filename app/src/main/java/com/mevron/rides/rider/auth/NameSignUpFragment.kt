package com.mevron.rides.rider.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.NameSignUpFragmentBinding

class NameSignUpFragment : Fragment() {

    companion object {
        fun newInstance() = NameSignUpFragment()
    }

    private lateinit var viewModel: NameSignUpViewModel
    private lateinit var binding: NameSignUpFragmentBinding
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.name_sign_up_fragment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nextButton.setOnClickListener {
            validateText()
        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.riderName.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                   // validateText()
                }

                override fun afterTextChanged(p0: Editable?) {
                 //   validateText()
                }

            }
        }
    }

    fun validateText(){
       val name = binding.riderName.text.toString()
        if (AuthUtil.validateFullName(name)){
          //  binding.nextButton.isEnabled = true
           // binding.nextButton.setImageResource(R.drawable.next_enabled)
            val action = NameSignUpFragmentDirections.actionNameSignUpFragmentToEmailLoginFragment(name)
            findNavController().navigate(action)
        }else{
            Toast.makeText(context, "Enter your full name", Toast.LENGTH_LONG).show()
           // binding.nextButton.isEnabled = false
            //binding.nextButton.setImageResource(R.drawable.next_unenabled)
        }
    }

}