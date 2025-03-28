package com.example.thundora.view.map

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.view.utilies.LoadingScreen


@Composable
fun MapScreen(
    floatingFlag: MutableState<Boolean>,
    navToHome: () -> Unit,
    navToFavorite: () -> Unit,
) {
    floatingFlag.value = false

    val context = LocalContext.current
    val client = remember { Places.createClient(context) }
    val viewModel: MapViewModel = viewModel(
        factory = MapFactory(
            client,
            Repository.getInstance(
                RemoteDataSource(ApiClient.weatherService),
                LocalDataSource(
                    WeatherDataBase.getInstance(context).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        )
    )


    val locationState by viewModel.locationState.collectAsStateWithLifecycle()
    val latitude by viewModel.latitude.collectAsStateWithLifecycle()
    val longitude by viewModel.longitude.collectAsStateWithLifecycle()

    val markerState = remember { mutableStateOf(MarkerState(LatLng(latitude, longitude))) }
    val selectedPrediction = remember { mutableStateOf<AutocompletePrediction?>(null) }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.value.position, 10f)
    }

    LaunchedEffect(selectedPrediction.value) {
        viewModel.getCityLocation(selectedPrediction.value?.getPrimaryText(null).toString())
    }

    when (locationState) {
        is Response.Loading -> {
            LoadingScreen()
        }

        is Response.Success -> {
            val data = (locationState as Response.Success).data
            if (data.lat != 53.3201094 && data.lon != -8.567809712252107) {
                markerState.value.position = LatLng(data.lat, data.lon)
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(markerState.value.position, 15f)
            }
            MapBranch(
                navToHome = {
                    navToHome()
                },
                navToFavorite = navToFavorite,
                viewModel = viewModel,
                markerState = markerState,
                cameraPositionState = cameraPositionState,
                selectedPrediction = selectedPrediction,
            )
        }

        is Response.Error -> {
            Text(text = "Error: ${(locationState as Response.Error).message}")
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun MapBranch(
    navToHome: () -> Unit,
    navToFavorite: () -> Unit,
    viewModel: MapViewModel,
    markerState: MutableState<MarkerState>,
    cameraPositionState: CameraPositionState,
    selectedPrediction: MutableState<AutocompletePrediction?>,

    ) {
    val scope = rememberCoroutineScope()
    val text = remember { mutableStateOf("") }
    val predictionsState = remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    val isExpanded = remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false
            ),
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(mapType = MapType.SATELLITE, isMyLocationEnabled = false),
            cameraPositionState = cameraPositionState
        ) {
            Marker(state = markerState.value, title = "Location Marker")
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
                label = {
                    Text(
                        stringResource(R.string.search),
                        color = colorResource(R.color.blue_1200)
                    )
                },
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
                    .border(1.dp, colorResource(R.color.blue_1200), RoundedCornerShape(12.dp))
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                                    .clickable {
                                        text.value = prediction.getFullText(null).toString()
                                        selectedPrediction.value = prediction
                                        isExpanded.value = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = colorResource(R.color.blue_1200),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = prediction.getFullText(null).toString(),
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W800),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    ExpandableFAB(
        navToHome = {
            val position = markerState.value.position
            SharedPreference.getInstance()
                .saveData(SharedKeys.LAT.toString(), position.latitude.toString())
            SharedPreference.getInstance()
                .saveData(SharedKeys.LON.toString(), position.longitude.toString())
            navToHome()
        },
        navToFavorite = {
            val position = markerState.value.position
            viewModel.addFavoriteCity(position.latitude, position.longitude)
            navToFavorite()
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
                        onClick = {

                            navToFavorite()
                        },
                        containerColor = colorResource(R.color.blue_1200),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.favorite0),
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
                            contentDescription = stringResource(R.string.location0),
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
                contentDescription = stringResource(R.string.toggle_fab),
                tint = Color.White
            )
            Text(
                text = if (isExpanded.value) stringResource(R.string.close_) else stringResource(R.string.add_item),
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

