package com.example.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val festivalName = intent.getStringExtra("FESTIVAL_NAME") ?: "Spiritual Day Alert"
        val deity = intent.getStringExtra("DEITY") ?: "None"
        val tithi = intent.getStringExtra("TITHI") ?: "Special Tithi"
        val ritualsSummary = intent.getStringExtra("RITUALS_SUMMARY") ?: "Check out what to do on this day."
        val isFasting = intent.getBooleanExtra("IS_FASTING", false)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Ensure channels are built
        NotificationHelper.createNotificationChannel(context)

        // Intent to launch MainActivity when clicking the notification
        val resultIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val statusText = if (isFasting) "🕉️ Active Fasting (Vrat)" else "🚩 Special Festive Observance"
        val contentTitle = "Vrat & Festival: $festivalName"
        val contentText = "$tithi • dedicated to $deity\n$ritualsSummary"

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Using a solid standard system alarm icon for high visibility
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSubText(statusText)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$contentText\n\nPrasado/Rituals: Fast with sincerity and read prayers dedicated to $deity."))
            .build()

        notificationManager.notify(festivalName.hashCode(), notification)
    }
}
