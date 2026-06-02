package com.example.efishapp.feature.notification.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.efishapp.R

class DailyStudyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "EnglishApp"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Time to study!"

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // rung/âm (nếu emulator có)
            .build()

        nm.notify(NOTIFICATION_ID, notification)

        // daily: schedule lại cho ngày mai
        DailyStudyAlarmScheduler.rescheduleFromReceiver(context, intent)
    }

    companion object {
        const val CHANNEL_ID = "daily_study_channel"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
    }
}