package com.econect.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: String,
    val recyclerId: String,
    val date: String,       // ISO-8601 "yyyy-MM-dd"
    val status: String,
    val stopsJson: String   // JSON: [{"materialId":"","citizenId":"","scheduledTime":"","lat":0.0,"lng":0.0}]
)
