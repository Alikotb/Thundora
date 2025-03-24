package com.example.thundora.model.repositary

import com.example.thundora.model.pojos.ForecastDto
import com.example.thundora.model.pojos.api.ApiResponse
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather
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
    suspend fun getRoomForecast(lat: Double, lon: Double): Flow<ForecastDto>
    suspend fun addRoomForecast(forecast: ForecastDto)
    suspend fun deleteRoomForecast(lat: Double, lon: Double)
    suspend fun getAllRoomForecasts(): Flow<List<ForecastDto>>
    fun <T> saveData(key: String, value: T)
    fun <T> fetchData(key: String, defaultValue: T): T



}