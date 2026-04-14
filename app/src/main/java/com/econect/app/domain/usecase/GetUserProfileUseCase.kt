package com.econect.app.domain.usecase

import com.econect.app.domain.model.Result
import com.econect.app.domain.model.User
import com.econect.app.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String): Result<User> =
        userRepository.getUserProfile(uid)
}
