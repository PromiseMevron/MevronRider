package com.mevron.rides.ridertest.settings.referal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.ReferalItemBinding
import com.mevron.rides.ridertest.localdb.ReferalDetail

class ReferalAdapter(val sel: SelectedReferal, val data: List<ReferalDetail>): RecyclerView.Adapter<ReferalAdapter.ReferalHolder>() {

    class ReferalHolder(val binding: ReferalItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferalHolder {
        return ReferalHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.referal_item, parent, false))

    }

    override fun onBindViewHolder(holder: ReferalHolder, position: Int) {
//[ home, work or others ]
   //     val dt = data[position]
      /*  holder.binding.referalHeading.text = dt.title
        holder.binding.referalSub.text = dt.description
        holder.binding.promoDate.text = dt.createAt*/
        holder.binding.root.setOnClickListener {
            sel.select("ssssss")
        }

    }

    override fun getItemCount(): Int {
        return 7
    }
}

interface SelectedReferal{
    fun select(id: String)
}

