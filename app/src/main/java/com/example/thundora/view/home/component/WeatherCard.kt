package com.example.thundora.view.home.component

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.thundora.R
import com.example.thundora.domain.model.api.Weather
import com.example.thundora.utils.CountryHelper
import com.example.thundora.utils.DateTimeHelper
import com.example.thundora.utils.formatNumberBasedOnLanguage
import com.example.thundora.utils.isInternetAvailable
import com.example.thundora.view.components.getIcon
import com.example.thundora.view.components.getWeatherColors
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherCard(
    weatherState: Weather?,
    temperatureUnit: String,
    speedUnit: String,
    navToMaps: () -> Unit
) {
    val ctx = LocalContext.current
    val iconCode = weatherState?.weather?.firstOrNull()?.icon ?: "01d"
    val (backgroundColor, textColor) = getWeatherColors(iconCode)
    val messageState = remember { MutableSharedFlow<String>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messageState) {
        messageState.collectLatest { message ->
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showToast(message: String) {
        coroutineScope.launch {
            messageState.emit(message)
        }
    }


    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
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
                    text = "${weatherState?.name ?: "--"}\n" +
                            (CountryHelper.getCountryName(weatherState?.sys?.country.toString())),
                    color = textColor,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable(
                            onClick = {
                                if (isInternetAvailable()) {
                                    navToMaps()
                                } else {
                                    showToast(ctx.getString(R.string.no_internet_connect_to_network_please))

                                }
                            }
                        ),
                )
                Column {
                    Text(
                        text = formatNumberBasedOnLanguage(
                            DateTimeHelper.getDayOfWeek(weatherState?.dt?.toLong())
                        ),
                        color = textColor,
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = formatNumberBasedOnLanguage(
                            DateTimeHelper.getFormattedDate(
                                weatherState?.dt?.toLong()
                            )
                        ),
                        color = textColor,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(getIcon(icon = iconCode)),
                contentDescription = stringResource(R.string.weather_icon),
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "${
                    weatherState?.main?.temp?.toInt().let {
                        formatNumberBasedOnLanguage(
                            it.toString()
                        )
                    }
                } ° $temperatureUnit",
                fontSize = 35.sp,
                color = textColor,
                fontWeight = FontWeight.Bold

            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = weatherState?.weather?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "--",
                fontSize = 20.sp,
                color = textColor
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfo(
                    value = weatherState?.wind?.speed.let {
                        formatNumberBasedOnLanguage(
                            it.toString()
                        )
                    },
                    unit = " $speedUnit",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                    iconTint = textColor,
                    color = textColor

                )
                WeatherInfo(
                    value = weatherState?.main?.humidity.let {
                        formatNumberBasedOnLanguage(
                            it.toString()
                        )
                    },
                    unit = "%",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                    iconTint = textColor,
                    color = textColor

                )
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        weatherState?.main?.pressure.toString()
                    ),
                    unit = " hPa",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
                    iconTint = textColor,
                    color = textColor

                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        DateTimeHelper.formatUnixTimestamp(
                            weatherState?.sys?.sunrise?.toLong(),
                            "hh:mm a"
                        )
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sunrise),
                    iconTint = textColor,
                    color = textColor

                )
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        DateTimeHelper.formatUnixTimestamp(
                            weatherState?.sys?.sunset?.toLong(),
                            "hh:mm a"
                        )
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sunsunset),
                    iconTint = textColor,
                    color = textColor

                )
                WeatherInfo(
                    value = weatherState?.clouds?.all?.let {
                        formatNumberBasedOnLanguage(
                            it.toString()
                        )
                    } ?: "--",
                    unit = "%",
                    icon = Icons.Default.Cloud,
                    iconTint = textColor,
                    color = textColor
                )
            }
        }
    }
}