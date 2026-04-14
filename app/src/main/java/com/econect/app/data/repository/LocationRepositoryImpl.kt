package com.econect.app.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.RecyclingCenter
import com.econect.app.domain.model.Result
import com.econect.app.domain.repository.LocationRepository
import com.econect.app.domain.repository.RecyclingCenterRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val recyclingCenterRepository: RecyclingCenterRepository,
    @ApplicationContext private val context: Context
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<LatLng> = withContext(Dispatchers.IO) {
        runCatching {
            val cancellationTokenSource = CancellationTokenSource()
            val location = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                .await()
            requireNotNull(location) { "No se pudo obtener la ubicación actual" }
            LatLng(location.latitude, location.longitude)
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }

    override suspend fun getRecyclingCenters(
        latLng: LatLng,
        radiusKm: Double
    ): Result<List<RecyclingCenter>> = withContext(Dispatchers.IO) {
        when (val all = recyclingCenterRepository.getAll()) {
            is Result.Success -> {
                val nearby = all.data.filter { center ->
                    haversineDistanceKm(latLng, center.location) <= radiusKm
                }
                Result.Success(nearby)
            }
            is Result.Error -> all
            Result.Loading -> Result.Loading
        }
    }

    override suspend fun reverseGeocode(latLng: LatLng): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val geocoder = Geocoder(context, Locale.getDefault())
            val fallback = "${latLng.latitude}, ${latLng.longitude}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                        continuation.resume(
                            addresses.firstOrNull()?.getAddressLine(0) ?: fallback
                        )
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    ?.firstOrNull()?.getAddressLine(0) ?: fallback
            }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )
    }

    /** Fórmula de Haversine: distancia en km entre dos coordenadas. */
    private fun haversineDistanceKm(from: LatLng, to: LatLng): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLng = Math.toRadians(to.longitude - from.longitude)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
            sin(dLng / 2).pow(2)
        return earthRadiusKm * 2 * asin(sqrt(a))
    }
}
