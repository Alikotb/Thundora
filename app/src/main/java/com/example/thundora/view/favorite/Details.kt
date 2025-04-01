package com.example.thundora.view.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.domain.model.api.Response
import com.example.thundora.domain.model.api.Weather
import com.example.thundora.data.remote.api.ApiClient
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.utils.CountryHelper
import com.example.thundora.utils.DateTimeHelper
import com.example.thundora.utils.formatNumberBasedOnLanguage
import com.example.thundora.utils.getDegree
import com.example.thundora.utils.getLanguage
import com.example.thundora.utils.getWindSpeed
import com.example.thundora.view.favorite.viewModel.FavoriteFactory
import com.example.thundora.view.favorite.viewModel.FavoriteViewModel
import com.example.thundora.view.home.WeatherInfo
import com.example.thundora.view.components.getIcon
import com.example.thundora.view.components.isInternetAvailable

@Composable
fun DetailsScreen(floatingFlag: MutableState<Boolean>, city: String, lat: Double, lon: Double) {
    floatingFlag.value = false
    val viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteFactory(
            RepositoryImpl.getInstance(
                RemoteDataSource(
                    ApiClient.weatherService
                ),
                LocalDataSource
                    (
                    WeatherDataBase.getInstance(LocalContext.current).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        )
    )


    val language by viewModel.language.collectAsStateWithLifecycle()
    val temp by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val temperatureUnit = getDegree(getLanguage(language), temp)
    val favoriteCities by viewModel.favoriteCity.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        if (isInternetAvailable()) {
            viewModel.getFavoriteCityApi(city, lat, lon)
        } else {
            viewModel.getFavoriteCityRoom(city)
        }

    }
    val speedUnit = getWindSpeed(language, temp)
    when (favoriteCities) {
        is Response.Error -> {
            Text(text = (favoriteCities as Response.Error).message)
        }

        Response.Loading -> {
            Box {
                CircularProgressIndicator()
            }
        }

        is Response.Success -> {
            WeatherCardDetails(
                weatherState = (favoriteCities as Response.Success<Weather>).data,
                temperatureUnit,
                speedUnit
            )
        }
    }
}

@Composable
fun WeatherCardDetails(
    weatherState: Weather,
    temperatureUnit: String,
    speedUnit: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.deep_blue)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue_accent)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 64.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${weatherState.name}\n" +
                               (CountryHelper.getCountryName((weatherState.sys.country))),
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(start = 8.dp),
                    )
                    Column {
                        Text(
                            text = formatNumberBasedOnLanguage(
                                DateTimeHelper.getDayOfWeek(weatherState.dt.toLong()),
                            ),
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = formatNumberBasedOnLanguage(
                                DateTimeHelper.getFormattedDate(
                                    weatherState.dt.toLong()
                                )
                            ),
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = painterResource(getIcon(icon = weatherState.weather.firstOrNull()?.icon.toString())),
                    contentDescription = stringResource(R.string.weather_icon),
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${
                        weatherState.main.temp.let {
                            formatNumberBasedOnLanguage(
                                it.toString(),
                            )
                        }
                    } ° $temperatureUnit",
                    fontSize = 35.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = weatherState.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                        .toString(),
                    fontSize = 20.sp,
                    color = Color.White
                )

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue_accent)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .weight(.45f),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    WeatherInfo(
                        value = weatherState.wind.speed.let {
                            formatNumberBasedOnLanguage(
                                it.toString(),
                            )
                        },
                        unit = " $speedUnit",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                        iconTint = Color.White,
                        color = Color.White

                    )
                    Spacer(Modifier.height(8.dp))
                    WeatherInfo(
                        value = formatNumberBasedOnLanguage(
                            weatherState.main.pressure.toString(),
                        ),
                        unit = " hPa",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                        iconTint = Color.White,
                        color = Color.White

                    )
                }

            }

            Card(
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue_accent)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .weight(.45F)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    WeatherInfo(
                        value = weatherState.main.sea_level.let {
                            formatNumberBasedOnLanguage(
                                it.toString(),
                            )
                        }.toString(),
                        unit = "",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                        iconTint = Color.White,
                        color = Color.White

                    )
                    Spacer(Modifier.height(8.dp))
                    WeatherInfo(
                        value = "72",
                        unit = "%",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                        iconTint = Color.White,
                        color = Color.White

                    )
                }
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue_accent)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .padding(horizontal = 64.dp),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        DateTimeHelper.formatUnixTimestamp(
                            weatherState.sys.sunrise.toLong(),
                            "hh:mm a"
                        )
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                    iconTint = Color.White,
                    color = Color.White

                )
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        DateTimeHelper.formatUnixTimestamp(
                            weatherState.sys.sunset.toLong(),
                            "hh:mm a"
                        )
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                    iconTint = Color.White,
                    color = Color.White

                )
            }

        }
    }
}
