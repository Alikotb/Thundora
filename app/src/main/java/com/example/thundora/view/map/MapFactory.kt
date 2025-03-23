package com.example.thundora.view.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundora.model.repositary.Repository
import com.google.android.libraries.places.api.net.PlacesClient

@Suppress("UNCHECKED_CAST")
class MapFactory(val client: PlacesClient, val repo: Repository) :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(client ,repo) as T
    }
}