package com.example.thundora

import android.app.Application
import com.example.thundora.model.sharedpreference.SharedPreference
import com.google.android.libraries.places.api.Places

class ThundoraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.WEATHER_API_KEY)
        }
        SharedPreference.initSharedPreferences(this)

    }
}