package com.example.thundora.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.utils.getArabicTemperatureDisplayUnit
import com.example.thundora.model.utils.getArabicWindUnit
import com.example.thundora.model.utils.getTemperatureDisplayUnit
import com.example.thundora.model.utils.getWindDisplayUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: Repository) : ViewModel() {
    private val _selectedLanguage =
        MutableStateFlow(repository.fetchData(SharedKeys.LANGUAGE.toString(), "English"))
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _selectedTempUnit =
        if (_selectedLanguage.value == "English")
            MutableStateFlow(
                getTemperatureDisplayUnit(
                    repository.fetchData(
                        SharedKeys.DEGREE.toString(),
                        "Celsius"
                    )
                )
            )
        else
            MutableStateFlow(
                getArabicTemperatureDisplayUnit(
                    repository.fetchData(
                        SharedKeys.DEGREE.toString(),
                        "Celsius"
                    )
                )
            )
    val selectedTempUnit: StateFlow<String> = _selectedTempUnit

    private val _selectedLocation =
        if (_selectedLanguage.value == "English")
            MutableStateFlow(repository.fetchData(SharedKeys.LOCATION.toString(), "GPS"))
        else
             MutableStateFlow(repository.fetchData(SharedKeys.LOCATION.toString(), "جي بي اس"))
    val selectedLocation: StateFlow<String> = _selectedLocation

    private val _selectedWindSpeed =
        if (_selectedLanguage.value == "English")
            MutableStateFlow(
                getWindDisplayUnit(
                    repository.fetchData(
                        SharedKeys.SPEED_UNIT.toString(),
                        "m/s"
                    )
                )
            )
        else
            MutableStateFlow(
                getArabicWindUnit(
                    repository.fetchData(
                        SharedKeys.SPEED_UNIT.toString(),
                        "m/s"
                    )
                )
            )

    val selectedWindSpeed: StateFlow<String> = _selectedWindSpeed

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        repository.saveData(SharedKeys.LANGUAGE.toString(), language)
    }

    fun setTempUnit(unit: String) {
        if (_selectedLanguage.value == "English")
            _selectedTempUnit.value = getTemperatureDisplayUnit(unit)
        else
            _selectedTempUnit.value = getArabicTemperatureDisplayUnit(unit)
        viewModelScope.launch {
            repository.saveData(SharedKeys.DEGREE.toString(), unit)
        }
    }


    fun setLocationMode(mode: String) {
        viewModelScope.launch {
            _selectedLocation.value = mode
            repository.saveData(SharedKeys.LOCATION.toString(), mode)
        }
    }

    fun setWindSpeed(unit: String) {
        viewModelScope.launch {
            if (_selectedLanguage.value == "English")
                _selectedWindSpeed.value = getWindDisplayUnit(unit)
            else
                _selectedWindSpeed.value = getArabicWindUnit(unit)
            repository.saveData(SharedKeys.SPEED_UNIT.toString(), unit)
        }
    }

    fun setLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.saveData(SharedKeys.LAT.toString(), latitude.toString())
            repository.saveData(SharedKeys.LON.toString(), longitude.toString())
        }
    }

    fun setUnit(temp: String, wind: String) {
        setTempUnit(temp)
        setWindSpeed(wind)
    }

}
