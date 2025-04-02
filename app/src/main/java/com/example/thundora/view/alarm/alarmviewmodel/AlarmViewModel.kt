package com.example.thundora.view.alarm.alarmviewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.Response
import com.example.thundora.services.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch



class AlarmViewModel(
    private val repository: RepositoryImpl,
    private val alarmScheduler: AlarmScheduler
) : ViewModel(),IAlarmViewModel {
    private val _alarm = MutableStateFlow<Response<AlarmEntity?>>(Response.Loading)
    override val alarm = _alarm.asStateFlow()

    private val _alarms = MutableStateFlow<Response<List<AlarmEntity>>>(Response.Loading)
    override val allAlarms = _alarms.asStateFlow()


    init {
        getAllAlarms()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.insertAlarm(alarm)
            alarmScheduler.scheduleAlarm(alarm)
            getAllAlarms()
        }
    }

    override fun deleteAlarmById(alarmId: Int) {
        viewModelScope.launch {
            repository.deleteAlarmById(alarmId)
            getAllAlarms()
        }
    }

    override fun getAllAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms()
                .catch { e -> _alarms.emit(Response.Error(e.message ?: "Error fetching alarms")) }
                .collect { alarmList -> _alarms.emit(Response.Success(alarmList)) }
        }
    }

}
