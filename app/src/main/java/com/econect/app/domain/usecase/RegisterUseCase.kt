package com.econect.app.domain.usecase

import com.econect.app.domain.model.Result
import com.econect.app.domain.model.User
import com.econect.app.domain.model.UserType
import com.econect.app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        phone: String,
        userType: UserType
    ): Result<User> = authRepository.register(
        email = email,
        password = password,
        name = name,
        phone = phone,
        userType = userType
    )
}
