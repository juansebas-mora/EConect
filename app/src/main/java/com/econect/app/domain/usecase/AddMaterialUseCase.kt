package com.econect.app.domain.usecase

import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Result
import com.econect.app.domain.repository.MaterialRepository
import java.util.UUID
import javax.inject.Inject

class AddMaterialUseCase @Inject constructor(
    private val repository: MaterialRepository
) {

    /**
     * Valida y registra un nuevo material reciclable.
     *
     * Validaciones:
     * - La cantidad debe ser positiva (> 0).
     * - La ubicación de recogida no puede ser (0, 0) (indica ausencia de selección).
     */
    suspend operator fun invoke(material: RecyclableMaterial): Result<Unit> {
        if (material.quantity.value <= 0.0) {
            return Result.Error(
                IllegalArgumentException("La cantidad debe ser mayor que cero")
            )
        }

        val locationIsEmpty = material.pickupLocation.latitude == 0.0 &&
            material.pickupLocation.longitude == 0.0
        if (locationIsEmpty) {
            return Result.Error(
                IllegalArgumentException("La ubicación de recogida es obligatoria")
            )
        }

        val prepared = material.copy(
            id = material.id.ifBlank { UUID.randomUUID().toString() },
            status = MaterialStatus.AVAILABLE,
            createdAt = if (material.createdAt == 0L) System.currentTimeMillis() else material.createdAt
        )

        return repository.addMaterial(prepared)
    }
}
