package com.mevron.rides.ridertest.settings.emerg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.EmergencyAddContactBinding


class AddEmergencyAdapter(val cnt: List<Contact>, val add: SaveNumber) : RecyclerView.Adapter<AddEmergencyAdapter.AddEmergHolder>() {

    class AddEmergHolder(val binding: EmergencyAddContactBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddEmergHolder {
        return AddEmergHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.emergency_add_contact, parent, false))

    }

    override fun onBindViewHolder(holder: AddEmergHolder, position: Int) {
//[ home, work or others ]



        holder.binding.checkb.setOnCheckedChangeListener(null)
        holder.binding.checkb.setOnCheckedChangeListener { _, b ->
            holder.binding.checkb.isSelected = b
            val ct = Contact(name = cnt[position].name, phoneNumber = cnt[position].phoneNumber, isSelected = b, id = cnt[position].id)
            add.addRemoveContact(ct)
        }
        holder.binding.name.text = cnt[position].name
        holder.binding.number.text = cnt[position].phoneNumber
        holder.binding.root.setOnClickListener {
            holder.binding.checkb.isSelected =  !holder.binding.checkb.isSelected
            val ct = Contact(name = cnt[position].name, phoneNumber = cnt[position].phoneNumber, isSelected = !holder.binding.checkb.isSelected, id = cnt[position].id)
            add.addRemoveContact(ct)
        }

    }

    override fun getItemCount(): Int {
        return cnt.size
    }
}

interface SaveNumber{
    fun addRemoveContact (cnt: Contact)
}



