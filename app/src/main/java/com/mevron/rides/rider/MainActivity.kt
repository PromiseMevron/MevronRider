package com.mevron.rides.rider


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mevron.rides.rider.home.HomeActivity
import com.mevron.rides.rider.util.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)

        val editor = sPref.edit()
        editor.putString(Constants.TOKEN, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NywiZW1haWwiOiIiLCJuYW1lIjoiIiwidXVpZCI6ImZjYWI2NTAzLTNkZDAtNDNlNC1hZDRjLTc5MzMxYTVhOWNhMSIsInBob25lTnVtYmVyIjoiMjM0NzAzMzUwNTAwNCIsInR5cGUiOiJyaWRlciIsImlhdCI6MTY0NjM1NzAxMH0.tLvawk9BZUI5uBk3c_xx1zfy9-4LX8kxiVNNAs8VuRI")
        editor.putString(Constants.UUID, "fcab6503-3dd0-43e4-ad4c-79331a5a9ca1")
        editor.apply()

        val token = sPref.getString(Constants.TOKEN, null)
        val uuid = sPref.getString(Constants.UUID, null)
        if (token.isNullOrEmpty() || uuid.isNullOrEmpty()){
            startActivity(Intent(this, IntroScreenActivity::class.java))
        }else{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        finish()
    }
}