package com.mevron.rides.rider.sharedprefrence.data

import android.content.Context
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.util.Constants

class PreferenceRepository( context: Context) : IPreferenceRepository {

    private val sPref= context.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)
    private val editor = sPref.edit()

    override fun getStringForKey(key: String): String = sPref.getString(key, "") ?: ""

    override fun setStringForKey(key: String, value: String) {
        editor.putString(key, value)
        editor?.apply()
    }

    override fun clear() {
        editor.clear()
    }
}