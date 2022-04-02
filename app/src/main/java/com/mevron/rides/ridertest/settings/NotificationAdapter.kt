package com.mevron.rides.ridertest.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.NotificationItemBinding

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.NotifiHolder>() {

    class NotifiHolder(val binding:NotificationItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.NotifiHolder {
        return NotifiHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.notification_item, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationAdapter.NotifiHolder, position: Int) {
        val adatpter = NotificationSubAdapter(holder.binding.recyclerView.context)
        holder.binding.recyclerView.adapter = adatpter
    }

    override fun getItemCount(): Int {
      return 5
    }


}