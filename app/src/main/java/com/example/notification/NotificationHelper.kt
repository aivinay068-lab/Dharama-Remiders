package com.example.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.model.FestivalEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object NotificationHelper {
    const val CHANNEL_ID = "utsav_reminder_channel"
    private const val CHANNEL_NAME = "Sacred Days & Vrat Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifies of upcoming religious fasts, tithis, and festival guidelines"
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun triggerImmediateNotification(
        context: Context,
        festivalName: String,
        tithi: String,
        deity: String,
        ritualsSummary: String,
        isFasting: Boolean
    ) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("FESTIVAL_NAME", festivalName)
            putExtra("TITHI", tithi)
            putExtra("DEITY", deity)
            putExtra("RITUALS_SUMMARY", ritualsSummary)
            putExtra("IS_FASTING", isFasting)
        }
        context.sendBroadcast(intent)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, festival: FestivalEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Parse the date (format: YYYY-MM-DD)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = try {
            sdf.parse(festival.date)
        } catch (e: Exception) {
            null
        } ?: return

        val calendar = Calendar.getInstance().apply {
            time = date
            // Set alert for 8:00 AM on that specific festival day
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Avoid scheduling alarms in the past
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            Log.d("NotificationHelper", "Skipping schedule: ${festival.name} date is in the past.")
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("FESTIVAL_NAME", festival.name)
            putExtra("TITHI", festival.tithi)
            putExtra("DEITY", festival.deity)
            putExtra("RITUALS_SUMMARY", festival.whatToDo)
            putExtra("IS_FASTING", festival.isFastingDay)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            festival.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("NotificationHelper", "Scheduled reminder alarm for ${festival.name} at: ${calendar.time}")
        } catch (e: SecurityException) {
            // Safe fallback if permission is missing
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("NotificationHelper", "SecurityException during schedule, falling back: ${e.message}")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Failed to schedule reminder alarm for ${festival.name}: ${e.message}")
        }
    }

    fun cancelNotification(context: Context, festivalId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            festivalId,
            intent,
            PendingIntent.FLAG_NO_CREATE or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
