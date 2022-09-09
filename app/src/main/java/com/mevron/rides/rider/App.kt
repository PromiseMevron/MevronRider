package com.mevron.rides.rider

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp
import zendesk.android.Zendesk
import zendesk.messaging.android.DefaultMessagingFactory


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

        Zendesk.initialize(
            context = this,
            channelKey = "eyJzZXR0aW5nc191cmwiOiJodHRwczovL21ldnJvbjgyNTcuemVuZGVzay5jb20vbW9iaWxlX3Nka19hcGkvc2V0dGluZ3MvMDFGV1MxSEg5S1hEUTBQVzRSWURONloyQk0uanNvbiJ9",
            successCallback = {
                Log.i("IntegrationApplication", "Initialization successful")
            },
            failureCallback = { error ->
                // Tracking the cause of exceptions in your crash reporting dashboard will help to triage any unexpected failures in production
                Log.e("IntegrationApplication", "Initialization failed", error)
            },
            messagingFactory = DefaultMessagingFactory()
        )
    }
}