package com.econect.app.domain.usecase

import com.econect.app.domain.model.Route
import com.econect.app.domain.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecyclerRoutesUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    operator fun invoke(recyclerId: String): Flow<List<Route>> =
        repository.getRoutesByRecycler(recyclerId)
}
