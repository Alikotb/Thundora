package com.example.thundora.domain.model

import java.util.Locale

enum class LanguagesEnum(val code: String, val value: String, val arValue: String) {
    ENGLISH("en", "English", "English"),
    ARABIC("ar", "العربية", "العربية"),
    DEFAULT("Default", "Default", "لغة النظام");

    companion object {
        fun geCodeByValue(value: String): String {
            val lang = Locale.getDefault().language
            return when (lang) {
                "ar" -> LanguagesEnum.values().find { it.arValue == value }?.code
                    ?: LanguagesEnum.ENGLISH.code

                else -> LanguagesEnum.values().find { it.value == value }?.code
                    ?: LanguagesEnum.ENGLISH.code
            }

        }

        fun getValue(code: String): String {
            val lang = Locale.getDefault().language
            return when (lang) {
                "ar" -> LanguagesEnum.values().find { it.code==code }?.arValue?:LanguagesEnum.ENGLISH.arValue
                else -> LanguagesEnum.values().find { it.code==code }?.value?:LanguagesEnum.ENGLISH.value
            }
        }
    }
}


