package com.example.thundora

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.thundora.model.pojos.BottomNAvigationBar
import com.example.thundora.view.navigation.SetUpNavHost
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navController = rememberNavController()
            var sho by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(Unit) {
                delay(3000L)
                sho = true
            }
            MainScreen(sho)
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
        val navigationItems = listOf(
            BottomNAvigationBar(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
            ),
            BottomNAvigationBar(
                title = "Alarm",
                selectedIcon = Icons.Filled.Notifications,
                unselectedIcon = Icons.Outlined.Notifications,
            ),
            BottomNAvigationBar(
                title = "Favorite",
                selectedIcon = Icons.Filled.Favorite,
                unselectedIcon = Icons.Outlined.Favorite,
            ),
            BottomNAvigationBar(
                title = "Setting",
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
            )
        )

        NavigationBar(containerColor = Color.White) {
            navigationItems.forEachIndexed { index, item ->
                val isSelected = selectedNavigationIndex.intValue == index
                NavigationBarItem(
                    selected = selectedNavigationIndex.intValue == index,
                    onClick = {
                        selectedNavigationIndex.intValue = index
                        navController.popBackStack()
                        navController.navigate(item.title)
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = if (isSelected) colorResource(R.color.blue_900) else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = if (isSelected) colorResource(R.color.blue_900) else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.blue_900),
                        indicatorColor = colorResource(R.color.blue_100),
                        unselectedIconColor = Color.Gray
                    )

                )

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MainScreen(sho: Boolean) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (sho)
                    BottomNavigationBar(navController)

            }
        ) { innerPadding ->
            SetUpNavHost(
                navController = navController,
                sho
            )
        }
    }
}

