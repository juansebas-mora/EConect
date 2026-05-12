package com.econect.app.presentation.citizen.material

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialCondition
import com.econect.app.domain.model.MaterialQuantity
import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.PreferredLocation
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Result
import com.econect.app.domain.usecase.AddMaterialUseCase
import com.econect.app.domain.usecase.GetUserProfileUseCase
import com.econect.app.ml.MaterialClassifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClasificacionEstado {
    object Idle : ClasificacionEstado()
    object Clasificando : ClasificacionEstado()
    data class Resultado(val tipo: MaterialType, val label: String, val confianza: Float) : ClasificacionEstado()
    data class Error(val mensaje: String) : ClasificacionEstado()
}

data class AddMaterialUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val capturedImageUri: Uri? = null,
    val clasificacion: ClasificacionEstado = ClasificacionEstado.Idle,
    val detalles: String = "",
    val preferredLocations: List<PreferredLocation> = emptyList(),
    val selectedLocationId: String? = null,
    val useCustomLocation: Boolean = false,
    val customPickupLocation: LatLng? = null,
    val showValidationErrors: Boolean = false
) {
    val efectivePickupLocation: LatLng? get() = when {
        useCustomLocation -> customPickupLocation
        selectedLocationId != null -> preferredLocations.find { it.id == selectedLocationId }?.let { LatLng(it.latitude, it.longitude) }
        else -> null
    }
    val tipoDetectado: MaterialType? get() = (clasificacion as? ClasificacionEstado.Resultado)?.tipo
    val canSubmit: Boolean get() = !isLoading && capturedImageUri != null && tipoDetectado != null && efectivePickupLocation != null
}

@HiltViewModel
class AddMaterialViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val addMaterialUseCase: AddMaterialUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val materialClassifier: MaterialClassifier
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMaterialUiState())
    val uiState: StateFlow<AddMaterialUiState> = _uiState.asStateFlow()
    private var citizenId: String = ""

    init { loadPreferredLocations() }

    private fun loadPreferredLocations() {
        viewModelScope.launch {
            val uid = userDataStore.uidFlow.first() ?: return@launch
            citizenId = uid
            when (val result = getUserProfileUseCase(uid)) {
                is Result.Success -> {
                    val locations = result.data.preferredLocations
                    _uiState.update { it.copy(preferredLocations = locations, selectedLocationId = locations.firstOrNull()?.id, useCustomLocation = locations.isEmpty()) }
                }
                else -> Unit
            }
        }
    }

    fun setImageUri(uri: Uri) = _uiState.update { it.copy(capturedImageUri = uri, clasificacion = ClasificacionEstado.Idle) }

    fun clasificarConModelo(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(clasificacion = ClasificacionEstado.Clasificando) }
            try {
                val resultado = materialClassifier.classify(context, uri)
                _uiState.update {
                    it.copy(clasificacion = ClasificacionEstado.Resultado(
                        tipo = labelToMaterialType(resultado.label),
                        label = resultado.label,
                        confianza = resultado.confidence
                    ))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(clasificacion = ClasificacionEstado.Error("No se pudo clasificar: ${e.message}")) }
            }
        }
    }

    private fun labelToMaterialType(label: String): MaterialType = when (label.lowercase()) {
        "plastic" -> MaterialType.PLASTIC
        "glass", "vidrio" -> MaterialType.GLASS
        "metal" -> MaterialType.METAL
        "paper", "papel" -> MaterialType.PAPER
        "cardboard", "carton", "cartón" -> MaterialType.CARDBOARD
        "organic", "organico" -> MaterialType.ORGANIC
        "electronic", "electronico" -> MaterialType.ELECTRONIC
        else -> MaterialType.OTHER
    }

    fun setDetalles(texto: String) = _uiState.update { it.copy(detalles = texto) }
    fun selectPreferredLocation(id: String) = _uiState.update { it.copy(selectedLocationId = id, useCustomLocation = false, customPickupLocation = null) }
    fun selectCustomLocation() = _uiState.update { it.copy(selectedLocationId = null, useCustomLocation = true) }
    fun setCustomPickupLocation(location: LatLng) = _uiState.update { it.copy(customPickupLocation = location) }
    fun clearError() = _uiState.update { it.copy(error = null) }

    fun addMaterial() {
        val state = _uiState.value
        if (!state.canSubmit) { _uiState.update { it.copy(showValidationErrors = true) }; return }
        val tipo = requireNotNull(state.tipoDetectado)
        val location = requireNotNull(state.efectivePickupLocation)
        val material = RecyclableMaterial(id = "", citizenId = citizenId, type = tipo, condition = MaterialCondition.MIXED, quantity = MaterialQuantity(value = 1.0, unit = MaterialUnit.UNITS), pickupLocation = location, status = MaterialStatus.AVAILABLE, createdAt = 0L)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = addMaterialUseCase(material)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, success = true) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = "No se pudo registrar el material. Intenta de nuevo.") }
                Result.Loading -> Unit
            }
        }
    }
}
