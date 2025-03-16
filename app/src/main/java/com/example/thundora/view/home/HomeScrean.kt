package com.example.thundora.view.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.thundora.R
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.Weather
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
        viewModel.getWeather(50.0444, 81.2357)
        viewModel.getForecast(30.0444, 31.2357)
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(
                color = colorResource(id = R.color.deep_blue),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        weatherState?.let { WeatherCard(it) }
        forecastState?.let { WeatherForecast(it) }
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
            Text(
                text = "Today ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}",
                modifier = Modifier.align(Alignment.End),
                color = colorResource(id = R.color.white)
            )
            Spacer(Modifier.height(16.dp))

            GlideImage(
                model = iconUrl,
                contentDescription = "Weather Icon",
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(16.dp))
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
                    value = weatherState?.wind?.speed?.toInt() ?: 0,
                    unit = "km/h",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_wind)
                )
                WeatherDataDisplay(
                    value = weatherState?.main?.humidity ?: 0,
                    unit = "%",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_humidity)
                )
                WeatherDataDisplay(
                    value = weatherState?.main?.pressure ?: 0,
                    unit = "hPa",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_pressure)
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
    value: Int,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp, color = Color.White),
    iconTint: Color = Color.White
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

