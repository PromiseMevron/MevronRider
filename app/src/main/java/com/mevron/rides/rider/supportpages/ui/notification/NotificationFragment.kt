package com.mevron.rides.rider.supportpages.ui.notification

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
import com.mevron.rides.rider.databinding.NotificationFragmentBinding
import com.mevron.rides.rider.supportpages.domain.model.Supports
import com.mevron.rides.rider.supportpages.ui.adapter.NotificationAdapter
import com.mevron.rides.rider.supportpages.ui.notification.event.NotificationEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    companion object {
        fun newInstance() = NotificationFragment()
    }

    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var binding: NotificationFragmentBinding
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.notification_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
                viewModel.state.collect { state ->
                    if (state.backButton) {
                        activity?.onBackPressed()
                    }

                    if (state.data.isNotEmpty()) {
                        setUpAdapter(state.data)
                        binding.emptyData.visibility = View.GONE
                    }else{
                        binding.emptyData.visibility = View.VISIBLE
                    }

            }
        }

        viewModel.handleEvent(NotificationEvents.GetNotifications)

        binding.backButton.setOnClickListener {
            viewModel.handleEvent(NotificationEvents.OnBackButtonClicked)
        }
    }

    private fun setUpAdapter(data: MutableList<Supports>) {
        adapter = NotificationAdapter()
        binding.recyclerView.adapter = adapter
        adapter.submitList(data)
    }

}