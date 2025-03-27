package com.example.thundora.view.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.model.repositary.Repository
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(
    private val client: PlacesClient,
    private val repo: Repository
) : ViewModel() {

    private val _locationState = MutableStateFlow<Response<GeocodingResponseItem>>(Response.Loading)
    val locationState: StateFlow<Response<GeocodingResponseItem>> = _locationState

    private val _latitude = MutableStateFlow(repo.fetchData(SharedKeys.LAT.toString(), "0.0").toDouble())
    val latitude: StateFlow<Double> = _latitude

    private val _longitude = MutableStateFlow(repo.fetchData(SharedKeys.LON.toString(), "0.0").toDouble())
    val longitude: StateFlow<Double> = _longitude

    fun getCityLocation(city: String) {
        Log.d("MapScreen", "🔥 getCityLocation() CALLED with city: $city") // <---- Add this

        if (city.isEmpty()) return
        viewModelScope.launch {
            Log.d("MapScreen", "Fetching location for: $city")
            try {
                repo.getCoordinates(city)
                    .catch {
                        Log.e("MapScreen", "Error fetching coordinates: ${it.message}")
                        _locationState.emit(Response.Error(it.message ?: "Unknown error")) }
                    .collect { result ->
                        Log.d("MapScreen", "Coordinates received: $result")

                        _locationState.emit(Response.Success(result.first()))
                    }
            } catch (e: Exception) {
                Log.e("MapScreen", "Exception in getCityLocation: ${e.message}")

                _locationState.emit(Response.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun addFavoriteCity(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                repo.getWeather(lat, lon, "metric", "en")
                    .catch {

                    }
                    .collect { weather ->
                        repo.addWeather(weather)
                    }
            } catch (e: Exception) {

            }

        }
    }

    suspend fun getAddressPredictions(
        sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance(),
        inputString: String
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










//class MapViewModel(private val client: PlacesClient, private val repo: Repository) : ViewModel() {
//
//    private val _locationState = MutableStateFlow<Response<GeocodingResponseItem>>(Response.Loading)
//    val locationFlow = _locationState.asStateFlow()
//    private val _error = MutableStateFlow<String>("")
//
//    fun getCityLocation(city: String) {
//        viewModelScope.launch {
//            try {
//                repo.getCoordinates(city)
//                    .catch {
//                        _error.emit(it.message ?: "Unknown error")
//
//                    }
//                    .collect {
//                        _locationState.emit(Response.Success(it[0]))
//                    }
//            } catch (e: Exception) {
//                _error.emit(e.message ?: "Unknown error")
//
//            }
//        }
//
//    }
//
//    fun addFavoriteCity( lat: Double, lon: Double) {
//        viewModelScope.launch {
//            try {
//                repo.getWeather(lat, lon, "metric", "en").catch {
//                    _error.emit(it.message ?: "Unknown error")
//                }.collect { weather ->
//                    repo.addWeather(weather)
//                }
//            } catch (e: Exception) {
//                Log.e("MapViewModel", "Error adding favorite: ${e.message}")
//            }
//        }
//    }
//
//    suspend fun getAddressPredictions(
//        sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance(),
//        inputString: String,
//    ): List<AutocompletePrediction> = suspendCoroutine { continuation ->
//
//        val request = FindAutocompletePredictionsRequest.builder()
//            .setTypesFilter(listOf(PlaceTypes.CITIES))
//            .setSessionToken(sessionToken)
//            .setQuery(inputString)
//            .build()
//
//        client.findAutocompletePredictions(request)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful && task.result != null) {
//                    continuation.resume(task.result.autocompletePredictions)
//                } else {
//                    continuation.resume(emptyList())
//                }
//            }
//    }
//}



