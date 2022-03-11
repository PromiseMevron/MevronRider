package com.mevron.rides.rider.payment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mevron.rides.rider.R

class RateRiderFragment : Fragment() {

    companion object {
        fun newInstance() = RateRiderFragment()
    }

    private lateinit var viewModel: RateRiderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rate_rider_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RateRiderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}