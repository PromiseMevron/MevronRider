package com.mevron.rides.ridertest.settings.promos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.PromoItemBinding

class PromoAdapter(): RecyclerView.Adapter<PromoAdapter.PromoHolder>() {

    class PromoHolder(val binding: PromoItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoHolder {
        return PromoHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.promo_item, parent, false))
    }

    override fun onBindViewHolder(holder: PromoHolder, position: Int) {
//[ home, work or others ]


    }

    override fun getItemCount(): Int {
        return 7
    }
}

