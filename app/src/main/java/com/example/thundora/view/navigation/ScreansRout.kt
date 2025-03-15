package com.example.thundora.view.navigation

import kotlinx.serialization.Serializable

sealed class ScreansRout{
    @Serializable
    object Splash :ScreansRout()
    @Serializable
    object Home : ScreansRout()
}