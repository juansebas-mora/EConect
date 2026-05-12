package com.econect.app.presentation.recycler.materials

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.econect.app.domain.model.MaterialCondition
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.RecyclableMaterial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableMaterialsScreen(
    viewModel: AvailableMaterialsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(uiState.error) {
        val msg = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    LaunchedEffect(uiState.successMessage) {
        val msg = uiState.successMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearSuccess()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Materiales disponibles",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Elige los materiales que quieres recoger",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                uiState.materials.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Eco,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No hay materiales disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.materials, key = { it.id }) { material ->
                        MaterialCard(
                            material = material,
                            isAccepting = uiState.acceptingId == material.id,
                            onShowMap = { viewModel.showOnMap(material) }
                        )
                    }
                }
            }
        }
    }

    // BottomSheet del mapa — fuera del Scaffold
    uiState.materialToShowOnMap?.let { material ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissMap() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Punto de recogida",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = material.type.label(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "%.6f, %.6f".format(
                            material.pickupLocation.latitude,
                            material.pickupLocation.longitude
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val lat = material.pickupLocation.latitude
                        val lng = material.pickupLocation.longitude
                        val label = "Recogida: ${material.type.label()}"
                        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            val fallbackUri = Uri.parse("https://maps.google.com/?q=$lat,$lng")
                            context.startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Map, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir en Google Maps")
                }
                OutlinedButton(
                    onClick = {
                        viewModel.dismissMap()
                        viewModel.acceptMaterial(material.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmar recogida")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MaterialCard(
    material: RecyclableMaterial,
    isAccepting: Boolean,
    onShowMap: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = material.type.label(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                ConditionChip(material.condition)
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoItem(
                    icon = Icons.Filled.Scale,
                    text = "%.1f %s".format(
                        material.quantity.value,
                        if (material.quantity.unit == MaterialUnit.KG) "kg" else "unid."
                    )
                )
                InfoItem(
                    icon = Icons.Filled.LocationOn,
                    text = "%.4f, %.4f".format(
                        material.pickupLocation.latitude,
                        material.pickupLocation.longitude
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onShowMap,
                enabled = !isAccepting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isAccepting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.Map, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Ver ubicación y asignar")
                }
            }
        }
    }
}

@Composable
private fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConditionChip(condition: MaterialCondition) {
    val (label, color) = when (condition) {
        MaterialCondition.CLEAN -> "Limpio" to Color(0xFF2E7D32)
        MaterialCondition.DIRTY -> "Sucio" to Color(0xFFF57C00)
        MaterialCondition.MIXED -> "Mixto" to Color(0xFF1565C0)
    }
    SuggestionChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = SuggestionChipDefaults.suggestionChipColors(labelColor = color),
        border = SuggestionChipDefaults.suggestionChipBorder(enabled = false)
    )
}

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