package com.example.thundora.view.home.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.domain.model.api.ApiResponse
import com.example.thundora.domain.model.api.Response
import com.example.thundora.domain.model.view.SharedKeys
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.utils.getLanguage
import com.example.thundora.utils.getTemperatureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepositoryImpl) : ViewModel(),IHomeViewModel {

    private val _forecast = MutableStateFlow<Response<ApiResponse>>(Response.Loading)
    override val forecast = _forecast.asStateFlow()

    private val _messageState = MutableStateFlow("")
    override val message = _messageState.asStateFlow()

    private val _language = MutableStateFlow("")
    override val language: StateFlow<String> = _language
    private val _temperatureUnit = MutableStateFlow("")
    override val temperatureUnit: StateFlow<String> =  _temperatureUnit.asStateFlow()


    private val _units = MutableStateFlow("")
    override val units: StateFlow<String> = _units
    private val _latitude = MutableStateFlow(0.0)
    private val _longitude = MutableStateFlow(0.0)


     override fun fetchSettings() {
        _language.value = repo.fetchData(SharedKeys.LANGUAGE.toString(), "en")
        _temperatureUnit.value = repo.fetchData(SharedKeys.DEGREE.toString(), getTemperatureUnit("Celsius"))
        _units.value = repo.fetchData(SharedKeys.SPEED_UNIT.toString(), "metric")
        _latitude.value = repo.fetchData(SharedKeys.HOME_LAT.toString(), "0.0").toDouble()
        _longitude.value = repo.fetchData(SharedKeys.HOME_LON.toString(), "0.0").toDouble()
    }

    override fun getForecast() {
        viewModelScope.launch {
            try {
                repo.getApiForecast(_latitude.value ,_longitude.value, _temperatureUnit.value,getLanguage( _language.value))
                    .catch {
                        _messageState.emit(it.message ?: "Unknown error")
                    }
                    .collect {apiForecast ->
                        _forecast.emit(Response.Success(apiForecast))
                        repo.insertHome(apiForecast)
                    }
            } catch (e: Exception) {
                _messageState.emit(e.message ?: "Unknown error")

            }
        }
    }
    override fun getForecastFromLocal() {
        viewModelScope.launch {
            try {
                repo.getHome()
                    .catch {
                        _messageState.emit(it.message ?: "Unknown error")
                        }
                    .collect {
                        _forecast.emit(Response.Success(it))
                    }
            } catch (e: Exception) {
                _messageState.emit(e.message ?: "Unknown error")
            }
        }
    }
}
