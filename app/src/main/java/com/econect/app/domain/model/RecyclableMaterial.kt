package com.econect.app.domain.model

data class RecyclableMaterial(
    val id: String,
    val citizenId: String,
    val type: MaterialType,
    val condition: MaterialCondition,
    val quantity: MaterialQuantity,
    val pickupLocation: LatLng,
    val status: MaterialStatus,
    val createdAt: Long // epoch millis
)
