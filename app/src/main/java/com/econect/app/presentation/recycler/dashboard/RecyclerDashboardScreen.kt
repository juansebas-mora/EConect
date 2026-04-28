package com.econect.app.presentation.recycler.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.RecyclableMaterial
import com.econect.app.domain.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.Logout
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun RecyclerDashboardScreen(
    onNavigateToRouteDetail: (String) -> Unit,
    onNavigateToAvailableMaterials: () -> Unit,
    onLogout: () -> Unit,  // ← NUEVO
    viewModel: RecyclerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var locationPermissionGranted by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted = isGranted
        if (isGranted) {
            viewModel.loadDistances()  // Si acepta, carga distancias inmediatamente
        }
    }

    LaunchedEffect(uiState.error) {
        val msg = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAvailableMaterials,
                icon = { Icon(Icons.Filled.Eco, contentDescription = null) },
                text = { Text("Ver materiales") }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            item {
                DashboardHeader(
                    recyclerName = uiState.recyclerName,
                    isSyncing = uiState.isSyncing,
                    onLogout = { viewModel.logout(onLogout) }
                )
            }

            // Tarjetas de resumen
            item {
                SummaryCardsRow(
                    pendingMaterials = uiState.pendingMaterials,
                    kgCollectedThisMonth = uiState.kgCollectedThisMonth,
                    earningsThisMonth = uiState.earningsThisMonth,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Pedidos aceptados
            item {
                SectionTitle(
                    title = "Pedidos aceptados",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (uiState.assignedMaterials.isEmpty()) {
                item {
                    EmptyMaterialsHint(
                        onNavigate = onNavigateToAvailableMaterials,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(uiState.assignedMaterials, key = { it.id }) { material ->
                    AssignedMaterialCard(
                        material = material,
                        distanceKm = uiState.distancesKm[material.id],
                        onCardClick = {
                            if (locationPermissionGranted) {
                                viewModel.loadDistances()
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Últimas recogidas
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

@Composable
private fun DashboardHeader(
    recyclerName: String,
    isSyncing: Boolean,
    onLogout: () -> Unit,                                    // ← NUEVO
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isSyncing) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(                                             // ← Cambia Column por Row
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
                IconButton(onClick = onLogout) {             // ← Botón logout
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

@Composable
private fun SummaryCardsRow(
    pendingMaterials: Int,
    kgCollectedThisMonth: Double,
    earningsThisMonth: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            icon = Icons.Filled.ShoppingBag,
            value = pendingMaterials.toString(),
            label = "Pedidos",
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
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
private fun AssignedMaterialCard(
    material: RecyclableMaterial,
    distanceKm: Double?,        // ← NUEVO
    onCardClick: () -> Unit,    // ← NUEVO
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onCardClick,  // ← NUEVO: hace clickeable la card
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = material.type.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = material.type.label(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "%.1f %s".format(
                            material.quantity.value,
                            if (material.quantity.unit == MaterialUnit.KG) "kg" else "unid."
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    // ← NUEVO: muestra la distancia si está disponible
                    if (distanceKm != null) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = if (distanceKm < 1.0) {
                                    "%.0f m".format(distanceKm * 1000)
                                } else {
                                    "%.1f km".format(distanceKm)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            SuggestionChip(
                onClick = {},
                label = { Text("Asignado", style = MaterialTheme.typography.labelSmall) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = Color(0xFFD4EDDA),
                    labelColor = Color(0xFF155724)
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(enabled = false)
            )
        }
    }
}

@Composable
private fun EmptyMaterialsHint(
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "No tienes pedidos aceptados aún",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onNavigate) {
                Text("Ver materiales disponibles")
            }
        }
    }
}

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
                    text = "%.1f %s · %s".format(qty.value, unitLabel, formatCurrency(transaction.totalAmount)),
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

private val SHORT_DATE_FORMAT = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
private fun Long.toShortDate(): String = if (this == 0L) "—" else SHORT_DATE_FORMAT.format(Date(this))
private fun formatCurrency(amount: Double): String {
    if (amount == 0.0) return "$0"
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}