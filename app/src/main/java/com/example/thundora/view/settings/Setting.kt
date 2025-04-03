package com.example.thundora.view.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.remote.api.ApiClient
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.view.settings.component.LanguageSelectionChips
import com.example.thundora.view.settings.component.LocationSelectionChips
import com.example.thundora.view.settings.component.TempSelectionChips
import com.example.thundora.view.settings.component.WendSpeedSelectionChips
import com.example.thundora.view.settings.viewmodel.SettingsFactory
import com.example.thundora.view.settings.viewmodel.SettingsViewModel


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

