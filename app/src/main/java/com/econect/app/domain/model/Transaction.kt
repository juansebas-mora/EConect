package com.econect.app.domain.model

data class Transaction(
    val id: String,
    val routeId: String,
    val recyclerId: String,
    val citizenId: String,
    val materialId: String,
    val confirmedQuantity: MaterialQuantity,
    val pricePerUnit: Double,
    val totalAmount: Double,
    val recyclingCenterId: String,
    val completedAt: Long // epoch millis
)
