package com.mevron.rides.rider.home.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.AddressItemBinding

class PlaceAdapter(val context: Context, private var autoCompletePredictions: List<AutocompletePrediction>, private val itemClicked: OnItemClicked): RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {
   // private var autoCompletePredictions: List<AutocompletePrediction> = ArrayList()
    inner class PlaceViewHolder(val binding: AddressItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.address_item, parent, false))
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
      //  Toast.makeText(context, "2121", Toast.LENGTH_LONG).show()
        holder.binding.displayImage.setImageResource(R.drawable.ic_address_image)
        holder.binding.destAddres.text = autoCompletePredictions[position].getSecondaryText(null).toString()
        holder.binding.destType.text = autoCompletePredictions[position].getPrimaryText(null).toString()
        holder.binding.root.setOnClickListener {
            itemClicked.returnedPred(autoCompletePredictions[position])
          //  autoCompletePredictions = ArrayList<AutocompletePrediction>()
           // notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return autoCompletePredictions.size
    }

    fun setData(){
        autoCompletePredictions = ArrayList<AutocompletePrediction>()

       notifyDataSetChanged()
    }

    interface OnItemClicked{
        fun returnedPred(pred: AutocompletePrediction)
    }
}
