package com.mevron.rides.rider.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PayTypeItemBinding
import com.mevron.rides.rider.payment.domain.PAYTYPE
import com.mevron.rides.rider.payment.domain.PaymentCard
import com.mevron.rides.rider.payment.domain.getCardImage
import com.mevron.rides.rider.payment.domain.isCash

class PaymentAdapter(
    private val paySelected: OnPaymentMethodSelectedListener,
    val selectedPosition: Int
) : ListAdapter<PaymentCard, PaymentAdapter.PayHolder>(PaymentAdapterDiffUtil()){

    class PayHolder(val binding: PayTypeItemBinding) : RecyclerView.ViewHolder(binding.root)

    class PaymentAdapterDiffUtil: DiffUtil.ItemCallback<PaymentCard>(){
        override fun areItemsTheSame(oldItem: PaymentCard, newItem: PaymentCard): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PaymentCard, newItem: PaymentCard): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayHolder {
        return PayHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.pay_type_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PayHolder, position: Int) {
        Log.d("we reached here", "we reached here 44444")
        holder.binding.next.visibility = View.INVISIBLE
        val currentPaymentMethod = getItem(position)
        holder.binding.clicjButton.setOnClickListener {
            paySelected.onPaymentMethodSelected(
                currentPaymentMethod
            )
        }

        holder.binding.image.setOnClickListener {
            Log.d("we reached here", "we reached here 3333333")
            paySelected.onPaymentMethodSelected(
                currentPaymentMethod
            )
        }

        if (selectedPosition != -1) {
            if (position == selectedPosition) {
                holder.binding.rootView.setBackgroundResource(R.drawable.rounded_border_car)
            }
        }

        when(currentPaymentMethod.isCash()){
            PAYTYPE.CARD -> {
                holder.binding.image.setImageResource(currentPaymentMethod.getCardImage())
                holder.binding.typeName.text = "****" + currentPaymentMethod.lastDigits
            }
            PAYTYPE.CASH ->  {
                holder.binding.image.setImageResource(R.drawable.ic_cash_add)
                holder.binding.typeName.text = "Cash"
            }
            PAYTYPE.WALLET -> {
                holder.binding.image.setImageResource(R.drawable.ic_wallet)
                holder.binding.typeName.text = "Wallet"
            }
        }
    }
}

interface OnPaymentMethodSelectedListener {
    fun onPaymentMethodSelected(paymentCard: PaymentCard)
}