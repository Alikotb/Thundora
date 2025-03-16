package com.example.thundora.model.repositary

import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather

interface IRepository {
    suspend fun getWeather(lat: Double, lon: Double): Weather
    suspend fun getForecast(lat: Double, lon: Double): Forecast
    suspend fun getCoordinates(city: String): List<GeocodingResponseItem>
}