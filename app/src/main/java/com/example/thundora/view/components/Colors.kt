package com.example.thundora.view.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.thundora.ui.theme.DarkBlue
import kotlin.random.Random


fun getRandomGradient(): Brush {
    val compatibleColors = listOf(
        listOf(Color(0xFF1A3A6F), Color(0xFF3A7BD5)),
        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
        listOf(Color(0xFF283048), Color(0xFF859398)),
        listOf(Color(0xFF16222A), Color(0xFF3A6073)),
        listOf(Color(0xFF000428), Color(0xFF004e92)),
        listOf(Color(0xFF1D2B64), Color(0xFFF8CDDA)),
        listOf(Color(0xFF114357), Color(0xFFF29492)),
        listOf(Color(0xFF1E3C72), Color(0xFF2A5298)),


        listOf(Color(0xFF000046), Color(0xFF1CB5E0)),
        listOf(Color(0xFF654EA3), Color(0xFFEAAFC8)),
        listOf(Color(0xFF1A2980), Color(0xFF26D0CE)),
        listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF)),
        listOf(Color(0xFF114357), Color(0xFFF29492)),
        listOf(Color(0xFF085078), Color(0xFF85D8CE)),
        listOf(Color(0xFF360033), Color(0xFF0B8793)),
        listOf(Color(0xFF2193B0), Color(0xFF6DD5ED)),
        listOf(Color(0xFF800080), Color(0xFFffc0cb)),
        listOf(Color(0xFF0575E6), Color(0xFF021B79)),
        listOf(Color(0xFF3a7bd5), Color(0xFF00d2ff))
    )

    val randomColors = compatibleColors[Random.nextInt(compatibleColors.size)]

    return Brush.linearGradient(
        colors = randomColors,
        start = androidx.compose.ui.geometry.Offset(0f, Float.POSITIVE_INFINITY),
        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, 0f)
    )
}

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

