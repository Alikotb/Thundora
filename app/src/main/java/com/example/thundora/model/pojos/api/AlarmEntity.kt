package com.example.thundora.model.pojos.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.thundora.model.pojos.AlarmConverter
import java.time.LocalTime

@Entity(tableName = "alarms")
@TypeConverters(AlarmConverter::class)
data class AlarmEntity(
    @PrimaryKey
    var id: Int,
    val label: String,
    val time: LocalTime,
    val duration: Int,
)

