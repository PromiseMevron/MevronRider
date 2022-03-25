package com.mevron.rides.ridertest.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mevron.rides.ridertest.R

class NameSignUpFragment : Fragment() {

    companion object {
        fun newInstance() = NameSignUpFragment()
    }


    private lateinit var nextButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var riderName: EditText
    //
    private var name = ""
    private var submit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //  binding = DataBindingUtil.inflate(inflater, R.layout.name_sign_up_fragment, container, false)
        val view = inflater.inflate(R.layout.name_sign_up_fragment, container, false)
        riderName = view.findViewById(R.id.rider_name)
        nextButton = view.findViewById(R.id.next_button)
        backButton = view.findViewById(R.id.back_button)
        // return binding.root
        return  view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextButton.setOnClickListener {
            submit = true
            validateText()

        }

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

    }
    fun addWatcher(){
        riderName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // validateText()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    validateText()
            }
        })
    }
    fun validateText(){
        name = riderName.text.toString()
        if (AuthUtil.validateFullName(name)){
            nextButton.isEnabled = true
            nextButton.setImageResource(R.drawable.next_enabled)

            riderName.setBackground(resources.getDrawable(R.drawable.rounded_corner_field,))
            riderName.setTextColor(resources.getColor(R.color.field_color ))
            nextButton.setImageResource(R.drawable.next_enabled)
            if (submit){
                val action = NameSignUpFragmentDirections.actionNameSignUpFragmentToEmailLoginFragment(name)
                findNavController().navigate(action)
            }



        }else{
            submit = false
            // Toast.makeText(context, "Enter your full name", Toast.LENGTH_LONG).show()
            nextButton.isEnabled = true
            nextButton.setImageResource(R.drawable.next_enabled)
            riderName.setBackground(resources.getDrawable(R.drawable.rounded_corner_field_red,))
            riderName.setTextColor(resources.getColor(R.color.red ))
            addWatcher()
        }
    }

}