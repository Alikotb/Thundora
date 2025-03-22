package com.example.thundora.view.navigation

import android.annotation.SuppressLint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thundora.view.map.MapScreen
import com.example.thundora.view.alarm.AlarmScreen
import com.example.thundora.view.favorite.FavoriteScreen
import com.example.thundora.view.home.HomeScreen
import com.example.thundora.view.settings.SettingScreen
import com.example.thundora.view.splash.Splash
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.example.thundora.model.pojos.view.ScreensRout

@SuppressLint("ComposableDestinationInComposeScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(navController: NavHostController, flag: MutableState<Boolean>) {
    NavHost(
        navController = navController,
        startDestination =  ScreensRout.Splash
    ) {
        composable<ScreensRout.Splash>() {
            Splash(flag){
                navController.navigate(ScreensRout.Home(0.0,0.0))
            }
        }
        composable<ScreensRout.Home>{ backStackEntry ->
            HomeScreen(flag) { newLat, newLon ->
                navController.navigate(ScreensRout.Map)
            }
        }

        composable<ScreensRout.Alarm>() { AlarmScreen() }

        composable<ScreensRout.Favorite>() { FavoriteScreen() }

        composable<ScreensRout.Settings>() { SettingScreen() }

        composable<ScreensRout.Map> { backStackEntry ->
            MapScreen { selectedLat, selectedLon ->
                navController.navigate(ScreensRout.Home(selectedLat,selectedLon)) {
                    popUpTo("map") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}

