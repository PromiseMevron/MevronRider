package com.mevron.rides.rider.util

import android.content.res.Resources

object Screen {
    val width: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val height: Int
        get() = Resources.getSystem().displayMetrics.heightPixels
}