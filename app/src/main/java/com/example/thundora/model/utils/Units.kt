package com.example.thundora.model.utils

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
    return when (tempUnit) {
        "C","س" -> tempDegree
        "F" , "ف" -> (tempDegree * 9 / 5) + 32
        "K" ,"ك" -> tempDegree + 273.15
        else -> tempDegree
    }
}

