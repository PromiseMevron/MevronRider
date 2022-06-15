package com.mevron.rides.rider.emerg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Data(
    val id: String,
    val name: String,
    val phoneNumber: String
):Parcelable