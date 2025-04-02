package com.example.thundora.services

import android.annotation.SuppressLint
import android.app.AlarmManager
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.thundora.MainActivity
import com.example.thundora.R
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.domain.model.view.SharedKeys
import com.example.thundora.data.remote.api.ApiClient
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.utils.getWeatherNotification
import com.example.thundora.utils.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime


class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private var mediaPlayer: MediaPlayer? = null
        private var currentNotificationId: Int = -1

        fun stopAlarmSound() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        val id = intent?.getIntExtra("id", -1) ?: return
        currentNotificationId = id
        val repo = RepositoryImpl.getInstance(
            RemoteDataSource(ApiClient.weatherService),
            LocalDataSource(
                WeatherDataBase.getInstance(context).getForecastDao(),
                SharedPreference.getInstance()
            )
        )
        when (intent.action) {
            "android.intent.action.ALARM_TRIGGERED" -> {
                val alarmLabel = intent.getStringExtra("ALARM_LABEL") ?: "Thundora Alarm!"
                val duration = intent.getIntExtra("ALARM_DURATION", 5)

                CoroutineScope(Dispatchers.IO).launch {
                    val description = try {
                        ApiClient.weatherService
                            .getWeather(
                                SharedPreference.getInstance()
                                    .fetchData(SharedKeys.LAT.toString(), "0.0").toDouble(),
                                SharedPreference.getInstance()
                                    .fetchData(SharedKeys.LON.toString(), "0.0").toDouble(),
                                "en",
                                "metric"
                            ).body()?.weather?.firstOrNull()?.icon?.getWeatherNotification()
                            ?: "No weather update available 🌍"
                    } catch (e: Exception) {
                        "No weather update available 🌍"
                    }

                    playAlarmSound(context)
                    showCustomNotification(
                        context,
                        id,
                        alarmLabel,
                        duration,
                        if (isInternetAvailable()) description else "No internet Connection"
                    )
                }
            }

            "android.intent.action.STOP_ALARM" -> {
                stopAlarmSound()
            }

            "android.intent.action.CANCEL_NOTIFICATION" -> {
                val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.cancel(notificationId)
                stopAlarmSound()
                removeAlarm(notificationId, repo)
            }

            "android.intent.action.SNOOZE_ALARM" -> {
                val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
                val alarmLabel = intent.getStringExtra("ALARM_LABEL") ?: "Thundora Alarm!"
                val duration = intent.getIntExtra("ALARM_DURATION", 5)

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.cancel(notificationId)
                stopAlarmSound()

                scheduleSnoozedAlarm(context, notificationId, alarmLabel, duration, repo)

                Toast.makeText(context, "Alarm snoozed for 1 minute", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleSnoozedAlarm(
        context: Context,
        alarmId: Int,
        alarmLabel: String,
        duration: Int,
        repo: RepositoryImpl
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val triggerTime = System.currentTimeMillis() + 60_000L
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "android.intent.action.ALARM_TRIGGERED"
            putExtra("ALARM_LABEL", alarmLabel)
            putExtra("ALARM_DURATION", duration)
            putExtra("id", alarmId)
            CoroutineScope(Dispatchers.IO).launch {
                repo.insertAlarm(AlarmEntity(alarmId, alarmLabel, LocalTime.now(), duration))
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    private fun removeAlarm(id: Int, repo: RepositoryImpl) {

        CoroutineScope(Dispatchers.IO).launch {
            repo.deleteAlarmById(id)
        }
    }

    private fun showCustomNotification(
        context: Context,
        alarmId: Int,
        alarmLabel: String,
        duration: Int,
        asd: String?
    ) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Alarms", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = asd ?: "Alarm notification"
                enableLights(true)
                enableVibration(true)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_id", alarmId)
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
            context,
            alarmId,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "android.intent.action.SNOOZE_ALARM"
            putExtra("NOTIFICATION_ID", alarmId)
            putExtra("ALARM_LABEL", alarmLabel)
            putExtra("ALARM_DURATION", duration)
            putExtra("id", alarmId)

        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId + 1000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle(alarmLabel)
            .setContentText(asd)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainPendingIntent)
            .setAutoCancel(false)
            .setDeleteIntent(cancelPendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.i_01d, context.getString(R.string.dismiss), cancelPendingIntent)
            .addAction(
                R.drawable.notifications,
                context.getString(R.string.snooze),
                snoozePendingIntent
            )
            .build()

        notificationManager.notify(alarmId, notification)
        Handler(Looper.getMainLooper()).postDelayed({
            notificationManager.cancel(alarmId)
            stopAlarmSound()
        }, duration * 1000L)
    }

    private fun playAlarmSound(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.notification).apply {
                isLooping = true
                setVolume(1.0f, 1.0f)
            }
        }
        mediaPlayer?.start()
    }
}