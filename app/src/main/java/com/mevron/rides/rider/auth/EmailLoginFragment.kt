package com.mevron.rides.rider.auth

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.App
import com.mevron.rides.rider.R
import com.mevron.rides.rider.auth.model.details.SaveDetailsRequest
import com.mevron.rides.rider.databinding.EmailLoginFragmentBinding
import com.mevron.rides.rider.home.HomeActivity
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.LauncherUtil
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
            validateEmail()
        }
    }

    fun moveToHome(){
        startActivity(Intent(activity, HomeActivity::class.java))
        activity?.finish()
    }

    fun validateEmail(){
        email = binding.riderName.text.toString()
        if (AuthUtil.validateEmail(email)){
            submitDetail()
        }else{
            Toast.makeText(context, "Enter a valid email", Toast.LENGTH_LONG).show()
        }
    }

    fun submitDetail(){
        toggleBusyDialog(true,"Submitting Data...")
        val data = SaveDetailsRequest(email = email, fullName = name)
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