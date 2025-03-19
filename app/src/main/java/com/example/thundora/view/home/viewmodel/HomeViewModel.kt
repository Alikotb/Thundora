package com.example.thundora.view.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.repositary.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: Repository) : ViewModel() {
    private val _weather = MutableLiveData<Weather?>()
    val weather: MutableLiveData<Weather?> = _weather

    private val _forecast = MutableLiveData<Forecast?>()
    val forecast: MutableLiveData<Forecast?> = _forecast
    private val _error = MutableLiveData<String?>()

    fun getForecast(lat: Double, lon: Double, units: String) {
        viewModelScope.launch {
            try {
                _forecast.postValue(repo.getForecast(lat, lon, units))
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun getWeather(lat: Double, lon: Double, units: String) {
        viewModelScope.launch {
            try {
                _weather.postValue(repo.getWeather(lat, lon, units))
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}