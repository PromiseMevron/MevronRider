package com.mevron.rides.rider.supportpages.ui.promo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PromoFragmentBinding
import com.mevron.rides.rider.supportpages.domain.model.Supports
import com.mevron.rides.rider.supportpages.ui.adapter.PromoAdapter
import com.mevron.rides.rider.supportpages.ui.notification.event.NotificationEvents
import com.mevron.rides.rider.supportpages.ui.promo.event.PromoEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class PromoFragment : Fragment() {

    companion object {
        fun newInstance() = PromoFragment()
    }

    private val viewModel: PromoViewModel by viewModels()

    private lateinit var binding: PromoFragmentBinding
    private lateinit var adapter: PromoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.promo_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.backButton) {
                        activity?.onBackPressed()
                    }

                    if (state.data.isNotEmpty()) {
                        setUpAdapter(state.data)
                    }
                }
            }
        }

        viewModel.handleEvent(PromoEvents.GetPromotions)

        binding.backButton.setOnClickListener {
            viewModel.handleEvent(PromoEvents.OnBackButtonClicked)
        }
    }

    private fun setUpAdapter(data: MutableList<Supports>) {
        adapter = PromoAdapter()
        binding.recyclerView.adapter = adapter
        adapter.submitList(data)
    }

}