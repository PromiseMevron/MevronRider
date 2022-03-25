package com.mevron.rides.ridertest.settings.myrides

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.MyRideItemBinding

class RideAdapter(val sel: SelectedRide): RecyclerView.Adapter<RideAdapter.RideHolder>() {

    class RideHolder(val binding: MyRideItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHolder {
        return RideHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.my_ride_item, parent, false))

    }

    override fun onBindViewHolder(holder: RideHolder, position: Int) {
//[ home, work or others ]
        holder.binding.root.setOnClickListener {
            sel.select()
        }

    }

    override fun getItemCount(): Int {
        return 7
    }
}

interface SelectedRide{
    fun select()
}

