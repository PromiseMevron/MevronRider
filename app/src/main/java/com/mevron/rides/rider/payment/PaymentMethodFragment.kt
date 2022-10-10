package com.mevron.rides.rider.payment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PaymentMethodFragmentBinding
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.baseRequest
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentMethodFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentMethodFragment()
    }

    private val viewModel: PaymentMethodViewModel by viewModels()
    private lateinit var binding: PaymentMethodFragmentBinding
    private var mDialog: Dialog? = null
    private lateinit var client: PaymentsClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.payment_method_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.useWideViewPort = true

        val options = Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        client = Wallet.getPaymentsClient(requireContext(), options)
        val request = IsReadyToPayRequest.fromJson(Constants.baseRequest.toString())
        request.let {
            val task = client.isReadyToPay(request)
            task.addOnCompleteListener { completedTask ->
                val result = completedTask.getResult(ApiException::class.java)
                result?.let {
                    if (it)
                    binding.google.visibility = View.GONE
                    else
                        binding.google.visibility = View.GONE
                }
            }
        }

        binding.backButton.setOnClickListener {
            if (binding.webView.visibility == View.GONE) {
                activity?.onBackPressed()
            } else {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    binding.webView.visibility = View.GONE
                }
            }
        }

        binding.credit.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setMessage("To add your card to mevron, we will charge a fee that will be refunded into your wallet")
            builder.setTitle("Info!")
            builder.setCancelable(true)
            builder.setPositiveButton("Proceed",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    toggleBusyDialog(
                        true, "Please wait"
                    )
                    viewModel.updateState(amount = "100")
                    viewModel.getPayLink()
                } as DialogInterface.OnClickListener)

            builder.setNegativeButton("No",
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                    dialog.cancel()
                } as DialogInterface.OnClickListener)

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        binding.google.setOnClickListener {
            binding.google.isClickable = false
        }


        lifecycleScope.launch {

                viewModel.state.collect { state ->

                    if (state.error.isNotEmpty()) {
                        Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                    }

                    if (state.paymentLink.isNotEmpty()) {
                        toggleBusyDialog(
                            false,
                        )
                        loadWebView(state.paymentLink)
                        binding.webView.visibility = View.VISIBLE
                        viewModel.updateState(payLink = "")
                    }

                    if (state.successFund) {
                        toggleBusyDialog(
                            false,
                        )
                        Toast.makeText(
                            requireContext(),
                            "Card Added Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.webView.visibility = View.GONE
                    }
                }

        }

    }

    private fun loadWebView(webUrl: String) {
        toggleBusyDialog(
            false,
        )
        binding.webView.loadUrl(webUrl)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (url.contains("confirm-payment/", ignoreCase = true)){
                    viewModel.updateState(confirmLink = url)
                    viewModel.confirmPayment()
                    binding.webView.visibility = View.GONE
                    // Toast.makeText(requireContext(), "Payment successful", Toast.LENGTH_LONG).show()
                    // activity?.onBackPressed()
                }
              //  view?.loadUrl(url)
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
                super.onReceivedError(view, request, error)
            }
        }
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