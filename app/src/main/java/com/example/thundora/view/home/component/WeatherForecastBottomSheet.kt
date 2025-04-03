package com.example.thundora.view.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.thundora.R
import com.example.thundora.domain.model.api.Forecast
import com.example.thundora.ui.theme.DarkBlue
import com.example.thundora.utils.formatNumberBasedOnLanguage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun WeatherForecastBottomSheet(
    weatherState: Forecast.Item0?,
    temperatureUnit: String,
    onClose: () -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onClose() },
            sheetState = sheetState,
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBlue
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkBlue),
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
                                .format(Date(weatherState?.dt?.times(1000L) ?: 0L)),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = weatherState?.weather?.get(0)?.description?.capitalize(Locale.ROOT)
                                ?: R.string.unknown.toString(),
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "${
                            formatNumberBasedOnLanguage(
                                (weatherState?.main?.temp?.toInt() ?: 0).toString()
                            )
                        }° $temperatureUnit",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.main?.humidity?.toString() ?: "--"
                            ),
                            unit = "%",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                            iconTint = Color.White,
                            color = Color.White
                        )
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.wind?.speed?.toString() ?: "--"
                            ),
                            unit = " km/h",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                            iconTint = Color.White,
                            color = Color.White
                        )
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.main?.pressure?.toString() ?: "--"
                            ),
                            unit = stringResource(R.string.hpa),
                            icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
                            iconTint = Color.White,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.clouds?.all?.toString() ?: "--"
                            ),
                            unit = "%",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                            iconTint = Color.White,
                            color = Color.White
                        )
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.rain?.`3h`?.toString()
                                    ?: stringResource(R.string.norain)
                            ),
                            unit = "",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                            iconTint = Color.White,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showBottomSheet = false
                            }
                            onClose()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.close),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}