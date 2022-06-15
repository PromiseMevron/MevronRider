package com.mevron.rides.rider.supportpages.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PromoItemBinding
import com.mevron.rides.rider.supportpages.domain.model.Supports

class PromoAdapter() : ListAdapter<Supports, PromoAdapter.PromoHolder>(PromoDiffUtil()) {

    class PromoHolder(val binding: PromoItemBinding) : RecyclerView.ViewHolder(binding.root)

    class PromoDiffUtil : DiffUtil.ItemCallback<Supports>() {
        override fun areItemsTheSame(oldItem: Supports, newItem: Supports): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Supports, newItem: Supports): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoHolder {
        return PromoHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.promo_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PromoHolder, position: Int) {
        val data = getItem(position)
        holder.binding.promoHeading.text = data.heading
        holder.binding.promoSub.text = data.subHeading
    }
}

