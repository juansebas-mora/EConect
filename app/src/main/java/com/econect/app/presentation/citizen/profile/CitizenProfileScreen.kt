package com.econect.app.presentation.citizen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.PreferredLocation
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.model.User
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.time.DayOfWeek
import com.google.android.gms.maps.model.LatLng as GmsLatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: CitizenProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        val msg = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    LaunchedEffect(uiState.successMessage) {
        val msg = uiState.successMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearSuccessMessage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            uiState.user != null -> ProfileContent(
                user = uiState.user!!,
                isSaving = uiState.isSaving,
                onSavePhone = viewModel::savePhone,
                onAddLocation = viewModel::addLocation,
                onRemoveLocation = viewModel::removeLocation,
                onAddSchedule = viewModel::addSchedule,
                onRemoveSchedule = viewModel::removeSchedule,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    user: User,
    isSaving: Boolean,
    onSavePhone: (String) -> Unit,
    onAddLocation: (LatLng, String) -> Unit,
    onRemoveLocation: (String) -> Unit,
    onAddSchedule: (Schedule) -> Unit,
    onRemoveSchedule: (Schedule) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLocationPicker by remember { mutableStateOf(false) }
    var showAddScheduleDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            UserInfoSection(
                user = user,
                isSaving = isSaving,
                onSavePhone = onSavePhone
            )
        }

        item { ProfileSectionDivider() }

        item {
            SectionHeader(
                icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                title = "Ubicaciones preferidas de recogida"
            )
        }

        if (user.preferredLocations.isEmpty()) {
            item {
                EmptyStateText(
                    text = "Sin ubicaciones guardadas",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            items(
                items = user.preferredLocations,
                key = { it.id }
            ) { location ->
                LocationItem(
                    location = location,
                    onRemove = { onRemoveLocation(location.id) }
                )
            }
        }

        item {
            OutlinedButton(
                onClick = { showLocationPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Agregar ubicación")
            }
        }

        item { ProfileSectionDivider() }

        item {
            SectionHeader(
                icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                title = "Horarios de disponibilidad"
            )
        }

        item {
            if (user.availableSchedules.isEmpty()) {
                EmptyStateText(
                    text = "Sin horarios configurados",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    user.availableSchedules.forEach { schedule ->
                        ScheduleChip(
                            schedule = schedule,
                            onRemove = { onRemoveSchedule(schedule) }
                        )
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = { showAddScheduleDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Agregar horario")
            }
        }

        item { ProfileSectionDivider() }

        item { TransactionHistorySection() }

        item { Spacer(Modifier.height(32.dp)) }
    }

    if (showLocationPicker) {
        LocationPickerDialog(
            onDismiss = { showLocationPicker = false },
            onConfirm = { latLng, label ->
                onAddLocation(latLng, label)
                showLocationPicker = false
            }
        )
    }

    if (showAddScheduleDialog) {
        AddScheduleDialog(
            onDismiss = { showAddScheduleDialog = false },
            onConfirm = { schedule ->
                onAddSchedule(schedule)
                showAddScheduleDialog = false
            }
        )
    }
}

// ─── Sección: información de usuario ───────────────────────────────────────

@Composable
private fun UserInfoSection(
    user: User,
    isSaving: Boolean,
    onSavePhone: (String) -> Unit
) {
    var isEditingPhone by rememberSaveable { mutableStateOf(false) }
    var phoneInput by rememberSaveable(user.phone) { mutableStateOf(user.phone) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Información personal",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Nombre (solo lectura)
        ReadOnlyField(label = "Nombre", value = user.name)

        // Correo (solo lectura)
        ReadOnlyField(label = "Correo electrónico", value = user.email)

        // Teléfono (editable inline)
        if (isEditingPhone) {
            OutlinedTextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                trailingIcon = {
                    Row {
                        IconButton(
                            onClick = {
                                onSavePhone(phoneInput)
                                isEditingPhone = false
                            },
                            enabled = !isSaving && phoneInput.isNotBlank()
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Guardar teléfono")
                            }
                        }
                        IconButton(
                            onClick = {
                                phoneInput = user.phone
                                isEditingPhone = false
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar edición")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Teléfono",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = user.phone.ifEmpty { "Sin teléfono registrado" },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                IconButton(onClick = { isEditingPhone = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar teléfono",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Calificación (solo lectura)
        if (user.ratingCount > 0) {
            ReadOnlyField(
                label = "Calificación",
                value = "${"%.1f".format(user.rating)} ⭐ (${user.ratingCount} reseñas)"
            )
        }
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.ifEmpty { "—" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// ─── Sección: ubicación ─────────────────────────────────────────────────────

@Composable
private fun LocationItem(
    location: PreferredLocation,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.label.ifEmpty { "Ubicación sin etiqueta" },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${"%.5f".format(location.latitude)}, ${"%.5f".format(location.longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar ubicación",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

// ─── Sección: horarios ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleChip(
    schedule: Schedule,
    onRemove: () -> Unit
) {
    val dayName = schedule.dayOfWeek.toSpanishAbbrev()
    InputChip(
        selected = false,
        onClick = {},
        label = {
            Text(
                text = "$dayName ${schedule.startTime}–${schedule.endTime}",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar horario $dayName",
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    )
}

// ─── Sección: historial (placeholder Fase 6) ─────────────────────────────────

@Composable
private fun TransactionHistorySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SectionHeader(
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            title = "Historial de transacciones"
        )
        Spacer(Modifier.height(8.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
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
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Disponible próximamente",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Aquí verás el historial de tus entregas de reciclaje y pagos recibidos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─── Diálogo: selector de ubicación en mapa ──────────────────────────────────

@Composable
private fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (LatLng, String) -> Unit
) {
    var selectedGmsLatLng by remember { mutableStateOf<GmsLatLng?>(null) }
    var label by rememberSaveable { mutableStateOf("") }

    val defaultPosition = GmsLatLng(4.711, -74.072) // Bogotá
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 12f)
    }
    val markerState = rememberMarkerState()

    LaunchedEffect(selectedGmsLatLng) {
        selectedGmsLatLng?.let { markerState.position = it }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Seleccionar ubicación",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Toca el mapa para marcar el punto de recogida",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { gmsLatLng -> selectedGmsLatLng = gmsLatLng }
                    ) {
                        if (selectedGmsLatLng != null) {
                            Marker(
                                state = markerState,
                                title = label.ifEmpty { "Punto seleccionado" }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Etiqueta (ej: Casa, Trabajo)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            selectedGmsLatLng?.let { gms ->
                                onConfirm(LatLng(gms.latitude, gms.longitude), label)
                            }
                        },
                        enabled = selectedGmsLatLng != null && label.isNotBlank()
                    ) {
                        Text("Agregar")
                    }
                }
            }
        }
    }
}

// ─── Diálogo: agregar horario (3 pasos) ──────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (Schedule) -> Unit
) {
    var step by rememberSaveable { mutableIntStateOf(0) } // 0=día, 1=inicio, 2=fin
    var selectedDay by rememberSaveable { mutableStateOf<DayOfWeek?>(null) }
    val startTimeState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
    val endTimeState = rememberTimePickerState(initialHour = 17, initialMinute = 0, is24Hour = true)

    val title = when (step) {
        0 -> "Seleccionar día"
        1 -> "Hora de inicio"
        else -> "Hora de fin"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            when (step) {
                0 -> DayOfWeekSelector(
                    selectedDay = selectedDay,
                    onDaySelected = { selectedDay = it }
                )
                1 -> TimePicker(state = startTimeState)
                else -> TimePicker(state = endTimeState)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (step) {
                        0 -> if (selectedDay != null) step = 1
                        1 -> step = 2
                        2 -> {
                            val day = selectedDay ?: return@Button
                            val startTime = formatTime(startTimeState.hour, startTimeState.minute)
                            val endTime = formatTime(endTimeState.hour, endTimeState.minute)
                            onConfirm(
                                Schedule(
                                    dayOfWeek = day,
                                    startTime = startTime,
                                    endTime = endTime
                                )
                            )
                        }
                    }
                },
                enabled = when (step) {
                    0 -> selectedDay != null
                    else -> true
                }
            ) {
                Text(if (step == 2) "Guardar" else "Siguiente")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { if (step > 0) step-- else onDismiss() }
            ) {
                Text(if (step > 0) "Atrás" else "Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DayOfWeekSelector(
    selectedDay: DayOfWeek?,
    onDaySelected: (DayOfWeek) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DayOfWeek.entries.forEach { day ->
            FilterChip(
                selected = selectedDay == day,
                onClick = { onDaySelected(day) },
                label = { Text(day.toSpanishAbbrev()) }
            )
        }
    }
}

// ─── Helpers de UI ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    icon: @Composable () -> Unit,
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon()
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptyStateText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun ProfileSectionDivider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

private fun DayOfWeek.toSpanishAbbrev(): String = when (this) {
    DayOfWeek.MONDAY -> "Lun"
    DayOfWeek.TUESDAY -> "Mar"
    DayOfWeek.WEDNESDAY -> "Mié"
    DayOfWeek.THURSDAY -> "Jue"
    DayOfWeek.FRIDAY -> "Vie"
    DayOfWeek.SATURDAY -> "Sáb"
    DayOfWeek.SUNDAY -> "Dom"
}

private fun formatTime(hour: Int, minute: Int): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
