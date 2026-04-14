package com.econect.app.domain.model

data class Rating(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val routeId: String,
    val score: Int, // 1-5
    val comment: String,
    val createdAt: Long // epoch millis
)
