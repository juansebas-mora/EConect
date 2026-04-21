package com.econect.app.data.repository

import com.econect.app.data.local.db.dao.RecyclableMaterialDao
import com.econect.app.data.model.toDomain
import com.econect.app.data.model.toEntity
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialCondition
import com.econect.app.domain.model.MaterialQuantity
import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Result
import com.econect.app.domain.repository.MaterialRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MaterialRepositoryImpl @Inject constructor(
    private val dao: RecyclableMaterialDao,
    private val firestore: FirebaseFirestore
) : MaterialRepository {

    override suspend fun addMaterial(material: RecyclableMaterial): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                firestore.collection(COLLECTION)
                    .document(material.id)
                    .set(material.toFirestoreMap())
                    .await()
                dao.insert(material.toEntity())
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    override fun getMaterialsByCitizen(citizenId: String): Flow<List<RecyclableMaterial>> =
        dao.observeByCitizen(citizenId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getMaterialById(id: String): Result<RecyclableMaterial> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cached = dao.getById(id)
                if (cached != null) return@runCatching cached.toDomain()

                val doc = firestore.collection(COLLECTION).document(id).get().await()
                val data = requireNotNull(doc.data) { "Material no encontrado: $id" }
                mapDocumentToMaterial(id, data).also { dao.insert(it.toEntity()) }
            }.fold(
                onSuccess = { Result.Success(it) },
                onFailure = { Result.Error(it) }
            )
        }

    override suspend fun updateMaterialStatus(id: String, status: MaterialStatus): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                firestore.collection(COLLECTION)
                    .document(id)
                    .update("status", status.name)
                    .await()
                val entity = dao.getById(id)
                if (entity != null) dao.insert(entity.copy(status = status.name))
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    override suspend fun deleteMaterial(id: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                firestore.collection(COLLECTION).document(id).delete().await()
                dao.deleteById(id)
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    override suspend fun syncCitizenMaterials(citizenId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val snapshot = firestore.collection(COLLECTION)
                    .whereEqualTo("citizenId", citizenId)
                    .get()
                    .await()
                val entities = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    mapDocumentToMaterial(doc.id, data).toEntity()
                }
                dao.insertAll(entities)
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    // --- helpers privados ---

    private fun RecyclableMaterial.toFirestoreMap(): Map<String, Any> = mapOf(
        "citizenId" to citizenId,
        "type" to type.name,
        "condition" to condition.name,
        "quantityValue" to quantity.value,
        "quantityUnit" to quantity.unit.name,
        "pickupLat" to pickupLocation.latitude,
        "pickupLng" to pickupLocation.longitude,
        "status" to status.name,
        "createdAt" to createdAt
    )

    private fun mapDocumentToMaterial(id: String, data: Map<String, Any>): RecyclableMaterial =
        RecyclableMaterial(
            id = id,
            citizenId = data["citizenId"] as? String ?: "",
            type = MaterialType.valueOf(data["type"] as? String ?: MaterialType.OTHER.name),
            condition = MaterialCondition.valueOf(
                data["condition"] as? String ?: MaterialCondition.MIXED.name
            ),
            quantity = MaterialQuantity(
                value = (data["quantityValue"] as? Number)?.toDouble() ?: 0.0,
                unit = MaterialUnit.valueOf(data["quantityUnit"] as? String ?: MaterialUnit.KG.name)
            ),
            pickupLocation = LatLng(
                latitude = (data["pickupLat"] as? Number)?.toDouble() ?: 0.0,
                longitude = (data["pickupLng"] as? Number)?.toDouble() ?: 0.0
            ),
            status = MaterialStatus.valueOf(
                data["status"] as? String ?: MaterialStatus.AVAILABLE.name
            ),
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L
        )
    override suspend fun getAvailableMaterials(): Result<List<RecyclableMaterial>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val snapshot = firestore.collection(COLLECTION)
                    .whereEqualTo("status", MaterialStatus.AVAILABLE.name)
                    .get()
                    .await()
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    mapDocumentToMaterial(doc.id, data)
                }
            }.fold(
                onSuccess = { Result.Success(it) },
                onFailure = { Result.Error(it) }
            )
        }

    override suspend fun assignMaterial(materialId: String, recyclerId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                firestore.collection(COLLECTION)
                    .document(materialId)
                    .update(
                        mapOf(
                            "status" to MaterialStatus.ASSIGNED.name,
                            "recyclerId" to recyclerId
                        )
                    )
                    .await()
                val entity = dao.getById(materialId)
                if (entity != null) dao.insert(entity.copy(status = MaterialStatus.ASSIGNED.name))
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }
    companion object {
        private const val COLLECTION = "materials"
    }
}
