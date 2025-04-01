package com.example.thundora.view.navigation

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.thundora.R
import com.example.thundora.domain.model.view.ScreensRout
import com.example.thundora.domain.model.view.SharedKeys
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.utils.isInternetAvailable
import com.example.thundora.view.alarm.AlarmScreen
import com.example.thundora.view.favorite.DetailsScreen
import com.example.thundora.view.favorite.FavoriteScreen
import com.example.thundora.view.home.HomeScreen
import com.example.thundora.view.map.MapScreen
import com.example.thundora.view.settings.SettingScreen
import com.example.thundora.view.splash.Splash

@SuppressLint("ComposableDestinationInComposeScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(
    navController: NavHostController,
    flag: MutableState<Boolean>,
    floatingFlag: MutableState<Boolean>,
    fabIcon: MutableState<ImageVector>,
    fabAction: MutableState<() -> Unit>
) {
    val ctx = navController.context
    NavHost(
        navController = navController,
        startDestination = if (SharedPreference.getInstance()
                .fetchData(SharedKeys.RESTARTED_FLAG.toString(), false)
        ) {
            ScreensRout.Home
        } else {
            ScreensRout.Splash
        }

    ) {
        composable<ScreensRout.Splash> {
            Splash(flag) {
                SharedPreference.getInstance().saveData(SharedKeys.RESTARTED_FLAG.toString(), true)
                navController.popBackStack()
                navController.navigate(ScreensRout.Home)
            }
        }

        composable<ScreensRout.Home> {
            HomeScreen(flag, floatingFlag) {
//                if (isInternetAvailable()) {
                    navController.navigate(ScreensRout.Map)

            }
        }

        composable<ScreensRout.Alarm> {
            AlarmScreen(floatingFlag, fabIcon, fabAction)
        }
        composable<ScreensRout.Favorite> {
            floatingFlag.value = true
            flag.value = true
            fabIcon.value = Icons.Default.Favorite
            fabAction.value = {
                if (isInternetAvailable()) {
                    navController.navigate(ScreensRout.Map)
                } else {
                    Toast.makeText(
                        ctx,
                        ctx.getString(R.string.no_internet_connect_to_network_please),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            FavoriteScreen { city, lang, lat ->
                navController.navigate(ScreensRout.Details(city, lang, lat))
            }
        }

        composable<ScreensRout.Settings> {
            floatingFlag.value = false
            fabIcon.value = Icons.Default.Favorite
            fabAction.value = { navController.navigate(ScreensRout.Map) }
            SettingScreen(floatingFlag) {
                navController.navigate(ScreensRout.Map)
            }
        }

        composable<ScreensRout.Map> {
            flag.value = false
            floatingFlag.value = false
            fabIcon.value = Icons.Default.Favorite
            fabAction.value = { }
            MapScreen(
                floatingFlag = floatingFlag,
                navToHome = {
                    navController.navigate(ScreensRout.Home)
                },
                navToFavorite = {
                    navController.navigateUp()
                }
            )
        }

        composable<ScreensRout.Details> {
            val details = it.toRoute<ScreensRout.Details>()
            floatingFlag.value = false
            DetailsScreen(floatingFlag, details.city, details.lat, details.lang)
        }
    }
}
