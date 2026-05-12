package com.econect.app.presentation.recycler.profile

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.econect.app.domain.model.RecyclingCenter
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.model.User
import java.time.DayOfWeek
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclerProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecyclerProfileViewModel = hiltViewModel()
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

            uiState.user != null -> RecyclerProfileContent(
                user = uiState.user!!,
                isSaving = uiState.isSaving,
                recyclingCenters = uiState.recyclingCenters,
                isLoadingCenters = uiState.isLoadingCenters,
                onSavePhone = viewModel::savePhone,
                onSetPreferredCenter = viewModel::setPreferredCenter,
                onAddSchedule = viewModel::addSchedule,
                onRemoveSchedule = viewModel::removeSchedule,
                onRetryLoadCenters = viewModel::retryLoadCenters,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun RecyclerProfileContent(
    user: User,
    isSaving: Boolean,
    recyclingCenters: List<RecyclingCenter>,
    isLoadingCenters: Boolean,
    onSavePhone: (String) -> Unit,
    onSetPreferredCenter: (String) -> Unit,
    onAddSchedule: (Schedule) -> Unit,
    onRemoveSchedule: (Schedule) -> Unit,
    onRetryLoadCenters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCenterPicker by remember { mutableStateOf(false) }
    var showAddScheduleDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            RecyclerInfoSection(
                user = user,
                isSaving = isSaving,
                onSavePhone = onSavePhone
            )
        }

        item { ProfileSectionDivider() }

        // Calificación promedio
        item {
            RatingSection(rating = user.rating, ratingCount = user.ratingCount)
        }

        item { ProfileSectionDivider() }

        // Centro de reciclaje preferido
        item {
            SectionHeader(
                icon = { Icon(Icons.Default.Recycling, contentDescription = null) },
                title = "Centro de reciclaje preferido"
            )
        }

        item {
            RecyclingCenterSection(
                preferredCenterId = user.preferredRecyclingCenterId,
                recyclingCenters = recyclingCenters,
                isLoading = isLoadingCenters,
                onChangeTap = { showCenterPicker = true },
                onRetry = onRetryLoadCenters
            )
        }

        item { ProfileSectionDivider() }

        // Horarios de disponibilidad para recorridos
        item {
            SectionHeader(
                icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                title = "Disponibilidad para recorridos"
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

        item { Spacer(Modifier.height(32.dp)) }
    }

    if (showCenterPicker) {
        RecyclingCenterPickerDialog(
            recyclingCenters = recyclingCenters,
            currentCenterId = user.preferredRecyclingCenterId,
            onDismiss = { showCenterPicker = false },
            onConfirm = { centerId ->
                onSetPreferredCenter(centerId)
                showCenterPicker = false
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

// ─── Sección: información del reciclador ─────────────────────────────────────

@Composable
private fun RecyclerInfoSection(
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

        ReadOnlyField(label = "Nombre", value = user.name)
        ReadOnlyField(label = "Correo electrónico", value = user.email)

        // Teléfono editable inline
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
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Guardar teléfono")
                            }
                        }
                        IconButton(onClick = {
                            phoneInput = user.phone
                            isEditingPhone = false
                        }) {
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
    }
}

// ─── Sección: calificación promedio ──────────────────────────────────────────

@Composable
private fun RatingSection(rating: Float, ratingCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Calificación",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        if (ratingCount == 0) {
            Text(
                text = "Sin calificaciones aún",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StarRatingRow(rating = rating)
                Text(
                    text = "${"%.1f".format(rating)}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "($ratingCount ${if (ratingCount == 1) "reseña" else "reseñas"})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StarRatingRow(rating: Float) {
    val fullStars = floor(rating).toInt()
    val hasHalf = (rating - fullStars) >= 0.5f
    val emptyStars = 5 - fullStars - if (hasHalf) 1 else 0

    Row {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        if (hasHalf) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        repeat(emptyStars.coerceAtLeast(0)) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ─── Sección: centro de reciclaje preferido ───────────────────────────────────

@Composable
private fun RecyclingCenterSection(
    preferredCenterId: String?,
    recyclingCenters: List<RecyclingCenter>,
    isLoading: Boolean,
    onChangeTap: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))

            recyclingCenters.isEmpty() -> {
                Text(
                    text = "No se pudieron cargar los centros",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onRetry) { Text("Reintentar") }
            }

            else -> {
                val selected = recyclingCenters.find { it.id == preferredCenterId }

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (selected != null) {
                                Text(
                                    text = selected.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = selected.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Sin centro seleccionado",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = onChangeTap) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Cambiar centro",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Diálogo: selección de centro de reciclaje ───────────────────────────────

@Composable
private fun RecyclingCenterPickerDialog(
    recyclingCenters: List<RecyclingCenter>,
    currentCenterId: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedId by rememberSaveable { mutableStateOf(currentCenterId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar centro de reciclaje") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                recyclingCenters.forEach { center ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedId == center.id,
                            onClick = { selectedId = center.id }
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = center.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = center.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedId?.let { onConfirm(it) } },
                enabled = selectedId != null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ─── Diálogo: agregar horario (3 pasos) ──────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (Schedule) -> Unit
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var selectedDay by rememberSaveable { mutableStateOf<DayOfWeek?>(null) }
    val startTimeState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
    val endTimeState = rememberTimePickerState(initialHour = 17, initialMinute = 0, is24Hour = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (step) {
                    0 -> "Seleccionar día"
                    1 -> "Hora de inicio"
                    else -> "Hora de fin"
                }
            )
        },
        text = {
            when (step) {
                0 -> FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DayOfWeek.entries.forEach { day ->
                        FilterChip(
                            selected = selectedDay == day,
                            onClick = { selectedDay = day },
                            label = { Text(day.toSpanishAbbrev()) }
                        )
                    }
                }
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
                            onConfirm(Schedule(dayOfWeek = day, startTime = startTime, endTime = endTime))
                        }
                    }
                },
                enabled = step != 0 || selectedDay != null
            ) {
                Text(if (step == 2) "Guardar" else "Siguiente")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (step > 0) step-- else onDismiss() }) {
                Text(if (step > 0) "Atrás" else "Cancelar")
            }
        }
    )
}

// ─── Chip de horario ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleChip(schedule: Schedule, onRemove: () -> Unit) {
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

// ─── Helpers de UI ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(icon: @Composable () -> Unit, title: String) {
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
private fun ReadOnlyField(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value.ifEmpty { "—" }, style = MaterialTheme.typography.bodyLarge)
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
