package com.econect.app.presentation.citizen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.repository.MaterialRepository
import com.econect.app.domain.usecase.GetCitizenMaterialsUseCase
import com.econect.app.domain.usecase.GetUserProfileUseCase
import com.econect.app.domain.usecase.LogoutUseCase  // ← NUEVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

data class CitizenDashboardUiState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val userName: String = "",
    val totalAvailable: Int = 0,
    val collectedThisMonth: Int = 0,
    val estimatedEarningsThisMonth: Double = 0.0,
    val upcomingSchedules: List<Schedule> = emptyList(),
    val recentMaterials: List<RecyclableMaterial> = emptyList(),
    val unreadMessages: Int = 0,
    val activeRouteId: String? = null
)

@HiltViewModel
class CitizenDashboardViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getCitizenMaterialsUseCase: GetCitizenMaterialsUseCase,
    private val materialRepository: MaterialRepository,
    private val logoutUseCase: LogoutUseCase  // ← NUEVO
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenDashboardUiState())
    val uiState: StateFlow<CitizenDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            val uid = userDataStore.uidFlow.first() ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no encontrada") }
                return@launch
            }

            launch {
                when (val result = getUserProfileUseCase(uid)) {
                    is Result.Success -> _uiState.update { state ->
                        state.copy(
                            userName = result.data.name,
                            upcomingSchedules = sortedSchedules(result.data.availableSchedules)
                        )
                    }
                    is Result.Error -> Unit
                    Result.Loading -> Unit
                }
            }

            launch {
                getCitizenMaterialsUseCase(uid).collect { materials ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            totalAvailable = materials.count { it.status == MaterialStatus.AVAILABLE },
                            collectedThisMonth = countCollectedThisMonth(materials),
                            recentMaterials = materials
                                .sortedByDescending { it.createdAt }
                                .take(5)
                        )
                    }
                }
            }

            _uiState.update { it.copy(isSyncing = true) }
            materialRepository.syncCitizenMaterials(uid)
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    // ← NUEVO: función de logout
    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            userDataStore.clearActiveUser()
            onDone()
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    private fun countCollectedThisMonth(materials: List<RecyclableMaterial>): Int {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        return materials.count { m ->
            if (m.status != MaterialStatus.COLLECTED) return@count false
            val materialCal = Calendar.getInstance().apply { timeInMillis = m.createdAt }
            materialCal.get(Calendar.MONTH) == month && materialCal.get(Calendar.YEAR) == year
        }
    }

    private fun sortedSchedules(schedules: List<Schedule>): List<Schedule> {
        val todayOrdinal = LocalDate.now().dayOfWeek.value
        return schedules.sortedBy { (it.dayOfWeek.value - todayOrdinal + 7) % 7 }
    }
}