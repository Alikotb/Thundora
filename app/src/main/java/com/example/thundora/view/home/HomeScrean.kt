package com.example.thundora.view.home


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val weatherState by viewModel.weather.observeAsState()
    val geocodingState by viewModel.geocoding.observeAsState()
    val forecastState by viewModel.forecast.observeAsState()
    val errorState by viewModel.error.observeAsState()

    LaunchedEffect(viewModel) {
        Log.i("HomeScreen", "Fetching weather, geocoding, and forecast...")
        viewModel.getWeather(30.0444, 31.2357)
        viewModel.getCoordinates("Cairo")
        viewModel.getForecast(30.0444, 31.2357)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        errorState?.let {
            Log.i("HomeScreen error", it)
        }
        geocodingState?.let {

            Log.i("HomeScreen geocoding", geocodingState?.toString() ?: "geocodingState is NULL")
        }

        weatherState?.let {

            Log.i("HomeScreen weather", it.toString())
        }

        forecastState?.let {
            Log.i("HomeScreen forecast", it.toString())
        }

        Text(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = "Hello HomeScreen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
