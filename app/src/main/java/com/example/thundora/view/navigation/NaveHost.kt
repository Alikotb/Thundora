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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument

@SuppressLint("ComposableDestinationInComposeScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(navController: NavHostController, splashFlag: Boolean) {
    NavHost(
        navController = navController,
        startDestination = if (splashFlag) "home/0.0/0.0" else "splash"
    ) {
        composable("splash") {
            Splash()
        }
        composable(
            route = "home/{lat}/{lon}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0

            HomeScreen(lat, lon) { newLat, newLon ->
                navController.navigate("map/$newLat/$newLon")
            }
        }

        composable("alarm") { AlarmScreen() }

        composable("favorite") { FavoriteScreen() }

        composable("settings") { SettingScreen() }

        composable(
            route = "map/{lat}/{lon}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0

            MapScreen(latitude = lat, longitude = lon) { selectedLat, selectedLon ->
                navController.navigate("home/$selectedLat/$selectedLon") {
                    popUpTo("map") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}
