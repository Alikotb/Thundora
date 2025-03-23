package com.example.thundora.model.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeHelper {


    fun formatUnixTimestamp(timestamp: Long?, pattern: String = "HH:mm"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp!! * 1000L))
    }
    fun isDayTime(sunrise: Long, sunset: Long, currentTime: Long): Boolean {
        return currentTime in sunrise..sunset
    }

    fun isToday(unixTimestamp: Long): Boolean {
        val itemCalendar = Calendar.getInstance().apply {
            timeInMillis = unixTimestamp * 1000L
        }
        val currentCalendar = Calendar.getInstance()

        return itemCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                itemCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)
    }

    fun getDayOfWeek(timestamp: Long?): String {
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(
            Date((timestamp?.times(1000L)) ?: System.currentTimeMillis())
        )
    }
    fun getFormattedDate(timestamp: Long?): String {
        return formatUnixTimestamp(timestamp!!, "d MMM")
    }

}
