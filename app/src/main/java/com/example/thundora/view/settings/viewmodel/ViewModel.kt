package com.example.thundora.view.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.domain.model.view.SharedKeys
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.domain.model.LanguagesEnum
import com.example.thundora.utils.getArabicTemperatureDisplayUnit
import com.example.thundora.utils.getArabicWindUnit
import com.example.thundora.utils.getTemperatureDisplayUnit
import com.example.thundora.utils.getWindDisplayUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsViewModel(private val repository: RepositoryImpl) : ViewModel() {
    private val _selectedLanguage =
        MutableStateFlow(LanguagesEnum.getValue(repository.fetchData(SharedKeys.LANGUAGE.toString(), LanguagesEnum.ENGLISH.code)))
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _selectedTempUnit =
        if (Locale.getDefault().language == LanguagesEnum.ENGLISH.code)
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
        if (Locale.getDefault().language == LanguagesEnum.ENGLISH.code)
            MutableStateFlow(repository.fetchData(SharedKeys.LOCATION.toString(), "GPS"))
        else
             MutableStateFlow(repository.fetchData(SharedKeys.LOCATION.toString(), "جي بي اس"))
    val selectedLocation: StateFlow<String> = _selectedLocation

    private val _selectedWindSpeed =
        if (Locale.getDefault().language == LanguagesEnum.ENGLISH.code)
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
        _selectedLanguage.value = LanguagesEnum.getValue(language)
        repository.saveData(SharedKeys.LANGUAGE.toString(), LanguagesEnum.geCodeByValue(language))
    }

    fun setTempUnit(unit: String) {
        if (Locale.getDefault().language == LanguagesEnum.ENGLISH.code)
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
            if (Locale.getDefault().language == LanguagesEnum.ENGLISH.code)
                _selectedWindSpeed.value = getWindDisplayUnit(unit)
            else
                _selectedWindSpeed.value = getArabicWindUnit(unit)
            repository.saveData(SharedKeys.SPEED_UNIT.toString(), unit)
        }
    }

    fun setLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.saveData(SharedKeys.HOME_LAT.toString(), latitude.toString())
            repository.saveData(SharedKeys.HOME_LON.toString(), longitude.toString())
        }
    }

    fun setUnit(temp: String, wind: String) {
        setTempUnit(temp)
        setWindSpeed(wind)
    }

}
