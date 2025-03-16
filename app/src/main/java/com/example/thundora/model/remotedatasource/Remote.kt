package com.example.thundora.model.remotedatasource

import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather

class RemoteDataSource(val api: WeatherService) {
    suspend fun getWeather(lat: Double, lon: Double): Weather {
        val response = api.getWeather(lat, lon)
        return response.body()!!
    }

    suspend fun getForecast(lat: Double, lon: Double): Forecast {
        val response = api.getForecast(lat, lon)
        return response.body()!!
    }

    suspend fun getCoordinates(city: String): List<GeocodingResponseItem> {
        val response = api.getCoordinates(city)
        return response.body()!!
    }
}