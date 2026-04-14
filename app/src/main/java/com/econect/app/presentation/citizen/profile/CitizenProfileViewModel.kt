package com.econect.app.presentation.citizen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.PreferredLocation
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.model.User
import com.econect.app.domain.usecase.GetUserProfileUseCase
import com.econect.app.domain.usecase.ManageLocationsUseCase
import com.econect.app.domain.usecase.ManageSchedulesUseCase
import com.econect.app.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CitizenProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val user: User? = null,
    val uid: String? = null
)

@HiltViewModel
class CitizenProfileViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val manageLocationsUseCase: ManageLocationsUseCase,
    private val manageSchedulesUseCase: ManageSchedulesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenProfileUiState())
    val uiState: StateFlow<CitizenProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val uid = userDataStore.uidFlow.first() ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no encontrada") }
                return@launch
            }
            _uiState.update { it.copy(uid = uid) }
            when (val result = getUserProfileUseCase(uid)) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, user = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = "No se pudo cargar el perfil")
                }
                Result.Loading -> Unit
            }
        }
    }

    /** Recarga silenciosa tras mutaciones que generan IDs en servidor (ej: agregar ubicación). */
    private fun reloadSilently() {
        val uid = _uiState.value.uid ?: return
        viewModelScope.launch {
            when (val result = getUserProfileUseCase(uid)) {
                is Result.Success -> _uiState.update { it.copy(user = result.data) }
                else -> Unit
            }
        }
    }

    fun savePhone(phone: String) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (updateProfileUseCase(user.copy(phone = phone))) {
                is Result.Success -> _uiState.update {
                    it.copy(isSaving = false, user = user.copy(phone = phone), successMessage = "Teléfono actualizado")
                }
                is Result.Error -> _uiState.update {
                    it.copy(isSaving = false, error = "No se pudo guardar el teléfono")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun addLocation(latLng: LatLng, label: String) {
        val uid = _uiState.value.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (manageLocationsUseCase.add(uid, latLng, label)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Ubicación agregada") }
                    reloadSilently()
                }
                is Result.Error -> _uiState.update {
                    it.copy(isSaving = false, error = "No se pudo agregar la ubicación")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun removeLocation(locationId: String) {
        val uid = _uiState.value.uid ?: return
        viewModelScope.launch {
            when (manageLocationsUseCase.remove(uid, locationId)) {
                is Result.Success -> _uiState.update { state ->
                    state.copy(
                        user = state.user?.copy(
                            preferredLocations = state.user.preferredLocations
                                .filter { it.id != locationId }
                        )
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(error = "No se pudo eliminar la ubicación")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun addSchedule(schedule: Schedule) {
        val uid = _uiState.value.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (manageSchedulesUseCase.add(uid, schedule)) {
                is Result.Success -> _uiState.update { state ->
                    state.copy(
                        isSaving = false,
                        successMessage = "Horario agregado",
                        user = state.user?.copy(
                            availableSchedules = state.user.availableSchedules + schedule
                        )
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isSaving = false, error = "No se pudo agregar el horario")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun removeSchedule(schedule: Schedule) {
        val uid = _uiState.value.uid ?: return
        val scheduleId = "${schedule.dayOfWeek.name}_${schedule.startTime}"
        viewModelScope.launch {
            when (manageSchedulesUseCase.remove(uid, scheduleId)) {
                is Result.Success -> _uiState.update { state ->
                    state.copy(
                        user = state.user?.copy(
                            availableSchedules = state.user.availableSchedules.filter {
                                "${it.dayOfWeek.name}_${it.startTime}" != scheduleId
                            }
                        )
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(error = "No se pudo eliminar el horario")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun clearSuccessMessage() = _uiState.update { it.copy(successMessage = null) }
}
