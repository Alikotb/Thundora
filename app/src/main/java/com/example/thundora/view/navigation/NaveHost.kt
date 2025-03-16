package com.example.thundora.view.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thundora.model.pojos.view.ScreensRout
import com.example.thundora.view.alarm.AlarmScreen
import com.example.thundora.view.favorite.FavoriteScreen
import com.example.thundora.view.home.HomeScreen
import com.example.thundora.view.settings.SettingScreen
import com.example.thundora.view.splash.Splash


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(navController: NavHostController, splashFlag: Boolean, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination =
            if (splashFlag)  ScreensRout.Home.route else ScreensRout.Splash.route ,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(ScreensRout.Splash.route) { Splash () }
        composable(ScreensRout.Home.route) { HomeScreen() }
        composable(ScreensRout.Alarm.route) { AlarmScreen() }
        composable(ScreensRout.Favorite.route) { FavoriteScreen() }
        composable(ScreensRout.Setting.route) { SettingScreen() }
    }

}
