package com.example.thundora.view.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.model.pojos.api.ApiResponse
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.repositary.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: Repository) : ViewModel() {
    private val _forecast = MutableStateFlow<Response<ApiResponse>>(Response.Loading)
    val forecast = _forecast.asStateFlow()
    private val _messageState = MutableStateFlow<String>("")
    val message = _messageState.asStateFlow()


    fun getForecast(lat: Double, lon: Double, units: String,language:String) {
        viewModelScope.launch {
            try {
                repo.getApiForecast(lat, lon, units,language)
                    .catch {
                        _messageState.emit(it.message ?: "Unknown error")
                    }
                    .collect { apiForecast ->
                        _forecast.emit(Response.Success(apiForecast))
                    }
            } catch (e: Exception) {
                _messageState.emit(e.message ?: "Unknown error")
            }

        }

    }
}