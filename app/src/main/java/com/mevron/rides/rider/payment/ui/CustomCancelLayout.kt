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
import com.mevron.rides.rider.util.hideKeyboard

class CustomCancelLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private var closeButton: ImageButton
    private var ratingField: EditText
    private var numberText: TextView
    private var headingText: TextView
    private var doneButton: ImageButton
    private var backGround: Button
    private var customEventListener: CustomCancelEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.add_custom_cancel_layout, this, true)
        closeButton = findViewById(R.id.close_add_fund)
        ratingField = findViewById(R.id.add_custom_rating)
        doneButton = findViewById(R.id.add_fund_done)
        numberText = findViewById(R.id.rate_count)
        headingText = findViewById(R.id.title)
        backGround = findViewById(R.id.touch_background)
        closeButton.setOnClickListener {
            hideKeyboard()
            customEventListener?.closeCustomCancelButton()
        }
        backGround.setOnClickListener {
            hideKeyboard()
            customEventListener?.closeCustomCancelButton()
        }
        ratingField.doOnTextChanged { text, _, _, _ ->

            numberText.text = "${text.toString().length}/240"
        }

    }

    fun setEventListener(listener: CustomCancelEventListener) {
        customEventListener = listener
    }

    fun setUp(context: Context, heading: String? = null) {
        ratingField.setText("")
        doneButton.setOnClickListener {
            if (ratingField.text.toString().isEmpty()){
                return@setOnClickListener
            }
            hideKeyboard()
            customEventListener?.addRCancelRating(ratingField.text.toString())
            customEventListener?.ratingCancelDone()

        }
        heading?.let {
            headingText.text = it
        }

    }

}

interface CustomCancelEventListener {
    fun closeCustomCancelButton()
    fun ratingCancelDone()
    fun addRCancelRating(rating: String)
}