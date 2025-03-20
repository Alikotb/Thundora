package com.example.thundora.view.map

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundora.BuildConfig
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.repositary.Repository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch

class MapViewModel( context: Context,private val  repo: Repository): ViewModel()  {
    private val client = Places.createClient(context)
    private val locationLiveData = MutableLiveData<GeocodingResponseItem?>()
    val location: LiveData<GeocodingResponseItem?> = locationLiveData
    private val _error = MutableLiveData<String?>()

    init {
        Places.initialize(context, BuildConfig.GOOGLE_MAPS_API_KEY)
    }

    fun getCityLocation(city: String){
        viewModelScope.launch {
            try {
                locationLiveData.postValue(repo.getCoordinates(city)[0])
                Log.i("has", "getCityLocation:${repo.getCoordinates(city)[0].lon}")

            } catch (
                e: Exception
            ) {
                _error.postValue(e.message)
            }
        }

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



