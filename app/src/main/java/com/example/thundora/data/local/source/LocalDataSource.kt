package com.example.thundora.data.local.source

import com.example.thundora.data.local.database.Dao
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.ApiResponse
import com.example.thundora.domain.model.api.Weather
import com.example.thundora.data.local.sharedpreference.SharedPreference
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val dao: Dao, private val sharedPreference: SharedPreference):ILocalDataSource {
    override suspend fun insertWeather(weather: Weather) {
        dao.insertWeather(weather)
    }

    override fun getWeather(cityName: String): Flow<Weather> {
        return dao.getWeather(cityName)
    }

    override suspend fun deleteWeather(cityName: String) {
        dao.deleteWeather(cityName)
    }

    override fun getAllWeather(): Flow<List<Weather>> {
        return dao.getAllWeather()
    }

    override suspend fun updateWeather(weather: Weather) {
        dao.updatesWeathe(weather)
    }


    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return dao.getAllAlarms()
    }

    override suspend fun getAlarmById(id: Int): AlarmEntity? {
        return dao.getAlarmById(id)
    }

    override suspend fun insertAlarm(alarm: AlarmEntity) {
        dao.insertAlarm(alarm)
    }

    override suspend fun  deleteAlarmById(alarmId: Int) {
        dao.deleteAlarmById(alarmId)
    }

    override suspend fun updateAlarm(alarm: AlarmEntity) {
        dao.updateAlarm(alarm)
    }
    override suspend fun insertHome(home: ApiResponse) {
        dao.insertHome(home)
    }
    override fun getHome(): Flow<ApiResponse> {
        return dao.getHome()
    }

    override fun <T> saveData(key: String, value: T) {
        sharedPreference.saveData(key, value)
    }

    override fun <T> fetchData(key: String, defaultValue: T): T {
        return sharedPreference.fetchData(key, defaultValue)
    }

}