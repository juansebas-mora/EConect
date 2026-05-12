package com.econect.app.domain.model

import java.time.DayOfWeek

data class Schedule(
    val dayOfWeek: DayOfWeek,
    val startTime: String, // formato "HH:mm"
    val endTime: String    // formato "HH:mm"
)
