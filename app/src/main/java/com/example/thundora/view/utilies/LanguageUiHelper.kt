package com.example.thundora.view.utilies

import android.content.Context
import java.util.Locale
import androidx.core.content.edit

fun saveLanguage(context: Context, languageCode: String) {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit() { putString("app_language", languageCode) }
}

fun getSavedLanguage(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return sharedPreferences.getString("app_language", Locale.getDefault().language) ?: "en"
}