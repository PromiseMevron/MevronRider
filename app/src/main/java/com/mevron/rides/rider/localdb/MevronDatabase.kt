package com.mevron.rides.rider.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedAddress::class], version = 1)
abstract class MevronDatabase: RoomDatabase() {
    abstract fun addDAO(): MevronDao

}