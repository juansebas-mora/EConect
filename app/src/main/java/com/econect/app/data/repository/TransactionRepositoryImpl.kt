package com.econect.app.data.repository

import com.econect.app.data.local.db.dao.TransactionDao
import com.econect.app.data.model.toDomain
import com.econect.app.data.model.toEntity
import com.econect.app.domain.model.MaterialQuantity
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Transaction
import com.econect.app.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao,
    private val firestore: FirebaseFirestore
) : TransactionRepository {

    override fun getTransactionsByRecycler(recyclerId: String): Flow<List<Transaction>> =
        dao.observeByRecycler(recyclerId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun syncRecyclerTransactions(recyclerId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val snapshot = firestore.collection(COLLECTION)
                    .whereEqualTo("recyclerId", recyclerId)
                    .get()
                    .await()
                val entities = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    mapDocumentToTransaction(doc.id, data).toEntity()
                }
                dao.insertAll(entities)
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    // --- helpers privados ---

    private fun mapDocumentToTransaction(id: String, data: Map<String, Any>): Transaction =
        Transaction(
            id = id,
            routeId = data["routeId"] as? String ?: "",
            recyclerId = data["recyclerId"] as? String ?: "",
            citizenId = data["citizenId"] as? String ?: "",
            materialId = data["materialId"] as? String ?: "",
            confirmedQuantity = MaterialQuantity(
                value = (data["confirmedQuantityValue"] as? Number)?.toDouble() ?: 0.0,
                unit = runCatching {
                    MaterialUnit.valueOf(data["confirmedQuantityUnit"] as? String ?: "")
                }.getOrDefault(MaterialUnit.KG)
            ),
            pricePerUnit = (data["pricePerUnit"] as? Number)?.toDouble() ?: 0.0,
            totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0,
            recyclingCenterId = data["recyclingCenterId"] as? String ?: "",
            completedAt = (data["completedAt"] as? Number)?.toLong() ?: 0L
        )

    companion object {
        private const val COLLECTION = "transactions"
    }
}
