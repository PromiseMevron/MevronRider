package com.mevron.rides.rider.sharedprefrence.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferenceRepositoryTest{
    private lateinit var prefsRepo: PreferenceRepository
    private lateinit var context: Context


    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        prefsRepo = PreferenceRepository(context)
    }

    @Test
    fun test_when_data_saved_it_returns_same_data(){
        prefsRepo.setStringForKey("abc", "bcd")
        val result = prefsRepo.getStringForKey("abc")
        assert(result == "bcd")
    }

    @After
    fun tearDown() {
        prefsRepo.clear()
    }

    @Test
    fun test_when_data_was_not_saved_it_returns_empty_string(){
        val result = prefsRepo.getStringForKey("wed")
        assert(result == "")
    }
}