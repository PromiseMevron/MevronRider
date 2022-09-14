package com.mevron.rides.rider.payment

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.SavedPaymentFragmentBinding
import com.mevron.rides.rider.home.model.getCard.Data
import com.mevron.rides.rider.payment.domain.PaymentCard
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SavedPaymentFragment : Fragment(), PaySelected2, OnPaymentMethodSelectedListener {

    private val viewModel: SavedPaymentViewModel by viewModels()
    private lateinit var binding: SavedPaymentFragmentBinding
    private var mDialog: Dialog? = null
    private lateinit var adapter: PaymentAdapter

    companion object {
        fun newInstance() = SavedPaymentFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.saved_payment_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.useWideViewPort = true

        adapter = PaymentAdapter(this@SavedPaymentFragment, -1)
        binding.recyclerView.adapter = adapter
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
            viewModel.updateState(amount = "100")
            viewModel.getPayLink()
        }

        viewModel.getPaymentMethods()

        lifecycleScope.launchWhenResumed {

                viewModel.state.collect { state ->
                    toggleBusyDialog(
                        state.isLoading,
                        desc = if (state.isLoading) "Processing..." else null
                    )
                    adapter = PaymentAdapter(this@SavedPaymentFragment, -1)
                    binding.recyclerView.adapter = adapter
                    adapter.submitList(state.paymentCards)

                    if (state.error.isNotEmpty()) {
                        Log.d("Failure", state.error)
                        Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                    }

                    if (state.paymentLink.isNotEmpty()) {
                        loadWebView(state.paymentLink)
                        binding.webView.visibility = View.VISIBLE
                        viewModel.updateState(payLink = "")
                    }
                }

        }

    }

    private fun loadWebView(webUrl: String) {
        binding.webView.loadUrl(webUrl)
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

    override fun selected(data: Data) {
      //  val action =
          //  SavedPaymentFragmentDirections.actionSavedpaymentFragmentToCardDetailsFragment(data)
      //  findNavController().navigate(action)
    }

    override fun onPaymentMethodSelected(paymentCard: PaymentCard) {
        if (paymentCard.type.toLowerCase() != "cash" && paymentCard.type.toLowerCase() != "wallet") {
            val action =
                SavedPaymentFragmentDirections.actionSavedpaymentFragmentToCardDetailsFragment(
                    paymentCard
                )
            findNavController().navigate(action)
        }
    }


}