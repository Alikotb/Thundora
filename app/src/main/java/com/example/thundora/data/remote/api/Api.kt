package com.example.thundora.data.remote.api

import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.domain.model.api.GeocodingResponseItem
import com.example.thundora.domain.model.api.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang : String="en",
        @Query("units") units: String = "metric"
    ): Response<Forecast>

    @GET("geo/1.0/direct")
    suspend fun getCoordinates(
        @Query("q") city: String,
        @Query("limit") limit: Int = 1
    ): Response<List<GeocodingResponseItem>>

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang : String="en",
        @Query("units") units: String = "metric"
    ): Response<Weather>

}
