package com.econect.app.presentation.citizen.dashboard

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Schedule
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date
import java.util.Locale

// --- Pantalla principal ---

@Composable
fun CitizenDashboardScreen(
    onNavigateToAddMaterial: () -> Unit,
    onNavigateToMaterialList: () -> Unit,
    onNavigateToChat: (routeId: String) -> Unit,
    onLogout: () -> Unit,
    viewModel: CitizenDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        val msg = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddMaterial,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Agregar material") }
            )
        }
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
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Solo saludo + botón logout, sin estadísticas
            item {
                DashboardHeader(
                    userName = uiState.userName,
                    isSyncing = uiState.isSyncing,
                    onLogout = { viewModel.logout(onLogout) }
                )
            }

            // Horarios si los hay
            if (uiState.upcomingSchedules.isNotEmpty()) {
                item {
                    SchedulesSection(
                        schedules = uiState.upcomingSchedules,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Banner de chat si hay ruta activa o mensajes
            if (uiState.activeRouteId != null || uiState.unreadMessages > 0) {
                item {
                    ChatBannerCard(
                        unreadMessages = uiState.unreadMessages,
                        onClick = { uiState.activeRouteId?.let { onNavigateToChat(it) } },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Lista de materiales recientes
            if (uiState.recentMaterials.isNotEmpty()) {
                item {
                    RecentMaterialsHeader(
                        onViewAll = onNavigateToMaterialList,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(uiState.recentMaterials, key = { it.id }) { material ->
                    CompactMaterialItem(
                        material = material,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                item {
                    EmptyMaterialsHint(
                        onAddMaterial = onNavigateToAddMaterial,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// --- Encabezado simple: solo saludo + logout ---

@Composable
private fun DashboardHeader(
    userName: String,
    isSyncing: Boolean,
    onLogout: () -> Unit,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${timeGreeting()}, $userName",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Bienvenido a EConect",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

// --- Horarios próximos ---

@Composable
private fun SchedulesSection(
    schedules: List<Schedule>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Tus horarios",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(schedules) { schedule ->
                ScheduleChip(schedule)
            }
        }
    }
}

@Composable
private fun ScheduleChip(schedule: Schedule) {
    FilterChip(
        selected = false,
        onClick = {},
        label = {
            Text("${schedule.dayOfWeek.shortLabel()} ${schedule.startTime}–${schedule.endTime}")
        }
    )
}

// --- Banner de chat activo ---

@Composable
private fun ChatBannerCard(
    unreadMessages: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BadgedBox(
                    badge = {
                        if (unreadMessages > 0) {
                            Badge { Text(unreadMessages.toString()) }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Chat,
                        contentDescription = "Chat",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Conversación activa",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    if (unreadMessages > 0) {
                        val label = if (unreadMessages == 1) "mensaje sin leer" else "mensajes sin leer"
                        Text(
                            text = "$unreadMessages $label",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            TextButton(onClick = onClick) {
                Text("Ver", color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

// --- Encabezado de materiales recientes ---

@Composable
private fun RecentMaterialsHeader(
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mis materiales",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        TextButton(onClick = onViewAll) {
            Text("Ver todos")
        }
    }
}

// --- Ítem compacto de material ---

@Composable
private fun CompactMaterialItem(
    material: RecyclableMaterial,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = material.type.label(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            },
            supportingContent = {
                Text(
                    text = "%.1f %s · %s".format(
                        material.quantity.value,
                        material.quantity.unit.label(),
                        material.createdAt.toShortDate()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Icon(
                    imageVector = material.type.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingContent = {
                CompactStatusChip(material.status)
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun CompactStatusChip(status: MaterialStatus) {
    val (label, containerColor, contentColor) = when (status) {
        MaterialStatus.AVAILABLE -> Triple("Disponible", Color(0xFFD4EDDA), Color(0xFF155724))
        MaterialStatus.ASSIGNED  -> Triple("Asignado",   Color(0xFFFFF3CD), Color(0xFF856404))
        MaterialStatus.COLLECTED -> Triple("Recogido",   Color(0xFFE2E3E5), Color(0xFF383D41))
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

// --- Estado vacío ---

@Composable
private fun EmptyMaterialsHint(
    onAddMaterial: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Aún no tienes materiales registrados",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onAddMaterial) {
                Text("Registrar mi primer material")
            }
        }
    }
}

// --- Extensiones ---

private fun MaterialType.icon(): ImageVector = when (this) {
    MaterialType.PAPER      -> Icons.Filled.Article
    MaterialType.PLASTIC    -> Icons.Filled.LocalDrink
    MaterialType.GLASS      -> Icons.Filled.WineBar
    MaterialType.METAL      -> Icons.Filled.Build
    MaterialType.CARDBOARD  -> Icons.Filled.Archive
    MaterialType.ORGANIC    -> Icons.Filled.Eco
    MaterialType.ELECTRONIC -> Icons.Filled.Memory
    MaterialType.OTHER      -> Icons.Filled.Category
}

private fun MaterialType.label(): String = when (this) {
    MaterialType.PAPER      -> "Papel"
    MaterialType.PLASTIC    -> "Plástico"
    MaterialType.GLASS      -> "Vidrio"
    MaterialType.METAL      -> "Metal"
    MaterialType.CARDBOARD  -> "Cartón"
    MaterialType.ORGANIC    -> "Orgánico"
    MaterialType.ELECTRONIC -> "Electrónico"
    MaterialType.OTHER      -> "Otro"
}

private fun MaterialUnit.label(): String = when (this) {
    MaterialUnit.KG    -> "kg"
    MaterialUnit.UNITS -> "unid."
}

private fun DayOfWeek.shortLabel(): String = when (this) {
    DayOfWeek.MONDAY    -> "Lun"
    DayOfWeek.TUESDAY   -> "Mar"
    DayOfWeek.WEDNESDAY -> "Mié"
    DayOfWeek.THURSDAY  -> "Jue"
    DayOfWeek.FRIDAY    -> "Vie"
    DayOfWeek.SATURDAY  -> "Sáb"
    DayOfWeek.SUNDAY    -> "Dom"
}

private val SHORT_DATE_FORMAT = SimpleDateFormat("dd MMM", Locale.getDefault())

private fun Long.toShortDate(): String =
    if (this == 0L) "—" else SHORT_DATE_FORMAT.format(Date(this))

private fun timeGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Buenos días"
        hour < 19 -> "Buenas tardes"
        else      -> "Buenas noches"
    }
}