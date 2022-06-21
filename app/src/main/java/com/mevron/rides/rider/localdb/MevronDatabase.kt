package com.mevron.rides.rider.localdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedAddress::class, ReferalDetail::class], version = 2)
abstract class MevronDatabase: RoomDatabase() {
    abstract fun addDAO(): MevronDao
}