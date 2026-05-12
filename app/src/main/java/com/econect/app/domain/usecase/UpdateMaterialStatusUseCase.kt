package com.econect.app.domain.usecase

import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.Result
import com.econect.app.domain.repository.MaterialRepository
import javax.inject.Inject

class UpdateMaterialStatusUseCase @Inject constructor(
    private val repository: MaterialRepository
) {

    suspend operator fun invoke(id: String, status: MaterialStatus): Result<Unit> {
        if (id.isBlank()) {
            return Result.Error(IllegalArgumentException("El ID del material no puede estar vacío"))
        }
        return repository.updateMaterialStatus(id, status)
    }
}
