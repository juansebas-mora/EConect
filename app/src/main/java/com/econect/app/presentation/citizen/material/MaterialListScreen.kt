package com.econect.app.presentation.citizen.material

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.econect.app.domain.model.MaterialCondition
import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.RecyclableMaterial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Pantalla principal ---

@Composable
fun MaterialListScreen(
    onShowSnackbar: suspend (String) -> Unit = {},
    viewModel: MaterialListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error) {
        val msg = uiState.error ?: return@LaunchedEffect
        onShowSnackbar(msg)
        viewModel.clearError()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        if (uiState.isSyncing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Filtros por estado
        StatusFilterRow(
            activeFilter = uiState.activeFilter,
            onFilterChange = viewModel::setFilter
        )

        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            uiState.filteredMaterials.isEmpty() -> EmptyMaterialsMessage(uiState.activeFilter)

            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.filteredMaterials,
                    key = { it.id }
                ) { material ->
                    val locationKey = "%.5f,%.5f".format(
                        material.pickupLocation.latitude,
                        material.pickupLocation.longitude
                    )
                    MaterialCard(
                        material = material,
                        address = uiState.geocodedAddresses[locationKey],
                        onClick = { viewModel.selectMaterial(material) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    // Bottom Sheet al tocar tarjeta
    uiState.selectedMaterial?.let { material ->
        MaterialDetailBottomSheet(
            material = material,
            address = uiState.geocodedAddresses[
                "%.5f,%.5f".format(material.pickupLocation.latitude, material.pickupLocation.longitude)
            ],
            isDeleting = uiState.isDeleting,
            onDelete = { viewModel.deleteMaterial(material.id) },
            onDismiss = viewModel::clearSelectedMaterial
        )
    }
}

// --- Fila de filtros ---

private data class StatusFilterOption(
    val status: MaterialStatus?,
    val label: String
)

private val filterOptions = listOf(
    StatusFilterOption(null, "Todos"),
    StatusFilterOption(MaterialStatus.AVAILABLE, "Disponible"),
    StatusFilterOption(MaterialStatus.ASSIGNED, "Asignado"),
    StatusFilterOption(MaterialStatus.COLLECTED, "Recogido")
)

@Composable
private fun StatusFilterRow(
    activeFilter: MaterialStatus?,
    onFilterChange: (MaterialStatus?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filterOptions) { option ->
            FilterChip(
                selected = activeFilter == option.status,
                onClick = { onFilterChange(option.status) },
                label = { Text(option.label) }
            )
        }
    }
}

// --- Tarjeta de material ---

@Composable
private fun MaterialCard(
    material: RecyclableMaterial,
    address: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Fila superior: tipo + chip de estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = material.type.icon(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = material.type.label(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                StatusChip(material.status)
            }

            Spacer(Modifier.height(8.dp))

            // Condición y cantidad
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LabelValue("Condición", material.condition.label())
                LabelValue(
                    "Cantidad",
                    "%.1f %s".format(material.quantity.value, material.quantity.unit.name.lowercase())
                )
            }

            Spacer(Modifier.height(8.dp))

            // Ubicación
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = address ?: "Cargando dirección…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(4.dp))

            // Fecha
            Text(
                text = material.createdAt.toFormattedDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Bottom Sheet de detalle ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialDetailBottomSheet(
    material: RecyclableMaterial,
    address: String?,
    isDeleting: Boolean,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Encabezado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = material.type.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = material.type.label(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                StatusChip(material.status)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Detalle completo
            DetailRow("Condición", material.condition.label())
            DetailRow(
                "Cantidad",
                "%.1f %s".format(material.quantity.value, material.quantity.unit.name.lowercase())
            )
            DetailRow("Ubicación", address ?: "%.4f, %.4f".format(
                material.pickupLocation.latitude, material.pickupLocation.longitude
            ))
            DetailRow("Registrado", material.createdAt.toFormattedDate())
            DetailRow("ID", material.id)

            // Eliminar solo si AVAILABLE
            if (material.status == MaterialStatus.AVAILABLE) {
                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    enabled = !isDeleting,
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, MaterialTheme.colorScheme.error
                    )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Eliminar material")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("¿Eliminar material?") },
            text = { Text("Esta acción no se puede deshacer. El material será eliminado permanentemente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// --- Mensaje vacío ---

@Composable
private fun EmptyMaterialsMessage(activeFilter: MaterialStatus?) {
    val msg = when (activeFilter) {
        MaterialStatus.AVAILABLE -> "No tienes materiales disponibles"
        MaterialStatus.ASSIGNED -> "No tienes materiales asignados"
        MaterialStatus.COLLECTED -> "No tienes materiales recogidos"
        null -> "Aún no has registrado materiales"
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = msg,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// --- Chip de estado con color ---

@Composable
private fun StatusChip(status: MaterialStatus) {
    val (label, containerColor, contentColor) = when (status) {
        MaterialStatus.AVAILABLE -> Triple(
            "Disponible",
            Color(0xFFD4EDDA),
            Color(0xFF155724)
        )
        MaterialStatus.ASSIGNED -> Triple(
            "Asignado",
            Color(0xFFFFF3CD),
            Color(0xFF856404)
        )
        MaterialStatus.COLLECTED -> Triple(
            "Recogido",
            Color(0xFFE2E3E5),
            Color(0xFF383D41)
        )
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

// --- Composables auxiliares ---

@Composable
private fun LabelValue(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f))
        Text(value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f))
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

private fun MaterialCondition.label(): String = when (this) {
    MaterialCondition.CLEAN -> "Limpio"
    MaterialCondition.DIRTY -> "Sucio"
    MaterialCondition.MIXED -> "Mixto"
}

private val DATE_FORMAT = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

private fun Long.toFormattedDate(): String =
    if (this == 0L) "—" else DATE_FORMAT.format(Date(this))
