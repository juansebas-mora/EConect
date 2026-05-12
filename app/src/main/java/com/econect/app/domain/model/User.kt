package com.econect.app.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val userType: UserType,
    val preferredLocations: List<PreferredLocation>,
    val availableSchedules: List<Schedule>,
    val rating: Float,
    val ratingCount: Int,
    val preferredRecyclingCenterId: String? = null
)
