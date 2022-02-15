package com.mevron.rides.rider


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mevron.rides.rider.home.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}