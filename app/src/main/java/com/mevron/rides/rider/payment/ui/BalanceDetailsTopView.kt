package com.mevron.rides.rider.payment.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mevron.rides.rider.R

class BalanceDetailsTopView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr), View.OnClickListener {
    private var balance: TextView
    private var dueDate: TextView
    private var addFund: ImageButton
    private var backButton: ImageButton
    private var onBalanceDetailButtonClickListener: OnBalanceDetailButtonClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.balance_detail_top_layout, this, true)
        balance = findViewById(R.id.balance)
        dueDate = findViewById(R.id.due_date)
        addFund = findViewById(R.id.add_fund)
        backButton = findViewById(R.id.back_button)
        addFund.setOnClickListener(this)
        backButton.setOnClickListener(this)
    }

    fun setUpView(amount: String){
        balance.text = amount
    }
    fun setEventsClickListener(listener: OnBalanceDetailButtonClickListener) {
        onBalanceDetailButtonClickListener = listener
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.add_fund -> onBalanceDetailButtonClickListener?.onDetailOutClicked()
            R.id.back_button -> onBalanceDetailButtonClickListener?.backButtonClicked()
        }
    }
}


interface OnBalanceDetailButtonClickListener {
    fun onDetailOutClicked()
    fun backButtonClicked()
}
