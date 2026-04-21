package com.econect.app.domain.repository

import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface MaterialRepository {

    /** Escribe el material en Firestore y lo refleja en Room. */
    suspend fun addMaterial(material: RecyclableMaterial): Result<Unit>

    /**
     * Emite la lista cacheada en Room (offline-first).
     * Llamar [syncCitizenMaterials] para refrescar desde Firestore.
     */
    fun getMaterialsByCitizen(citizenId: String): Flow<List<RecyclableMaterial>>

    /** Busca primero en Room; si no existe, consulta Firestore. */
    suspend fun getMaterialById(id: String): Result<RecyclableMaterial>

    /** Actualiza el estado en Firestore y luego en Room. */
    suspend fun updateMaterialStatus(id: String, status: MaterialStatus): Result<Unit>

    /** Elimina de Firestore y luego de Room. */
    suspend fun deleteMaterial(id: String): Result<Unit>

    /** Descarga materiales del ciudadano desde Firestore y actualiza Room. */
    suspend fun syncCitizenMaterials(citizenId: String): Result<Unit>
    suspend fun getAvailableMaterials(): Result<List<RecyclableMaterial>>

    suspend fun assignMaterial(materialId: String, recyclerId: String): Result<Unit>

}
