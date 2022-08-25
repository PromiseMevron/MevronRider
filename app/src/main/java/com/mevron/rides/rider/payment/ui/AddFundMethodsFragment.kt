package com.mevron.rides.rider.payment.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.FragmentAddFundMethodsBinding
import com.mevron.rides.rider.payment.PaymentAdapter
import com.mevron.rides.rider.payment.PaymentAdapter2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFundMethodsFragment : Fragment() {

    companion object {
        fun newInstance() = AddFundMethodsFragment()
    }

    private lateinit var viewModel: AddFundMethodsViewModel
    private lateinit var binding: FragmentAddFundMethodsBinding
    private lateinit var adapter: PaymentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_fund_methods, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     /*   viewModel.onEvent(CashOutAddFundEvent.GetCards)
        adapter = context?.let { PaymentAdapter(it) }!!
        binding.recyclerView.adapter = adapter
        val amount = arguments?.let { CashOutCardsFragmentArgs.fromBundle(it).amount }
        viewModel.updateState(addFund = amount)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.useWideViewPort = true

        binding.addOtherMethod.setOnClickListener {
            viewModel.getPayLink()
        }

        binding.addCard.setOnClickListener {
            viewModel.updateState(addFund = "100")
            viewModel.getPayLink()
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
*/

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

}