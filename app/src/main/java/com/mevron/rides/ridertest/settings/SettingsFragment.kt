package com.mevron.rides.ridertest.settings


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.mevron.rides.ridertest.App
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.SettingsFragmentBinding
import com.mevron.rides.ridertest.remote.model.getprofile.Data
import com.mevron.rides.ridertest.util.Constants
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }
    private lateinit var binding: SettingsFragmentBinding

    private lateinit var viewModel: SettingsViewModel

    val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        val gson = Gson()
        val json = sPref.getString(Constants.PROFILE, null)
        json?.let {
            val user = gson.fromJson(it, Data::class.java)
            binding.userName.text = "${user.firstName}  ${user.lastName}"
            binding.userEmail.text = user.email.toString()
            binding.userRating.text = user.rating.toString()
            Picasso.get().load(user.profilePicture.toString()).placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo).into(binding.profileImage)

        }


        binding.profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_global_profileFragment)
        }

        binding.promos.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_promoFragment)
        }

        binding.addEmerg.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_emergencyFragment)
        }

        binding.referal.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_referalFragment)
        }

        binding.addHome.setOnClickListener {
            val title = "Add Home"
            val holder = "Enter your home address"
            val type = "home"
            val action = SettingsFragmentDirections.actionGlobalSaveAddressFragment(title, holder, type)
            findNavController().navigate(action)
        }

        binding.addWork.setOnClickListener {
            val title = "Add Work"
            val holder = "Enter your work address"
            val type = "work"
            val action = SettingsFragmentDirections.actionGlobalSaveAddressFragment(title, holder, type)
            findNavController().navigate(action)
        }

        binding.addSaved.setOnClickListener {

            findNavController().navigate(R.id.action_global_addSavedPlaceFragment)
        }

        binding.logout.setOnClickListener {
            showDialog()
        }

    }
    private fun showDialog() {
        val dialog = activity?.let { Dialog(it) }!!
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.logout_dialod)
        // val body = dialog.findViewById(R.id.body) as TextView
        //  body.text = title
        val yesBtn = dialog.findViewById(R.id.do_cancel) as MaterialButton
        val noBtn = dialog.findViewById(R.id.dont) as MaterialButton
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

}