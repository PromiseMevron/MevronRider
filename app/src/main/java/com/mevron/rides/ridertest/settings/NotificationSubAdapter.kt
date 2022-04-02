package com.mevron.rides.ridertest.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.NotificationSubItemBinding

class NotificationSubAdapter(val context: Context): RecyclerView.Adapter<NotificationSubAdapter.SubNotifiHolder>() {

    class SubNotifiHolder(val binding: NotificationSubItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubNotifiHolder {
        return SubNotifiHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.notification_sub_item, parent, false))
    }

    override fun onBindViewHolder(holder: SubNotifiHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 3
    }


}