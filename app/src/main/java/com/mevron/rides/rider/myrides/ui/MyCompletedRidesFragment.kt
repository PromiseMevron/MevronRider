package com.mevron.rides.rider.myrides.ui

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.MyCompletedRidesFragmentBinding
import com.mevron.rides.rider.localdb.SavedAddress
import com.mevron.rides.rider.myrides.domain.model.AllTripsResult

import com.mevron.rides.rider.myrides.ui.adapter.RideAdapter
import com.mevron.rides.rider.myrides.ui.adapter.SelectedRide
import com.mevron.rides.rider.myrides.ui.event.MyRidesEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MyCompletedRidesFragment : Fragment(), SelectedRide {

    companion object {
        fun newInstance() = MyCompletedRidesFragment()
    }

    private val viewModel: MyCompletedRidesViewModel by viewModels()
    private lateinit var binding: MyCompletedRidesFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_completed_rides_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = RideAdapter(this)
        viewModel.handleEvent(MyRidesEvents.GetAddress)
        binding.recyclerView.adapter = adapter
        lifecycleScope.launchWhenResumed {
           // repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect {

                    if (it.data.isNotEmpty()){
                        adapter.submitList(it.data)
                        binding.emptyData.visibility = View.GONE
                    }else{
                        binding.emptyData.visibility = View.VISIBLE
                    }
                }
           // }
        }
    }

    override fun select(id: String) {
        val action = MyCompletedRidesFragmentDirections.actionGlobalRideDetailsFragment(id)
        findNavController().navigate(action)
    }

}