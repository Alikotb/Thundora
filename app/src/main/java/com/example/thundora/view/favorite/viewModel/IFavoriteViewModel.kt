package com.example.thundora.view.favorite.viewModel

import com.example.thundora.domain.model.api.Response
import com.example.thundora.domain.model.api.Weather
import kotlinx.coroutines.flow.StateFlow

interface IFavoriteViewModel {
    val favoriteCities: StateFlow<Response<List<Weather>>>
    val favoriteCity: StateFlow<Response<Weather>>
    val language: StateFlow<String>
    val temperatureUnit: StateFlow<String>

    fun getFavoriteCities()
    fun deleteFavoriteCity(city: String)
    fun addFavoriteCity(city: Weather)
    fun getFavoriteCityApi(city: String, lat: Double, lon: Double)
    fun getFavoriteCityRoom(city: String)
}
