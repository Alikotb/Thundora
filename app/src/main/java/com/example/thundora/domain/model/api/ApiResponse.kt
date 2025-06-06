package com.example.thundora.domain.model.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.thundora.data.local.database.ApiResponseConverter
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "api_response_table")
@TypeConverters(ApiResponseConverter::class)
data class ApiResponse (
    @PrimaryKey val id: Int = 1,
    @Serializable
    val forecast: Forecast,
    @Serializable
    val weather: Weather
)









