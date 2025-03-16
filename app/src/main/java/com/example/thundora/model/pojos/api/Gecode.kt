package com.example.thundora.model.pojos.api

data class GeocodingResponseItem(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)