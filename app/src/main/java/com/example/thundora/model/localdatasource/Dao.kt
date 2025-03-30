package com.example.thundora.model.localdatasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.thundora.model.pojos.api.AlarmEntity
import com.example.thundora.model.pojos.api.ApiResponse
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
    @Update
    suspend fun updatesWeathe(weather: Weather)

    @Query("SELECT * FROM alarms ")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Int): AlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Query("DELETE FROM alarms WHERE id = :alarmId")
    suspend fun deleteAlarmById(alarmId: Int)

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHome(home: ApiResponse)
    @Query("SELECT * FROM api_response_table")
    fun getHome(): Flow<ApiResponse>
}

