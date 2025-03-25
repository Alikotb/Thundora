package com.example.thundora.model.pojos

import androidx.room.Entity

@Entity(tableName = "forecast_table", primaryKeys = ["lat", "lon"])
data class ForecastDto(
    val name: String,
    val icon: String,
    val temp: Double,
    val description: String,
    val speed: Double,
    val humidity: Int,
    val pressure: Int,
    val sunrise: Int,
    val sunset: Int,
    val sea_level: Int,
    val country: String,
    val lat: Double,
    val lon: Double,
)

