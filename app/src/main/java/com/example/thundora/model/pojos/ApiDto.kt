package com.example.thundora.model.pojos

import androidx.room.Entity


@Entity(tableName = "forecast_table", primaryKeys = ["lat", "lon"])
data class ForecastDto(
    val city: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val sunrise: Int,
    val sunset: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val speed: Double

)
