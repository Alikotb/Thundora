package com.example.thundora.domain.model.view

import kotlinx.serialization.Serializable

sealed class ScreensRout {
    @Serializable
    object Splash : ScreensRout()
    @Serializable
    object Home : ScreensRout()
    @Serializable
    object Alarm : ScreensRout()
    @Serializable
    object Favorite : ScreensRout()
    @Serializable
    object Settings : ScreensRout()
    @Serializable
    object Map: ScreensRout()
    @Serializable
    data class Details(val city: String,val lang: Double, val lat:Double): ScreensRout()

}
