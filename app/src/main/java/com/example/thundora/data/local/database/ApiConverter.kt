package com.example.thundora.data.local.database

import androidx.room.TypeConverter
import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.domain.model.api.Weather
import kotlinx.serialization.json.Json

class ApiResponseConverter {

    @TypeConverter
    fun fromForecast(value:Forecast): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toForecast(value: String): Forecast {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromWeather(value: Weather): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toWeather(value: String): Weather {
        return Json.decodeFromString(value)
    }
}
