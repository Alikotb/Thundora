package com.example.thundora.model.utils

import com.example.thundora.model.pojos.ForecastDto
import com.example.thundora.model.pojos.api.Forecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Forecast.dailyForecasts(): Map<Int, List<Forecast.Item0>> {
    val forecastMap = mutableMapOf<Int, MutableList<Forecast.Item0>>()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    for (item in list) {
        val dateKey = dateFormat.format(Date(item.dt * 1000L)).replace("-", "").toInt()
        if (forecastMap[dateKey] == null) {
            forecastMap[dateKey] = mutableListOf()
        }
        forecastMap[dateKey]?.add(item)
    }
    return forecastMap.mapValues { it.value.take(8) }
}
fun Forecast.convertToDto(forcast: Forecast): ForecastDto {
    return ForecastDto(
        city = forcast.city.name,
        lat = forcast.city.coord.lat,
        lon = forcast.city.coord.lon,
        country = forcast.city.country,
        sunrise = forcast.city.sunrise,
        sunset = forcast.city.sunset,
        humidity = forcast.list[0].main.humidity,
        pressure = forcast.list[0].main.pressure,
        sea_level = forcast.list[0].main.sea_level,
        temp = forcast.list[0].main.temp,
        speed = forcast.list[0].wind.speed
    )
}





