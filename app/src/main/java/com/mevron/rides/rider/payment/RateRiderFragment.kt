package com.mevron.rides.rider.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mevron.rides.rider.R
import com.mevron.rides.rider.payment.rating.RateRiderViewModel

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
}