package com.example.thundora.model.repositary

import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.remotedatasource.RemoteDataSource

class Repository private constructor(private val remote: RemoteDataSource) : IRepository {
    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String
    ): Weather {

        return remote.getWeather(lat, lon, units)
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Forecast {
        return remote.getForecast(lat, lon, units)
    }

    override suspend fun getCoordinates(city: String): List<GeocodingResponseItem> {
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

