package com.example.thundora.view.utilies

import android.content.Context
import android.net.ConnectivityManager
import com.example.thundora.ThunderApp


fun isInternetAvailable(): Boolean {
    val connectivityManager =
        ThunderApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}