package com.example.thundora.model.utils

import java.util.Locale

fun convertToArabicNumbers(number: String): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}

fun formatNumberBasedOnLanguage(number: String): String {
        val language = Locale.getDefault().language
        return if (language == "ar") convertToArabicNumbers(number) else number
 }