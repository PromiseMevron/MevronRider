package com.mevron.rides.rider

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mevron.rides.rider.home.data.DeviceID
import com.mevron.rides.rider.home.data.ProfileApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MevronFirebaseInstanceIDService: FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenUseCase: ProfileApi

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        fetchAndUpdateToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val isRequest = data["request"] == "1"
        val soundUrl = if (isRequest) "android.resource://com.mevron.rides.rider/raw/ringtone2" else "android.resource://com.mevron.rides.rider/raw/ringtone1"

        val audioAttributesDefault = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        val title = message.notification?.title
        val text = message.notification?.body
        val intent =
            Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            application,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "Default"
        val builder =
            NotificationCompat.Builder(application, channelId)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(
                    Uri.parse(
                        soundUrl
                    )
                )
                .setContentIntent(pendingIntent)
        val manager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "Default channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#FF9B04")
            notificationChannel.enableVibration(true)
            notificationChannel.setSound(Uri.parse(soundUrl),
                audioAttributesDefault)
           // notificationChannel.vibrationPattern =
              //  longArrayOf(0, 100, 1000, 300, 200, 100, 500, 200, 100)
            manager.createNotificationChannel(notificationChannel)
            //ringtone2
        }
        manager.notify(0, builder.build())

    }

    private fun fetchAndUpdateToken(token: String) {
        try {
            fcmTokenUseCase.updateToken(DeviceID(device_id = token)).execute()
        }catch (e: Throwable){
            e.printStackTrace()
        }
    }
}