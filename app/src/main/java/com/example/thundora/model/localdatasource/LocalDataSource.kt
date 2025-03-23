package com.example.thundora.model.localdatasource

import com.example.thundora.model.pojos.ForecastDto
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val dao: Dao) {

     fun addForecast(forecast: ForecastDto) {
        dao.insert(forecast)
    }
     fun getForecast(lat: Double, lon: Double): Flow<ForecastDto> {
        return dao.getForecast(lat, lon)
    }
     fun deleteForecast(lat: Double, lon: Double) {
        dao.deleteForecast(lat, lon)
    }
     fun getAllForecasts(): Flow<List<ForecastDto>> {
        return dao.getAllForecasts()
    }
}