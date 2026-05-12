package com.econect.app.domain.usecase

import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.Result
import com.econect.app.domain.repository.UserRepository
import javax.inject.Inject

class ManageLocationsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun add(uid: String, latLng: LatLng, label: String): Result<Unit> =
        userRepository.addPreferredLocation(uid, latLng, label)

    suspend fun remove(uid: String, locationId: String): Result<Unit> =
        userRepository.removePreferredLocation(uid, locationId)
}
