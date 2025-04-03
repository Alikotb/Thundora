package com.example.thundora.data.remote.remotedatasource

import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.domain.model.api.GeocodingResponseItem
import com.example.thundora.domain.model.api.Weather
import com.example.thundora.data.remote.api.WeatherService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RemoteDataSource(val api: WeatherService) :IRemoteDataSource{
    override suspend fun getWeather(lat: Double, lon: Double, units:String, language:String): Flow<Weather> {
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
    override suspend fun getForecast(lat: Double, lon: Double, units:String, language:String): Flow<Forecast> {
        val response = api.getForecast(
            lon = lon,
            lat = lat,
            units = units,
            lang = language)
        return flowOf (
            response.body()!!
        )
    }
    override suspend fun getCoordinates(city: String): Flow<List<GeocodingResponseItem>> {
        val response = api.getCoordinates(city)
        return flowOf (
            response.body()!!
        )
    }
}