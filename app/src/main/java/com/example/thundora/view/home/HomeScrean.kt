@file:Suppress("CAST_NEVER_SUCCEEDS")

package com.example.thundora.view.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.remote.api.ApiClient
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.domain.model.api.ApiResponse
import com.example.thundora.domain.model.api.Response
import com.example.thundora.utils.DateTimeHelper
import com.example.thundora.utils.formatNumberBasedOnLanguage
import com.example.thundora.utils.getDegree
import com.example.thundora.utils.getWindSpeed
import com.example.thundora.utils.isInternetAvailable
import com.example.thundora.view.components.Error
import com.example.thundora.view.components.LoadingScreen
import com.example.thundora.view.components.getWeatherColors
import com.example.thundora.view.home.component.DayDisplay
import com.example.thundora.view.home.component.LineChartScreen
import com.example.thundora.view.home.component.WeatherCard
import com.example.thundora.view.home.component.WeatherForecast
import com.example.thundora.view.home.viewmodel.HomeFactory
import com.example.thundora.view.home.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    flag: MutableState<Boolean>,
    floatingFlag: MutableState<Boolean>,
    navToMaps: () -> Unit
) {
    flag.value = true
    floatingFlag.value = false

    val viewModel: HomeViewModel = viewModel(
        factory = HomeFactory(
            RepositoryImpl.getInstance(
                RemoteDataSource(ApiClient.weatherService),
                LocalDataSource(
                    WeatherDataBase.getInstance(LocalContext.current).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        )
    )


    val language by viewModel.language.collectAsStateWithLifecycle()
    val temp by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val temperatureUnit = getDegree(language, temp)
    val wind by viewModel.units.collectAsStateWithLifecycle()
    val windSpeedUnit = getWindSpeed(language, wind)

    val apiForecast by viewModel.forecast.collectAsStateWithLifecycle()

    var dataPoints: MutableList<Float> = mutableListOf()
    var hourlyData: MutableList<String> = mutableListOf()

    LaunchedEffect(Unit) {
        if (isInternetAvailable()) {
            viewModel.fetchSettings()
            viewModel.getForecast()
        } else {
            viewModel.getForecastFromLocal()
        }
    }

    when (apiForecast) {
        is Response.Success -> {
            dataPoints = (apiForecast as Response.Success).data.forecast.list.take(4)
                .filter { DateTimeHelper.isToday(it.dt.toLong()) }
                .map { it.main.temp.toFloat() }
                .toMutableList()

            hourlyData = (apiForecast as Response.Success).data.forecast.list.take(4)
                .filter { DateTimeHelper.isToday(it.dt.toLong()) }
                .map {
                    "${
                        formatNumberBasedOnLanguage(
                            DateTimeHelper.formatUnixTimestamp(it.dt.toLong(), "HH:mm"),
                        )
                    }\n\n" +
                            "${
                                formatNumberBasedOnLanguage(
                                    it.main.temp.toInt().toString(),
                                )
                            } ° $temperatureUnit"
                }.toMutableList()

            Home(
                (apiForecast as Response.Success).data,
                navToMaps,
                dataPoints,
                hourlyData,
                temperatureUnit,
                windSpeedUnit
            )
        }

        is Response.Error -> {
            Error()
        }

        is Response.Loading -> {

            Box(
                Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            ) {
                LoadingScreen()
            }
        }
    }
}

@Composable
fun Home(
    apiForecast: ApiResponse,
    navToMaps: () -> Unit,
    dataPoints: MutableList<Float>,
    hourlyData: MutableList<String>,
    temperatureUnit: String,
    speedUnit: String,
) {
    val todayForecastList =
        apiForecast.forecast.list.filter { DateTimeHelper.isToday(it.dt.toLong()) }
    val iconCode = apiForecast.weather.weather.firstOrNull()?.icon ?: "01d"
    val (backgroundColor, textColor) = getWeatherColors(iconCode)
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(
                color = colorResource(id = R.color.deep_blue),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        WeatherCard(
            apiForecast.weather,
            temperatureUnit,
            speedUnit,
        ) {
            navToMaps()
        }
        LineChartScreen(
            dataPoints = dataPoints,
            data = hourlyData,
            isDayTime = DateTimeHelper.isDayTime(
                apiForecast.forecast.city.sunrise.toLong(),
                apiForecast.forecast.city.sunset.toLong(),
                System.currentTimeMillis() / 1000
            ),
            weatherCondition = apiForecast.weather.weather.firstOrNull()?.main ?: "Clear",
            textColor,
        )

        WeatherForecast(todayForecastList, temperatureUnit)
        Spacer(Modifier.height(4.dp))
        DayDisplay(apiForecast.forecast, temperatureUnit)

        Spacer(Modifier.height(150.dp))
    }
}





