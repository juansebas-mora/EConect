package com.econect.app.domain.model

data class Route(
    val id: String,
    val recyclerId: String,
    val stops: List<RouteStop>,
    val date: String, // formato ISO-8601 "yyyy-MM-dd"
    val status: RouteStatus
)
