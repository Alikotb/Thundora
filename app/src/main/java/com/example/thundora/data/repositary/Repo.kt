package com.example.thundora.data.repositary

import com.example.thundora.data.local.source.ILocalDataSource
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.remote.remotedatasource.IRemoteDataSource
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.ApiResponse
import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.domain.model.api.GeocodingResponseItem
import com.example.thundora.domain.model.api.Weather
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RepositoryImpl (private val remote: IRemoteDataSource, private val local: ILocalDataSource) : IRepository {


    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): Flow<Weather> {
        return remote.getWeather(lat, lon, units,language)
    }
     override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): Flow<Forecast> {
        return remote.getForecast(lat, lon, units,language)
    }

    override suspend fun getApiForecast(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): Flow<ApiResponse> {
        val weatherFlow = getWeather(lat, lon, units,language)
        val forecastFlow = getForecast(lat, lon, units,language)
        return combine(weatherFlow, forecastFlow) { weather, forecast ->
            ApiResponse(1,forecast, weather)
        }
    }

    override suspend fun addWeather(weather: Weather) {
        local.insertWeather(weather)
    }

    override fun getWeather(cityName: String): Flow<Weather> {
        return local.getWeather(cityName)
    }

    override suspend fun deleteWeather(cityName: String) {
        local.deleteWeather(cityName)
    }

    override fun getAllWeather(): Flow<List<Weather>> {
        return local.getAllWeather()
    }

    override suspend fun updateWeather(weather: Weather) {
        local.updateWeather(weather)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
       return local.getAllAlarms()
    }

    override suspend fun getAlarmById(id: Int): AlarmEntity? {
       return local.getAlarmById(id)
    }

    override suspend fun insertAlarm(alarm: AlarmEntity) {
        local.insertAlarm(alarm)
    }

    override suspend fun deleteAlarmById(alarmId: Int) {
        local.deleteAlarmById(alarmId)
    }

    override suspend fun updateAlarm(alarm: AlarmEntity) {
        local.updateAlarm(alarm)
    }

    override suspend fun insertHome(home: ApiResponse) {
        local.insertHome(home)
    }

    override fun getHome(): Flow<ApiResponse> {
        return local.getHome()
    }


    override fun <T> saveData(key: String, value: T) {
        local.saveData(key, value)
    }

    override fun <T> fetchData(key: String, defaultValue: T): T {
        return local.fetchData(key, defaultValue)
    }

    override suspend fun getCoordinates(city: String): Flow<List<GeocodingResponseItem>>{
        return remote.getCoordinates(city)
    }
    companion object{
        private var instance: RepositoryImpl? = null
        fun getInstance(remote: RemoteDataSource, local: LocalDataSource): RepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: RepositoryImpl(remote,local).also { instance = it }
            }
        }
    }
}

