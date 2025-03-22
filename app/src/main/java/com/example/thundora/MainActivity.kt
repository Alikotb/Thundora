package com.example.thundora

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.thundora.model.pojos.view.BottomNAvigationBar
import com.example.thundora.model.pojos.view.ScreensRout
import com.example.thundora.ui.theme.DeepBlue
import com.example.thundora.view.navigation.SetUpNavHost

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    lateinit var flag :MutableState<Boolean>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setLightStatusBar(true)
        setContent {
            flag = remember { mutableStateOf(true) }
            navController = rememberNavController()
           // var splashFlag by remember { mutableStateOf(false) }


            MainScreen(flag)
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
        val navigationItems = listOf(
            BottomNAvigationBar(ScreensRout.Home(0.0,0.0),"Home", Icons.Filled.Home, Icons.Outlined.Home),
            BottomNAvigationBar(ScreensRout.Alarm,"Alarm", Icons.Filled.Notifications, Icons.Outlined.Notifications),
            BottomNAvigationBar(ScreensRout.Favorite, "Favorite",Icons.Filled.Favorite, Icons.Outlined.Favorite),
            BottomNAvigationBar(ScreensRout.Settings,"Setting", Icons.Filled.Settings, Icons.Outlined.Settings)

        )

        NavigationBar(containerColor = DeepBlue) {
            navigationItems.forEachIndexed { index, item ->
                val isSelected = selectedNavigationIndex.intValue == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (selectedNavigationIndex.intValue != index) {
                            selectedNavigationIndex.intValue = index
                            navController.navigate(item.title) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else colorResource(R.color.blue_200)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = if (isSelected) Color.White else colorResource(R.color.blue_200),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        indicatorColor = colorResource(R.color.blue_accent),
                        unselectedIconColor = colorResource(R.color.blue_200)
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MainScreen(flag: MutableState<Boolean>) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (flag.value)
                {
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->

            SetUpNavHost(navController = navController,flag)

        }
    }

    fun setLightStatusBar(isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.let { window ->
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    !isLight
            }
        } else {
            @Suppress("DEPRECATION")
            window?.decorView?.systemUiVisibility = if (isLight) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }
}
