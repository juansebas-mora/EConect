package com.econect.app.domain.repository

import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Route
import com.econect.app.domain.model.RouteStatus
import kotlinx.coroutines.flow.Flow

interface RouteRepository {

    /**
     * Emite la lista cacheada en Room (offline-first).
     * Llamar [syncRecyclerRoutes] para refrescar desde Firestore.
     */
    fun getRoutesByRecycler(recyclerId: String): Flow<List<Route>>

    /** Descarga rutas del reciclador desde Firestore y actualiza Room. */
    suspend fun syncRecyclerRoutes(recyclerId: String): Result<Unit>

    /** Actualiza el estado de la ruta en Firestore y luego en Room. */
    suspend fun updateRouteStatus(routeId: String, status: RouteStatus): Result<Unit>
}
