package com.mevron.rides.rider.payment.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.BalanceItemsBinding
import com.mevron.rides.rider.payment.domain.PaymentDetailsDomainDatum

class BalanceSubAdapter(val context: Context) :
    ListAdapter<PaymentDetailsDomainDatum, BalanceSubAdapter.BalanceSubHolder>(
        BalanceSubDiffUtil()
    ) {

    class BalanceSubDiffUtil : DiffUtil.ItemCallback<PaymentDetailsDomainDatum>() {
        override fun areItemsTheSame(
            oldItem: PaymentDetailsDomainDatum,
            newItem: PaymentDetailsDomainDatum
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PaymentDetailsDomainDatum,
            newItem: PaymentDetailsDomainDatum
        ): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

    class BalanceSubHolder(val binding: BalanceItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceSubHolder {
        return BalanceSubHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.balance_items,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: BalanceSubHolder, position: Int) {
        val item = getItem(position)
        holder.binding.title.text = item.narration
        holder.binding.amount.text = item.amount
        holder.binding.date.text = item.time
        holder.binding.description.text = item.method
    }


}