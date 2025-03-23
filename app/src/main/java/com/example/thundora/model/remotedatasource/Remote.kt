package com.example.thundora.model.remotedatasource

import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RemoteDataSource(val api: WeatherService) {
    suspend fun getWeather(lat: Double, lon: Double,units:String,language:String): Flow<Weather> {
        val response = api.getWeather(
            lon = lon,
            lat = lat,
            units = units,
            lang = language
        )
        return flowOf (
            response.body()!!
        )
    }
    suspend fun getForecast(lat: Double, lon: Double,units:String,language:String): Flow<Forecast> {
        val response = api.getForecast(
            lon = lon,
            lat = lat,
            units = units,
            lang = language)
        return flowOf (
            response.body()!!
        )
    }
    suspend fun getCoordinates(city: String): Flow<List<GeocodingResponseItem>> {
        val response = api.getCoordinates(city)
        return flowOf (
            response.body()!!
        )
    }
}