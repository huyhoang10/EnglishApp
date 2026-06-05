package com.example.efishapp.feature.notification.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object DailyStudyAlarmScheduler {

    private const val REQ_CODE = 9001
    private const val ACTION_DAILY_STUDY = "com.example.efishapp.ACTION_DAILY_STUDY"

    private const val EXTRA_HOUR = "extra_hour"
    private const val EXTRA_MINUTE = "extra_minute"

    fun scheduleDaily(
        context: Context,
        hour: Int,
        minute: Int,
        title: String,
        message: String
    ) {
        cancel(context)
        NotificationChannels.ensureDailyStudyChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, DailyStudyReminderReceiver::class.java).apply {

            action = ACTION_DAILY_STUDY
            putExtra(DailyStudyReminderReceiver.EXTRA_TITLE, title)
            putExtra(DailyStudyReminderReceiver.EXTRA_MESSAGE, message)
            putExtra(EXTRA_HOUR, hour)
            putExtra(EXTRA_MINUTE, minute)
        }

        val pi = PendingIntent.getBroadcast(
            context,
            REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = computeNextTriggerMillis(hour, minute)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pi
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyStudyReminderReceiver::class.java).apply {
            action = ACTION_DAILY_STUDY
        }
        val pi = PendingIntent.getBroadcast(
            context,
            REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi)
    }

    internal fun rescheduleFromReceiver(context: Context, intent: Intent) {
        val title = intent.getStringExtra(DailyStudyReminderReceiver.EXTRA_TITLE) ?: "EnglishApp"
        val message = intent.getStringExtra(DailyStudyReminderReceiver.EXTRA_MESSAGE) ?: "Time to study!"

        val hour = intent.getIntExtra(EXTRA_HOUR, 20)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)

        scheduleDaily(context, hour, minute, title, message)
    }

    private fun computeNextTriggerMillis(hour: Int, minute: Int): Long {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now) add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }
}