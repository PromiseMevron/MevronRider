package com.mevron.rides.rider.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.AddressItemBinding
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.localdb.SavedAddress

class HomeAdapter(val select: AddressSelected, val context: Context) :
    ListAdapter<SavedAddress, HomeAdapter.HomeViewHolder>(
        PlaceAdapterDiffUti()
    ) {

    class HomeViewHolder(val binding: AddressItemBinding) : RecyclerView.ViewHolder(binding.root)

    class PlaceAdapterDiffUti : DiffUtil.ItemCallback<SavedAddress>() {
        override fun areItemsTheSame(
            oldItem: SavedAddress,
            newItem: SavedAddress
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SavedAddress,
            newItem: SavedAddress
        ): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.address_item,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val dt = getItem(position)

        if (dt.type == "home") {
            holder.binding.displayImage.setImageResource(R.drawable.ic_home_address)
            holder.binding.destType.text = context.getString(R.string.home)
        }
        if (dt.type == "work") {
            holder.binding.displayImage.setImageResource(R.drawable.ic_work_address)
            holder.binding.destType.text = context.getString(R.string.work)
        }
        if (dt.type == "others") {
            holder.binding.displayImage.setImageResource(R.drawable.ic_all_saved)
            holder.binding.destType.text = dt.name
        }
        holder.binding.destAddres.text = dt.address

        holder.binding.root.setOnClickListener {
            val data = LocationModel(
                lat = dt.lat.toDouble(),
                lng = dt.lng.toDouble(),
                address = dt.address
            )
            select.selectedAddress(data, dt)
        }
    }
}

interface AddressSelected {
    fun selectedAddress(data: LocationModel, dt: SavedAddress)
}