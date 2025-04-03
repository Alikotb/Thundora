package com.example.thundora.data.repositary

import com.example.thundora.data.local.source.ILocalDataSource
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.ApiResponse
import com.example.thundora.domain.model.api.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeDataSource(var alarms: MutableList<AlarmEntity>? = mutableListOf()):ILocalDataSource {
    override suspend fun insertWeather(weather: Weather) {
        TODO("Not yet implemented")
    }

    override fun getWeather(cityName: String): Flow<Weather> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather(cityName: String) {
        TODO("Not yet implemented")
    }

    override fun getAllWeather(): Flow<List<Weather>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWeather(weather: Weather) {
        TODO("Not yet implemented")
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> = flow {
        emit(alarms ?: emptyList())
    }

    override suspend fun getAlarmById(id: Int): AlarmEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: AlarmEntity) {
        this.alarms?.add(alarm)
    }

    override suspend fun deleteAlarmById(alarmId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlarm(alarm: AlarmEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun insertHome(home: ApiResponse) {
        TODO("Not yet implemented")
    }

    override fun getHome(): Flow<ApiResponse> {
        TODO("Not yet implemented")
    }

    override fun <T> saveData(key: String, value: T) {
        TODO("Not yet implemented")
    }

    override fun <T> fetchData(key: String, defaultValue: T): T {
        TODO("Not yet implemented")
    }
}