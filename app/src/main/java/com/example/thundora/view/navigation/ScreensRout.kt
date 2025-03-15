package com.example.thundora.view.navigation

sealed class ScreensRout(val route: String) {
    object Splash : ScreensRout("splash")
    object Home : ScreensRout("home")
    object Alarm : ScreensRout("alarm")
    object Favorite : ScreensRout("favorite")
    object Setting : ScreensRout("setting")
}
