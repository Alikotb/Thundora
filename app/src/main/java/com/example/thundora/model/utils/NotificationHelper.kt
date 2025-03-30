package com.example.thundora.model.utils

import java.util.Locale

fun String.getWeatherNotification(): String {
    val notifications = mapOf(
        "01d" to mapOf(
            "en" to "Clear sky during the day! Enjoy the sunshine. ☀️",
            "ar" to "سماء صافية خلال النهار! استمتع بأشعة الشمس. ☀️",
        ),
        "01n" to mapOf(
            "en" to "Clear night sky! Perfect for stargazing. 🌙",
            "ar" to "سماء صافية في الليل! مثالية لمشاهدة النجوم. 🌙",
        ),
        "02d" to mapOf(
            "en" to "A few clouds in the sky, but still a nice day! ⛅",
            "ar" to "بعض السحب في السماء، لكن الجو لا يزال جميلاً! ⛅",
        ),
        "02n" to mapOf(
            "en" to "Partly cloudy night! Enjoy the cool breeze. 🌌",
            "ar" to "ليلة غائمة جزئيًا! استمتع بالنسيم البارد. 🌌",
        ),
        "03d" to mapOf(
            "en" to "Scattered clouds today. ☁️",
            "ar" to "غيوم متفرقة اليوم. ☁️",
        ),
        "03n" to mapOf(
            "en" to "Scattered clouds at night. 🌥️",
            "ar" to "غيوم متفرقة في الليل. 🌥️",
        ),
        "04d" to mapOf(
            "en" to "Broken clouds covering the sky. 🌥️",
            "ar" to "غيوم متقطعة تغطي السماء. 🌥️",
        ),
        "04n" to mapOf(
            "en" to "Broken clouds tonight. Might feel chilly! 🌙",
            "ar" to "غيوم متقطعة الليلة. قد يكون الجو باردًا! 🌙",
        ),
        "09d" to mapOf(
            "en" to "Shower rain expected. Carry an umbrella! 🌧️",
            "ar" to "متوقع هطول أمطار غزيرة. احمل مظلة! 🌧️",
        ),
        "09n" to mapOf(
            "en" to "Shower rain at night. Stay warm! 🌧️",
            "ar" to "أمطار غزيرة في الليل. ابقَ دافئًا! 🌧️",
        ),
        "10d" to mapOf(
            "en" to "Rain expected during the day. Don't forget your raincoat! ☔",
            "ar" to "من المتوقع هطول أمطار خلال النهار. لا تنس معطف المطر! ☔",
        ),
        "10n" to mapOf(
            "en" to "Rainy night ahead. Stay dry! ☔",
            "ar" to "ليلة ماطرة قادمة. ابقَ جافًا! ☔",
        ),
    )
    val language = Locale.getDefault().language
    return notifications[this]?.get(language) ?: this
}