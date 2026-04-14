package com.econect.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recyclable_materials")
data class RecyclableMaterialEntity(
    @PrimaryKey val id: String,
    val citizenId: String,
    val type: String,
    val condition: String,
    val quantityValue: Double,
    val quantityUnit: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val status: String,
    val createdAt: Long
)
