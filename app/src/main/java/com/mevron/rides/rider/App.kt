package com.mevron.rides.rider

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App: Application() {

    companion object {
        lateinit var ApplicationContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        ApplicationContext = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val options = FirebaseOptions.Builder()
            .setApplicationId("1:210013538605:android:570d8f4733e69b182b6ba4") // Required for Analytics.
            .setProjectId("mevron-1330b") // Required for Firebase Installations.
            .setApiKey("AIzaSyBJIQiM4tcpqcmnN4II6UHep9OJ025zsA8") // Required for Auth.
            .build()
        FirebaseApp.initializeApp(this, options, "Mevron Rider")
    }
}