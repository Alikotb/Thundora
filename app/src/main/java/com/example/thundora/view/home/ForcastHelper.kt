package com.example.thundora.view.home

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





