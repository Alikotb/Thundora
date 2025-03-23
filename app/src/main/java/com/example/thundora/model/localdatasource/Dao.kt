package com.example.thundora.model.localdatasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thundora.model.pojos.ForecastDto
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(forecast: ForecastDto)
    @Query("SELECT * FROM forecast_table WHERE lat = :lat AND lon = :lon")
     fun getForecast(lat: Double, lon: Double): Flow<ForecastDto>
    @Query("DELETE FROM forecast_table WHERE lat = :lat AND lon = :lon")
     fun deleteForecast(lat: Double, lon: Double)
    @Query("SELECT * FROM forecast_table")
     fun getAllForecasts(): Flow<List<ForecastDto>>


}