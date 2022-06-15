package com.mevron.rides.rider.supportpages.ui.help

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mevron.rides.rider.R
import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityCompat

import com.mevron.rides.rider.databinding.HelpFragmentBinding

class HelpFragment : Fragment() {

    companion object {
        fun newInstance() = HelpFragment()
    }

    private lateinit var binding: HelpFragmentBinding
    private val requestCall = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.help_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.callButton.setOnClickListener {
            makePhoneCall()
        }
    }

    private fun makePhoneCall() {
        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CALL_PHONE
                )
            } != PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    requestCall
                )
            }
        } else {
            val dial = "tel:08033214929"
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == requestCall) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(context, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

}