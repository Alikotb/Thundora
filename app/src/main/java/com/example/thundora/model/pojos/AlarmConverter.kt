package com.example.thundora.model.pojos

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmConverter {
    @SuppressLint("NewApi")
    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    @SuppressLint("NewApi")
    @TypeConverter
    fun fromLocalTime(localTime: LocalTime?): String? {
        return localTime?.format(formatter)
    }

    @SuppressLint("NewApi")
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it, formatter) }
    }
}

