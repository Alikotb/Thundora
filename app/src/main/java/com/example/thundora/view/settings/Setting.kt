package com.example.thundora.view.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.pojos.view.SharedKeys
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
import com.example.thundora.ui.theme.DarkBlue

@Composable
fun SettingScreen(floatingFlag: MutableState<Boolean>) {
    floatingFlag.value=false
    val viewModel: SettingViewModel= viewModel(
        factory = SettingsFactory(
            Repository.getInstance(
                RemoteDataSource(ApiClient.weatherService),
                LocalDataSource(
                    WeatherDataBase.getInstance(LocalContext.current).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colorResource(id = R.color.deep_blue),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        LanguageSelectionChips(viewModel)
        TempSelectionChips(viewModel)
        LocationSelectionChips(viewModel)
        WendSpeedSelectionChips(viewModel)
    }
}

@Composable
fun LanguageSelectionChips(view: SettingViewModel) {
    val context = LocalContext.current
    val defaultLanguage = "English"
    var selectedOption by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        selectedOption = view.fetchData(SharedKeys.LANGUAGE.toString(), defaultLanguage)
    }

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
                listOf(stringResource(id = R.string.language_english), stringResource(id = R.string.language_arabic)).forEach { option ->
                    val isSelected = option == selectedOption
                    val chipColor = if (isSelected) Color(0xFF1565C0) else Color(0xFFBBDEFB)
                    val textColor = if (isSelected) Color.White else Color.Black

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedOption = option
                            view.saveData(SharedKeys.LANGUAGE.toString(),selectedOption)
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
fun TempSelectionChips(view: SettingViewModel) {
    val cel = stringResource(R.string.celsius_c)
    val far = stringResource(R.string.fahrenheit_f)
    val kel = stringResource(R.string.kelvin_k)
    var selectedOption by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val storedTemp = view.fetchData(SharedKeys.DEGREE.toString(),"celsius")
        selectedOption = when (storedTemp) {
            "celsius" -> cel
            "fahrenheit" -> far
            "kelvin" -> kel
            else -> cel
        }
    }
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
                   cel,
                    far,
                    kel
                ).forEach { option ->
                    val isSelected = option == selectedOption
                    var chipColor by remember { mutableStateOf(Color(0xFFBBDEFB)) }
                    chipColor = if (isSelected) Color(0xFF1565C0) else Color(0xFFBBDEFB)
                    val textColor = if (isSelected) Color.White else Color.Black
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if(option==cel)
                                selectedOption = "celsius"
                            else if(option==far)
                                selectedOption = "fahrenheit"
                            else if(option==kel)
                                selectedOption = "kelvin"
                            view.saveData(SharedKeys.DEGREE.toString(),selectedOption)
                            chipColor=Color(0xFF1565C0)
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
fun LocationSelectionChips(view: SettingViewModel) {
    val `default-location` = stringResource(R.string.location)
    var selectedOption by remember {
        mutableStateOf(view.fetchData(SharedKeys.LOCATION.toString(),`default-location`))
    }

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
                listOf(stringResource(R.string.gps), stringResource(R.string.map)).forEach { option ->
                    FilterChip(
                        selected = (option == selectedOption),
                        onClick = {
                            selectedOption = option
                            view.saveData(SharedKeys.LOCATION.toString(),selectedOption)
//                            shared.edit { putString("location", selectedOption) }
                                  }
                        ,
                        enabled = true,
                        label = {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1565C0),
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
fun WendSpeedSelectionChips(view: SettingViewModel) {
    val `default-wend-speed` = stringResource(R.string.wend_speed_unit)
    var selectedOption by remember {
        mutableStateOf(view.fetchData(SharedKeys.SPEED_UNIT.toString(),`default-wend-speed`))
    }




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
                    painter = painterResource(id = R.drawable.ic_wind),
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
                listOf("meter/sec", "mile/hour").forEach { option ->
                    FilterChip(
                        selected = (option == selectedOption),
                        onClick = {
                            selectedOption = option
                            view.saveData(SharedKeys.SPEED_UNIT.toString(),selectedOption)
                                  },
                        enabled = true,
                        label = {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1565C0),
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
fun ContactUsSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp)
                )
                Text(
                    text = "Contact Us",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ContactIcon(Icons.Default.Email, "Email") {
                }
                ContactImageIcon(R.drawable.ic_facebook, "Facebook") {
                }
                ContactImageIcon(R.drawable.ic_linkedin, "LinkedIn") {
                }
            }
        }
    }
}

@Composable
fun ContactIcon(icon: ImageVector, contentDesc: String, onClick: () -> Unit) {
    Icon(
        imageVector = icon,
        contentDescription = contentDesc,
        tint = Color.White,
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick)
            .padding(8.dp)
    )
}

@Composable
fun ContactImageIcon(imageRes: Int, contentDesc: String, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = contentDesc,
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick)
            .padding(8.dp)
    )
}

fun restartActivity(context: Context) {
    val intent = (context as? Activity)?.intent
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    (context as? Activity)?.finish()
}
