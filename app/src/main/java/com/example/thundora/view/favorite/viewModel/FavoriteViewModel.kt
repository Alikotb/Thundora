package com.example.thundora.view.favorite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.domain.model.api.Response
import com.example.thundora.domain.model.api.Weather
import com.example.thundora.domain.model.view.SharedKeys
import com.example.thundora.data.repositary.RepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

class FavoriteViewModel(private val repository: RepositoryImpl) : ViewModel(),IFavoriteViewModel  {
    private val _favoriteCities = MutableStateFlow<Response<List<Weather>>>(Response.Loading)
    override val favoriteCities = _favoriteCities.asStateFlow()

    private val _favoriteCity = MutableStateFlow<Response<Weather>>(Response.Loading)
    override val favoriteCity = _favoriteCity.asStateFlow()

    private val _language = MutableStateFlow("")
    override val language: StateFlow<String> = _language
    private val _temperatureUnit = MutableStateFlow("")
    override val temperatureUnit: StateFlow<String> =  _temperatureUnit.asStateFlow()


    fun fetchSettings() {
        _language.value = Locale.getDefault().language
        _temperatureUnit.value = repository.fetchData(SharedKeys.DEGREE.toString(),"Celsius")
    }

    override fun getFavoriteCities() {
        try {
            viewModelScope.launch {
                repository.getAllWeather()
                    .catch { e ->
                        _favoriteCities.value =
                            Response.Error(e.localizedMessage ?: "Error fetching favorites")
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
        }catch (e: Exception){
        }
    }

    override fun deleteFavoriteCity(city: String) {
        viewModelScope.launch {
            try {
                repository.deleteWeather(city)
            } catch (e: Exception) {
                _favoriteCity.emit(Response.Error(e.localizedMessage ?: "Error deleting favorite"))
            }
        }
    }

    override fun addFavoriteCity(city: Weather) {
        viewModelScope.launch {
            try {
                repository.addWeather(city)
            } catch (e: Exception) {
                _favoriteCity.emit(Response.Error(e.localizedMessage ?: "Error adding favorite"))
            }
        }
    }

    override fun getFavoriteCityApi(city:String, lat: Double, lon: Double) {

        try {
            viewModelScope.launch {
                repository.getWeather(lat, lon, "metric", "ar")
                    .catch { e ->
                        _favoriteCity.value =
                            Response.Error(e.localizedMessage ?: "Error fetching favorite")
                    }
                    .collect { weather ->
                        _favoriteCity.emit(Response.Success(weather))
                        weather.name = city
                        repository.updateWeather(weather)
                    }
            }
        } catch (e: Exception) {
            _favoriteCity.value = Response.Error(e.localizedMessage ?: "Error fetching favorite")
        }
    }
    override fun getFavoriteCityRoom(city: String){
        try {
            viewModelScope.launch {
                repository.getWeather(city)
                    .catch { e ->
                        _favoriteCity.value =
                            Response.Error(e.localizedMessage ?: "Error fetching favorite")
                    }.collect { weather ->
                        _favoriteCity.emit(Response.Success(weather))
                    }
            }
        } catch (e: Exception) {
            _favoriteCity.value = Response.Error(e.localizedMessage ?: "Error fetching favorite")
        }
    }
}
