package com.example.mediaplayer.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MusicApplication : Application() {
    companion object {
        const val CHANNEL_ID = "MusicNotification"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Now Playing Song",
                NotificationManager.IMPORTANCE_LOW
            ).apply { setSound(null, null) }
            notificationChannel.description = "Needed to Show Notification for Playing Song"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}