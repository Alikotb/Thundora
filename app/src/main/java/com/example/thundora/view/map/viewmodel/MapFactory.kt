package com.example.thundora.view.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundora.data.repositary.RepositoryImpl
import com.google.android.libraries.places.api.net.PlacesClient

@Suppress("UNCHECKED_CAST")
class MapFactory(val client: PlacesClient, val repo: RepositoryImpl) :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(client ,repo) as T
    }
}