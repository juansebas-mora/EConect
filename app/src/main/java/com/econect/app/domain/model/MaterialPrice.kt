package com.econect.app.domain.model

data class MaterialPrice(
    val materialType: MaterialType,
    val pricePerUnit: Double,
    val unit: MaterialUnit
)