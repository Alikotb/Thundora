package com.example.thundora.model.repositary

import com.example.thundora.model.pojos.api.ApiResponse
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.remotedatasource.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class Repository private constructor(private val remote: RemoteDataSource) : IRepository {
     override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<Weather> {

        return remote.getWeather(lat, lon, units)
    }
     override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<Forecast> {
        return remote.getForecast(lat, lon, units)
    }

    override suspend fun getApiForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<ApiResponse> {
        val weatherFlow = getWeather(lat, lon, units)
        val forecastFlow = getForecast(lat, lon, units)

        return combine(weatherFlow, forecastFlow) { weather, forecast ->
            ApiResponse(forecast, weather)
        }
    }
    override suspend fun getCoordinates(city: String): Flow<List<GeocodingResponseItem>>{
        return remote.getCoordinates(city)
    }
    companion object{
        private var instance: Repository? = null
        fun getInstance(remote: RemoteDataSource): Repository {
            return instance ?: synchronized(this) {
                instance ?: Repository(remote).also { instance = it }
            }

        }
    }
}

