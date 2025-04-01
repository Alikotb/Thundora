package com.example.thundora.view.alarm.alarmviewmodel

import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.Response
import kotlinx.coroutines.flow.StateFlow

interface IAlarmViewModel {
    val alarm: StateFlow<Response<AlarmEntity?>>
    val allAlarms: StateFlow<Response<List<AlarmEntity>>>

    fun addAlarm(alarm: AlarmEntity)
    fun deleteAlarmById(alarmId: Int)
    fun getAllAlarms()
}