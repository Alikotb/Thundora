package com.example.thundora

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.thundora.model.pojos.view.BottomNAvigationBar
import com.example.thundora.model.pojos.view.ScreensRout
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.model.sharedpreference.SharedPreference
import com.example.thundora.model.utils.getLanguage
import com.example.thundora.ui.theme.DeepBlue
import com.example.thundora.view.map.GPSLocation
import com.example.thundora.view.map.GPSLocation.getLocation
import com.example.thundora.view.navigation.SetUpNavHost
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Locale

const val LOCATION_CODE = 27

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationState: MutableState<Location>
    lateinit var flag: MutableState<Boolean>
    lateinit var floatingFlag: MutableState<Boolean>
    val gpsLocation = GPSLocation
    val sharedPref=SharedPreference.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lang=getLanguage(sharedPref.fetchData(
             SharedKeys.LANGUAGE.toString(),
            Locale.getDefault().language)
        )
        applyLanguage(lang)

        enableEdgeToEdge()
        setLightStatusBar(true)
        setContent {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationState = remember { mutableStateOf(Location("")) }
            flag = remember { mutableStateOf(true) }
            floatingFlag = remember { mutableStateOf(false) }
            navController = rememberNavController()
            MainScreen(flag)
        }
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    override fun onStart() {
        super.onStart()
        val context = this
        if(sharedPref.fetchData(SharedKeys.LOCATION.toString(),"GPS")=="GPS") {
            if (gpsLocation.checkPermission(context)) {
                if (!gpsLocation.isLocationEnabled(context)) {
                    gpsLocation.enableLocationService(context)
                } else {
                    lifecycleScope.launch {
                        try {
                            val location = getLocation(context)
                            location?.let {
                                locationState.value = it
                                sharedPref.saveData(SharedKeys.LAT.toString(),it.latitude.toString())
                                sharedPref.saveData(SharedKeys.LON.toString(),it.longitude.toString())

                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_CODE
                )
            }
        }
    }


    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
        val navigationItems = listOf(
            BottomNAvigationBar(
                ScreensRout.Home(0.0, 0.0),
                stringResource(R.string.home),
                Icons.Filled.Home,
                Icons.Outlined.Home
            ),
            BottomNAvigationBar(
                ScreensRout.Alarm,
                stringResource(R.string.alarm),
                Icons.Filled.Notifications,
                Icons.Outlined.Notifications
            ),
            BottomNAvigationBar(
                ScreensRout.Favorite,
                stringResource(R.string.favorite),
                Icons.Filled.Favorite,
                Icons.Outlined.Favorite
            ),
            BottomNAvigationBar(
                ScreensRout.Settings,
                stringResource(R.string.setting),
                Icons.Filled.Settings,
                Icons.Outlined.Settings
            )

        )

        NavigationBar(containerColor = DeepBlue) {
            navigationItems.forEachIndexed { index, item ->
                val isSelected = selectedNavigationIndex.intValue == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (selectedNavigationIndex.intValue != index) {
                            selectedNavigationIndex.intValue = index
                            navController.popBackStack()
                            navController.navigate(item.title)

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
                if (flag.value) {
                    BottomNavigationBar(navController)
                }
            },
            floatingActionButton = {
                if (floatingFlag.value) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(ScreensRout.Map)
                        },
                        containerColor = colorResource(R.color.blue_1200),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { innerPadding ->
            SetUpNavHost(navController = navController, flag, floatingFlag)

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

