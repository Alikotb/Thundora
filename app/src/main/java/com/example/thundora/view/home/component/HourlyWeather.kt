package com.example.thundora.view.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.thundora.R
import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.utils.DateTimeHelper
import com.example.thundora.utils.formatNumberBasedOnLanguage

@Composable
fun WeatherForecast(forecastList: List<Forecast.Item0>, temperatureUnit: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.today), fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        if (forecastList.isEmpty()) {
            Text(
                text = "......",
                color = Color.White,
                fontSize = 16.sp
            )
        } else {
            LazyRow {
                items(forecastList) { item ->
                    HourlyDataRow(
                        time = formatNumberBasedOnLanguage(
                            DateTimeHelper.formatUnixTimestamp(
                                item.dt.toLong(),
                                "HH:mm"
                            )
                        ),
                        temp = "${item.main.temp.toInt()} ° $temperatureUnit",
                        icon = item.weather.firstOrNull()?.icon ?: "01n"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HourlyDataRow(time: String, temp: String, icon: String) {
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
            contentDescription = stringResource(R.string.weather_icon2),
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = formatNumberBasedOnLanguage(temp),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}