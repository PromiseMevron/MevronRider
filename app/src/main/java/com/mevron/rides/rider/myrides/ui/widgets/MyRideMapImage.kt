package com.mevron.rides.rider.myrides.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mevron.rides.rider.R

class MyRideMapImage @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    private var mapImageView: ImageView
    init {
        LayoutInflater.from(context).inflate(R.layout.my_ride_map_image, this, true)
        mapImageView = findViewById(R.id.map_image)
    }
}