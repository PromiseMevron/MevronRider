package com.mevron.rides.ridertest.localdb

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "referral_detail")
data class ReferalDetail(
    val category: String,
    val createAt: String,
    val description: String,
    val id: String,
    val title: String,

    @PrimaryKey(autoGenerate = true)
val dbId: Int? = null
): Parcelable
