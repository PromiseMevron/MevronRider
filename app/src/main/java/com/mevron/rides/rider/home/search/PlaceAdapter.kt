package com.mevron.rides.rider.home.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.AddressItemBinding

class PlaceAdapter(private val itemClicked: OnItemClicked) :
    ListAdapter<AutocompletePrediction, PlaceAdapter.PlaceViewHolder>(
        PlaceAdapterDiffUti()
    ) {
    inner class PlaceViewHolder(val binding: AddressItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class PlaceAdapterDiffUti : DiffUtil.ItemCallback<AutocompletePrediction>() {
        override fun areItemsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.address_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val autoCompletePredictions = getItem(position)
        holder.binding.displayImage.setImageResource(R.drawable.ic_address_image)
        holder.binding.destAddres.text = autoCompletePredictions.getSecondaryText(null).toString()
        holder.binding.destType.text = autoCompletePredictions.getPrimaryText(null).toString()
        holder.binding.root.setOnClickListener {
            itemClicked.returnedPred(autoCompletePredictions)
        }
    }

    interface OnItemClicked {
        fun returnedPred(pred: AutocompletePrediction)
    }
}
