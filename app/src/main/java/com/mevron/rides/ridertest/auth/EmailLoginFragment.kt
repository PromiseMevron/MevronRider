package com.mevron.rides.ridertest.auth

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.auth.model.details.SaveDetailsRequest
import com.mevron.rides.ridertest.databinding.EmailLoginFragmentBinding
import com.mevron.rides.ridertest.home.HomeActivity
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailLoginFragment : Fragment() {

    companion object {
        fun newInstance() = EmailLoginFragment()
    }
    private var mDialog: Dialog? = null


    private val viewModel: EmailLoginViewModel by viewModels()
    private lateinit var binding: EmailLoginFragmentBinding
    private var email = ""
    private var name = ""
    private var submit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.email_login_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        name = arguments?.let { EmailLoginFragmentArgs.fromBundle(it).name }!!
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.nextButton.setOnClickListener {
            submit = true
            validateEmail()


            //moveToHome()
        }


    }

    fun moveToHome(){
        startActivity(Intent(activity, HomeActivity::class.java))
        activity?.finish()
    }

    fun addWatcher(){

        binding.riderName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //   validateEmail()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
            }
        })
    }

    fun validateEmail(){
        email = binding.riderName.text.toString()
        if (AuthUtil.validateEmail(email)){
          //  submitDetail()
            binding.nextButton.isEnabled = true
            binding.nextButton.setImageResource(R.drawable.ic_done_enabled)

            binding.incorrectNumber.visibility = View.INVISIBLE
            binding.nextButton.setImageResource(R.drawable.next_enabled)
            binding.nextButton.isEnabled = true
            binding.riderName.setBackground(resources.getDrawable(R.drawable.rounded_corner_field,))
            binding.riderName.setTextColor(resources.getColor(R.color.field_color ))
            if (submit){
                submitDetail()
            }
            //  binding.otpView.setItemBackgroundResources(R.drawable.rounded_corner_field)
         //   binding.otpView.setItemBackground(resources.getDrawable(R.drawable.rounded_corner_field,))
            //  binding.otpView.itemBackground.setBackgroundResource(R.drawable.rounded_corner_field)
          //  binding.otpView.setTextColor(resources.getColor(R.color.field_color ))


        }else{
            submit = false
           // Toast.makeText(context, "Enter a valid email", Toast.LENGTH_LONG).show
         //   binding.nextButton.isEnabled = true
            binding.nextButton.setImageResource(R.drawable.ic_done_enabled)
           // binding.incorrectNumber.visibility = View.INVISIBLE
            binding.nextButton.isEnabled = true
            binding.riderName.setBackground(resources.getDrawable(R.drawable.rounded_corner_field_red,))
            binding.riderName.setTextColor(resources.getColor(R.color.red ))
            addWatcher()
        }
    }

    fun submitDetail(){
        toggleBusyDialog(true,"Submitting Data...")
        val fullName = name.split(" ")
        val fName = fullName[0]
        var lName = ""
        for (i in 1 until (fullName.size)){
            lName += fullName[i]
        }
        val data = SaveDetailsRequest(email = email, firstName = fName, lastName = lName)
        //val data = SaveDetailsRequest(email = email, fullName = name)
        viewModel.sendDetail(data).observe(viewLifecycleOwner, Observer {
            it.let { res ->
                when(res){
                    is GenericStatus.Error -> {
                        toggleBusyDialog(false)
                        val snackbar = res.error?.error?.message?.let { it1 ->
                            Snackbar
                                .make(binding.root, it1, Snackbar.LENGTH_LONG).setAction("Retry", View.OnClickListener {
                                    ::submitDetail
                                })
                        }
                        snackbar?.show()

                    }
                    is  GenericStatus.Success ->{
                        toggleBusyDialog(false)
                        moveToHome()
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