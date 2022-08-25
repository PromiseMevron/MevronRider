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
import com.mevron.rides.rider.myrides.domain.model.AllTripsResult
import com.mevron.rides.rider.util.toReadableDate

class RideAdapter(val sel: SelectedRide): ListAdapter<AllTripsResult, RideAdapter.RideHolder>(RideAdapterDiffUti()) {

    class RideHolder(val binding: MyRideItemBinding): RecyclerView.ViewHolder(binding.root)

    class RideAdapterDiffUti: DiffUtil.ItemCallback<AllTripsResult>() {
        override fun areItemsTheSame(oldItem: AllTripsResult, newItem: AllTripsResult): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AllTripsResult, newItem: AllTripsResult): Boolean {
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
        val data = getItem(position)
        holder.binding.dateDisplay.text = data.date.toReadableDate()
        holder.binding.amountDisplay.text = data.amount
        holder.binding.carDisplay.text = "${data.vehicleName} . ${data.vehicleNum}"
        holder.binding.root.setOnClickListener {
            sel.select(data.id)
        }
    }
}

interface SelectedRide{
    fun select(id: String)
}

