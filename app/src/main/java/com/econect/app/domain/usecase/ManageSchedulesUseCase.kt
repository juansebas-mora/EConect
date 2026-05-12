package com.econect.app.domain.usecase

import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.repository.UserRepository
import javax.inject.Inject

class ManageSchedulesUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun add(uid: String, schedule: Schedule): Result<Unit> =
        userRepository.addSchedule(uid, schedule)

    /**
     * @param scheduleId clave compuesta: "${dayOfWeek.name}_${startTime}" (ej: "MONDAY_08:00")
     */
    suspend fun remove(uid: String, scheduleId: String): Result<Unit> =
        userRepository.removeSchedule(uid, scheduleId)
}
