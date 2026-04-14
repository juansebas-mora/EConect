package com.econect.app.presentation.recycler.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.Route
import com.econect.app.domain.model.RouteStatus
import com.econect.app.domain.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

// --- Pantalla principal ---

@Composable
fun RecyclerDashboardScreen(
    onNavigateToRouteDetail: (routeId: String) -> Unit,
    viewModel: RecyclerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        val msg = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardHeader(
                    recyclerName = uiState.recyclerName,
                    isSyncing = uiState.isSyncing
                )
            }

            item {
                SummaryCardsRow(
                    pendingRoutesToday = uiState.pendingRoutesToday,
                    kgCollectedThisMonth = uiState.kgCollectedThisMonth,
                    earningsThisMonth = uiState.earningsThisMonth,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                SectionTitle(
                    title = "Rutas próximas",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (uiState.upcomingRoutes.isEmpty()) {
                item {
                    EmptyRoutesHint(modifier = Modifier.padding(horizontal = 16.dp))
                }
            } else {
                items(uiState.upcomingRoutes, key = { it.id }) { route ->
                    RouteCard(
                        route = route,
                        onViewRoute = { onNavigateToRouteDetail(route.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            if (uiState.recentPickups.isNotEmpty()) {
                item {
                    SectionTitle(
                        title = "Últimas recogidas",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(uiState.recentPickups, key = { it.id }) { tx ->
                    CompactPickupItem(
                        transaction = tx,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// --- Encabezado ---

@Composable
private fun DashboardHeader(
    recyclerName: String,
    isSyncing: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isSyncing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Hola, $recyclerName",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Resumen de tu actividad",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// --- Tarjetas de resumen ---

@Composable
private fun SummaryCardsRow(
    pendingRoutesToday: Int,
    kgCollectedThisMonth: Double,
    earningsThisMonth: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            icon = Icons.Filled.CalendarToday,
            value = pendingRoutesToday.toString(),
            label = "Rutas hoy",
            iconTint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            icon = Icons.Filled.Eco,
            value = "%.1f kg".format(kgCollectedThisMonth),
            label = "Este mes",
            iconTint = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            icon = Icons.Filled.AttachMoney,
            value = formatCurrency(earningsThisMonth),
            label = "Ingresos",
            iconTint = Color(0xFFF57C00),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    icon: ImageVector,
    value: String,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Título de sección ---

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

// --- Tarjeta de ruta ---

@Composable
private fun RouteCard(
    route: Route,
    onViewRoute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Fila superior: fecha + chip de estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = route.date.toDisplayDate(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                RouteStatusChip(route.status)
            }

            Spacer(Modifier.height(10.dp))

            // Hora de inicio + paradas
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                val firstTime = route.stops.minOfOrNull { it.scheduledTime }
                if (firstTime != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Inicio: $firstTime",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.PinDrop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    val stopLabel = if (route.stops.size == 1) "1 parada" else "${route.stops.size} paradas"
                    Text(
                        text = stopLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tipos de material distintos en la ruta
            val materialTypes = route.stops
                .map { it.materialType }
                .distinct()
                .filter { it != MaterialType.OTHER }
                .take(4)

            if (materialTypes.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    materialTypes.forEach { type ->
                        Icon(
                            imageVector = type.icon(),
                            contentDescription = type.label(),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (route.stops.map { it.materialType }.distinct().size > materialTypes.size) {
                        Text(
                            text = "+más",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onViewRoute,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver ruta")
            }
        }
    }
}

@Composable
private fun RouteStatusChip(status: RouteStatus) {
    val (label, containerColor, contentColor) = when (status) {
        RouteStatus.PENDING -> Triple("Pendiente", Color(0xFFFFF3CD), Color(0xFF856404))
        RouteStatus.IN_PROGRESS -> Triple("En progreso", Color(0xFFCCE5FF), Color(0xFF004085))
        RouteStatus.COMPLETED -> Triple("Completada", Color(0xFFE2E3E5), Color(0xFF383D41))
    }
    SuggestionChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(enabled = false)
    )
}

// --- Estado vacío de rutas ---

@Composable
private fun EmptyRoutesHint(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "No tienes rutas asignadas próximas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Ítem compacto de recogida ---

@Composable
private fun CompactPickupItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = transaction.completedAt.toShortDate(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            },
            supportingContent = {
                val qty = transaction.confirmedQuantity
                val unitLabel = if (qty.unit == MaterialUnit.KG) "kg" else "unid."
                Text(
                    text = "%.1f %s · %s".format(
                        qty.value,
                        unitLabel,
                        formatCurrency(transaction.totalAmount)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

// --- Extensiones de presentación ---

private fun MaterialType.icon(): ImageVector = when (this) {
    MaterialType.PAPER -> Icons.Filled.Article
    MaterialType.PLASTIC -> Icons.Filled.LocalDrink
    MaterialType.GLASS -> Icons.Filled.WineBar
    MaterialType.METAL -> Icons.Filled.Build
    MaterialType.CARDBOARD -> Icons.Filled.Archive
    MaterialType.ORGANIC -> Icons.Filled.Eco
    MaterialType.ELECTRONIC -> Icons.Filled.Memory
    MaterialType.OTHER -> Icons.Filled.Category
}

private fun MaterialType.label(): String = when (this) {
    MaterialType.PAPER -> "Papel"
    MaterialType.PLASTIC -> "Plástico"
    MaterialType.GLASS -> "Vidrio"
    MaterialType.METAL -> "Metal"
    MaterialType.CARDBOARD -> "Cartón"
    MaterialType.ORGANIC -> "Orgánico"
    MaterialType.ELECTRONIC -> "Electrónico"
    MaterialType.OTHER -> "Otro"
}

private fun String.toDisplayDate(): String = runCatching {
    val date = LocalDate.parse(this)
    val day = date.dayOfWeek.shortLabel()
    val month = date.monthValue.monthShortLabel()
    "$day ${date.dayOfMonth} de $month"
}.getOrElse { this }

private fun DayOfWeek.shortLabel(): String = when (this) {
    DayOfWeek.MONDAY -> "Lun"
    DayOfWeek.TUESDAY -> "Mar"
    DayOfWeek.WEDNESDAY -> "Mié"
    DayOfWeek.THURSDAY -> "Jue"
    DayOfWeek.FRIDAY -> "Vie"
    DayOfWeek.SATURDAY -> "Sáb"
    DayOfWeek.SUNDAY -> "Dom"
}

private fun Int.monthShortLabel(): String = when (this) {
    1 -> "ene"; 2 -> "feb"; 3 -> "mar"; 4 -> "abr"
    5 -> "may"; 6 -> "jun"; 7 -> "jul"; 8 -> "ago"
    9 -> "sep"; 10 -> "oct"; 11 -> "nov"; 12 -> "dic"
    else -> "?"
}

private val SHORT_DATE_FORMAT = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

private fun Long.toShortDate(): String =
    if (this == 0L) "—" else SHORT_DATE_FORMAT.format(Date(this))

private fun formatCurrency(amount: Double): String {
    if (amount == 0.0) return "$0"
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}
