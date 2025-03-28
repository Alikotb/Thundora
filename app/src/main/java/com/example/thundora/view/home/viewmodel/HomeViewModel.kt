package com.example.thundora.view.home.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.model.pojos.api.ApiResponse
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.utils.getLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: Repository) : ViewModel() {

    private val _forecast = MutableStateFlow<Response<ApiResponse>>(Response.Loading)
    val forecast = _forecast.asStateFlow()

    private val _messageState = MutableStateFlow("")
    val message = _messageState.asStateFlow()

    private val _language = MutableStateFlow("")
    val language: StateFlow<String> = _language
    private val _temperatureUnit = MutableStateFlow("")
    val temperatureUnit: StateFlow<String> =  _temperatureUnit.asStateFlow()


    private val _units = MutableStateFlow("")
    val units: StateFlow<String> = _units
    private val _latitude = MutableStateFlow(0.0)
    private val _longitude = MutableStateFlow(0.0)

    init {
        fetchSettings()
    }

    private fun fetchSettings() {
        _language.value = repo.fetchData(SharedKeys.LANGUAGE.toString(), "en")
        _units.value = repo.fetchData(SharedKeys.SPEED_UNIT.toString(), "metric")
        _temperatureUnit.value = repo.fetchData(SharedKeys.DEGREE.toString(),"Celsius")
        _latitude.value = repo.fetchData(SharedKeys.LAT.toString(), "0.0").toDouble()
        _longitude.value = repo.fetchData(SharedKeys.LON.toString(), "0.0").toDouble()
        getForecast()
    }

    fun getForecast() {
        viewModelScope.launch {
            try {

                repo.getApiForecast(_latitude.value ,_longitude.value, _temperatureUnit.value,getLanguage( _language.value))
                    .catch {
                        _messageState.emit(it.message ?: "Unknown error")

                    }
                    .collect {apiForecast ->
                         _forecast.emit(Response.Success(apiForecast))

                    }
            } catch (e: Exception) {
                _messageState.emit(e.message ?: "Unknown error")

            }
        }
    }
}
