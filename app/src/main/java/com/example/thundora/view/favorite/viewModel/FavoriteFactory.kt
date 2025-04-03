package com.example.thundora.view.favorite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundora.data.repositary.RepositoryImpl

@Suppress("UNCHECKED_CAST")
class FavoriteFactory(private val repo: RepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModel(repo) as T
    }
}