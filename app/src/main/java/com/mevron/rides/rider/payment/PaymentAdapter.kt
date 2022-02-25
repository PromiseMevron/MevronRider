package com.mevron.rides.rider.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PayTypeItemBinding

class PaymentAdapter(val paySelected: PaySelected): RecyclerView.Adapter<PaymentAdapter.PayHolder>() {


    class PayHolder(val binding: PayTypeItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayHolder {
        return PayHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pay_type_item, parent, false))

    }

    override fun onBindViewHolder(holder: PayHolder, position: Int) {
        holder.binding.next.visibility = View.INVISIBLE
        if (position == 0){
            holder.binding.image.setImageResource(R.drawable.ic_add_type)
            holder.binding.typeName.text = "Add a Payment Method"
            holder.binding.root.setOnClickListener {
                paySelected.selected()
            }
        }else{
            holder.binding.image.setImageResource(R.drawable.ic_cash_add)
            holder.binding.typeName.text = "Cash"
        }
    }

    override fun getItemCount(): Int {
       return  2
    }
}

interface PaySelected{
    fun selected()
}