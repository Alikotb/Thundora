package com.example.thundora.model.repositary

import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.pojos.ForecastDto
import com.example.thundora.model.pojos.api.ApiResponse
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.remotedatasource.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class Repository private constructor(private val remote: RemoteDataSource,private val local: LocalDataSource) : IRepository {
     override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): Flow<Weather> {

        return remote.getWeather(lat, lon, units,language)
    }
     override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): Flow<Forecast> {
        return remote.getForecast(lat, lon, units,language)
    }

    override suspend fun getApiForecast(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): Flow<ApiResponse> {
        val weatherFlow = getWeather(lat, lon, units,language)
        val forecastFlow = getForecast(lat, lon, units,language)

        return combine(weatherFlow, forecastFlow) { weather, forecast ->
            ApiResponse(forecast, weather)
        }
    }

    override suspend fun getRoomForecast(
        lat: Double,
        lon: Double
    ): Flow<ForecastDto> {
        return local.getForecast(lat, lon)
    }

    override suspend fun addRoomForecast(forecast: ForecastDto) {
        local.addForecast(forecast)
    }

    override suspend fun deleteRoomForecast(lat: Double, lon: Double) {
        local.deleteForecast(lat, lon)
    }

    override suspend fun getAllRoomForecasts(): Flow<List<ForecastDto>> {
        return local.getAllForecasts()
    }
    override fun <T> saveData(key: String, value: T) {
        local.saveData(key, value)
    }

    override fun <T> fetchData(key: String, defaultValue: T): T {
        return local.fetchData(key, defaultValue)
    }



    override suspend fun getCoordinates(city: String): Flow<List<GeocodingResponseItem>>{
        return remote.getCoordinates(city)
    }
    companion object{
        private var instance: Repository? = null
        fun getInstance(remote: RemoteDataSource, local: LocalDataSource): Repository {
            return instance ?: synchronized(this) {
                instance ?: Repository(remote,local).also { instance = it }
            }

        }
    }
}

