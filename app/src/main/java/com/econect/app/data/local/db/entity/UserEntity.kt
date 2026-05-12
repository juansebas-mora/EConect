package com.econect.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val userType: String,
    val preferredLocationsJson: String, // JSON: [{"id":"...","lat":0.0,"lng":0.0,"label":"..."}]
    val schedulesJson: String,          // JSON: [{"dayOfWeek":"MONDAY","startTime":"08:00","endTime":"17:00"}]
    val rating: Float,
    val ratingCount: Int,
    val preferredRecyclingCenterId: String? = null
)
