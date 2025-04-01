package com.example.thundora.view.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.domain.model.view.SharedKeys
import com.example.thundora.data.repositary.RepositoryImpl
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapViewModel(
    private val client: PlacesClient,
    private val repo: RepositoryImpl
) : ViewModel(),IMapViewModel {
    private val _markerState = MutableStateFlow(MarkerState(LatLng(0.0, 0.0)))
    override val markerState: StateFlow<MarkerState> = _markerState

    private val _homeLocation = MutableStateFlow(LatLng(0.0, 0.0))

    init {
        loadHomeLocation()
    }

    private fun loadHomeLocation() {
        val lat = repo.fetchData(SharedKeys.HOME_LAT.toString(), "33.0").toDouble()
        val lon = repo.fetchData(SharedKeys.HOME_LON.toString(), "35.0").toDouble()
        _homeLocation.value = LatLng(lat, lon)
        _markerState.value = MarkerState(LatLng(lat, lon))
    }

    override fun updateMarkerPosition(latLng: LatLng) {
        _markerState.value = MarkerState(latLng)
    }

    override fun setHomeLocation(latLng: LatLng) {
        _homeLocation.value = latLng
        repo.saveData(SharedKeys.HOME_LAT.toString(), latLng.latitude.toString())
        repo.saveData(SharedKeys.HOME_LON.toString(), latLng.longitude.toString())
        _markerState.value = MarkerState(latLng)
    }

    override suspend fun getAddressPredictions(
        inputString: String
    ): List<AutocompletePrediction> = suspendCoroutine { continuation ->
        val sessionToken = AutocompleteSessionToken.newInstance()
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

    override fun getPlaceDetails(placeId: String, onResult: (LatLng?) -> Unit) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        client.fetchPlace(request).addOnSuccessListener { response ->
            response.place.latLng?.let { latLng ->
                updateMarkerPosition(latLng)
                onResult(latLng)
            } ?: onResult(null)
        }.addOnFailureListener {
            onResult(null)
        }
    }

    override fun addFavoriteCity(lat: Double, lon: Double) {
        viewModelScope.launch {
            repo.getWeather(lat, lon, "metric", "en")
                .catch { e -> Log.e("MapViewModel", "Error adding favorite", e) }
                .collect { weather ->
                    repo.addWeather(weather)

                }
        }
    }
}
