package com.example.thundora.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundora.data.repositary.RepositoryImpl

@Suppress("UNCHECKED_CAST")
class SettingsFactory(private val repo: RepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repo) as T
    }
}