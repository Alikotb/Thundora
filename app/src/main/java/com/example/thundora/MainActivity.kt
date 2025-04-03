package com.example.thundora

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.domain.model.LanguagesEnum
import com.example.thundora.domain.model.view.BottomNAvigationBar
import com.example.thundora.domain.model.view.ScreensRout
import com.example.thundora.domain.model.view.SharedKeys
import com.example.thundora.services.AlarmReceiver
import com.example.thundora.ui.theme.DeepBlue
import com.example.thundora.utils.ConnectivityObserver
import com.example.thundora.utils.GPSLocation
import com.example.thundora.utils.GPSLocation.getLocation
import com.example.thundora.utils.GPSLocation.isLocationEnabled
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
    private lateinit var connectivityObserver: ConnectivityObserver
    val gpsLocation = GPSLocation
    val sharedPref = SharedPreference.getInstance()
    val context = this

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlarmReceiver.stopAlarmSound()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        val lang =
            sharedPref.fetchData(SharedKeys.LANGUAGE.toString(), LanguagesEnum.DEFAULT.code)
        if(lang== LanguagesEnum.DEFAULT.code)
        {
            applyLanguage(this.resources.configuration.locales[0].language)
        }else {
            applyLanguage(lang)
        }
        connectivityObserver = ConnectivityObserver(applicationContext)
        enableEdgeToEdge()
        setLightStatusBar(true)
        setContent {
            navController = rememberNavController()
            val isOnline by connectivityObserver.isOnline.observeAsState(initial = true)
            DisposableEffect(connectivityObserver) {
                onDispose { connectivityObserver.unregister() }
            }

            val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }

            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationState = remember { mutableStateOf(Location("")) }
            flag = remember { mutableStateOf(true) }
            floatingFlag = remember { mutableStateOf(false) }

            val fabIcon = remember { mutableStateOf(Icons.Default.Favorite) }
            val fabAction = remember { mutableStateOf({}) }



            MainScreen(
                flag,
                fabIcon,
                fabAction,
                currentBackStackEntry,
                selectedNavigationIndex,
                isOnline
            )
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


        if (sharedPref.fetchData(
                SharedKeys.LOCATION.toString(),
                context.getString(R.string.gps)
            ) == context.getString(R.string.gps)
        ) {
            if (gpsLocation.checkPermission(context)) {
                if (!isLocationEnabled(this)) {

                    gpsLocation.enableLocationService(context)
                } else {
                    lifecycleScope.launch {
                        try {
                            val location = getLocation(context)
                            location?.let {
                                locationState.value = it
                                sharedPref.saveData(
                                    SharedKeys.LAT.toString(),
                                    it.latitude.toString()
                                )
                                sharedPref.saveData(
                                    SharedKeys.LON.toString(),
                                    it.longitude.toString()
                                )
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val context = this
                lifecycleScope.launch {
                    try {
                        val location = getLocation(context)
                        location?.let {
                            sharedPref.saveData(
                                SharedKeys.HOME_LAT.toString(),
                                it.latitude.toString()
                            )
                            sharedPref.saveData(
                                SharedKeys.HOME_LON.toString(),
                                it.longitude.toString()
                            )
                        } ?: run {
                            Toast.makeText(context, "Unable to get location!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Composable
    fun BottomNavigationBar(
        navController: NavController,
        currentBackStackEntry: NavBackStackEntry?,
        selectedNavigationIndex: MutableIntState
    ) {
        val navigationItems = listOf(
            BottomNAvigationBar(
                ScreensRout.Home,
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

        LaunchedEffect(currentBackStackEntry) {
            val currentRoute = currentBackStackEntry?.destination?.route?.substringAfterLast(".")
            val matchedScreen = navigationItems.firstOrNull {
                it.title::class.simpleName == currentRoute
            }
            matchedScreen?.let {
                selectedNavigationIndex.intValue = navigationItems.indexOf(it)
            }
        }

        NavigationBar(containerColor = DeepBlue) {
            navigationItems.forEachIndexed { index, item ->
                val isSelected = selectedNavigationIndex.intValue == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (selectedNavigationIndex.intValue != index) {
                            selectedNavigationIndex.intValue = index

                            if (item.title == ScreensRout.Home) {
                                navController.popBackStack(item.title, inclusive = false)
                            } else {
                                navController.navigate(item.title) {
                                    popUpTo(ScreensRout.Home) { inclusive = false }
                                    launchSingleTop = true
                                }
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
    fun MainScreen(
        flag: MutableState<Boolean>,
        fabIcon: MutableState<ImageVector>,
        fabAction: MutableState<() -> Unit>,
        currentBackStackEntry: NavBackStackEntry?,
        selectedNavigationIndex: MutableIntState,
        isOnline: Boolean

    ) {
        Scaffold(

            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (flag.value) {
                    BottomNavigationBar(
                        navController,
                        currentBackStackEntry,
                        selectedNavigationIndex
                    )
                }
            },

            floatingActionButton = {
                if (floatingFlag.value) {
                    FloatingActionButton(
                        onClick = fabAction.value,
                        containerColor = colorResource(R.color.blue_1200),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = fabIcon.value,
                            contentDescription = "Floating Button",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { innerPadding ->

            Column {
                if (!isOnline) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(R.color.deep_blue))
                            .padding(top = innerPadding.calculateTopPadding())
                            .background(Color.Red)
                    ) {
                        Text(
                            text = "No internet connection",
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                SetUpNavHost(navController = navController, flag, floatingFlag, fabIcon, fabAction)
            }

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

    override fun onDestroy() {
        super.onDestroy()
        // connectivityObserver.unregister()

    }

}

