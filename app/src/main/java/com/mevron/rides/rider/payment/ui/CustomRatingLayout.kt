package com.mevron.rides.rider.payment.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.mevron.rides.rider.R
import com.mevron.rides.rider.util.hideKeyboard

class CustomRatingLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private var closeButton: ImageButton
    private var ratingField: EditText
    private var numberText: TextView
    private var doneButton: ImageButton
    private var customEventListener: CustomRatingEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.add_custom_rating_layout, this, true)
        closeButton = findViewById(R.id.close_add_fund)
        ratingField = findViewById(R.id.add_custom_rating)
        doneButton = findViewById(R.id.add_fund_done)
        numberText = findViewById(R.id.rate_count)
        closeButton.setOnClickListener {
            hideKeyboard()
            customEventListener?.closeCustomButton()
        }
        ratingField.doOnTextChanged { text, _, _, _ ->

            numberText.text = "${text.toString().length}/240"
        }

    }

    fun setEventListener(listener: CustomRatingEventListener) {
        customEventListener = listener
    }

    fun setUp(context: Context) {
        ratingField.setText("")
        doneButton.setOnClickListener {
            if (ratingField.text.toString().isEmpty()){
                return@setOnClickListener
            }
            hideKeyboard()
            customEventListener?.addRating(ratingField.text.toString())
            customEventListener?.ratingDone()

        }

    }

}

interface CustomRatingEventListener {
    fun closeCustomButton()
    fun ratingDone()
    fun addRating(rating: String)
}