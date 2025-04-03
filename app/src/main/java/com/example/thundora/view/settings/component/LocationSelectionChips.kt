package com.example.thundora.view.settings.component

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thundora.R
import com.example.thundora.ui.theme.DarkBlue
import com.example.thundora.utils.GPSLocation
import com.example.thundora.utils.GPSLocation.getLocation
import com.example.thundora.utils.isInternetAvailable
import com.example.thundora.view.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

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