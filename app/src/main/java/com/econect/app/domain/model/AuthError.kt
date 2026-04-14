package com.econect.app.domain.model

sealed class AuthError : Exception() {
    data object InvalidCredentials : AuthError()
    data object UserNotFound : AuthError()
    data object EmailAlreadyInUse : AuthError()
    data object NetworkError : AuthError()
    /** Auth user existe pero no tiene documento en Firestore */
    data object ProfileNotFound : AuthError()
    data class Unknown(override val cause: Throwable) : AuthError()
}
