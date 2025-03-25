package com.example.thundora.model.localdatasource

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
    fun <T> saveData(key: String, value: T) {
        sharedPreference.saveData(key, value)
    }
    fun <T> fetchData(key: String, defaultValue: T): T {
        return sharedPreference.fetchData(key, defaultValue)
    }

}