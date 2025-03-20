package com.example.thundora.view.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundora.model.repositary.Repository

class MapFactory(val context: Context,val repo: Repository) :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(context ,repo) as T
    }
}