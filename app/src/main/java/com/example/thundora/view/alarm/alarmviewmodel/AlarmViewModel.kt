package com.example.thundora.view.alarm.alarmviewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.thundora.model.pojos.api.AlarmEntity
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.services.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlarmViewModel (private val repository: Repository,private val alarmScheduler: AlarmScheduler) : ViewModel() {
    private val _alarm = MutableStateFlow<Response<AlarmEntity?>>(Response.Loading)
    val alarm = _alarm.asStateFlow()

    private val _alarms = MutableStateFlow< Response<List<AlarmEntity>>>(Response.Loading)
    val allAlarms = _alarms.asStateFlow()

    fun updateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
            getAllAlarms()
        }
    }

    fun getAlarmById(id: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getAlarmById(id)
                _alarm.emit(Response.Success(result))
            } catch (e: Exception) {
                _alarm.emit(Response.Error(e.message ?: "Error fetching alarm"))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.insertAlarm(alarm)
            alarmScheduler.scheduleAlarm(alarm) // Schedule Alarm
            getAllAlarms()
        }
    }

    fun deleteAlarmById(alarmId: Int) {
        viewModelScope.launch {
            repository.deleteAlarmById(alarmId)
            getAllAlarms()
        }
    }

    fun getAllAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms()
                .catch { e -> _alarms.emit(Response.Error(e.message ?: "Error fetching alarms")) }
                .collect { alarmList -> _alarms.emit(Response.Success(alarmList)) }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class AlarmFactory(private val repo: Repository, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val alarmScheduler = AlarmScheduler(context)
        return AlarmViewModel(repo, alarmScheduler) as T
    }
}