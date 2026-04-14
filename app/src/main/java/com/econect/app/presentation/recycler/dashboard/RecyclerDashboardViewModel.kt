package com.econect.app.presentation.recycler.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Route
import com.econect.app.domain.model.RouteStatus
import com.econect.app.domain.model.Transaction
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
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

data class RecyclerDashboardUiState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val recyclerName: String = "",
    val pendingRoutesToday: Int = 0,
    val kgCollectedThisMonth: Double = 0.0,
    val earningsThisMonth: Double = 0.0,
    val upcomingRoutes: List<Route> = emptyList(),
    val recentPickups: List<Transaction> = emptyList()
)

@HiltViewModel
class RecyclerDashboardViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getRecyclerRoutesUseCase: GetRecyclerRoutesUseCase,
    private val getRecyclerTransactionsUseCase: GetRecyclerTransactionsUseCase,
    private val routeRepository: RouteRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecyclerDashboardUiState())
    val uiState: StateFlow<RecyclerDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

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

            // Combinar rutas + transacciones para calcular métricas del dashboard
            launch {
                combine(
                    getRecyclerRoutesUseCase(uid),
                    getRecyclerTransactionsUseCase(uid)
                ) { routes, transactions ->
                    buildDashboardMetrics(routes, transactions)
                }.collect { metrics ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            pendingRoutesToday = metrics.pendingToday,
                            kgCollectedThisMonth = metrics.kgThisMonth,
                            earningsThisMonth = metrics.earningsThisMonth,
                            upcomingRoutes = metrics.upcomingRoutes,
                            recentPickups = metrics.recentPickups
                        )
                    }
                }
            }

            // Sincronizar ambas fuentes en paralelo
            _uiState.update { it.copy(isSyncing = true) }
            coroutineScope {
                launch { routeRepository.syncRecyclerRoutes(uid) }
                launch { transactionRepository.syncRecyclerTransactions(uid) }
            }
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    private fun buildDashboardMetrics(
        routes: List<Route>,
        transactions: List<Transaction>
    ): DashboardMetrics {
        val today = LocalDate.now().toString()
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        val pendingToday = routes.count { r ->
            r.date == today && r.status != RouteStatus.COMPLETED
        }

        val kgThisMonth = transactions
            .filter { t ->
                t.confirmedQuantity.unit == MaterialUnit.KG && t.completedAt.isInMonth(month, year)
            }
            .sumOf { it.confirmedQuantity.value }

        val earningsThisMonth = transactions
            .filter { t -> t.completedAt.isInMonth(month, year) }
            .sumOf { it.totalAmount }

        val upcoming = routes
            .filter { it.status != RouteStatus.COMPLETED }
            .sortedWith(compareBy(
                { it.date },
                { it.stops.minOfOrNull { s -> s.scheduledTime } ?: "" }
            ))

        val recentPickups = transactions
            .sortedByDescending { it.completedAt }
            .take(3)

        return DashboardMetrics(
            pendingToday = pendingToday,
            kgThisMonth = kgThisMonth,
            earningsThisMonth = earningsThisMonth,
            upcomingRoutes = upcoming,
            recentPickups = recentPickups
        )
    }

    private fun Long.isInMonth(month: Int, year: Int): Boolean {
        if (this == 0L) return false
        val cal = Calendar.getInstance().apply { timeInMillis = this@isInMonth }
        return cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year
    }

    private data class DashboardMetrics(
        val pendingToday: Int,
        val kgThisMonth: Double,
        val earningsThisMonth: Double,
        val upcomingRoutes: List<Route>,
        val recentPickups: List<Transaction>
    )
}
