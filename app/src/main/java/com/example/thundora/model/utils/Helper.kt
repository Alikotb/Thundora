package com.example.thundora.model.utils

import java.util.Locale

object CountryHelper {
    fun getCountryName(code: String): String? {
        return try {
            Locale("", code).displayCountry.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
