package com.example.thundora.domain.model.api

data class GeocodingResponseItem(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)