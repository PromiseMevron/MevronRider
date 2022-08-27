package com.mevron.rides.rider.settings


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.mevron.rides.rider.App
import com.mevron.rides.rider.MainActivity
import com.mevron.rides.rider.R
import com.mevron.rides.rider.authentication.data.models.profile.ProfileData
import com.mevron.rides.rider.databinding.SettingsFragmentBinding
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.savedplaces.ui.saveaddress.SaveAddressFragmentDirections
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.LauncherUtil
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }
    private lateinit var binding: SettingsFragmentBinding

    private  val viewModel: SettingsViewModel by viewModels()

    private var mDialog: Dialog? = null

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
        val homeAddres = sPref.getString(Constants.HOME, "") ?: ""
        val workAddres = sPref.getString(Constants.WORK, "") ?: ""
        if (homeAddres.isEmpty()){
            binding.addHome.visibility = View.VISIBLE
            binding.addHomeFilled.visibility = View.GONE
        }else{
            binding.addHome.visibility = View.GONE
            binding.addHomeFilled.visibility = View.VISIBLE
            binding.textAddressHome.text = homeAddres
        }

        if (workAddres.isEmpty()){
            binding.addWork.visibility = View.VISIBLE
            binding.addWorkFilled.visibility = View.GONE
        }else{
            binding.addWork.visibility = View.GONE
            binding.addWorkFilled.visibility = View.VISIBLE
            binding.textAddressWork.text = workAddres
        }

        lifecycleScope.launchWhenResumed {

            viewModel.state.collect { state ->
                toggleBusyDialog(
                    state.loader,
                    desc = if (state.loader) "Submitting Data..." else null
                )
                if (state.error.isNotEmpty()){
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                    viewModel.updateState(error = "")
                }

                if (state.isSuccess){
                    sPref.edit().clear().apply()
                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finishAffinity()
                }
            }
        }

        val gson = Gson()
        val json = sPref.getString(Constants.PROFILE, null)
        json?.let {
            val user = gson.fromJson(it, ProfileData::class.java)
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

        binding.addHomeFilled.setOnClickListener {
            val title = "Add Home"
            val holder = "Enter your home address"
            val type = "home"
            val action = SettingsFragmentDirections.actionGlobalSaveAddressFragment(title, holder, type)
            findNavController().navigate(action)
        }

        binding.addWorkFilled.setOnClickListener {
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
            viewModel.logOutDevice()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

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

}