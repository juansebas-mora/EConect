package com.econect.app.presentation.recycler.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.RecyclingCenter
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.model.User
import com.econect.app.domain.usecase.GetRecyclingCentersUseCase
import com.econect.app.domain.usecase.GetUserProfileUseCase
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

data class RecyclerProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val user: User? = null,
    val uid: String? = null,
    val recyclingCenters: List<RecyclingCenter> = emptyList(),
    val isLoadingCenters: Boolean = false
)

@HiltViewModel
class RecyclerProfileViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val manageSchedulesUseCase: ManageSchedulesUseCase,
    private val getRecyclingCentersUseCase: GetRecyclingCentersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecyclerProfileUiState())
    val uiState: StateFlow<RecyclerProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        loadRecyclingCenters()
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

    private fun loadRecyclingCenters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCenters = true) }
            when (val result = getRecyclingCentersUseCase()) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoadingCenters = false, recyclingCenters = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoadingCenters = false)
                    // Falla silenciosa: centros vacíos, usuario puede reintentar
                }
                Result.Loading -> Unit
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

    fun setPreferredCenter(centerId: String) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val updated = user.copy(preferredRecyclingCenterId = centerId)
            when (updateProfileUseCase(updated)) {
                is Result.Success -> _uiState.update {
                    it.copy(isSaving = false, user = updated, successMessage = "Centro actualizado")
                }
                is Result.Error -> _uiState.update {
                    it.copy(isSaving = false, error = "No se pudo guardar el centro")
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

    fun retryLoadCenters() = loadRecyclingCenters()

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun clearSuccessMessage() = _uiState.update { it.copy(successMessage = null) }
}
