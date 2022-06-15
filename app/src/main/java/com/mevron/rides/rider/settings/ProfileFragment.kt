package com.mevron.rides.rider.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mevron.rides.rider.App
import com.mevron.rides.rider.R
import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.authentication.data.models.profile.ProfileData
import com.mevron.rides.rider.databinding.ProfileFragmentBinding
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.isValidEmail
import com.mevron.rides.rider.util.toggleBusyDialog
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: ProfileFragmentBinding

    val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bitmap>("key")?.observe(viewLifecycleOwner) {result ->
            // Do something with the result.
            binding.profileImage.setImageBitmap(result)
        }

        binding.changeImage.setOnClickListener {

            if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
                != PackageManager.PERMISSION_GRANTED && context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.CAMERA)
                } != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), Constants.LOCATION_REQUEST_CODE)
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_profileFragment_to_faceLivenessDetectionFragment)
        }

        binding.resendLink.setOnClickListener {
            viewModel.resendLink().observe(viewLifecycleOwner, Observer {
                it.let {  res ->
                    when(res){

                        is  GenericStatus.Success ->{
                          Toast.makeText(context, "Email verification link sent", Toast.LENGTH_LONG).show()
                        }

                        is  GenericStatus.Error ->{
                            toggleBusyDialog(false)
                            Toast.makeText(context, "Error in resending email verification link", Toast.LENGTH_LONG).show()
                        }

                        is GenericStatus.Unaunthenticated -> {
                            toggleBusyDialog(false)
                            Toast.makeText(context, "Error in resending email verification link", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        }

        binding.updateProfile.setOnClickListener {
            updateProfile()
        }


        val gson = Gson()
        val json = sPref.getString(Constants.PROFILE, null)
        json?.let {
            val user = gson.fromJson(it, ProfileData::class.java)
            binding.riderName.setText("${user.firstName}  ${user.lastName}")
            binding.riderEmail.setText("${user.email}")
            binding.phoneNumber.setText("${user.phoneNumber}")
            if (user.email.toString().isValidEmail()){
                binding.checkEmail.visibility = View.VISIBLE
            }else{
                binding.checkEmail.visibility = View.GONE
            }



            if (user.emailStatus == 0){
                binding.emailCheck1.visibility = View.VISIBLE
                binding.emailCheck2.visibility = View.VISIBLE
                binding.resendLink.visibility = View.VISIBLE
            }else{
                binding.emailCheck1.visibility = View.GONE
                binding.emailCheck2.visibility = View.GONE
                binding.resendLink.visibility = View.GONE
            }

         //   Picasso.get().load(user.profilePicture.toString()).placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo).into(binding.profileImage)
            // name.text = user.firstName.toString()
        }


    }

    fun updateProfile(){
        val name = binding.riderName.text.toString()
        val phone = binding.phoneNumber.text.toString()
        val email = binding.riderEmail.text.toString()
        val fullName = name.split(" ")
        val fName = fullName[0]
        var lName = ""
        for (i in 1 until (fullName.size)){
            lName += fullName[i]
        }
        val data = SaveDetailsRequest(email = email, firstName = fName, lastName = lName, phoneNumber = phone)
        toggleBusyDialog(true, "Uploading Data")

        viewModel.saveProfile(data).observe(viewLifecycleOwner, Observer {
            toggleBusyDialog(false)
            it.let {  res ->

                when(res){

                    is  GenericStatus.Success ->{
                     getProfile()
                    }

                    is  GenericStatus.Error ->{
                         toggleBusyDialog(false)
                        Toast.makeText(context, "Failure in updating your details", Toast.LENGTH_LONG).show()
                    }

                    is GenericStatus.Unaunthenticated -> {
                         toggleBusyDialog(false)
                        Toast.makeText(context, "Failure in updating your details", Toast.LENGTH_LONG).show()

                    }
                }
            }
        })
    }

    fun getProfile(){
        toggleBusyDialog(true, "Uploading Data")
        viewModel.getProfile().observe(this, Observer {
            it.let {  res ->
                toggleBusyDialog(false)
                when(res){

                    is  GenericStatus.Success ->{
                        val user = res.data?.success?.profileData
                        binding.riderName.setText("${user?.firstName}  ${user?.lastName}")
                        binding.riderEmail.setText("${user?.email}")
                        binding.phoneNumber.setText("${user?.phoneNumber}")
                        Picasso.get().load(user?.profilePicture.toString()).placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo).into(binding.profileImage)
                    }

                    is  GenericStatus.Error ->{
                        // toggleBusyDialog(false)
                    }

                    is GenericStatus.Unaunthenticated -> {
                        // toggleBusyDialog(false)
                    }
                }
            }
        })
    }

}