package com.mevron.rides.rider.settings


import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }
    private lateinit var binding: SettingsFragmentBinding

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
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