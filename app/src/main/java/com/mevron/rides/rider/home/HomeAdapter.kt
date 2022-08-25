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
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData
import com.mevron.rides.rider.util.Constants

class HomeAdapter(
    val select: OnAddressSelectedListener,
    val context: Context
) : ListAdapter<GetSavedAddressData, HomeAdapter.HomeViewHolder>(PlaceAdapterDiffUti()) {

    class HomeViewHolder(val binding: AddressItemBinding) : RecyclerView.ViewHolder(binding.root)

    class PlaceAdapterDiffUti : DiffUtil.ItemCallback<GetSavedAddressData>() {
        override fun areItemsTheSame(
            oldItem: GetSavedAddressData,
            newItem: GetSavedAddressData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: GetSavedAddressData,
            newItem: GetSavedAddressData
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

        if (dt.type == Constants.HOME) {
            holder.binding.displayImage.setImageResource(R.drawable.ic_home_address)
            holder.binding.destType.text = context.getString(R.string.home)
        }
        if (dt.type == Constants.WORK) {
            holder.binding.displayImage.setImageResource(R.drawable.ic_work_address)
            holder.binding.destType.text = context.getString(R.string.work)
        }
        if (dt.type == Constants.OTHER) {
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
            select.onAddressSelected(data, dt)
        }
    }
}

interface OnAddressSelectedListener {
    fun onAddressSelected(locationModel: LocationModel, savedAddress: GetSavedAddressData)
}