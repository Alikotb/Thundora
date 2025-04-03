package com.example.thundora.view.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.thundora.R
import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.utils.DateTimeHelper
import com.example.thundora.utils.dailyForecasts
import com.example.thundora.utils.formatNumberBasedOnLanguage
import kotlin.collections.component1
import kotlin.collections.component2

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DayDisplayRow(time: String, date: String, temp: String, icon: String) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.deep_blue))
            .padding(vertical = 8.dp)
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
            text = formatNumberBasedOnLanguage(temp),
            color = Color.White,
            modifier = Modifier
                .constrainAs(tempereture) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)

                },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        GlideImage(
            model = "https://openweathermap.org/img/wn/$icon.png",
            contentDescription = stringResource(R.string.weather_icon2),
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
fun DayDisplay(forecastState: Forecast?, temperatureUnit: String) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Forecast.Item0?>(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.daily_forecast), fontSize = 20.sp, color = Color.White,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        forecastState?.let { forecast ->
            val forecastMap = forecast.dailyForecasts()
            forecastMap.forEach { (_, itemList) ->
                val firstItem = itemList.firstOrNull()
                firstItem?.let { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedItem = item
                                showBottomSheet = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DayDisplayRow(
                            time = formatNumberBasedOnLanguage(
                                DateTimeHelper.getDayOfWeek(item.dt.toLong())
                            ),
                            date = formatNumberBasedOnLanguage(
                                DateTimeHelper.formatUnixTimestamp(
                                    item.dt.toLong(),
                                    "dd/MM/yyyy"
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

    if (showBottomSheet && selectedItem != null) {
        WeatherForecastBottomSheet(weatherState = selectedItem, temperatureUnit) {
            showBottomSheet = false
            selectedItem = null
        }
    }
}