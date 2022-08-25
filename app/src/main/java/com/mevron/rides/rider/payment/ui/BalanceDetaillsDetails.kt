package com.mevron.rides.rider.payment.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mevron.rides.rider.R
import com.mevron.rides.rider.payment.domain.PaymentBalanceDetailsDomainDatum

class BalanceDetaillsDetails @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    private var recyclerView: RecyclerView
    private var none: ImageView
    init {
        LayoutInflater.from(context).inflate(R.layout.balance_details_view, this, true)
        recyclerView = findViewById(R.id.recycler_view)
        none = findViewById(R.id.none)
    }
    fun setUpAdapter(adapter: BalanceAdapter, data: List<PaymentBalanceDetailsDomainDatum>){
        none.visibility = GONE
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adapter.submitList(data)
    }
}