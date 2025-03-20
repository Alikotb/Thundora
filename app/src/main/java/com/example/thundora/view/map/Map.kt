package com.example.thundora.view.map

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.rememberCameraPositionState

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch
import androidx.compose.ui.res.colorResource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun MapScreen(
    latitude: Double,
    longitude: Double,
    navToHome: (lat: Double, lon: Double) -> Unit
) {
    val viewModel: MapViewModel =
        viewModel(
            factory = MapFactory(
                LocalContext.current,
                Repository.getInstance(RemoteDataSource(ApiClient.weatherService))
            )
        )
    val locationState = viewModel.location.observeAsState()
    val location = locationState.value
    val scope = rememberCoroutineScope()
    val text = remember { mutableStateOf("") }
    val locationMap = remember { mutableStateOf(LatLng(latitude, longitude)) }
    val predictionsState = remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    val selectedPrediction = remember { mutableStateOf<AutocompletePrediction?>(null) }
    val isExpanded = remember { mutableStateOf(false) }
    val markerState = remember { MarkerState.Companion(position = locationMap.value) }
    val cameraPositionState = rememberCameraPositionState {}
    LaunchedEffect(location) {
        location?.let {
            Log.d("MapScreen", "Updating camera to lat: $latitude, lon: $longitude")

            locationMap.value = LatLng(it.lat, it.lon)

            markerState.position = locationMap.value

            cameraPositionState.position = CameraPosition.fromLatLngZoom(locationMap.value, 15f)
        }
    }

    viewModel.getCityLocation(selectedPrediction.value?.getPrimaryText(null).toString())
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Log.i("MapScreen", "Received lat: $latitude, lon: $longitude")

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,

            ) {
            Marker(
                state = markerState,
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
                label = { Text("Search", color = colorResource(R.color.red_600)) },
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
        ExpandableFAB(
            navToHome = {
                location?.let {
                    navToHome(it.lat, it.lon)
                }
            }
        )
    }
}

@Composable
fun ExpandableFAB(navToHome: () -> Unit) {
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
                        onClick = { /* Handle Favorite action */ },
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


