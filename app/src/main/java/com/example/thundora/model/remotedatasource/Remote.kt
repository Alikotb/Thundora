package com.example.thundora.model.remotedatasource

import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather

class RemoteDataSource(val api: WeatherService) {
    suspend fun getWeather(lat: Double, lon: Double,units:String): Weather {
        val response = api.getWeather(lat, lon,units)
        return response.body()!!
    }

    suspend fun getForecast(lat: Double, lon: Double,units:String): Forecast {
        val response = api.getForecast(lat, lon,units)
        return response.body()!!
    }

    suspend fun getCoordinates(city: String): List<GeocodingResponseItem> {
        val response = api.getCoordinates(city)
        return response.body()!!
    }
}