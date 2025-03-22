package com.example.thundora.model.repositary

import com.example.thundora.model.pojos.api.ApiResponse
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend  fun getWeather(lat: Double, lon: Double, units: String): Flow<Weather>
    suspend fun getForecast(lat: Double, lon: Double, units: String): Flow<Forecast>
    suspend fun getCoordinates(city: String): Flow<List<GeocodingResponseItem>>
    suspend fun getApiForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<ApiResponse>
}