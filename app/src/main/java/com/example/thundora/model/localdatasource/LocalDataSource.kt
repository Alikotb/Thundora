package com.example.thundora.model.localdatasource

import com.example.thundora.model.pojos.api.AlarmEntity
import com.example.thundora.model.pojos.api.ApiResponse
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.sharedpreference.SharedPreference
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val dao: Dao, private val sharedPreference: SharedPreference) {
    suspend fun insertWeather(weather: Weather) {
        dao.insertWeather(weather)
    }

    fun getWeather(cityName: String): Flow<Weather> {
        return dao.getWeather(cityName)
    }

    suspend fun deleteWeather(cityName: String) {
        dao.deleteWeather(cityName)
    }

    fun getAllWeather(): Flow<List<Weather>> {
        return dao.getAllWeather()
    }

    suspend fun updateWeather(weather: Weather) {
        dao.updatesWeathe(weather)
    }


    fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return dao.getAllAlarms()
    }

    suspend fun getAlarmById(id: Int): AlarmEntity? {
        return dao.getAlarmById(id)
    }

    suspend fun insertAlarm(alarm: AlarmEntity) {
        dao.insertAlarm(alarm)
    }

    suspend fun  deleteAlarmById(alarmId: Int) {
        dao.deleteAlarmById(alarmId)
    }

    suspend fun updateAlarm(alarm: AlarmEntity) {
        dao.updateAlarm(alarm)
    }
    suspend fun insertHome(home: ApiResponse) {
        dao.insertHome(home)
    }
    fun getHome(): Flow<ApiResponse> {
        return dao.getHome()
    }

    fun <T> saveData(key: String, value: T) {
        sharedPreference.saveData(key, value)
    }

    fun <T> fetchData(key: String, defaultValue: T): T {
        return sharedPreference.fetchData(key, defaultValue)
    }

}