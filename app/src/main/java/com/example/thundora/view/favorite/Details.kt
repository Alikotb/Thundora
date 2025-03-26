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
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
import com.example.thundora.model.utils.CountryHelper
import com.example.thundora.model.utils.DateTimeHelper
import com.example.thundora.model.utils.formatNumberBasedOnLanguage
import com.example.thundora.model.utils.getDegree
import com.example.thundora.model.utils.getWindSpeed
import com.example.thundora.view.favorite.viewModel.FavoriteFactory
import com.example.thundora.view.favorite.viewModel.FavoriteViewModel
import com.example.thundora.view.home.WeatherInfo
import com.example.thundora.view.settings.SettingViewModel
import com.example.thundora.view.settings.SettingsFactory
import com.example.thundora.view.utilies.getIcon
import com.example.thundora.view.utilies.isInternetAvailable


@Composable
fun DetailsScreen(floatingFlag: MutableState<Boolean>, city: String, lat: Double, lon: Double){
    floatingFlag.value = false
    val viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteFactory(
            Repository.getInstance(
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

    var language = "en"
    if (setingViewModel.fetchData(SharedKeys.LANGUAGE.toString(), "") == "English")
        language = "en"
    else if (setingViewModel.fetchData(SharedKeys.LANGUAGE.toString(), "") == "العربية")
        language = "ar"
    val tempKey = setingViewModel.fetchData(SharedKeys.DEGREE.toString(), "celsius")

    val temp = when (tempKey) {
        "celsius" -> "metric"
        "fahrenheit" -> "imperial"
        "kelvin" -> "standard"
        else -> "metric"
    }

    val temperatureUnit = getDegree(language, temp)
    val favoriteCities by viewModel.favoriteCity.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        if(isInternetAvailable()){
            viewModel.getFavoriteCityApi(city,lat,lon)
        }else{
            viewModel.getFavoriteCityRomm(city)
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
            WeatherCardDetails(weatherState = (favoriteCities as Response.Success<Weather>).data,temperatureUnit,speedUnit,language)
        }
    }

}

@Composable
fun WeatherCardDetails(
    weatherState: Weather,
    temperatureUnit: String,
    speedUnit: String,
    language: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.deep_blue))
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue_accent)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 64.dp)
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
                        text = "${weatherState.name}\n"+
                               (CountryHelper.getCountryName(weatherState.sys.country.toString())),
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
                                language
                            ),
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text =  formatNumberBasedOnLanguage(
                                DateTimeHelper.getFormattedDate(
                                    weatherState.dt.toLong()
                                ), language
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
                                language
                            )
                        } 
                    } ° $temperatureUnit",
                    fontSize = 35.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = weatherState.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }.toString(),
                    fontSize = 20.sp,
                    color = Color.White
                )

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
            ,
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
                                language
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
                            weatherState?.main?.pressure.toString(),
                            language
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
                                language
                            )
                        }.toString() ,
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
                    .padding(horizontal = 64.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,

            ) {
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        DateTimeHelper.formatUnixTimestamp(
                            weatherState.sys.sunrise.toLong(),
                            "hh:mm a"
                        ), language
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
                        ), language
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
