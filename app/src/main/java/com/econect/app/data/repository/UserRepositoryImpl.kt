package com.econect.app.data.repository

import com.econect.app.data.local.db.dao.UserDao
import com.econect.app.data.model.toDomain
import com.econect.app.data.model.toEntity
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.PreferredLocation
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.model.User
import com.econect.app.domain.model.UserType
import com.econect.app.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUserProfile(uid: String): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            val cached = userDao.getById(uid)?.toDomain()
            if (cached != null) {
                // Devolver caché inmediato y actualizar en segundo plano
                fetchAndCache(uid)
                cached
            } else {
                fetchAndCache(uid)
            }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val data = buildMap<String, Any?> {
                put("name", user.name)
                put("phone", user.phone)
                put("rating", user.rating)
                put("ratingCount", user.ratingCount)
                put("preferredRecyclingCenterId", user.preferredRecyclingCenterId ?: "")
            }
            firestore.collection(COLLECTION_USERS).document(user.id).update(data).await()
            userDao.insert(user.toEntity())
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it) }
        )
    }

    override suspend fun addPreferredLocation(
        uid: String,
        latLng: LatLng,
        label: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val locationId = UUID.randomUUID().toString()
            val user = fetchAndCache(uid)
            val newLocation = PreferredLocation(
                id = locationId,
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                label = label
            )
            val updatedLocations = (user.preferredLocations + newLocation)
                .map { it.toFirestoreMap() }
            firestore.collection(COLLECTION_USERS).document(uid)
                .update("preferredLocations", updatedLocations).await()
            userDao.insert(user.copy(preferredLocations = user.preferredLocations + newLocation).toEntity())
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it) }
        )
    }

    override suspend fun removePreferredLocation(
        uid: String,
        locationId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val user = fetchAndCache(uid)
            val updatedLocations = user.preferredLocations
                .filter { it.id != locationId }
                .map { it.toFirestoreMap() }
            firestore.collection(COLLECTION_USERS).document(uid)
                .update("preferredLocations", updatedLocations).await()
            userDao.insert(
                user.copy(
                    preferredLocations = user.preferredLocations.filter { it.id != locationId }
                ).toEntity()
            )
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it) }
        )
    }

    override suspend fun addSchedule(uid: String, schedule: Schedule): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val user = fetchAndCache(uid)
                val updatedSchedules = (user.availableSchedules + schedule)
                    .map { it.toFirestoreMap() }
                firestore.collection(COLLECTION_USERS).document(uid)
                    .update("schedules", updatedSchedules).await()
                userDao.insert(
                    user.copy(availableSchedules = user.availableSchedules + schedule).toEntity()
                )
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    override suspend fun removeSchedule(uid: String, scheduleId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val user = fetchAndCache(uid)
                val updatedSchedules = user.availableSchedules
                    .filter { it.scheduleId() != scheduleId }
                    .map { it.toFirestoreMap() }
                firestore.collection(COLLECTION_USERS).document(uid)
                    .update("schedules", updatedSchedules).await()
                userDao.insert(
                    user.copy(
                        availableSchedules = user.availableSchedules
                            .filter { it.scheduleId() != scheduleId }
                    ).toEntity()
                )
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    // --- helpers privados ---

    private suspend fun fetchAndCache(uid: String): User {
        val doc = firestore.collection(COLLECTION_USERS).document(uid).get().await()
        val data = doc.data ?: error("Documento de usuario no encontrado: $uid")
        val user = mapDocumentToUser(uid, data)
        userDao.insert(user.toEntity())
        return user
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
                dayOfWeek = runCatching { DayOfWeek.valueOf(day) }
                    .getOrElse { return@mapNotNull null },
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

    companion object {
        private const val COLLECTION_USERS = "users"
    }
}

private fun PreferredLocation.toFirestoreMap(): Map<String, Any> = mapOf(
    "id" to id,
    "lat" to latitude,
    "lng" to longitude,
    "label" to label
)

private fun Schedule.toFirestoreMap(): Map<String, Any> = mapOf(
    "dayOfWeek" to dayOfWeek.name,
    "startTime" to startTime,
    "endTime" to endTime
)

/** Clave compuesta estable para identificar un horario: "${dayOfWeek}_${startTime}" */
private fun Schedule.scheduleId(): String = "${dayOfWeek.name}_$startTime"
