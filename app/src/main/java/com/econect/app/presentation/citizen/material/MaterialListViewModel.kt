package com.econect.app.presentation.citizen.material

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.repository.MaterialRepository
import com.econect.app.domain.usecase.GetCitizenMaterialsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

data class MaterialListUiState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val allMaterials: List<RecyclableMaterial> = emptyList(),
    val activeFilter: MaterialStatus? = null,
    val geocodedAddresses: Map<String, String> = emptyMap(),
    val selectedMaterial: RecyclableMaterial? = null,
    val isDeleting: Boolean = false
) {
    val filteredMaterials: List<RecyclableMaterial> get() =
        if (activeFilter == null) allMaterials
        else allMaterials.filter { it.status == activeFilter }
}

@HiltViewModel
class MaterialListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    private val getCitizenMaterialsUseCase: GetCitizenMaterialsUseCase,
    private val materialRepository: MaterialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialListUiState())
    val uiState: StateFlow<MaterialListUiState> = _uiState.asStateFlow()

    // Caché de geocoding: "lat,lng" -> dirección
    private val geocodeCache = mutableMapOf<String, String>()

    init {
        loadMaterials()
    }

    private fun loadMaterials() {
        viewModelScope.launch {
            val uid = userDataStore.uidFlow.first() ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no encontrada") }
                return@launch
            }

            // Observar caché Room (offline-first)
            launch {
                getCitizenMaterialsUseCase(uid).collect { materials ->
                    _uiState.update { it.copy(isLoading = false, allMaterials = materials) }
                    geocodeNewLocations(materials)
                }
            }

            // Sync Firestore en background
            _uiState.update { it.copy(isSyncing = true) }
            materialRepository.syncCitizenMaterials(uid)
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    fun setFilter(status: MaterialStatus?) =
        _uiState.update { it.copy(activeFilter = status) }

    fun selectMaterial(material: RecyclableMaterial) =
        _uiState.update { it.copy(selectedMaterial = material) }

    fun clearSelectedMaterial() =
        _uiState.update { it.copy(selectedMaterial = null) }

    fun deleteMaterial(materialId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            when (materialRepository.deleteMaterial(materialId)) {
                is com.econect.app.domain.model.Result.Success -> _uiState.update {
                    it.copy(isDeleting = false, selectedMaterial = null)
                }
                is com.econect.app.domain.model.Result.Error -> _uiState.update {
                    it.copy(isDeleting = false, error = "No se pudo eliminar el material")
                }
                com.econect.app.domain.model.Result.Loading -> Unit
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    private fun geocodeNewLocations(materials: List<RecyclableMaterial>) {
        val uncached = materials.filter { m ->
            val key = locationKey(m.pickupLocation)
            !geocodeCache.containsKey(key)
        }
        if (uncached.isEmpty()) return

        viewModelScope.launch {
            uncached.forEach { material ->
                val key = locationKey(material.pickupLocation)
                val address = resolveAddress(material.pickupLocation)
                geocodeCache[key] = address
            }
            _uiState.update { it.copy(geocodedAddresses = geocodeCache.toMap()) }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun resolveAddress(latLng: LatLng): String = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext formatCoords(latLng)
        runCatching {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            results?.firstOrNull()?.let { addr ->
                listOfNotNull(addr.thoroughfare, addr.subLocality, addr.locality)
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString(", ")
                    .ifBlank { formatCoords(latLng) }
            } ?: formatCoords(latLng)
        }.getOrElse { formatCoords(latLng) }
    }

    private fun locationKey(latLng: LatLng) = "%.5f,%.5f".format(latLng.latitude, latLng.longitude)

    private fun formatCoords(latLng: LatLng) =
        "%.4f, %.4f".format(latLng.latitude, latLng.longitude)
}
