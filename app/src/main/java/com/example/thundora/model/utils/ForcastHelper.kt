package com.example.thundora.model.utils

import com.example.thundora.model.pojos.api.Forecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Forecast.dailyForecasts(): Map<Int, List<Forecast.Item0>> {
    val forecastMap = mutableMapOf<Int, MutableList<Forecast.Item0>>()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDate = dateFormat.format(Date()).replace("-", "").toInt()

    for (item in list) {
        val dateKey = dateFormat.format(Date(item.dt * 1000L)).replace("-", "").toInt()
        if (dateKey != todayDate) {
            forecastMap.getOrPut(dateKey) { mutableListOf() }.add(item)
        }
    }

    return forecastMap.mapValues { it.value.take(8) }
}



