package com.example.thundora.model.remotedatasource

import com.example.thundora.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val apiKey = BuildConfig.WEATHER_API_KEY

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val weatherService: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }
}