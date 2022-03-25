package com.mevron.rides.ridertest.settings.myrides

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.MyCompletedRidesFragmentBinding

class MyCompletedRidesFragment : Fragment(), SelectedRide {

    companion object {
        fun newInstance() = MyCompletedRidesFragment()
    }

    private lateinit var viewModel: MyCompletedRidesViewModel
    private lateinit var binding:MyCompletedRidesFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_completed_rides_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var adapter = RideAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    override fun select() {
        findNavController().navigate(R.id.action_global_rideDetailsFragment)
    }

}