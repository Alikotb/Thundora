package com.example.thundora.utils

import android.content.Context
import android.util.Log
import com.example.thundora.R
import java.util.Locale

fun convertToArabicNumbers(number: String): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}

fun formatNumberBasedOnLanguage(number: String): String {
        val language = Locale.getDefault().language
        return if (language == "ar") convertToArabicNumbers(number) else number
 }

// fun getActualSystemLocale(context: Context): String {
//     Log.d("asd", "getActualSystemLocale:  ${ context.resources.configuration.locales[0].language}")
//
//     return context.resources.configuration.locales[0].language
//}
//
// fun Context.checkForLanguage(lang: String): String {
//    return if (lang == "لغة النظام"||lang=="Default") {
//        return this.resources.configuration.locales[0].language
//    } else {
//        getLanguage(lang)
//    }
//}