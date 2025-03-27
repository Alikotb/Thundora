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
import androidx.navigation.toRoute
import com.example.thundora.model.pojos.view.ScreensRout
import com.example.thundora.view.favorite.DetailsScreen

@SuppressLint("ComposableDestinationInComposeScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(
    navController: NavHostController,
    flag: MutableState<Boolean>,
    floatingFlag: MutableState<Boolean>,
) {
    NavHost(
        navController = navController,
        startDestination =  ScreensRout.Splash
    ) {
        composable<ScreensRout.Splash>() {
            Splash(flag){
                navController.popBackStack()
                navController.navigate(ScreensRout.Home(0.0,0.0))
            }
        }
        composable<ScreensRout.Home>{ backStackEntry ->
            HomeScreen(flag,floatingFlag) { newLat, newLon ->
                navController.navigate(ScreensRout.Map)
            }
        }

        composable<ScreensRout.Alarm>() { AlarmScreen(floatingFlag) }

        composable<ScreensRout.Favorite>() {

            FavoriteScreen(floatingFlag){
                city,lang,lat->
                navController.navigate(ScreensRout.Details(city,lang,lat))
            }
        }
        composable<ScreensRout.Settings>() { SettingScreen(floatingFlag) }
        composable<ScreensRout.Map> {
            MapScreen(floatingFlag=floatingFlag, navToHome = { selectedLat, selectedLon ->
                navController.navigate(ScreensRout.Home(selectedLat,selectedLon)) {
                    popUpTo("map") { inclusive = true }
                    launchSingleTop = true
                }
            }, navToFavorite = {
                navController.navigate(ScreensRout.Favorite) {
                    popUpTo("map") { inclusive = true }
                    launchSingleTop = true
                }
            }
            )
        }
        composable<ScreensRout.Details> {
            val Lat= it.toRoute<ScreensRout.Details>().lat
            val lon =it.toRoute<ScreensRout.Details>().lang
            val city=it.toRoute<ScreensRout.Details>().city
            DetailsScreen(floatingFlag,city,Lat,lon)
        }
    }
}

