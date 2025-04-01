package com.example.thundora.view.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.thundora.ui.theme.DarkBlue


@Composable
fun getWeatherColors(iconCode: String): Pair<Color, Color> {
    return when {
        iconCode.startsWith("01") -> if (iconCode.endsWith("d"))
            Pair(Color(0xFFFFE082), Color.Black)
        else
            Pair(Color(0xFF37474F), Color.White)

        iconCode.startsWith("02") -> if (iconCode.endsWith("d"))
            Pair(Color(0xFF90A4AE), Color.Black)
        else
            Pair(Color(0xFF263238), Color.White)

        iconCode.startsWith("03") || iconCode.startsWith("04") ->
            Pair(Color(0xFF607D8B), Color.White)

        iconCode.startsWith("09") || iconCode.startsWith("10") ->
            Pair(Color(0xFF4FC3F7), Color.Black)

        iconCode.startsWith("11") ->
            Pair(Color(0xFF616161), Color.White)

        iconCode.startsWith("13") ->
            Pair(Color(0xFFECEFF1), Color.Black)

        iconCode.startsWith("50") ->
            Pair(Color(0xFFB0BEC5), Color.Black)

        else ->
            Pair(Color(0xFF546E7A), Color.White)
    }
}

@Composable
fun getBackgroundColor(isDayTime: Boolean, weatherCondition: String): Color {
    return when (weatherCondition.lowercase()) {
        "rain" -> Color(0xFF4FC3F7)
        "clouds" -> Color(0xFF607D8B)
        "clear" -> if (isDayTime) Color(0xFFFFE082) else Color(0xFF37474F)
        "snow" -> Color(0xFFECEFF1)
        else -> if (isDayTime) Color.LightGray else DarkBlue
    }
}

