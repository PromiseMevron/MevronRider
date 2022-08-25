package com.mevron.rides.rider.payment.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.mevron.rides.rider.R
import com.mevron.rides.rider.home.booked.BookedFragment
import com.mevron.rides.rider.util.hideKeyboard

class CashOutAddFundLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private var closeButton: ImageButton
    private var addButton: ImageButton
    private var minusButton: ImageButton
    private var amountField: EditText
    private var skipButton: Button
    private var doneButton: ImageButton
    private var titleText: TextView
    private var cashOutAddFundEventListener: CashOutAddFundEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.add_fund_cash_out_layout, this, true)
        closeButton = findViewById(R.id.close_add_fund)
        addButton = findViewById(R.id.add_fund_plus)
        minusButton = findViewById(R.id.add_fund_minus)
        amountField = findViewById(R.id.add_fund_amount)
        skipButton = findViewById(R.id.skip)
        doneButton = findViewById(R.id.add_fund_done)
        titleText = findViewById(R.id.title)
        closeButton.setOnClickListener {
            hideKeyboard()
            cashOutAddFundEventListener?.closeButton()
        }
        skipButton.setOnClickListener {
            hideKeyboard()
            cashOutAddFundEventListener?.skipAction()
        }

    }

    fun setEventListener(listener: CashOutAddFundEventListener) {
        cashOutAddFundEventListener = listener
    }

    fun setUpAddFund(context: Context) {
        titleText.text = context.getString(R.string.enter_amount)
        skipButton.visibility = GONE
        doneButton.setOnClickListener {
            hideKeyboard()
            cashOutAddFundEventListener?.addFundDone()
        }
        amountField.doOnTextChanged { text, _, _, _ ->
            if (text?.isEmpty() == true){
                amountField.setText("0.0")
            }
            cashOutAddFundEventListener?.addFundAmount(text.toString())
        }
        amountField.setText("5.00")
        addButton.setOnClickListener {
            if (amountField.text.isNotEmpty()) {
                val amount = (amountField.text.toString()).toDouble() + 5.00
                amountField.setText("$amount")
            }
        }

        minusButton.setOnClickListener {
            if (amountField.text.isNotEmpty()){
                val amount = (amountField.text.toString()).toDouble() - 5.00
                if (amount > 0)
                    amountField.setText("$amount")
            }
        }
    }

    fun setUpCashOut(context: Context, balance: String) {
        val balanceAmount = (balance.toDouble() / 2)
        titleText.text = context.getString(R.string.add_custom_cash_out)
        skipButton.visibility = VISIBLE
        doneButton.setOnClickListener {
            hideKeyboard()
            cashOutAddFundEventListener?.cashOutDone()
        }
        amountField.doOnTextChanged { text, _, _, _ ->
            if (text?.isEmpty() == true){
                amountField.setText("0.0")
            }
            cashOutAddFundEventListener?.cashOutAmount(text.toString())
        }
        amountField.setText("$balanceAmount")

        addButton.setOnClickListener {
            if (amountField.text.isNotEmpty()){
                val amount = (amountField.text.toString()).toDouble() + 5.00
                if (amount < balance.toDouble())
                    amountField.setText("$amount")
                else
                    amountField.setText(balance)
            }
        }

        minusButton.setOnClickListener {
            if (amountField.text.isNotEmpty()){
                val amount = (amountField.text.toString()).toDouble() - 5.00
                if (amount > 0)
                    amountField.setText("$amount")
            }

        }
    }
}

interface CashOutAddFundEventListener {
    fun closeButton()
    fun skipAction()
    fun addFundDone()
    fun cashOutDone()
    fun cashOutAmount(amount: String)
    fun addFundAmount(amount: String)
}