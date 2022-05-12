package com.mevron.rides.rider.settings.myrides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.RideDetailsFragmentBinding

class RideDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = RideDetailsFragment()
    }

    private lateinit var viewModel: RideDetailsViewModel
    private lateinit var binding: RideDetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.ride_details_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }


}