package com.example.thundora.view.alarm.alarmviewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.services.AlarmScheduler


@Suppress("UNCHECKED_CAST")
class AlarmFactory(private val repo: RepositoryImpl, private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val alarmScheduler = AlarmScheduler(context)
        return AlarmViewModel(repo, alarmScheduler) as T
    }
}