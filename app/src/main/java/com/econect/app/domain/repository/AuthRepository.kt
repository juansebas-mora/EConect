package com.econect.app.domain.repository

import com.econect.app.domain.model.Result
import com.econect.app.domain.model.User
import com.econect.app.domain.model.UserType

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        userType: UserType
    ): Result<User>

    suspend fun logout()

    suspend fun getCurrentUser(): Result<User?>

    fun isLoggedIn(): Boolean
}
