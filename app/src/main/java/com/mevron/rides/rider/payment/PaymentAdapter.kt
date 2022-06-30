package com.mevron.rides.rider.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PayTypeItemBinding
import com.mevron.rides.rider.home.model.getCard.Data

class PaymentAdapter(val paySelected: PaySelected, val data: List<Data>,  val posi: Int): RecyclerView.Adapter<PaymentAdapter.PayHolder>() {


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
        } else if (position == 1){
                holder.binding.image.setImageResource(R.drawable.ic_cash_add)
                holder.binding.typeName.text = "Cash"
            holder.binding.root.setOnClickListener {
                paySelected.selectedMethod(false, method = "cash", pos = position)
            }

        }else{
            holder.binding.image.setImageResource(R.drawable.master_card_logo_svg)
            val dt = data[position - 2]
            holder.binding.typeName.text = "****" + dt.lastDigits
            holder.binding.root.setOnClickListener {
                paySelected.selectedMethod(true, dt.uuid, "card", position)
            }
        }
        if (posi != -1){
            if (position == posi){
                holder.binding.rootView.setBackgroundResource(R.drawable.rounded_border_car)
            }
        }
    }

    override fun getItemCount(): Int {
       return (data.size + 2)
    }
}

interface PaySelected{
    fun selected(pos: Int = -1)
    fun selectedMethod(isCard: Boolean, uiid: String = "", method: String, pos: Int)
}