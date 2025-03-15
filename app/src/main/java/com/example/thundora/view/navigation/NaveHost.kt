package com.example.thundora.view.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thundora.view.HomeScreen
import com.example.thundora.view.Splach
import com.example.thundora.view.navigation.ScreansRout.Splash


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost() {
    var navConroller = rememberNavController()
    NavHost(
        navController = navConroller,
        startDestination = Splash
    ) {
        composable<Splash>() {
            Splach {
                navConroller.navigate(ScreansRout.Home)
            }
        }
        composable<ScreansRout.Home>() {
            HomeScreen()
        }

    }


}
