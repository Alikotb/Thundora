package com.example.thundora.model.pojos.view

sealed class ScreensRout(val route: String) {
    object Splash : ScreensRout("splash")
    object Home : ScreensRout("home")
    object Alarm : ScreensRout("alarm")
    object Favorite : ScreensRout("favorite")
    object Setting : ScreensRout("setting")
    object Map : ScreensRout("map")

}