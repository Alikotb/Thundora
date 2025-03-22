package com.example.thundora.view.map

import android.content.Context
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
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
import androidx.compose.runtime.MutableState
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
import com.example.thundora.model.pojos.api.GeocodingResponseItem
import com.example.thundora.model.pojos.api.Response
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.CameraPositionState
import androidx.core.content.edit


@Composable
fun MapScreen(
    navToHome: (lat: Double, lon: Double) -> Unit
) {
    val viewModel: MapViewModel =
        viewModel(
            factory = MapFactory(
                LocalContext.current,
                Repository.getInstance(RemoteDataSource(ApiClient.weatherService))
            )
        )
    val shared = LocalContext.current.getSharedPreferences(
        "loc",
        Context.MODE_PRIVATE
    )
    val locationState = viewModel.locationFlow.collectAsStateWithLifecycle()
    val error = viewModel.error.collectAsStateWithLifecycle()
    val x = shared.getString("lat", "0.0")?.toDouble() ?: 0.0
    val y = shared.getString("long", "0.0")?.toDouble() ?: 0.0
    val markerState = remember { mutableStateOf(MarkerState(LatLng(x, y))) }

    val selectedPrediction = remember { mutableStateOf<AutocompletePrediction?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.value.position, 15f)
    }

    LaunchedEffect(locationState.value) {
        when (val result = locationState.value) {
            is Response.Success -> {
                val data = result.data
                if (data.lat != 53.3201094 && data.lon != -8.567809712252107) {
                    shared.edit() { putString("lat", data.lat.toString()) }
                    shared.edit() { putString("long", data.lon.toString()) }
                    markerState.value.position = LatLng(data.lat, data.lon)
                }
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(markerState.value.position, 15f)
            }

            is Response.Error -> {
            }

            Response.Loading -> {
            }
        }
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
                androidx.compose.material3.CircularProgressIndicator()
            }
        }

        is Response.Success<GeocodingResponseItem> -> {
            MapBransh(
                navToHome = navToHome,
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
    navToHome: (lat: Double, lon: Double) -> Unit,
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
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
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
                navToHome(it.lat, it.lon)
                Log.d("asd", "navToHome: $it.lat ${it.lon}")

            }
        }
    )
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


