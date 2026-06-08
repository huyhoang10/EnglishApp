package com.example.efishapp.feature.notification.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.efishapp.MainActivity
import com.example.efishapp.R

class ReviewReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val count = intent.getIntExtra(EXTRA_COUNT, 0)
        if (count <= 0) return

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationChannels.ensureDailyStudyChannel(context)

        val activityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, DailyStudyReminderReceiver.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Ôn tập từ vựng")
            .setContentText("Bạn có $count từ cần ôn tập hôm nay!")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        nm.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 1002
        const val EXTRA_COUNT = "extra_count"
    }
}
