package com.mevron.rides.rider.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PayTypeItemBinding
import com.mevron.rides.rider.payment.domain.PaymentCard
import com.mevron.rides.rider.payment.domain.getCardImage
import com.mevron.rides.rider.payment.domain.isCash

class PaymentAdapter(
    private val paySelected: OnPaymentMethodSelectedListener,
    val data: List<PaymentCard>,
    val selectedPosition: Int
) : RecyclerView.Adapter<PaymentAdapter.PayHolder>() {

    class PayHolder(val binding: PayTypeItemBinding) : RecyclerView.ViewHolder(binding.root) {}

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
        holder.binding.next.visibility = View.INVISIBLE
        val currentPaymentMethod = data[position]
        if (currentPaymentMethod.isCash()) {
            holder.binding.image.setImageResource(R.drawable.ic_cash_add)
            holder.binding.typeName.text = "Cash"
        } else {
            holder.binding.image.setImageResource(currentPaymentMethod.getCardImage())
            holder.binding.typeName.text = "****" + currentPaymentMethod.lastDigits
        }

        holder.binding.root.setOnClickListener {
            paySelected.onPaymentMethodSelected(
                currentPaymentMethod
            )
        }

        if (selectedPosition != -1) {
            if (position == selectedPosition) {
                holder.binding.rootView.setBackgroundResource(R.drawable.rounded_border_car)
            }
        }
    }

    override fun getItemCount(): Int = data.size
}

interface OnPaymentMethodSelectedListener {
    fun onPaymentMethodSelected(paymentCard: PaymentCard)
}