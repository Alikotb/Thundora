package com.example.thundora.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.thundora.R

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val label = inputData.getString("label") ?: "Alarm"
        val id = inputData.getInt("id", 0)
        val isEndNotification = inputData.getBoolean("isEndNotification", false)

        sendSilentNotification(id, label, isEndNotification)
        return Result.success()
    }

    private fun sendSilentNotification(id: Int, label: String, isEndNotification: Boolean) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "silent_alarm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Silent Alarms",
                NotificationManager.IMPORTANCE_LOW // No sound
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationText = if (isEndNotification) "Your alarm '$label' has ended." else "Your alarm '$label' is active."

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle("Alarm Reminder")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSilent(true)
            .build()

        notificationManager.notify(id, notification)
    }
}
