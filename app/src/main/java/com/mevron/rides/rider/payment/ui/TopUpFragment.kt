package com.mevron.rides.rider.payment.ui

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.FragmentTopUpBinding
import com.mevron.rides.rider.home.model.getCard.Data
import com.mevron.rides.rider.payment.OnPaymentMethodSelectedListener
import com.mevron.rides.rider.payment.PaySelected2
import com.mevron.rides.rider.payment.PaymentAdapter
import com.mevron.rides.rider.payment.domain.PaymentCard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopUpFragment : Fragment(), OnPaymentMethodSelectedListener, PaySelected2 {

    companion object {
        fun newInstance() = TopUpFragment()
    }

    private val viewModel: TopUpViewModel by viewModels()
    private lateinit var binding: FragmentTopUpBinding
    private lateinit var adapter: PaymentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_up, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.useWideViewPort = true
        val amount = arguments?.let { TopUpFragmentArgs.fromBundle(it).amount }
        viewModel.updateState(addFund = amount)
        viewModel.getCards()
        adapter = PaymentAdapter(this@TopUpFragment, 0)
        binding.recyclerView.adapter = adapter
        adapter = PaymentAdapter(this@TopUpFragment, 0)

        binding.recyclerView.adapter = adapter

        lifecycleScope.launchWhenResumed {

            viewModel.state.collect { state ->

                binding.backButton.setOnClickListener {
                    if (binding.webView.visibility == View.GONE) {
                        activity?.onBackPressed()
                    } else {
                        if (state.addCard) {
                            viewModel.getCards()
                        }
                        binding.webView.visibility = View.GONE
                    }
                }

                if (state.cardData.isNotEmpty()) {
                    Log.d("THE CARDS ARE", "THE CARDS ARE ${state.cardData}")
                    val dataToUse = state.cardData.filter {
                        it.uuid != null && it.lastDigits != null
                    }
                    if (dataToUse.isEmpty()) {
                        binding.addCard.visibility = View.VISIBLE
                    } else {
                        binding.addCard.visibility = View.GONE
                    }
                    adapter.submitList(
                        dataToUse/*.map {
                        CardData(
                            bin = it.bin,
                            brand = it.brand,
                            expiryYear = it.expiryYear,
                            expiryMonth = it.expiryMonth,
                            lastDigits = it.lastDigits,
                            uuid = it.uuid,
                            type = it.type
                        )
                    }*/
                    )
                }

                if (state.successFund) {
                    Toast.makeText(
                        requireContext(),
                        "Fund Added Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (state.payLink.isNotEmpty()) {
                    loadWebView(state.payLink)
                    binding.webView.visibility = View.VISIBLE
                    viewModel.updateState(payLink = "")
                }
            }

        }

        binding.addOtherMethod.setOnClickListener {
            viewModel.getPayLink()
        }

        binding.addCard.setOnClickListener {
            viewModel.updateState(addFund = "100", addCard = true)
            viewModel.getPayLink()
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

    override fun onPaymentMethodSelected(paymentCard: PaymentCard) {
        Log.d("we reached here", "we reached here 22222222")
        viewModel.updateState(cardNumber = paymentCard.uuid)
        viewModel.addFundToWallet()
    }

    override fun selected(data: Data) {

    }
}