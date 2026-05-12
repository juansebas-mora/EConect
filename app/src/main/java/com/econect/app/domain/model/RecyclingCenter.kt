package com.econect.app.domain.model

data class RecyclingCenter(
    val id: String,
    val name: String,
    val address: String,
    val location: LatLng,
    val phone: String
)
