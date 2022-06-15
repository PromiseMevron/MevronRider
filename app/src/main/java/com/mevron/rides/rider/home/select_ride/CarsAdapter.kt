package com.mevron.rides.rider.home.select_ride

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.VehicleItemBinding
import com.mevron.rides.rider.home.select_ride.model.Data
import com.squareup.picasso.Picasso

class CarsAdapter(val cars: List<Data>, val ctx: Context, val posi: Int, val selected: CarSelected): RecyclerView.Adapter<CarsAdapter.CarsHolder>() {
    class CarsHolder(val binding: VehicleItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsHolder {
        return CarsHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.vehicle_item, parent, false))
    }

    override fun onBindViewHolder(holder: CarsHolder, position: Int) {
        val dt = cars[position]
        if (posi != -1){
            if (position == posi){
                holder.binding.background.setBackgroundResource(R.drawable.rounded_border_car)
                holder.binding.vehicleArrival.text = "Drop-off by ${dt.dropOffTime}"
                holder.binding.vehicleArrival.setTextColor(Color.parseColor("#FF9B04"))
            }else{
                holder.binding.vehicleArrival.text = "${dt.dropOffTime}"
            }

        }

        Picasso.get().load(dt.image).into(holder.binding.vehicleImage)
        holder.binding.vehicleName.text = dt.name
        holder.binding.vehicleCount.text = dt.seats.toString()

        holder.binding.vehiclePrice.text = "${dt.currency}${dt.fare}"

        holder.binding.root.setOnClickListener {
            selected.selectedCar(position, dt.name)
        }


    }

    override fun getItemCount(): Int {
       return  cars.size
    }
}

interface CarSelected{
    fun selectedCar(pos: Int, car: String)
}