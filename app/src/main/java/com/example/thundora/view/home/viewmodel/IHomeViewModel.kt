package com.example.thundora.view.home.viewmodel

import com.example.thundora.domain.model.api.ApiResponse
import com.example.thundora.domain.model.api.Response
import kotlinx.coroutines.flow.StateFlow


interface IHomeViewModel {
    val forecast: StateFlow<Response<ApiResponse>>
    val message: StateFlow<String>
    val language: StateFlow<String>
    val temperatureUnit: StateFlow<String>
    val units: StateFlow<String>

    fun fetchSettings()
    fun getForecast()
    fun getForecastFromLocal()
}
