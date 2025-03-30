package com.example.thundora.model.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.thundora.MainActivity
import com.example.thundora.R
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "android.intent.action.ALARM_TRIGGERED") {
            val id = intent.getIntExtra("id", -1)
            removeAlarm(context,id)

            val alarmLabel = intent.getStringExtra("ALARM_LABEL") ?: "Thundora Alarm!"
            val duration = intent.getIntExtra("ALARM_DURATION", 5) // Default 30 seconds
            showCustomNotification(context, alarmLabel, duration)
        } else if (intent?.action == "android.intent.action.CANCEL_NOTIFICATION") {
            val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.cancel(notificationId)

        }
    }

    private fun removeAlarm(context: Context, id: Int) {
        val repo =
            Repository.getInstance(
                RemoteDataSource(ApiClient.weatherService),
                LocalDataSource(
                    WeatherDataBase.getInstance(context).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        GlobalScope.launch(Dispatchers.IO) {
            Log.i("TAG", "removeAlarm: ${id}")
            repo.deleteAlarmById(id)
        }

    }

    private fun showCustomNotification(context: Context, alarmLabel: String, duration: Int) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Alarms", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm Notifications"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "android.intent.action.CANCEL_NOTIFICATION"
            putExtra("NOTIFICATION_ID", 1)
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle(alarmLabel)
            .setContentText("Your alarm is ringing!")
            .setColor(ContextCompat.getColor(context, R.color.blue_400))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.i_01d, "Dismiss", cancelPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Wake up! It's time!"))
            .build()
        notificationManager.notify(1, notification)
        Handler(Looper.getMainLooper()).postDelayed({
            notificationManager.cancel(1)
        }, duration * 1000L)
    }
}