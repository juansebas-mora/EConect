package com.econect.app.domain.repository

import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.RecyclingCenter
import com.econect.app.domain.model.Result

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<LatLng>
    suspend fun getRecyclingCenters(latLng: LatLng, radiusKm: Double): Result<List<RecyclingCenter>>
    suspend fun reverseGeocode(latLng: LatLng): Result<String>
}
