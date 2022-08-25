package com.mevron.rides.rider.localdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MevronDao {

   /* @Insert
    suspend fun insert(add: SavedAddress)

    @Update
    suspend fun update(add: SavedAddress)


  /*  @Query("DELETE FROM saved_address")
    suspend fun deleteAllAddress(): Unit*/

    @Query("SELECT * FROM saved_address ORDER BY id DESC")
    fun getAllAddress(): LiveData<MutableList<SavedAddress>>


    @Query("SELECT * FROM referral_detail ORDER BY dbId DESC")
    fun getAllReferal(): LiveData<MutableList<ReferalDetail>>

   // @Query("DELETE FROM referral_detail")
 //   suspend fun deleteAllReferral()

    @Insert
    suspend fun insertRefer(add: ReferalDetail)*/
}