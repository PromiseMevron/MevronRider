package com.mevron.rides.ridertest.settings.myrides

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.MyScheduleItemBinding

class ScheduleAdapter(): RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder>() {

    class ScheduleHolder(val binding: MyScheduleItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        return ScheduleHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.my_schedule_item, parent, false))

    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
//[ home, work or others ]


    }

    override fun getItemCount(): Int {
        return 7
    }
}



