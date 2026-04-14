package com.econect.app.data.repository

import com.econect.app.data.local.db.dao.RouteDao
import com.econect.app.data.model.toDomain
import com.econect.app.data.model.toEntity
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Route
import com.econect.app.domain.model.RouteStatus
import com.econect.app.domain.model.RouteStop
import com.econect.app.domain.repository.RouteRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val dao: RouteDao,
    private val firestore: FirebaseFirestore
) : RouteRepository {

    override fun getRoutesByRecycler(recyclerId: String): Flow<List<Route>> =
        dao.observeByRecycler(recyclerId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun syncRecyclerRoutes(recyclerId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val snapshot = firestore.collection(COLLECTION)
                    .whereEqualTo("recyclerId", recyclerId)
                    .get()
                    .await()
                val entities = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    mapDocumentToRoute(doc.id, data).toEntity()
                }
                dao.insertAll(entities)
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    override suspend fun updateRouteStatus(routeId: String, status: RouteStatus): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                firestore.collection(COLLECTION)
                    .document(routeId)
                    .update("status", status.name)
                    .await()
                val entity = dao.getById(routeId)
                if (entity != null) dao.insert(entity.copy(status = status.name))
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(it) }
            )
        }

    // --- helpers privados ---

    private fun mapDocumentToRoute(id: String, data: Map<String, Any>): Route {
        val stopsRaw = data["stops"] as? List<*> ?: emptyList<Any>()
        val stops = stopsRaw.mapNotNull { item ->
            val stop = item as? Map<*, *> ?: return@mapNotNull null
            RouteStop(
                materialId = stop["materialId"] as? String ?: "",
                citizenId = stop["citizenId"] as? String ?: "",
                scheduledTime = stop["scheduledTime"] as? String ?: "",
                location = LatLng(
                    latitude = (stop["lat"] as? Number)?.toDouble() ?: 0.0,
                    longitude = (stop["lng"] as? Number)?.toDouble() ?: 0.0
                ),
                materialType = runCatching {
                    MaterialType.valueOf(stop["materialType"] as? String ?: "")
                }.getOrDefault(MaterialType.OTHER)
            )
        }
        return Route(
            id = id,
            recyclerId = data["recyclerId"] as? String ?: "",
            date = data["date"] as? String ?: "",
            status = runCatching {
                RouteStatus.valueOf(data["status"] as? String ?: "")
            }.getOrDefault(RouteStatus.PENDING),
            stops = stops
        )
    }

    companion object {
        private const val COLLECTION = "routes"
    }
}
