package com.econect.app.domain.usecase

import com.econect.app.domain.model.Result
import com.econect.app.domain.model.User
import com.econect.app.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User?> = authRepository.getCurrentUser()
}
