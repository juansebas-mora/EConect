package com.econect.app.presentation.citizen.material

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialCondition
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.PreferredLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng as GmsLatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.MapProperties
import com.econect.app.util.checkLocationPermission
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
// --- Pantalla principal ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaterialScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddMaterialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showMapPicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            snackbarHostState.showSnackbar("¡Material registrado con éxito!")
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.error) {
        val msg = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar material") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                MaterialTypeSection(
                    selected = uiState.selectedType,
                    showError = uiState.showValidationErrors && uiState.selectedType == null,
                    onSelect = viewModel::selectType
                )
            }

            item {
                ConditionSection(
                    selected = uiState.selectedCondition,
                    showError = uiState.showValidationErrors && uiState.selectedCondition == null,
                    onSelect = viewModel::selectCondition
                )
            }

            item {
                QuantitySection(
                    useKg = uiState.useKg,
                    quantityText = uiState.quantityText,
                    unitSubtype = uiState.unitSubtype,
                    quantityError = uiState.quantityError,
                    onToggleUnit = viewModel::setUseKg,
                    onQuantityChange = viewModel::setQuantityText,
                    onSubtypeChange = viewModel::setUnitSubtype
                )
            }

            item {
                LocationSection(
                    preferredLocations = uiState.preferredLocations,
                    selectedLocationId = uiState.selectedLocationId,
                    useCustomLocation = uiState.useCustomLocation,
                    customPickupLocation = uiState.customPickupLocation,
                    showError = uiState.showValidationErrors && uiState.effectivePickupLocation == null,
                    onSelectPreferred = viewModel::selectPreferredLocation,
                    onSelectCustom = {
                        viewModel.selectCustomLocation()
                        showMapPicker = true
                    },
                    onOpenMapPicker = { showMapPicker = true }
                )
            }

            item {
                Button(
                    onClick = viewModel::addMaterial,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Agregar material", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    if (showMapPicker) {
        MapPickerDialog(
            initialLocation = uiState.customPickupLocation,
            onConfirm = { latLng ->
                viewModel.setCustomPickupLocation(latLng)
                showMapPicker = false
            },
            onDismiss = { showMapPicker = false }
        )
    }
}

// --- Sección: Tipo de material ---

private data class MaterialTypeOption(
    val type: MaterialType,
    val label: String,
    val icon: ImageVector
)

private val materialTypeOptions = listOf(
    MaterialTypeOption(MaterialType.PAPER, "Papel", Icons.Filled.Article),
    MaterialTypeOption(MaterialType.PLASTIC, "Plástico", Icons.Filled.LocalDrink),
    MaterialTypeOption(MaterialType.GLASS, "Vidrio", Icons.Filled.WineBar),
    MaterialTypeOption(MaterialType.METAL, "Metal", Icons.Filled.Build),
    MaterialTypeOption(MaterialType.CARDBOARD, "Cartón", Icons.Filled.Archive),
    MaterialTypeOption(MaterialType.ORGANIC, "Orgánico", Icons.Filled.Eco),
    MaterialTypeOption(MaterialType.ELECTRONIC, "Electrónico", Icons.Filled.Memory),
    MaterialTypeOption(MaterialType.OTHER, "Otro", Icons.Filled.Category)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MaterialTypeSection(
    selected: MaterialType?,
    showError: Boolean,
    onSelect: (MaterialType) -> Unit
) {
    SectionTitle("¿Qué tipo de material tienes?")
    Spacer(Modifier.height(8.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        materialTypeOptions.forEach { option ->
            FilterChip(
                selected = selected == option.type,
                onClick = { onSelect(option.type) },
                label = { Text(option.label) },
                leadingIcon = {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            )
        }
    }
    if (showError) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Selecciona un tipo de material",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// --- Sección: Condición ---

private data class ConditionOption(
    val condition: MaterialCondition,
    val label: String,
    val description: String
)

private val conditionOptions = listOf(
    ConditionOption(MaterialCondition.CLEAN, "Limpio", "Materiales limpios, secos y separados"),
    ConditionOption(MaterialCondition.DIRTY, "Sucio", "Con residuos, humedad u olores"),
    ConditionOption(MaterialCondition.MIXED, "Mixto", "Mezcla de materiales o condiciones")
)

@Composable
private fun ConditionSection(
    selected: MaterialCondition?,
    showError: Boolean,
    onSelect: (MaterialCondition) -> Unit
) {
    SectionTitle("¿En qué condición está?")
    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        conditionOptions.forEach { option ->
            val isSelected = selected == option.condition
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = { onSelect(option.condition) },
                        role = Role.RadioButton
                    ),
                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                         else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isSelected, onClick = null)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(option.label, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            option.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    if (showError) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Selecciona la condición del material",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// --- Sección: Cantidad ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuantitySection(
    useKg: Boolean,
    quantityText: String,
    unitSubtype: UnitSubtype,
    quantityError: String?,
    onToggleUnit: (Boolean) -> Unit,
    onQuantityChange: (String) -> Unit,
    onSubtypeChange: (UnitSubtype) -> Unit
) {
    SectionTitle("¿Cuánto material tienes?")
    Spacer(Modifier.height(8.dp))

    // Toggle kg / unidades
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { onToggleUnit(true) },
            modifier = Modifier.weight(1f),
            border = if (useKg) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                     else ButtonDefaults.outlinedButtonBorder(enabled = true),
            colors = if (useKg) ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) else ButtonDefaults.outlinedButtonColors()
        ) {
            Text("Kilogramos (kg)")
        }
        OutlinedButton(
            onClick = { onToggleUnit(false) },
            modifier = Modifier.weight(1f),
            border = if (!useKg) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                     else ButtonDefaults.outlinedButtonBorder(enabled = true),
            colors = if (!useKg) ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) else ButtonDefaults.outlinedButtonColors()
        ) {
            Text("Unidades")
        }
    }

    Spacer(Modifier.height(12.dp))

    if (!useKg) {
        // Dropdown de subtipo
        var dropdownExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = unitSubtype.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de unidad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                UnitSubtype.entries.forEach { subtype ->
                    DropdownMenuItem(
                        text = { Text(subtype.label) },
                        onClick = {
                            onSubtypeChange(subtype)
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }

    OutlinedTextField(
        value = quantityText,
        onValueChange = { input ->
            // Solo dígitos, punto y coma decimal
            if (input.isEmpty() || input.matches(Regex("^\\d*[.,]?\\d*$"))) {
                onQuantityChange(input.replace(',', '.'))
            }
        },
        label = { Text(if (useKg) "Cantidad en kg" else "Cantidad de ${unitSubtype.label.lowercase()}") },
        isError = quantityError != null,
        supportingText = quantityError?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

// --- Sección: Ubicación ---

@Composable
private fun LocationSection(
    preferredLocations: List<PreferredLocation>,
    selectedLocationId: String?,
    useCustomLocation: Boolean,
    customPickupLocation: LatLng?,
    showError: Boolean,
    onSelectPreferred: (String) -> Unit,
    onSelectCustom: () -> Unit,
    onOpenMapPicker: () -> Unit
) {
    SectionTitle("¿Dónde recogen el material?")
    Spacer(Modifier.height(8.dp))

    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Ubicaciones preferidas del perfil
        preferredLocations.forEach { location ->
            val isSelected = selectedLocationId == location.id && !useCustomLocation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = { onSelectPreferred(location.id) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = isSelected, onClick = null)
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = location.label.ifBlank { "Ubicación guardada" },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Opción "Otra ubicación"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = useCustomLocation,
                    onClick = onSelectCustom,
                    role = Role.RadioButton
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = useCustomLocation, onClick = null)
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Otra ubicación", style = MaterialTheme.typography.bodyLarge)
        }

        // Indicador de ubicación personalizada seleccionada
        if (useCustomLocation) {
            if (customPickupLocation != null) {
                Text(
                    text = "Ubicación seleccionada: %.5f, %.5f".format(
                        customPickupLocation.latitude,
                        customPickupLocation.longitude
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 56.dp)
                )
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = onOpenMapPicker,
                    modifier = Modifier.padding(start = 40.dp)
                ) {
                    Text("Cambiar ubicación en mapa")
                }
            } else {
                OutlinedButton(
                    onClick = onOpenMapPicker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp)
                ) {
                    Icon(Icons.Filled.Place, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Seleccionar en mapa")
                }
            }
        }
    }

    if (showError) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Selecciona una ubicación de recogida",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// --- Diálogo: Selector de mapa ---

// Bogotá, Colombia como ubicación por defecto
private val DEFAULT_CAMERA = CameraPosition.fromLatLngZoom(GmsLatLng(4.711, -74.0721), 12f)

@Composable
private fun MapPickerDialog(
    initialLocation: LatLng?,
    onConfirm: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var pickedGms by remember {
        mutableStateOf(
            initialLocation?.let { GmsLatLng(it.latitude, it.longitude) }
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = initialLocation
            ?.let { CameraPosition.fromLatLngZoom(GmsLatLng(it.latitude, it.longitude), 15f) }
            ?: DEFAULT_CAMERA
    }

    // Pedir permiso de ubicación automáticamente
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationToken = CancellationTokenSource()
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { location ->
                location?.let {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(
                            GmsLatLng(it.latitude, it.longitude), 15f
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (checkLocationPermission(context)) {
            // Ya tiene permiso, centrar en ubicación actual
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(
                            GmsLatLng(it.latitude, it.longitude), 15f
                        )
                    )
                }
            }
        } else {
            // No tiene permiso, pedirlo
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { gmsLatLng -> pickedGms = gmsLatLng },
                    properties = MapProperties(
                        isMyLocationEnabled = checkLocationPermission(context)
                    )
                ) {
                    pickedGms?.let { gms ->
                        Marker(
                            state = rememberMarkerState(position = gms),
                            title = "Punto de recogida"
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (pickedGms == null) "Toca el mapa para marcar el punto de recogida"
                            else "Punto seleccionado: %.5f, %.5f".format(
                                pickedGms!!.latitude, pickedGms!!.longitude
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f)
                            ) { Text("Cancelar") }
                            Button(
                                onClick = {
                                    pickedGms?.let { gms ->
                                        onConfirm(LatLng(gms.latitude, gms.longitude))
                                    }
                                },
                                enabled = pickedGms != null,
                                modifier = Modifier.weight(1f)
                            ) { Text("Confirmar") }
                        }
                    }
                }
            }
        }
    }
}

// --- Utilidad compartida ---

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}
