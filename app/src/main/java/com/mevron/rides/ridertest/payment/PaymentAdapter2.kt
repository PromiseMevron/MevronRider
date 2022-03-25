package com.mevron.rides.ridertest.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.PayTypeItemBinding
import com.mevron.rides.ridertest.home.model.getCard.Data

class PaymentAdapter2(val paySelected: PaySelected2, val data: List<Data>): RecyclerView.Adapter<PaymentAdapter2.PayHolder>() {


    class PayHolder(val binding: PayTypeItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayHolder {
        return PayHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pay_type_item, parent, false))

    }

    override fun onBindViewHolder(holder: PayHolder, position: Int) {

        if (position == 0){
            holder.binding.image.setImageResource(R.drawable.ic_cash_add)
            holder.binding.typeName.text = "Cash"
            holder.binding.next.visibility = View.INVISIBLE

        }else{
            val dt = data[position - 1]
            holder.binding.image.setImageResource(R.drawable.master_card_logo_svg)
            holder.binding.typeName.text = "****" + dt.lastDigits
            holder.binding.next.visibility = View.VISIBLE
            holder.binding.root.setOnClickListener {
                paySelected.selected(dt)
            }
        }
    }

    override fun getItemCount(): Int {
        return  data.size + 1
    }
}

interface PaySelected2{
    fun selected(data: Data)
}