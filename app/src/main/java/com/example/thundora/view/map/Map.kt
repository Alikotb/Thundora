package com.example.thundora.view.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.data.remote.api.ApiClient
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


@Composable
fun MapScreen(
    floatingFlag: MutableState<Boolean>,
    navToHome: () -> Unit,
    navToFavorite: () -> Unit,
) {
    floatingFlag.value = false
    val context = LocalContext.current
    val viewModel: MapViewModel = viewModel(
        factory = MapFactory(
            Places.createClient(context),
            RepositoryImpl.getInstance(
                RemoteDataSource(ApiClient.weatherService),
                LocalDataSource(
                    WeatherDataBase.getInstance(context).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        )
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            viewModel.markerState.value.position,
            12f
        )
    }

    LaunchedEffect(Unit) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            viewModel.markerState.value.position,
            cameraPositionState.position.zoom
        )
    }

    MapBranch(
        navToHome = navToHome,
        navToFavorite = navToFavorite,
        viewModel = viewModel,
        cameraPositionState = cameraPositionState
    )
}

@Composable
fun MapBranch(
    navToHome: () -> Unit,
    navToFavorite: () -> Unit,
    viewModel: MapViewModel,
    cameraPositionState: CameraPositionState
) {
    val scope = rememberCoroutineScope()
    val text = remember { mutableStateOf("") }
    val predictionsState = remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    val isExpanded = remember { mutableStateOf(false) }
    val markerState by viewModel.markerState.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false
            ),
            modifier = Modifier.fillMaxSize(),
            onMapClick = { viewModel.updateMarkerPosition(it) },
            properties = MapProperties(mapType = MapType.HYBRID),
            cameraPositionState = cameraPositionState
        ) {
            Marker(state = markerState, title = "Location Marker")
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
                        predictionsState.value = viewModel.getAddressPredictions(query)
                    }
                }, label = {
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
                                        isExpanded.value = false
                                        viewModel.getPlaceDetails(prediction.placeId) { latLng ->
                                            latLng?.let {
                                                scope.launch {
                                                    cameraPositionState.animate(
                                                        CameraUpdateFactory.newLatLngZoom(it, 12f)
                                                    )
                                                }
                                            }
                                        }
                                    },
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
        ExpandableFAB(text,
            navToHome = {
                viewModel.setHomeLocation(markerState.position)
                navToHome()
            },
            navToFavorite = {
                viewModel.addFavoriteCity(
                    markerState.position.latitude,
                    markerState.position.longitude
                )
                navToFavorite()
            }
        )
    }
}

@Composable
fun ExpandableFAB(text: MutableState<String>, navToHome: () -> Unit, navToFavorite: () -> Unit) {
    val isExpanded = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter

    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
            ) {

                Text(
                    text = text.value,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(bottom = 24.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                ExtendedFloatingActionButton(
                    onClick = { isExpanded.value = !isExpanded.value },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    containerColor = colorResource(R.color.blue_1200),
                    icon = {
                        Icon(
                            imageVector = if (isExpanded.value) Icons.Default.Close else Icons.Default.Add,
                            tint = Color.White,
                            contentDescription = "Toggle"
                        )
                    },
                    text = {
                        Text(
                            if (isExpanded.value) "Close" else "Pick",
                            color = Color.White
                        )
                    },
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                )

                this@Card.AnimatedVisibility(
                    visible = isExpanded.value,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 8.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            navToFavorite()
                            isExpanded.value = false
                        },
                        containerColor = colorResource(R.color.blue_1200),
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                }

                this@Card.AnimatedVisibility(
                    visible = isExpanded.value,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            navToHome()
                            isExpanded.value = false
                        },
                        containerColor = colorResource(R.color.blue_1200),
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}



