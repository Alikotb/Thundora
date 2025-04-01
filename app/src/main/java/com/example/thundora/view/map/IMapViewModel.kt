package com.example.thundora.view.map

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.StateFlow

interface IMapViewModel {
    val markerState: StateFlow<MarkerState>

    fun updateMarkerPosition(latLng: LatLng)
    fun setHomeLocation(latLng: LatLng)
    suspend fun getAddressPredictions(inputString: String): List<AutocompletePrediction>
    fun getPlaceDetails(placeId: String, onResult: (LatLng?) -> Unit)
    fun addFavoriteCity(lat: Double, lon: Double)
}