package com.mevron.rides.rider.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.CardDetailsFragmentBinding
import com.mevron.rides.rider.home.select_ride.SelectRideFragmentArgs

class CardDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = CardDetailsFragment()
    }

    private lateinit var viewModel: CardDetailsViewModel
    private lateinit var binding: CardDetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.card_details_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // val cards = CardDetailsFragmentArgs.fromBundle(arguments).card
        val cards = arguments?.let { CardDetailsFragmentArgs.fromBundle(it).card }!!
        binding.close.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.cardNumber.text = "****${cards.lastDigits}"
        binding.expiryNumber.text = "${cards.expiryMonth}/${cards.expiryYear}"
    }



}