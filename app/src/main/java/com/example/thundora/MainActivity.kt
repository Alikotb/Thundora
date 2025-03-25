package com.example.thundora

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.pojos.view.BottomNAvigationBar
import com.example.thundora.model.pojos.view.ScreensRout
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
import com.example.thundora.ui.theme.DeepBlue
import com.example.thundora.view.map.GPSLocation
import com.example.thundora.view.navigation.SetUpNavHost
import com.example.thundora.view.settings.SettingViewModel
import com.example.thundora.view.settings.SettingsFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

const val LOCATION_CODE = 27

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationState: MutableState<Location>
    lateinit var flag :MutableState<Boolean>
    lateinit var floatingFlag: MutableState<Boolean>
    val gpsLocation = GPSLocation

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val repository = Repository.getInstance(
            RemoteDataSource(ApiClient.weatherService),
            LocalDataSource(
                WeatherDataBase.getInstance(applicationContext).getForecastDao(),
                SharedPreference.getInstance()
            )
        )
        val settingViewModel: SettingViewModel = ViewModelProvider(this, SettingsFactory(repository))[SettingViewModel::class.java]
        applyLanguage(
            when ( settingViewModel.fetchData(SharedKeys.LANGUAGE.toString(), Locale.getDefault().language)) {
                "english", "الإنجليزية", "en" -> "en"
                "arabic", "العربية", "ar" -> "ar"
                else -> "en"
            }
        )

        enableEdgeToEdge()
        setLightStatusBar(true)
        setContent {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationState = remember { mutableStateOf(Location("")) }
            flag = remember { mutableStateOf(true) }
            floatingFlag = remember { mutableStateOf(false) }
            navController = rememberNavController()
            Log.i("al", "onCreate: ${locationState.value.longitude}")

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


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                gpsLocation.getLocation(locationState,fusedLocationClient, Looper.getMainLooper())
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
                if (flag.value)
                {
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
            SetUpNavHost(navController = navController,flag,floatingFlag)

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

    fun gpsLocation(){
        if (gpsLocation.checkPermission(this)) {
            if (!gpsLocation.isLocationEnabled(this)) {
                gpsLocation.enableLocationService(this)
            } else {
                gpsLocation.getLocation(locationState = locationState,fusedLocationClient, Looper.getMainLooper())
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

