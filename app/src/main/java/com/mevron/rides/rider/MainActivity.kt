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