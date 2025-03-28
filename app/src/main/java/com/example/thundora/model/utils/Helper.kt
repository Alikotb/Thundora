package com.example.thundora.model.utils

import java.util.Locale

object CountryHelper {
    fun getCountryName(code: String?): String? {
        if(code.isNullOrEmpty())return ""
        return try {

            Locale("", code).displayCountry.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            code
        }
    }
}
