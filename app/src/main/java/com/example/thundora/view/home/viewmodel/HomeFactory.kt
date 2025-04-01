package com.example.thundora.view.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundora.data.repositary.RepositoryImpl

@Suppress("UNCHECKED_CAST")
class HomeFactory(private val repo: RepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}