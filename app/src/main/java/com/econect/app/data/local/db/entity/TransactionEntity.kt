package com.econect.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val routeId: String,
    val recyclerId: String,
    val citizenId: String,
    val materialId: String,
    val confirmedQuantityValue: Double,
    val confirmedQuantityUnit: String,
    val pricePerUnit: Double,
    val totalAmount: Double,
    val recyclingCenterId: String,
    val completedAt: Long
)
