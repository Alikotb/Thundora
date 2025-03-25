package com.example.thundora.model.pojos

import androidx.room.TypeConverter
import com.example.thundora.model.pojos.api.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCoord(coord: Weather.Coord): String = gson.toJson(coord)

    @TypeConverter
    fun toCoord(data: String): Weather.Coord =
        gson.fromJson(data, Weather.Coord::class.java)

    @TypeConverter
    fun fromMain(main: Weather.Main): String = gson.toJson(main)

    @TypeConverter
    fun toMain(data: String): Weather.Main =
        gson.fromJson(data, Weather.Main::class.java)

    @TypeConverter
    fun fromSys(sys: Weather.Sys): String = gson.toJson(sys)

    @TypeConverter
    fun toSys(data: String): Weather.Sys =
        gson.fromJson(data, Weather.Sys::class.java)

    @TypeConverter
    fun fromWind(wind: Weather.Wind): String = gson.toJson(wind)

    @TypeConverter
    fun toWind(data: String): Weather.Wind =
        gson.fromJson(data, Weather.Wind::class.java)

    @TypeConverter
    fun fromClouds(clouds: Weather.Clouds): String = gson.toJson(clouds)

    @TypeConverter
    fun toClouds(data: String): Weather.Clouds =
        gson.fromJson(data, Weather.Clouds::class.java)

    @TypeConverter
    fun fromWeatherList(weather: List<Weather.WeatherX>): String = gson.toJson(weather)

    @TypeConverter
    fun toWeatherList(data: String): List<Weather.WeatherX> {
        val listType = object : TypeToken<List<Weather.WeatherX>>() {}.type
        return gson.fromJson(data, listType)
    }
}
