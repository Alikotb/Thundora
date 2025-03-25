package com.example.thundora.view.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.BuildConfig
import com.example.thundora.R
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
import com.example.thundora.view.settings.SettingViewModel
import com.example.thundora.view.settings.SettingsFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(

    floatingFlag: MutableState<Boolean>,
    navToHome: (lat: Double, lon: Double) -> Unit,
    navToFavorite: () -> Unit
) {
    val setingViewModel: SettingViewModel = viewModel(
        factory = SettingsFactory(
            Repository.getInstance(
                RemoteDataSource(ApiClient.weatherService),
                LocalDataSource(
                    WeatherDataBase.getInstance(
                        LocalContext.current
                    ).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        )
    )

    floatingFlag.value = false
    val client = Places.createClient(LocalContext.current)
    val viewModel: MapViewModel =
        viewModel(
            factory = MapFactory(
                client,
                Repository.getInstance(
                    RemoteDataSource(ApiClient.weatherService),
                    LocalDataSource(
                        WeatherDataBase.getInstance(
                            LocalContext.current
                        ).getForecastDao(),
                        SharedPreference.getInstance()
                    )
                )
            )
        )

    val locationState = viewModel.locationFlow.collectAsStateWithLifecycle()
    val x = setingViewModel.fetchData(SharedKeys.LAT.toString(), "0.0").toDouble()
    val y = setingViewModel.fetchData(SharedKeys.LON.toString(), "0.0").toDouble()
    val markerState = remember { mutableStateOf(MarkerState(LatLng(x, y))) }
    Places.initialize(LocalContext.current, BuildConfig.googleApiKey)
    val selectedPrediction = remember { mutableStateOf<AutocompletePrediction?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.value.position, 15f)
    }
    viewModel.getCityLocation(selectedPrediction.value?.getPrimaryText(null).toString())
    when (locationState.value) {
        is Response.Error -> {
        }
        Response.Loading -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            ) {
                CircularProgressIndicator()
            }
        }
        is Response.Success<GeocodingResponseItem> -> {
            val data = (locationState.value as Response.Success<GeocodingResponseItem>).data
            if (data.lat != 53.3201094 && data.lon != -8.567809712252107) {
                markerState.value.position = LatLng(data.lat, data.lon)
            }
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(markerState.value.position, 15f)
            MapBransh(
                setingViewModel,
                navToHome = navToHome,
                navToFavorite = navToFavorite,
                viewModel = viewModel,
                (locationState.value as Response.Success).data,
                markerState,
                cameraPositionState,
                selectedPrediction
            )
        }
    }
}

@Composable
fun MapBransh(
    setingViewModel: SettingViewModel,
    navToHome: (Double, Double) -> Unit,
    navToFavorite: () -> Unit,
    viewModel: MapViewModel,
    locationState: GeocodingResponseItem,
    markerState: MutableState<MarkerState>,
    cameraPositionState: CameraPositionState,
    selectedPrediction: MutableState<AutocompletePrediction?>
) {
    val scope = rememberCoroutineScope()
    val text = remember { mutableStateOf("") }
    val predictionsState = remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    val isExpanded = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false
            ),
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(mapType = MapType.HYBRID, isMyLocationEnabled = false),
            cameraPositionState = cameraPositionState,
            onMapClick = {
                markerState.value = MarkerState(it)
            },
            onMapLongClick = {
                markerState.value = MarkerState(it)
            },

            ) {
            Marker(
                state = markerState.value,
                title = "One Marker"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                value = text.value,
                onValueChange = { query ->
                    text.value = query
                    isExpanded.value = true
                    scope.launch {
                        predictionsState.value =
                            viewModel.getAddressPredictions(inputString = query)
                    }
                },
                label = { Text("Search", color = colorResource(R.color.blue_1200)) },
                singleLine = true,
                textStyle = TextStyle(
                    color = colorResource(R.color.black),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            )
            if (isExpanded.value && predictionsState.value.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        predictionsState.value.forEach { prediction ->
                            Text(
                                text = prediction.getFullText(null).toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        text.value = prediction.getFullText(null).toString()
                                        predictionsState.value = emptyList()
                                        isExpanded.value = false
                                        selectedPrediction.value = prediction
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            )
                            Divider(color = Color.Gray.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
    }
    ExpandableFAB(
        navToHome = {
            locationState.let {
                if (it.lat != 53.3201094 && it.lon != -8.567809712252107) {
                    setingViewModel.saveData(SharedKeys.LAT.toString(), it.lat.toString())
                    setingViewModel.saveData(SharedKeys.LON.toString(), it.lon.toString())
                }
                navToHome(it.lat, it.lon)
            }
        },
        navToFavorite = {
            locationState.let {
                viewModel.addFavoriteCity(it.lat, it.lon)
                navToFavorite()
            }
        }
    )
}


@Composable
fun ExpandableFAB(navToHome: () -> Unit, navToFavorite: () -> Unit) {
    val isExpanded = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 24.dp, bottom = 128.dp)
        ) {
            AnimatedVisibility(visible = isExpanded.value) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = { navToFavorite() },
                        containerColor = colorResource(R.color.blue_1200),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            navToHome()
                        },
                        containerColor = colorResource(R.color.blue_1200),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.White
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { isExpanded.value = !isExpanded.value },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 128.dp),
            containerColor = colorResource(R.color.blue_1200),
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isExpanded.value) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Toggle FAB",
                tint = Color.White
            )
            Text(
                text = if (isExpanded.value) "Close" else "Add Item",
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

