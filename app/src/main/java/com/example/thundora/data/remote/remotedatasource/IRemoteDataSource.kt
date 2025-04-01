package com.example.thundora.data.remote.remotedatasource

import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.domain.model.api.GeocodingResponseItem
import com.example.thundora.domain.model.api.Weather
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    suspend fun getWeather(lat: Double, lon: Double, units: String, language: String): Flow<Weather>
    suspend fun getForecast(lat: Double, lon: Double, units: String, language: String): Flow<Forecast>
    suspend fun getCoordinates(city: String): Flow<List<GeocodingResponseItem>>
}
