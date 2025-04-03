package com.example.thundora.utils

fun getDegree(language: String, degree: String): String {
    return when {
        language == "en" && degree == "metric" -> "C"
        language == "en" && degree == "imperial" -> "F"
        language == "en" && degree == "standard" -> "K"
        language == "ar" && degree == "metric" -> "س"
        language == "ar" && degree == "imperial" -> "ف"
        language == "ar" && degree == "standard" -> "ك"
        else -> ""
    }
}

fun getWindSpeed(language: String, degree: String): String {
    return when {
        language == "en" && degree == "metric" -> "m/s"
        language == "en" && degree == "imperial" -> "mph"
        language == "en" && degree == "standard" -> "m/s"
        language == "ar" && degree == "metric" -> "م/ث"
        language == "ar" && degree == "imperial" -> "م/س"
        language == "ar" && degree == "standard" -> "م/ث"
        else -> ""
    }
}

fun transferUnit(tempUnit: String, tempDegree: Double): Double {
    return when {
        tempUnit.contains("C") || tempUnit.contains("س") -> tempDegree
        tempUnit.contains("F") || tempUnit.contains("ف") -> (tempDegree * 9 / 5) + 32
        tempUnit.contains("K") || tempUnit.contains("ك") -> tempDegree - 273.15
        else -> tempDegree
    }
}

fun getTemperatureUnit(setting: String): String {
    return when {
        setting.contains("Fahrenheit", ignoreCase = true) || setting.contains(
            "فهرنهيت",
            ignoreCase = true
        ) -> "imperial"

        setting.contains("Celsius", ignoreCase = true) || setting.contains(
            "سيليزيوس",
            ignoreCase = true
        ) -> "metric"

        setting.contains("Kelvin", ignoreCase = true) || setting.contains(
            "كيلفن",
            ignoreCase = true
        ) -> "standard"

        setting.contains("m/s", ignoreCase = true) || setting.contains(
            "م/ث",
            ignoreCase = true
        ) -> "metric"

        setting.contains("mile/h", ignoreCase = true) || setting.contains(
            "ميل/س",
            ignoreCase = true
        ) -> "imperial"

        else -> "Unknown Unit"
    }
}


fun getTemperatureDisplayUnit(apiUnit: String): String {
    return when (apiUnit) {
        "imperial" -> "Fahrenheit °F"
        "metric" -> "Celsius °C"
        "standard" -> "Kelvin °K"
        else -> "Celsius °C"
    }

}

fun getArabicTemperatureDisplayUnit(apiUnit: String): String {
    return when (apiUnit) {
        "imperial" -> "فهرنهيت ف"
        "metric" -> "سيليزيوس س"
        "standard" -> "كيلفن ك"
        else -> "سيليزيوس س"
    }
}


fun getWindDisplayUnit(apiUnit: String): String {
    return when (apiUnit) {
        "imperial" -> "mile/h"
        "metric" -> "m/s"
        else -> "m/s"
    }
}

fun getArabicWindUnit(apiUnit: String): String {
    return when (apiUnit) {
        "imperial" -> "ميل/س"
        "metric" -> "م/ث"
        else -> "م/ث"
    }
}

fun getLanguage(apiUnit: String): String {
    return when (apiUnit) {
        "English" -> "en"
        "العربية" -> "ar"

        else -> "en"
    }
}






