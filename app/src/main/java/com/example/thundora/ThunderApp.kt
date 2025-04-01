package com.example.thundora

import android.app.Application
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class ThunderApp : Application() {
    companion object {
        lateinit var instance: ThunderApp
            private set
        lateinit var placesClient: PlacesClient
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.googleApiKey)
        }
        placesClient = Places.createClient(this)

        SharedPreference.initSharedPreferences(this)
    }
}
