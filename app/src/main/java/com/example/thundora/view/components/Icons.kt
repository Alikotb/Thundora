package com.example.thundora.view.components

import com.example.thundora.R

fun getIcon (icon: String): Int{
    return when(icon){
        "01d" -> R.drawable.i_01d
        "01n"-> R.drawable.i_01n
        "02d"-> R.drawable.i_02d
        "02n"-> R.drawable.i_02n
        "03d"-> R.drawable.i_03d
        "03n"-> R.drawable.i_03n
        "04d"-> R.drawable.i_04d
        "04n"-> R.drawable.i_04n
        "09d"-> R.drawable.i_09d
        "09n"-> R.drawable.i_09n
        "10d"-> R.drawable.i_10d
        "10n"-> R.drawable.i_10n
        "11d"-> R.drawable.i_11d
        "11n"-> R.drawable.i_11n
        "50d"-> R.drawable.i_50d
        "50n"-> R.drawable.i_50n
        else -> R.drawable.i_01d
    }
}