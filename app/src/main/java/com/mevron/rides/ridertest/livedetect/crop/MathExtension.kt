package com.mevron.rides.ridertest.livedetect.crop

import kotlin.math.roundToInt


internal fun Double.roundDouble(): Double {
    return (this * 1000.0).roundToInt() / 1000.0
}

internal fun Float.roundFloat(): Float {
    return (this * 1000.0).roundToInt() / 1000.0f
}