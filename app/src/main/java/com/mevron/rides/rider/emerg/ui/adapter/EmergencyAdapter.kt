package com.mevron.rides.rider.emerg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.EmergencyItemBinding
import com.mevron.rides.rider.emerg.domain.model.GetContactDomainData

class EmergencyAdapter(val sele: SelectedContact) :
    ListAdapter<GetContactDomainData, EmergencyAdapter.EmergHolder>(EmergencyDiffUti()) {

    class EmergHolder(val binding: EmergencyItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    class EmergencyDiffUti : DiffUtil.ItemCallback<GetContactDomainData>() {
        override fun areItemsTheSame(
            oldItem: GetContactDomainData,
            newItem: GetContactDomainData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: GetContactDomainData,
            newItem: GetContactDomainData
        ): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergHolder {
        return EmergHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.emergency_item,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: EmergHolder, position: Int) {
        val data = getItem(position)
        holder.binding.root.setOnClickListener {
            sele.selected(data)
        }
        holder.binding.name.text = data.name
        holder.binding.number.text = data.phone


    }

}

interface SelectedContact {
    fun selected(data: GetContactDomainData)
}


