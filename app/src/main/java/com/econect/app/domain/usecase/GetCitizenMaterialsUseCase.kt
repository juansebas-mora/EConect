package com.econect.app.domain.usecase

import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCitizenMaterialsUseCase @Inject constructor(
    private val repository: MaterialRepository
) {

    /**
     * Retorna un Flow con los materiales del ciudadano desde caché local (Room).
     * La sincronización con Firestore se dispara por separado al abrir la pantalla.
     */
    operator fun invoke(citizenId: String): Flow<List<RecyclableMaterial>> =
        repository.getMaterialsByCitizen(citizenId)
}
