package com.mevron.rides.ridertest.settings.emerg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.EmergencyItemBinding

class EmergencyAdapter(): RecyclerView.Adapter<EmergencyAdapter.EmergHolder>() {

    class EmergHolder(val binding: EmergencyItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergHolder {
        return EmergHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.emergency_item, parent, false))

    }

    override fun onBindViewHolder(holder: EmergHolder, position: Int) {
//[ home, work or others ]
        holder.binding.root.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return 3
    }
}



