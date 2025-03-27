package com.example.thundora

import android.app.Application
import com.example.thundora.model.sharedpreference.SharedPreference
import com.google.android.libraries.places.api.Places

class ThunderApp : Application() {
    companion object {
        lateinit var instance: ThunderApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.googleApiKey)
        }

        SharedPreference.initSharedPreferences(this)
    }
}
