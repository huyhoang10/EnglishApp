package com.example.efishapp.feature.notification.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {

    fun ensureDailyStudyChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            DailyStudyReminderReceiver.CHANNEL_ID,
            "Daily study reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you to study English every day"
        }

        nm.createNotificationChannel(channel)
    }
}