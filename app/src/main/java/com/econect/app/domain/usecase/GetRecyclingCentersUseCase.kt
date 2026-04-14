package com.econect.app.domain.usecase

import com.econect.app.domain.model.RecyclingCenter
import com.econect.app.domain.model.Result
import com.econect.app.domain.repository.RecyclingCenterRepository
import javax.inject.Inject

class GetRecyclingCentersUseCase @Inject constructor(
    private val repository: RecyclingCenterRepository
) {
    suspend operator fun invoke(): Result<List<RecyclingCenter>> = repository.getAll()
}
