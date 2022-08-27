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
import com.squareup.picasso.Picasso

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
        if (data.driverProfile.isNotEmpty()){
            Picasso.get().load(data.driverProfile).error(R.drawable.profile).placeholder(R.drawable.profile).into(holder.binding.profileImage)
        }
        val mapImage = "https://maps.googleapis.com/maps/api/staticmap?size=300x300&path=color:0xFF9B04|weight:3|${data.pickupLatitude},${data.pickupLongitude}|${data.destinationLatitude},${data.destinationLongitude}&markers=icon:https://firebasestorage.googleapis.com/v0/b/mevron-1330b.appspot.com/o/MapMarkerImage%2FEllipse%203%20(1).png?alt=media&token=43a50b97-b6a1-475d-bacc-fd30a2d22446|${data.pickupLatitude},${data.pickupLongitude}&markers=icon:https://firebasestorage.googleapis.com/v0/b/mevron-1330b.appspot.com/o/MapMarkerImage%2FEllipse%203.png?alt=media&token=65f90d1d-0e93-4636-acbf-9ab01e006e4f|${data.destinationLatitude},${data.destinationLongitude}&sensor=false&key=AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE"
        Picasso.get().load(mapImage).error(R.drawable.street_map).placeholder(R.drawable.street_map).into(holder.binding.streetMap)
        holder.binding.root.setOnClickListener {
            sel.select(data.id)
        }
    }
}

interface SelectedRide{
    fun select(id: String)
}

