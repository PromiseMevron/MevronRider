package com.mevron.rides.rider.myrides.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.MyScheduleItemBinding

class ScheduleAdapte<T>(): ListAdapter<T, ScheduleAdapte.ScheduleHolder>(RideAdapter.RideAdapterDiffUti()) {

    class ScheduleHolder(val binding: MyScheduleItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        return ScheduleHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.my_schedule_item, parent, false))
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {

    }
}



