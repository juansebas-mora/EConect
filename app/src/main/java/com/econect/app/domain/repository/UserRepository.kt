package com.econect.app.domain.repository

import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.model.User

interface UserRepository {
    suspend fun getUserProfile(uid: String): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun addPreferredLocation(uid: String, latLng: LatLng, label: String): Result<Unit>
    suspend fun removePreferredLocation(uid: String, locationId: String): Result<Unit>
    suspend fun addSchedule(uid: String, schedule: Schedule): Result<Unit>
    suspend fun removeSchedule(uid: String, scheduleId: String): Result<Unit>
}
