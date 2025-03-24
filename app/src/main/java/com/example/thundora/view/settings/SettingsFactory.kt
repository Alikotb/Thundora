package com.example.thundora.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundora.model.repositary.Repository

class SettingsFactory( private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(repo) as T
    }
}