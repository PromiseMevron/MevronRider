package com.mevron.rides.rider.payment.tipping

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mevron.rides.rider.R

class AddCustomTipDialogView(
    context: Context,
    private val decreaseTipClicked: () -> Unit,
    private val increaseTipClicked: () -> Unit,
    private val doneClicked: () -> Unit
) : Dialog(context, R.style.FullScreenDialog), View.OnClickListener {

    private lateinit var doneButton: Button
    private lateinit var tipValueTextView: TextView
    private lateinit var reduceTipImage: ImageView
    private lateinit var increaseTipImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_add_custom_tip_dialog)
        doneButton = findViewById(R.id.done_button_custom_tip)
        tipValueTextView = findViewById(R.id.custom_tip_value)
        reduceTipImage = findViewById(R.id.reduce_tip)
        increaseTipImage = findViewById(R.id.increase_tip)

        doneButton.setOnClickListener(this)
        reduceTipImage.setOnClickListener(this)
        increaseTipImage.setOnClickListener(this)
        setTipValue(0, "")
    }

    fun setTipValue(currentTip: Int, currency: String) {
        if (currentTip == 0) {
            tipValueTextView.setTextColor(ContextCompat.getColor(context, R.color.gray_color))
            reduceTipImage.setImageResource(R.drawable.ic_disabled_minus_icon)
        } else {
            tipValueTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
            reduceTipImage.setImageResource(R.drawable.ic_minus_icon)
        }
        tipValueTextView.text = "$currency$currentTip"
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.done_button_custom_tip -> {
                doneClicked()
                dismiss()
            }
            R.id.reduce_tip -> decreaseTipClicked()
            R.id.increase_tip -> increaseTipClicked()
        }
    }
}