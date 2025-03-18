package com.example.thundora.view.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.thundora.R
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.pojos.view.Units
import com.example.thundora.model.utils.CountryHelper
import com.example.thundora.ui.theme.DarkBlue
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val weatherState by viewModel.weather.observeAsState()
    val forecastState by viewModel.forecast.observeAsState()
    //val errorState by viewModel.error.observeAsState()

    LaunchedEffect(viewModel) {
        Log.i("HomeScreen", "Fetching weather and forecast...")

        viewModel.getWeather(30.0444, 31.2357, Units.METRIC.toString())
        viewModel.getForecast(30.0444, 31.2357, Units.METRIC.toString())
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(
                color = colorResource(id = R.color.deep_blue),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        weatherState?.let { WeatherCard(it) }
        forecastState?.let { WeatherForecast(it) }
        DayDisplay(forecastState)

    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherCard(weatherState: Weather?) {
    val iconCode = weatherState?.weather?.firstOrNull()?.icon ?: "01d"
    val iconUrl = "https://openweathermap.org/img/wn/$iconCode.png"

    Log.d("WeatherCard", "Weather icon URL: $iconUrl")

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(16.dp)
    ) {
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
                    text = "${
                        weatherState?.name ?: "--"
                    }\n${
                        CountryHelper.getCountryName(weatherState?.sys?.country.toString())
                            .takeIf { it.isNotBlank() } ?: "Unknown"
                    }",
                    color = colorResource(id = R.color.white),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 8.dp)

                )
                Column {
                    Text(
                        text = SimpleDateFormat("EEEE", Locale.getDefault()).format(
                            Date(
                                (weatherState?.dt?.toLong()?.times(1000L))
                                    ?: System.currentTimeMillis()
                            )
                        ),
                        color = colorResource(id = R.color.white),
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = SimpleDateFormat("d MMM", Locale.getDefault()).format(
                            Date(
                                (weatherState?.dt?.toLong()?.times(1000L))
                                    ?: System.currentTimeMillis()
                            )
                        ),
                        color = colorResource(id = R.color.white),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            GlideImage(
                model = iconUrl,
                contentDescription = "Weather Icon",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "${weatherState?.main?.temp ?: "--"}°C",
                fontSize = 35.sp,
                color = colorResource(id = R.color.white)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = weatherState?.weather?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "--",
                fontSize = 20.sp,
                color = colorResource(id = R.color.white)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDataDisplay(
                    value = weatherState?.wind?.speed.toString(),
                    unit = " km/h",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                    iconTint = Color.White
                )
                WeatherDataDisplay(
                    value = weatherState?.main?.humidity.toString(),
                    unit = "%",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                    iconTint = Color.White
                )
                WeatherDataDisplay(
                    value = weatherState?.main?.pressure.toString(),
                    unit = " hPa",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
                    iconTint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDataDisplay(
                    value = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                        Date((weatherState?.sys?.sunrise ?: 0) * 1000L)
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sunrise),
                    iconTint = Color.Yellow
                )

                WeatherDataDisplay(
                    value = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                        Date((weatherState?.sys?.sunset ?: 0) * 1000L)
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sunsunset),
                    iconTint = Color.Yellow
                )

                WeatherDataDisplay(
                    value = weatherState?.main?.sea_level?.toString() ?: "--",
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sea),
                    iconTint = Color.White
                )
            }
        }
    }
}


@Composable
fun WeatherForecast(forecastState: Forecast?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Today", fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        forecastState?.list?.let { forecastList ->
            LazyRow {
                items(forecastList.take(8)) { item ->
                    HourlyDataDisplay(
                        time = SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(Date(item.dt * 1000L)),
                        temp = "${item.main.temp.toInt()}°C",
                        icon = item.weather.firstOrNull()?.icon ?: "01n"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HourlyDataDisplay(time: String, temp: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(100.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(text = time, color = Color.LightGray)
        GlideImage(
            model = "https://openweathermap.org/img/wn/$icon.png",
            contentDescription = "Weather Icon",
            modifier = Modifier.size(40.dp)
        )
        Text(text = temp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WeatherDataDisplay(
    value: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp, color = Color.White),
    iconTint: Color
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(25.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$value$unit",
            style = textStyle
        )
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DayDisplayRow(time: String, date: String, temp: String, icon: String) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.deep_blue))
            .padding(
                vertical = 8.dp
            )

    ) {
        val (descripyion, image, tempereture) = createRefs()
        Column(
            modifier = Modifier
                .constrainAs(descripyion) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, 16.dp)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Text(
                text = time,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.width(110.dp)
            )
            Text(
                text = date,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
        Text(
            text = temp,
            color = Color.White,
            modifier = Modifier
                .constrainAs(tempereture) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)

                }

        )

        GlideImage(
            model = "https://openweathermap.org/img/wn/$icon.png",
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(40.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, 16.dp)
                    bottom.linkTo(parent.bottom)
                }
                .size(48.dp)
        )
    }

}

@Composable
fun DayDisplay(forecastState: Forecast?) {
    forecastState?.let { forecast ->
        val forecastMap = forecast.dailyForecasts()
        forecastMap.forEach { (dayKey, itemList) ->
            val firstItem = itemList.firstOrNull()
            firstItem?.let { item ->
                DayDisplayRow(
                    time = SimpleDateFormat(
                        "EEEE",
                        Locale.getDefault()
                    ).format(Date(item.dt * 1000L)),
                    date = SimpleDateFormat(
                        "dd/MM",
                        Locale.getDefault()
                    ).format(Date(item.dt * 1000L)),
                    temp = "${item.main.temp.toInt()}°C",
                    icon = item.weather.firstOrNull()?.icon ?: "01n"
                )
            }
        }
    }
}


