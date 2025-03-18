package com.example.thundora.view.settings

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thundora.R
import com.example.thundora.ui.theme.DarkBlue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                color = colorResource(id = R.color.deep_blue),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        LanguageSelectionChips()
        TempSelectionChips()
        LocationSelectionChips()
        WendSpeedSelectionChips()

    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectionChips() {
    var selectedOption by remember { mutableStateOf("Arabic") }

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
                    modifier = Modifier.size(40.dp)
                    .padding(end = 12.dp),
                )
                Text(
                    text = "Language",
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
                listOf("Arabic", "English", "German").forEach { option ->
                    FilterChip(
                        selected = (option == selectedOption),
                        onClick = { selectedOption = option },
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
@Preview(showBackground = true)
@Composable
fun TempSelectionChips() {
    var selectedOption by remember { mutableStateOf("Arabic") }

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
                    modifier = Modifier.size(40.dp)
                        .padding(end = 12.dp),
                )
                Text(
                    text = "Temp Unit",
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
                    listOf("Celsius °C", "Fahrenheit °F","Kelvin °K").forEach { option ->
                        FilterChip(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option },
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



@Preview(showBackground = true)
@Composable
fun LocationSelectionChips() {
    var selectedOption by remember { mutableStateOf("Arabic") }

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
                    modifier = Modifier.size(40.dp)
                        .padding(end = 12.dp),
                )
                Text(
                    text = "Location",
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
                listOf("Gps", "Map").forEach { option ->
                    FilterChip(
                        selected = (option == selectedOption),
                        onClick = { selectedOption = option },
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


@Preview(showBackground = true)
@Composable
fun WendSpeedSelectionChips() {
    var selectedOption by remember { mutableStateOf("Arabic") }

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
                    modifier = Modifier.size(40.dp)
                        .padding(end = 12.dp),
                )
                Text(
                    text = "Wend Speed Unit",
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
                        onClick = { selectedOption = option },
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)), // Dark blue
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
