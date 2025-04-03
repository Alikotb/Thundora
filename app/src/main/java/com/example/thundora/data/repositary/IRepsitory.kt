package com.example.thundora.data.repositary
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.ApiResponse
import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.domain.model.api.GeocodingResponseItem
import com.example.thundora.domain.model.api.Weather
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend  fun getWeather(lat: Double, lon: Double, units: String,language: String): Flow<Weather>
    suspend fun getForecast(lat: Double, lon: Double, units: String,language: String): Flow<Forecast>
    suspend fun getCoordinates(city: String): Flow<List<GeocodingResponseItem>>
    suspend fun getApiForecast(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): Flow<ApiResponse>
    suspend fun addWeather(weather: Weather)
    fun getWeather(cityName: String): Flow<Weather>
    suspend fun deleteWeather(cityName: String)
    fun getAllWeather(): Flow<List<Weather>>
    suspend fun updateWeather(weather: Weather)
    fun getAllAlarms(): Flow<List<AlarmEntity>>
    suspend fun getAlarmById(id: Int): AlarmEntity?
    suspend fun insertAlarm(alarm: AlarmEntity)
    suspend fun deleteAlarmById(alarmId: Int)
    suspend fun updateAlarm(alarm: AlarmEntity)
    suspend fun insertHome(home: ApiResponse)
    fun getHome(): Flow<ApiResponse>
    fun <T> saveData(key: String, value: T)
    fun <T> fetchData(key: String, defaultValue: T): T
}