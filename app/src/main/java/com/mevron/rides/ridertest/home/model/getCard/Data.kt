package com.mevron.rides.ridertest.home.model.getCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Data(
    val bin: String,
    val brand: String,
    val expiryMonth: String,
    val expiryYear: String,
    val lastDigits: String,
    val type: String,
    val uuid: String
): Parcelable