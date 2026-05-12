package com.econect.app.presentation.recycler.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Route
import com.econect.app.domain.model.RouteStatus
import com.econect.app.domain.model.Transaction
import com.econect.app.domain.repository.MaterialRepository
import com.econect.app.domain.repository.RouteRepository
import com.econect.app.domain.repository.TransactionRepository
import com.econect.app.domain.usecase.GetRecyclerRoutesUseCase
import com.econect.app.domain.usecase.GetRecyclerTransactionsUseCase
import com.econect.app.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import com.econect.app.domain.usecase.LogoutUseCase
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import com.econect.app.domain.repository.LocationRepository

data class RecyclerDashboardUiState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val recyclerName: String = "",
    val pendingMaterials: Int = 0,
    val kgCollectedThisMonth: Double = 0.0,
    val earningsThisMonth: Double = 0.0,
    val assignedMaterials: List<RecyclableMaterial> = emptyList(),
    val recentPickups: List<Transaction> = emptyList(),
    val distancesKm: Map<String, Double> = emptyMap()
)

@HiltViewModel
class RecyclerDashboardViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getRecyclerRoutesUseCase: GetRecyclerRoutesUseCase,
    private val getRecyclerTransactionsUseCase: GetRecyclerTransactionsUseCase,
    private val routeRepository: RouteRepository,
    private val transactionRepository: TransactionRepository,
    private val materialRepository: MaterialRepository,
    private val logoutUseCase: LogoutUseCase ,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecyclerDashboardUiState())
    val uiState: StateFlow<RecyclerDashboardUiState> = _uiState.asStateFlow()

    init { loadDashboard() }

    private fun loadDashboard() {
        viewModelScope.launch {
            val uid = userDataStore.uidFlow.first() ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no encontrada") }
                return@launch
            }

            // Cargar nombre del perfil
            launch {
                when (val result = getUserProfileUseCase(uid)) {
                    is Result.Success -> _uiState.update { it.copy(recyclerName = result.data.name) }
                    else -> Unit
                }
            }

            // Cargar materiales asignados al reciclador
            launch {
                when (val result = materialRepository.getAssignedMaterials(uid)) {
                    is Result.Success -> {
                        val materials = result.data as List<RecyclableMaterial>
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                assignedMaterials = materials,
                                pendingMaterials = materials.size
                            )
                        }
                    }
                    is Result.Error -> _uiState.update {
                        it.copy(isLoading = false)
                    }
                    Result.Loading -> Unit
                }
            }

            // Cargar transacciones recientes
            launch {
                getRecyclerTransactionsUseCase(uid).collect { transactions ->
                    val cal = Calendar.getInstance()
                    val month = cal.get(Calendar.MONTH)
                    val year = cal.get(Calendar.YEAR)

                    val kgThisMonth = transactions
                        .filter { it.confirmedQuantity.unit == MaterialUnit.KG && it.completedAt.isInMonth(month, year) }
                        .sumOf { it.confirmedQuantity.value }

                    val earningsThisMonth = transactions
                        .filter { it.completedAt.isInMonth(month, year) }
                        .sumOf { it.totalAmount }

                    _uiState.update { state ->
                        state.copy(
                            kgCollectedThisMonth = kgThisMonth,
                            earningsThisMonth = earningsThisMonth,
                            recentPickups = transactions.sortedByDescending { it.completedAt }.take(3)
                        )
                    }
                }
            }

            // Sincronizar
            _uiState.update { it.copy(isSyncing = true) }
            coroutineScope {
                launch { transactionRepository.syncRecyclerTransactions(uid) }
            }
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun loadDistances() {
        viewModelScope.launch {
            val materials = _uiState.value.assignedMaterials
            if (materials.isEmpty()) return@launch

            when (val locationResult = locationRepository.getCurrentLocation()) {
                is Result.Success -> {
                    val myLocation = locationResult.data
                    val distances = materials.associate { material ->
                        val dist = haversineKm(
                            myLocation.latitude, myLocation.longitude,
                            material.pickupLocation.latitude, material.pickupLocation.longitude
                        )
                        material.id to dist
                    }
                    _uiState.update { it.copy(distancesKm = distances) }
                }
                else -> Unit // Falla silenciosa, el botón simplemente no muestra distancia
            }
        }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat/2).pow(2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon/2).pow(2)
        return R * 2 * Math.asin(Math.sqrt(a))
    }

    fun logout(onDone: () -> Unit) {  // ← NUEVO
        viewModelScope.launch {
            logoutUseCase()
            onDone()
        }
    }



    private fun Long.isInMonth(month: Int, year: Int): Boolean {
        if (this == 0L) return false
        val cal = Calendar.getInstance().apply { timeInMillis = this@isInMonth }
        return cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year
    }
}