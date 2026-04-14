package com.econect.app.domain.model

data class RouteStop(
    val materialId: String,
    val citizenId: String,
    val scheduledTime: String, // formato "HH:mm"
    val location: LatLng,
    val materialType: MaterialType = MaterialType.OTHER
)
