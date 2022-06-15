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
        val token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTAsImVtYWlsIjoiIiwibmFtZSI6IiIsInV1aWQiOiI5ODI4MDM0MC05MDk0LTRkMjktOTFiOC05M2Q1MWNhZTkzYzAiLCJwaG9uZU51bWJlciI6IjIzNDcwMzM1MDUwMTMiLCJ0eXBlIjoicmlkZXIiLCJpYXQiOjE2NDc2MDE4NDd9.thST-kg2lNieEF8LkbeTvp-U6kS_jhQCRJ9_XaEeF9U"

        val editor = sPref.edit()
        editor.putString(Constants.TOKEN, token1)
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