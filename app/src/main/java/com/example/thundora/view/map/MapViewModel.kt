package com.example.thundora.view.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.repositary.Repository
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(private val client: PlacesClient, private val repo: Repository) : ViewModel() {

    private val _locationState = MutableStateFlow<Response<GeocodingResponseItem>>(Response.Loading)
    val locationFlow = _locationState.asStateFlow()
    private val _error = MutableStateFlow<String>("")

    fun getCityLocation(city: String) {
        viewModelScope.launch {
            try {
                repo.getCoordinates(city)
                    .catch {
                        _error.emit(it.message ?: "Unknown error")

                    }
                    .collect {
                        _locationState.emit(Response.Success(it[0]))
                    }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Unknown error")

            }
        }

    }

    fun addFavoriteCity( lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                repo.getWeather(lat, lon, "metric", "en").catch {
                    _error.emit(it.message ?: "Unknown error")
                }.collect { weather ->
                    repo.addWeather(weather)
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error adding favorite: ${e.message}")
            }
        }
    }

    suspend fun getAddressPredictions(
        sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance(),
        inputString: String,
    ): List<AutocompletePrediction> = suspendCoroutine { continuation ->

        val request = FindAutocompletePredictionsRequest.builder()
            .setTypesFilter(listOf(PlaceTypes.CITIES))
            .setSessionToken(sessionToken)
            .setQuery(inputString)
            .build()

        client.findAutocompletePredictions(request)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    continuation.resume(task.result.autocompletePredictions)
                } else {
                    continuation.resume(emptyList())
                }
            }
    }
}



