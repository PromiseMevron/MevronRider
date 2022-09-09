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
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat
import com.mevron.rides.rider.App

import com.mevron.rides.rider.databinding.HelpFragmentBinding
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.SUPPORT_NUMBER
import zendesk.android.Zendesk

class HelpFragment : Fragment() {

    companion object {
        fun newInstance() = HelpFragment()
    }

    private lateinit var binding: HelpFragmentBinding
    private val requestCall = 1

    val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)


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
        loadWebView()

        binding.callButton.setOnClickListener {
            makePhoneCall()
        }
        binding.chatButton.setOnClickListener {
            Zendesk.instance.messaging.showMessaging(requireContext())
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
            val dial = "tel:${sPref.getString(SUPPORT_NUMBER, "")}"
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

    private fun loadWebView() {
        binding.webView.loadUrl("https://mevron.com/ride/support")
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.allowFileAccessFromFileURLs = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                  view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
               // Toast.makeText(requireContext(), error.description, Toast.LENGTH_LONG).show()
                super.onReceivedError(view, request, error)
            }
        }

    }

}