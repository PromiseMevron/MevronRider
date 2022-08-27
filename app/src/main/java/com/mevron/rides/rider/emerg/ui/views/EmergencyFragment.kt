package com.mevron.rides.rider.emerg.ui.views

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
import androidx.navigation.fragment.findNavController
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.EmergencyFragmentBinding
import com.mevron.rides.rider.emerg.domain.model.GetContactDomainData
import com.mevron.rides.rider.emerg.ui.EmergencyEvent
import com.mevron.rides.rider.emerg.ui.adapter.EmergencyAdapter
import com.mevron.rides.rider.emerg.ui.adapter.SelectedContact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EmergencyFragment : Fragment(), SelectedContact {

    companion object {
        fun newInstance() = EmergencyFragment()
    }

    private val viewModel: EmergencyViewModel by viewModels()
    private lateinit var binding: EmergencyFragmentBinding
    private lateinit var adapter: EmergencyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.emergency_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.addContact.setOnClickListener {
            findNavController().navigate(R.id.action_emergencyFragment_to_addEmergencyFragment)
        }

        lifecycleScope.launchWhenResumed {

                viewModel.state.collect { state ->
                    if (state.backButton) {
                        activity?.onBackPressed()
                    }

                    if (state.openNextPage) {
                        viewModel.handleEvent(EmergencyEvent.MakeAPICall)
                    }

                    if (state.isSuccess) {
                        activity?.onBackPressed()
                    }

                    if (state.result.isNotEmpty()) {
                        setUpAdapter(state.result)
                        binding.emptyData.visibility = View.GONE
                    }else{
                        binding.emptyData.visibility = View.VISIBLE
                    }

            }
        }
        viewModel.handleEvent(EmergencyEvent.MakeAPICall)
    }

    private fun setUpAdapter(data: MutableList<GetContactDomainData>) {
        adapter = EmergencyAdapter(this)
        binding.recyclerView.adapter = adapter
        adapter.submitList(data)

    }

    override fun selected(data: GetContactDomainData) {
        val action =
            EmergencyFragmentDirections.actionEmergencyFragmentToUpdateEmergencyFragment(
                data
            )
        findNavController().navigate(action)
    }


}