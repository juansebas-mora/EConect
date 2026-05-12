package com.econect.app.data.repository

import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialPrice
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
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

    override suspend fun save(recyclingCenter: RecyclingCenter): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val data = hashMapOf(
                "name" to recyclingCenter.name,
                "address" to recyclingCenter.address,
                "location" to mapOf(
                    "lat" to recyclingCenter.location.latitude,
                    "lng" to recyclingCenter.location.longitude
                ),
                "phone" to recyclingCenter.phone,
                "ownerUid" to recyclingCenter.ownerUid,
                "materialPrices" to recyclingCenter.acceptedMaterials.map { price ->
                    mapOf(
                        "type" to price.materialType.name,
                        "price" to price.pricePerUnit,
                        "unit" to price.unit.name
                    )
                }
            )
            firestore.collection(COLLECTION_RECYCLING_CENTERS).document(recyclingCenter.id).set(data).await()
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it) }
        )
    }

    override suspend fun getByOwnerUid(ownerUid: String): Result<RecyclingCenter?> = withContext(Dispatchers.IO) {
        runCatching {
            val snapshot = firestore.collection(COLLECTION_RECYCLING_CENTERS)
                .whereEqualTo("ownerUid", ownerUid)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.let { doc ->
                mapToRecyclingCenter(doc.id, doc.data ?: emptyMap())
            }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapToRecyclingCenter(id: String, data: Map<String, Any>): RecyclingCenter? {
        val locationData = data["location"] as? Map<String, Any> ?: return null
        val materialPricesData = data["materialPrices"] as? List<Map<String, Any>> ?: emptyList()
        val acceptedMaterials = materialPricesData.mapNotNull { priceData ->
            val typeStr = priceData["type"] as? String ?: return@mapNotNull null
            val price = (priceData["price"] as? Number)?.toDouble() ?: return@mapNotNull null
            val unitStr = priceData["unit"] as? String ?: return@mapNotNull null
            val materialType = try {
                MaterialType.valueOf(typeStr.uppercase())
            } catch (e: IllegalArgumentException) {
                return@mapNotNull null
            }
            val unit = try {
                MaterialUnit.valueOf(unitStr.uppercase())
            } catch (e: IllegalArgumentException) {
                return@mapNotNull null
            }
            MaterialPrice(materialType, price, unit)
        }
        return RecyclingCenter(
            id = id,
            name = (data["name"] as? String).orEmpty(),
            address = (data["address"] as? String).orEmpty(),
            location = LatLng(
                latitude = (locationData["lat"] as? Double) ?: 0.0,
                longitude = (locationData["lng"] as? Double) ?: 0.0
            ),
            phone = (data["phone"] as? String).orEmpty(),
            ownerUid = (data["ownerUid"] as? String).orEmpty(),
            acceptedMaterials = acceptedMaterials
        )
    }

    companion object {
        private const val COLLECTION_RECYCLING_CENTERS = "recyclingCenters"
    }
}
