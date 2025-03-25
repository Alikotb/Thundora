package com.example.thundora.view.favorite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.repositary.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: Repository) : ViewModel() {
    private val _favoriteCities = MutableStateFlow<Response<List<Weather>>>(Response.Loading)
    val favoriteCities = _favoriteCities.asStateFlow()

    private val _favoriteCity = MutableStateFlow<Response<Weather>>(Response.Loading)
    val favoriteCity = _favoriteCity.asStateFlow()

    fun getFavoriteCities() {
        viewModelScope.launch {
            repository.getAllWeather()
                .catch { e ->
                    _favoriteCities.value = Response.Error(e.localizedMessage ?: "Error fetching favorites")
                }
                .map {
                    it.sortedBy {
                        it.name
                    }
                }
                .collect { weatherList ->
                    _favoriteCities.emit(Response.Success(weatherList))
                }
        }
    }
    fun deleteFavoriteCity(city: String) {
        viewModelScope.launch {
            try {
                repository.deleteWeather(city)
            } catch (e: Exception) {
                _favoriteCity.emit(Response.Error(e.localizedMessage ?: "Error deleting favorite"))
            }
        }
    }
    fun addFavoriteCity(city: Weather) {
        viewModelScope.launch {
            try {
                repository.addWeather(city)
            } catch (e: Exception) {
                _favoriteCity.emit(Response.Error(e.localizedMessage ?: "Error adding favorite"))
                }
        }
    }
}
