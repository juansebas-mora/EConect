package com.econect.app.presentation.citizen.material

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UnitSubtype(val label: String) {
    BOXES("Cajas"),
    BOTTLES("Botellas"),
    BAGS("Bolsas")
}

data class AddMaterialUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,

    // Campos del formulario
    val selectedType: MaterialType? = null,
    val selectedCondition: MaterialCondition? = null,
    val useKg: Boolean = true,
    val quantityText: String = "",
    val unitSubtype: UnitSubtype = UnitSubtype.BOXES,

    // Ubicación
    val preferredLocations: List<PreferredLocation> = emptyList(),
    val selectedLocationId: String? = null,   // null = "Otra ubicación" activa
    val useCustomLocation: Boolean = false,
    val customPickupLocation: LatLng? = null,

    // Errores de validación (se muestran tras primer intento de envío)
    val showValidationErrors: Boolean = false
) {
    val quantityError: String? get() = if (!showValidationErrors) null else
        when {
            quantityText.isBlank() -> "Ingresa una cantidad"
            quantityText.toDoubleOrNull() == null -> "Ingresa un número válido"
            (quantityText.toDoubleOrNull() ?: 0.0) <= 0.0 -> "La cantidad debe ser mayor que cero"
            else -> null
        }

    val effectivePickupLocation: LatLng? get() = when {
        useCustomLocation -> customPickupLocation
        selectedLocationId != null -> preferredLocations
            .find { it.id == selectedLocationId }
            ?.let { LatLng(it.latitude, it.longitude) }
        else -> null
    }

    val canSubmit: Boolean get() =
        !isLoading &&
        selectedType != null &&
        selectedCondition != null &&
        (quantityText.toDoubleOrNull() ?: 0.0) > 0.0 &&
        effectivePickupLocation != null
}

@HiltViewModel
class AddMaterialViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val addMaterialUseCase: AddMaterialUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMaterialUiState())
    val uiState: StateFlow<AddMaterialUiState> = _uiState.asStateFlow()

    private var citizenId: String = ""

    init {
        loadPreferredLocations()
    }

    private fun loadPreferredLocations() {
        viewModelScope.launch {
            val uid = userDataStore.uidFlow.first() ?: return@launch
            citizenId = uid
            when (val result = getUserProfileUseCase(uid)) {
                is Result.Success -> {
                    val locations = result.data.preferredLocations
                    _uiState.update { state ->
                        state.copy(
                            preferredLocations = locations,
                            selectedLocationId = locations.firstOrNull()?.id,
                            useCustomLocation = locations.isEmpty()
                        )
                    }
                }
                is Result.Error -> Unit // No bloquear el formulario por esto
                Result.Loading -> Unit
            }
        }
    }

    fun selectType(type: MaterialType) = _uiState.update { it.copy(selectedType = type) }

    fun selectCondition(condition: MaterialCondition) =
        _uiState.update { it.copy(selectedCondition = condition) }

    fun setUseKg(useKg: Boolean) = _uiState.update {
        it.copy(useKg = useKg, quantityText = "")
    }

    fun setQuantityText(text: String) = _uiState.update { it.copy(quantityText = text) }

    fun setUnitSubtype(subtype: UnitSubtype) = _uiState.update { it.copy(unitSubtype = subtype) }

    fun selectPreferredLocation(locationId: String) = _uiState.update {
        it.copy(
            selectedLocationId = locationId,
            useCustomLocation = false,
            customPickupLocation = null
        )
    }

    fun selectCustomLocation() = _uiState.update {
        it.copy(selectedLocationId = null, useCustomLocation = true)
    }

    fun setCustomPickupLocation(location: LatLng) =
        _uiState.update { it.copy(customPickupLocation = location) }

    fun clearError() = _uiState.update { it.copy(error = null) }

    fun addMaterial() {
        val state = _uiState.value

        if (!state.canSubmit) {
            _uiState.update { it.copy(showValidationErrors = true) }
            return
        }

        val type = requireNotNull(state.selectedType)
        val condition = requireNotNull(state.selectedCondition)
        val quantity = requireNotNull(state.quantityText.toDoubleOrNull())
        val location = requireNotNull(state.effectivePickupLocation)
        val unit = if (state.useKg) MaterialUnit.KG else MaterialUnit.UNITS

        val material = RecyclableMaterial(
            id = "",
            citizenId = citizenId,
            type = type,
            condition = condition,
            quantity = MaterialQuantity(value = quantity, unit = unit),
            pickupLocation = location,
            status = MaterialStatus.AVAILABLE,
            createdAt = 0L
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = addMaterialUseCase(material)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, success = true) }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = "No se pudo registrar el material. Intenta de nuevo.")
                }
                Result.Loading -> Unit
            }
        }
    }
}
