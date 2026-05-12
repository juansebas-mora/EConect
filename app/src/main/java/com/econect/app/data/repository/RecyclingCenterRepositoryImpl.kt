package com.econect.app.data.repository

import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.RecyclingCenter
import com.econect.app.domain.model.Result
import com.econect.app.domain.repository.RecyclingCenterRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecyclingCenterRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RecyclingCenterRepository {

    override suspend fun getAll(): Result<List<RecyclingCenter>> = withContext(Dispatchers.IO) {
        runCatching {
            val snapshot = firestore.collection(COLLECTION_RECYCLING_CENTERS).get().await()
            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                mapToRecyclingCenter(doc.id, data)
            }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }

    override suspend fun getById(id: String): Result<RecyclingCenter?> = withContext(Dispatchers.IO) {
        runCatching {
            val doc = firestore.collection(COLLECTION_RECYCLING_CENTERS).document(id).get().await()
            doc.data?.let { mapToRecyclingCenter(id, it) }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapToRecyclingCenter(id: String, data: Map<String, Any>): RecyclingCenter? {
        val locationData = data["location"] as? Map<String, Any> ?: return null
        return RecyclingCenter(
            id = id,
            name = (data["name"] as? String).orEmpty(),
            address = (data["address"] as? String).orEmpty(),
            location = LatLng(
                latitude = (locationData["lat"] as? Double) ?: 0.0,
                longitude = (locationData["lng"] as? Double) ?: 0.0
            ),
            phone = (data["phone"] as? String).orEmpty()
        )
    }

    companion object {
        private const val COLLECTION_RECYCLING_CENTERS = "recyclingCenters"
    }
}
