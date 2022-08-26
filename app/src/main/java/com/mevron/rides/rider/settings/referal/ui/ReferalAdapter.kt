package com.mevron.rides.rider.settings.referal.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.ReferalItemBinding
import com.mevron.rides.rider.settings.referal.domain.model.ReferralData

class ReferalAdapter(val sel: SelectedReferal) :
    ListAdapter<ReferralData, ReferalAdapter.ReferalHolder>(
        ReferralDiffUtil()
    ) {
    class ReferralDiffUtil : DiffUtil.ItemCallback<ReferralData>() {
        override fun areItemsTheSame(oldItem: ReferralData, newItem: ReferralData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ReferralData, newItem: ReferralData): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

    }

    class ReferalHolder(val binding: ReferalItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferalHolder {
        return ReferalHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.referal_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReferalHolder, position: Int) {
        val dt = getItem(position)
        holder.binding.referalHeading.text = dt.title
        holder.binding.referalSub.text = dt.description
        holder.binding.promoDate.text = dt.createAt
        holder.binding.root.setOnClickListener {
            sel.select(dt.id)
        }

    }
}

interface SelectedReferal {
    fun select(id: String)
}

