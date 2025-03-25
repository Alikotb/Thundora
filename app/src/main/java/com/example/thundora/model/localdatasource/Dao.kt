package com.example.thundora.model.localdatasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thundora.model.pojos.api.Weather
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)
    @Query("SELECT * FROM weather_table WHERE name = :cityName")
    fun getWeather(cityName: String): Flow<Weather>
    @Query("DELETE FROM weather_table WHERE name = :cityName")
    suspend fun deleteWeather(cityName: String)
    @Query("SELECT * FROM weather_table")
    fun getAllWeather(): Flow<List<Weather>>
}

