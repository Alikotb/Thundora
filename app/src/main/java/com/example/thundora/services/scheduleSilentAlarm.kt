package com.example.thundora.services

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.thundora.domain.model.api.AlarmEntity
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit



@RequiresApi(Build.VERSION_CODES.O)
fun scheduleSilentAlarm(alarm: AlarmEntity, context: Context) {
    val workManager = WorkManager.getInstance(context)

    val now = LocalTime.now()
    val startDelay = Duration.between(now, alarm.time).toMillis()
    val endDelay = startDelay + TimeUnit.MINUTES.toMillis(alarm.duration.toLong())

    if (startDelay <= 0) return
    val startData = workDataOf(
        "id" to alarm.id,
        "label" to alarm.label,
        "isEndNotification" to false
    )

    val endData = workDataOf(
        "id" to alarm.id,
        "label" to alarm.label,
        "isEndNotification" to true
    )

    val startWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(startDelay, TimeUnit.MILLISECONDS)
        .setInputData(startData)
        .build()

    val endWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(endDelay, TimeUnit.MILLISECONDS)
        .setInputData(endData)
        .build()

    workManager.enqueue(startWorkRequest)
    workManager.enqueue(endWorkRequest)
}

