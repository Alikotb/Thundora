package com.example.thundora.view.map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

import com.google.android.libraries.places.api.Places

class MapViewModel(context: Context): ViewModel()  {
    private val _isCelsius = MutableLiveData<Boolean>()
    val isCelsius: LiveData<Boolean> = _isCelsius
    private val client = Places.createClient(context) // No need to initialize again

    init {
        Places.initialize(context, "AIzaSyCaj10hgcwGaosoYRyv79ppLviFJ9eMNmM")
    }

    suspend fun getAddressPredictions(
        sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance(),
        inputString: String,
        location: LatLng? = null
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