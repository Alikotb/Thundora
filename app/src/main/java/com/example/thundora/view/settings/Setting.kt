package com.example.thundora.view.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.remote.api.ApiClient
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.domain.model.LanguagesEnum
import com.example.thundora.ui.theme.DarkBlue
import com.example.thundora.utils.getTemperatureUnit
import com.example.thundora.utils.isInternetAvailable
import com.example.thundora.view.map.GPSLocation
import com.example.thundora.view.map.GPSLocation.getLocation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(floatingFlag: MutableState<Boolean>, navToMap: () -> Unit) {

    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsFactory(
            RepositoryImpl.getInstance(
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
    floatingFlag.value = false
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colorResource(id = R.color.deep_blue),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    Spacer(Modifier.height(48.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = Color.White
            )
            Text(
                text = stringResource(R.string.setting),
                color = Color.White,
                fontSize = 20.sp
            )
        }
        LanguageSelectionChips(viewModel)
        LocationSelectionChips(viewModel, navToMap)
        TempSelectionChips(viewModel)
        WendSpeedSelectionChips(viewModel)
    }
}

@Composable
fun LanguageSelectionChips(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val selectedOption by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.language),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp),
                )
                Text(
                    text = stringResource(R.string.select_language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Log.d("TAG", "LanguageSelectionChips: ${selectedOption}")
                listOf(
                    LanguagesEnum.getValue(LanguagesEnum.ENGLISH.code),
                    LanguagesEnum.getValue(LanguagesEnum.ARABIC.code),
                    LanguagesEnum.getValue(LanguagesEnum.DEFAULT.code),

                    ).forEach { option ->
                    val isSelected = option == selectedOption
                    val chipColor = if (isSelected) Color(0xFF1565C0) else Color(0xFFBBDEFB)
                    val textColor = if (isSelected) Color.White else Color.Black

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.setLanguage(option)
                            restartActivity(context)
                        },
                        label = {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor,
                            containerColor = Color(0xFFBBDEFB),
                            selectedLabelColor = Color.White,
                            labelColor = Color.Black
                        ),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TempSelectionChips(viewModel: SettingsViewModel) {
    val selectedOption by viewModel.selectedTempUnit.collectAsStateWithLifecycle()

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.temp),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp),
                )
                Text(
                    text = stringResource(R.string.temp_unit),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    stringResource(id = R.string.celsius_c),
                    stringResource(id = R.string.kelvin_k),
                    stringResource(id = R.string.fahrenheit_f)
                ).forEach { option ->
                    val isSelected = option == selectedOption
                    val chipColor = if (isSelected) Color(0xFF1565C0) else Color(0xFFBBDEFB)
                    val textColor = if (isSelected) Color.White else Color.Black

                    FilterChip(
                        selected = option == selectedOption,
                        onClick = {
                            viewModel.setUnit(
                                getTemperatureUnit(option),
                                getTemperatureUnit(option)
                            )

                        },
                        label = {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor,
                            containerColor = Color(0xFFBBDEFB),
                            selectedLabelColor = Color.White,
                            labelColor = Color.Black
                        ),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun LocationSelectionChips(viewModel: SettingsViewModel, navToMap: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gpsLocation = GPSLocation

    val selectedOption by viewModel.selectedLocation.collectAsStateWithLifecycle()

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp),
                )
                Text(
                    text = stringResource(R.string.location),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    stringResource(id = R.string.gps),
                    stringResource(id = R.string.map)
                ).forEach { option ->
                    val isSelected = option == selectedOption
                    val chipColor = if (isSelected) Color(0xFF1565C0) else Color(0xFFBBDEFB)
                    val textColor = if (isSelected) Color.White else Color.Black

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isInternetAvailable()) {
                                viewModel.setLocationMode(option)
                                if (option == context.getString(R.string.gps)) {
                                    scope.launch {
                                        try {
                                            if (gpsLocation.checkPermission(context)) {
                                                if (!gpsLocation.isLocationEnabled(context)) {
                                                    gpsLocation.enableLocationService(context)
                                                } else {
                                                    val location = getLocation(context)
                                                    location?.let {
                                                        viewModel.setLocation(
                                                            it.latitude,
                                                            it.longitude
                                                        )
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                } else {
                                    navToMap()
                                }

                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.no_internet_connect_to_network_please),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },

                        label = {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor,
                            containerColor = Color(0xFFBBDEFB),
                            selectedLabelColor = Color.White,
                            labelColor = Color.Black
                        ),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun WendSpeedSelectionChips(viewModel: SettingsViewModel) {
    val selectedOption by viewModel.selectedWindSpeed.collectAsStateWithLifecycle()
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp),
                )
                Text(
                    text = stringResource(R.string.wend_speed_unit),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    stringResource(R.string.m_s),
                    stringResource(R.string.mile_h)
                ).forEach { option ->
                    val isSelected = option == selectedOption
                    val chipColor = if (isSelected) Color(0xFF1565C0) else Color(0xFFBBDEFB)
                    val textColor = if (isSelected) Color.White else Color.Black

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.setUnit(
                                getTemperatureUnit(option),
                                getTemperatureUnit(option)
                            )

                        },
                        label = {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor,
                            containerColor = Color(0xFFBBDEFB),
                            selectedLabelColor = Color.White,
                            labelColor = Color.Black
                        ),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }
    }
}

fun restartActivity(context: Context) {
    val intent = (context as? Activity)?.intent
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    (context as? Activity)?.finish()
}
