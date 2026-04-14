package com.econect.app.data.repository

import com.econect.app.domain.model.AuthError
import com.econect.app.domain.model.PreferredLocation
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.model.User
import com.econect.app.domain.model.UserType
import java.util.UUID
import com.econect.app.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        val uid = requireNotNull(auth.currentUser?.uid) { "UID nulo tras login exitoso" }
        fetchUserFromFirestore(uid)
    }.fold(
        onSuccess = { user -> Result.Success(user) },
        onFailure = { e -> Result.Error(mapFirebaseError(e)) }
    )

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        userType: UserType
    ): Result<User> {
        // Paso 1: crear usuario en Auth
        val authResult = runCatching {
            auth.createUserWithEmailAndPassword(email, password).await()
        }.getOrElse { e -> return Result.Error(mapFirebaseError(e)) }

        val uid = requireNotNull(authResult.user?.uid) { "UID nulo tras registro exitoso" }
        val newUser = User(
            id = uid,
            name = name,
            email = email,
            phone = phone,
            userType = userType,
            preferredLocations = emptyList(),
            availableSchedules = emptyList(),
            rating = 0f,
            ratingCount = 0
        )

        // Paso 2: guardar perfil en Firestore; si falla, revertir el usuario de Auth
        return runCatching {
            saveUserToFirestore(newUser)
            newUser
        }.fold(
            onSuccess = { user -> Result.Success(user) },
            onFailure = { e ->
                runCatching { auth.currentUser?.delete()?.await() }
                Result.Error(mapFirebaseError(e))
            }
        )
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): Result<User?> {
        val firebaseUser = auth.currentUser ?: return Result.Success(null)
        return runCatching {
            fetchUserFromFirestore(firebaseUser.uid)
        }.fold(
            onSuccess = { user -> Result.Success(user) },
            onFailure = { e -> Result.Error(mapFirebaseError(e)) }
        )
    }

    override fun isLoggedIn(): Boolean = auth.currentUser != null

    // --- helpers privados ---

    private class ProfileNotFoundException(uid: String) :
        Exception("Documento de usuario no encontrado en Firestore: $uid")

    private suspend fun fetchUserFromFirestore(uid: String): User {
        val doc = firestore.collection(COLLECTION_USERS).document(uid).get().await()
        val data = doc.data ?: throw ProfileNotFoundException(uid)
        return mapDocumentToUser(uid, data)
    }

    private suspend fun saveUserToFirestore(user: User) {
        val data = mapOf(
            "type" to user.userType.name.lowercase(),
            "name" to user.name,
            "email" to user.email,
            "phone" to user.phone,
            "rating" to user.rating,
            "ratingCount" to user.ratingCount,
            "preferredLocations" to emptyList<Map<String, Any>>(),
            "schedules" to emptyList<Map<String, Any>>()
        )
        firestore.collection(COLLECTION_USERS).document(user.id).set(data).await()
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapDocumentToUser(uid: String, data: Map<String, Any>): User {
        val typeStr = (data["type"] as? String).orEmpty().uppercase()
        val userType = runCatching { UserType.valueOf(typeStr) }.getOrDefault(UserType.CITIZEN)

        val rawLocations = data["preferredLocations"] as? List<Map<String, Any>> ?: emptyList()
        val locations = rawLocations.map { loc ->
            PreferredLocation(
                id = (loc["id"] as? String) ?: UUID.randomUUID().toString(),
                latitude = (loc["lat"] as? Double) ?: 0.0,
                longitude = (loc["lng"] as? Double) ?: 0.0,
                label = (loc["label"] as? String).orEmpty()
            )
        }

        val rawSchedules = data["schedules"] as? List<Map<String, Any>> ?: emptyList()
        val schedules = rawSchedules.mapNotNull { s ->
            val day = s["dayOfWeek"] as? String ?: return@mapNotNull null
            Schedule(
                dayOfWeek = runCatching { DayOfWeek.valueOf(day) }.getOrElse { return@mapNotNull null },
                startTime = (s["startTime"] as? String).orEmpty(),
                endTime = (s["endTime"] as? String).orEmpty()
            )
        }

        return User(
            id = uid,
            name = (data["name"] as? String).orEmpty(),
            email = (data["email"] as? String).orEmpty(),
            phone = (data["phone"] as? String).orEmpty(),
            userType = userType,
            preferredLocations = locations,
            availableSchedules = schedules,
            rating = ((data["rating"] as? Number)?.toFloat()) ?: 0f,
            ratingCount = ((data["ratingCount"] as? Number)?.toInt()) ?: 0,
            preferredRecyclingCenterId = (data["preferredRecyclingCenterId"] as? String)?.ifEmpty { null }
        )
    }

    private fun mapFirebaseError(e: Throwable): AuthError = when (e) {
        is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
        is FirebaseAuthInvalidUserException -> AuthError.UserNotFound
        is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse
        is FirebaseNetworkException -> AuthError.NetworkError
        is ProfileNotFoundException -> AuthError.ProfileNotFound
        else -> AuthError.Unknown(e)
    }

    companion object {
        private const val COLLECTION_USERS = "users"
    }
}
