package com.mevron.rides.rider.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.CardDetailsFragmentBinding
import com.mevron.rides.rider.home.model.getCard.Data
import com.mevron.rides.rider.payment.domain.PaymentCard
import com.mevron.rides.rider.payment.domain.getCardImage
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.util.toggleBusyDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = CardDetailsFragment()
    }

    private  val viewModel: CardDetailsViewModel by viewModels()
    private lateinit var binding: CardDetailsFragmentBinding
    private lateinit var cards: PaymentCard

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.card_details_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // val cards = CardDetailsFragmentArgs.fromBundle(arguments).card
        cards = arguments?.let { CardDetailsFragmentArgs.fromBundle(it).card }!!
        binding.close.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.cardNumber.text = "****${cards.lastDigits}"
        binding.expiryNumber.text = "${cards.expiryMonth}/${cards.expiryYear}"
        binding.cardName.text = "*****"
        binding.cardLogo.setImageResource(cards.getCardImage())


        binding.removeCard.setOnClickListener {
            deleteCard()
        }
    }

    private fun deleteCard(){
        toggleBusyDialog(true, "Deleting Card")
        viewModel.deleteCard(cards.uuid).observe(viewLifecycleOwner, androidx.lifecycle.Observer  {
            it.let {  res ->
                when(res){

                    is  GenericStatus.Success ->{
                        toggleBusyDialog(false)
                        Toast.makeText(context, "Card Deleted Successfully", Toast.LENGTH_LONG).show()
                        activity?.onBackPressed()
                    }

                    is  GenericStatus.Error ->{
                        toggleBusyDialog(false)

                        Toast.makeText(context, res.error?.error?.message, Toast.LENGTH_LONG).show()
                    }

                    is GenericStatus.Unaunthenticated -> {
                        toggleBusyDialog(false)
                    }
                }
            }
        })
    }
}