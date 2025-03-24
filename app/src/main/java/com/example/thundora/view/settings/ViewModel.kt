package com.example.thundora.view.settings

import androidx.lifecycle.ViewModel
import com.example.thundora.model.repositary.Repository

class SettingViewModel(private val repository: Repository) : ViewModel() {
     fun <T> saveData(key: String, value: T) {
         repository.saveData(key,value)
    }
    fun <T> fetchData(key: String, defaultValue: T): T {
        return repository.fetchData(key,defaultValue)
    }


}
