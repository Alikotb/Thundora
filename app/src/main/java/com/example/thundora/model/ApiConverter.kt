package com.example.thundora.model

import androidx.room.TypeConverter
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.Weather
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
