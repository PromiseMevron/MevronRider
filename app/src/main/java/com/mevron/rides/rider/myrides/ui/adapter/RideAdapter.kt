package com.mevron.rides.rider.myrides.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.MyRideItemBinding
import com.mevron.rides.rider.home.HomeAdapter

class RideAdapter<T>(val sel: SelectedRide): ListAdapter<T, RideAdapter.RideHolder>(RideAdapterDiffUti()) {

    class RideHolder(val binding: MyRideItemBinding): RecyclerView.ViewHolder(binding.root)

    class RideAdapterDiffUti<T>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHolder {
        return RideHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.my_ride_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RideHolder, position: Int) {
//[ home, work or others ]
        holder.binding.root.setOnClickListener {
            sel.select()
        }
    }
}

interface SelectedRide{
    fun select()
}

