package com.example.thundora.model.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.thundora.MainActivity
import com.example.thundora.R
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private var mediaPlayer: MediaPlayer? = null
        fun stopAlarmSound() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val id = intent?.getIntExtra("id", -1) ?: return

        when (intent.action) {
            "android.intent.action.ALARM_TRIGGERED" -> {
                removeAlarm(context, id)

                val alarmLabel = intent.getStringExtra("ALARM_LABEL") ?: "Thundora Alarm!"
                val duration = intent.getIntExtra("ALARM_DURATION", 5)

                playAlarmSound(context)
                showCustomNotification(context, id, alarmLabel, duration)
            }

            "android.intent.action.STOP_ALARM" -> {
                stopAlarmSound()
            }

            "android.intent.action.CANCEL_NOTIFICATION" -> {
                val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.cancel(notificationId)

                stopAlarmSound()
            }
        }
    }

    private fun removeAlarm(context: Context, id: Int) {
        val repo = Repository.getInstance(
            RemoteDataSource(ApiClient.weatherService),
            LocalDataSource(
                WeatherDataBase.getInstance(context).getForecastDao(),
                SharedPreference.getInstance()
            )
        )
        CoroutineScope(Dispatchers.IO).launch {
            repo.deleteAlarmById(id)
        }
    }

    private fun showCustomNotification(context: Context, alarmId: Int, alarmLabel: String, duration: Int) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Alarms", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm Notifications"
                enableLights(true)
                enableVibration(true)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "android.intent.action.CANCEL_NOTIFICATION"
            putExtra("NOTIFICATION_ID", alarmId)
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, alarmId, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle(alarmLabel)
            .setContentText("Your alarm is ringing!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainPendingIntent)
            .setAutoCancel(false)
            .setDeleteIntent(cancelPendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.i_01d, "Dismiss", cancelPendingIntent)
            .build()

        notificationManager.notify(alarmId, notification)

        Handler(Looper.getMainLooper()).postDelayed({
            notificationManager.cancel(alarmId)
            stopAlarmSound()
        }, duration * 1000L)
    }

    private fun playAlarmSound(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.notification)
            mediaPlayer?.isLooping = true
        }
        mediaPlayer?.start()
    }
}

