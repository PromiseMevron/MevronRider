package com.mevron.rides.rider.emerg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.EmergencyAddContactBinding
import com.mevron.rides.rider.emerg.data.model.Contact

class AddEmergencyAdapter(val add: SaveNumber) :
    ListAdapter<Contact, AddEmergencyAdapter.AddEmergHolder>(AddEmergencyDiffUti()) {

    class AddEmergHolder(val binding: EmergencyAddContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    class AddEmergencyDiffUti : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddEmergHolder {
        return AddEmergHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.emergency_add_contact,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AddEmergHolder, position: Int) {
        val cnt = getItem(position)
        holder.binding.checkb.setOnCheckedChangeListener(null)
        holder.binding.checkb.isChecked = cnt.isSelected
        holder.binding.checkb.setOnCheckedChangeListener { _, b ->
            cnt.isSelected = b
            val ct =
                Contact(name = cnt.name, phoneNumber = cnt.phoneNumber, isSelected = b, id = cnt.id)
            add.addRemoveContact(ct)
        }
        holder.binding.name.text = cnt.name
        holder.binding.number.text = cnt.phoneNumber
        /* holder.binding.root.setOnClickListener {
             val ct = Contact(
                 name = cnt.name,
                 phoneNumber = cnt.phoneNumber,
                 isSelected = !holder.binding.checkb.isSelected,
                 id = cnt.id
             )
             add.addRemoveContact(ct)
         }*/
    }

}

interface SaveNumber {
    fun addRemoveContact(cnt: Contact)
}



