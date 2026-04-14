package com.econect.app.domain.model

data class ChatMessage(
    val id: String,
    val routeId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long, // epoch millis
    val read: Boolean
)
