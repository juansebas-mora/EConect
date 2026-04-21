package com.econect.app.presentation.recycler.materials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Result
import com.econect.app.domain.repository.MaterialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AvailableMaterialsUiState(
    val isLoading: Boolean = true,
    val materials: List<RecyclableMaterial> = emptyList(),
    val acceptingId: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class AvailableMaterialsViewModel @Inject constructor(
    private val materialRepository: MaterialRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvailableMaterialsUiState())
    val uiState: StateFlow<AvailableMaterialsUiState> = _uiState.asStateFlow()

    init { loadMaterials() }

    private fun loadMaterials() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = materialRepository.getAvailableMaterials()) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, materials = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = "No se pudieron cargar los materiales")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun acceptMaterial(materialId: String) {
        viewModelScope.launch {
            val recyclerId = userDataStore.uidFlow.first() ?: return@launch
            _uiState.update { it.copy(acceptingId = materialId) }
            when (materialRepository.assignMaterial(materialId, recyclerId)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            acceptingId = null,
                            successMessage = "¡Material aceptado! Ya está asignado a ti",
                            materials = state.materials.filter { it.id != materialId }
                        )
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(acceptingId = null, error = "No se pudo aceptar el material")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun clearSuccess() = _uiState.update { it.copy(successMessage = null) }
}