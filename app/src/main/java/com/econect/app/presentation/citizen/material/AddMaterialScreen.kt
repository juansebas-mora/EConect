package com.econect.app.presentation.citizen.material

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.PreferredLocation
import com.econect.app.util.checkLocationPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.LatLng as GmsLatLng
import java.io.File

// =============================================================================
// Pantalla principal
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaterialScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddMaterialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // 1. Sección de cámara + clasificación IA
            item {
                FotoYClasificacionSection(
                    imageUri = uiState.capturedImageUri,
                    clasificacion = uiState.clasificacion,
                    showError = uiState.showValidationErrors && uiState.capturedImageUri == null,
                    onImageCaptured = { uri ->
                        viewModel.setImageUri(uri)
                        viewModel.clasificarConModelo(context, uri)
                    },
                    onReintentarClasificacion = {
                        uiState.capturedImageUri?.let { uri ->
                            viewModel.clasificarConModelo(context, uri)
                        }
                    }
                )
            }

            // 2. Detalles adicionales (texto libre)
            item {
                DetallesSection(
                    detalles = uiState.detalles,
                    onDetallesChange = viewModel::setDetalles
                )
            }

            // 3. Ubicación de recogida
            item {
                LocationSection(
                    preferredLocations = uiState.preferredLocations,
                    selectedLocationId = uiState.selectedLocationId,
                    useCustomLocation = uiState.useCustomLocation,
                    customPickupLocation = uiState.customPickupLocation,
                    showError = uiState.showValidationErrors && uiState.efectivePickupLocation == null,
                    onSelectPreferred = viewModel::selectPreferredLocation,
                    onSelectCustom = {
                        viewModel.selectCustomLocation()
                        showMapPicker = true
                    },
                    onOpenMapPicker = { showMapPicker = true }
                )
            }

            // 4. Botón registrar
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
                        Text("Registrar material", style = MaterialTheme.typography.labelLarge)
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

// =============================================================================
// Sección 1: Foto + Clasificación IA
// =============================================================================

@Composable
private fun FotoYClasificacionSection(
    imageUri: Uri?,
    clasificacion: ClasificacionEstado,
    showError: Boolean,
    onImageCaptured: (Uri) -> Unit,
    onReintentarClasificacion: () -> Unit
) {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) tempUri?.let { onImageCaptured(it) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            tempUri = crearUriTemporal(context)
            tempUri?.let { cameraLauncher.launch(it) }
        }
    }

    fun abrirCamara() {
        tempUri = crearUriTemporal(context)
        tempUri?.let { uri ->
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    SectionTitle("📷 Foto del material")
    Spacer(Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageUri != null) {
                // Foto tomada
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Foto del material",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(12.dp))

                // Estado de clasificación
                when (clasificacion) {
                    is ClasificacionEstado.Idle -> Unit

                    is ClasificacionEstado.Clasificando -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Analizando imagen con IA...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    is ClasificacionEstado.Resultado -> {
                        ResultadoClasificacionCard(clasificacion)
                    }

                    is ClasificacionEstado.Error -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                clasificacion.mensaje,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onReintentarClasificacion) {
                                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Botón para volver a tomar
                OutlinedButton(
                    onClick = ::abrirCamara,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tomar otra foto")
                }

            } else {
                // Sin foto todavía
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .then(
                            if (showError)
                                Modifier.border(
                                    2.dp,
                                    MaterialTheme.colorScheme.error,
                                    RoundedCornerShape(12.dp)
                                )
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Toma una foto del material\npara identificarlo automáticamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = ::abrirCamara,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tomar foto")
                }

                if (showError) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Toma una foto del material",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultadoClasificacionCard(resultado: ClasificacionEstado.Resultado) {
    val porcentaje = (resultado.confianza * 100).toInt()
    val labelAmigable = materialTypeLabel(resultado.tipo)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Material identificado",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    labelAmigable,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                "$porcentaje%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun materialTypeLabel(tipo: MaterialType): String = when (tipo) {
    MaterialType.PLASTIC    -> "Plástico"
    MaterialType.PAPER      -> "Papel"
    MaterialType.GLASS      -> "Vidrio"
    MaterialType.METAL      -> "Metal"
    MaterialType.CARDBOARD  -> "Cartón"
    MaterialType.ORGANIC    -> "Orgánico"
    MaterialType.ELECTRONIC -> "Electrónico"
    MaterialType.OTHER      -> "Otro"
}

// =============================================================================
// Sección 2: Detalles (texto libre)
// =============================================================================

@Composable
private fun DetallesSection(
    detalles: String,
    onDetallesChange: (String) -> Unit
) {
    SectionTitle("📝 Detalles del material")
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = detalles,
        onValueChange = { if (it.length <= 300) onDetallesChange(it) },
        placeholder = { Text("Ej: 3 botellas de plástico, cartón doblado, latas de aluminio…") },
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        maxLines = 4,
        supportingText = { Text("${detalles.length}/300") }
    )
}

// =============================================================================
// Sección 3: Ubicación (igual que antes, sin cambios)
// =============================================================================

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
    SectionTitle("📍 ¿Dónde recogen el material?")
    Spacer(Modifier.height(8.dp))

    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    location.label.ifBlank { "Ubicación guardada" },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Otra ubicación
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
                Icons.Filled.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Otra ubicación", style = MaterialTheme.typography.bodyLarge)
        }

        if (useCustomLocation) {
            if (customPickupLocation != null) {
                Text(
                    text = "Ubicación: %.5f, %.5f".format(
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
                ) { Text("Cambiar en el mapa") }
            } else {
                OutlinedButton(
                    onClick = onOpenMapPicker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp)
                ) {
                    Icon(Icons.Filled.Place, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Seleccionar en el mapa")
                }
            }
        }
    }

    if (showError) {
        Spacer(Modifier.height(4.dp))
        Text(
            "Selecciona una ubicación de recogida",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// =============================================================================
// Diálogo: selector de mapa (igual que antes)
// =============================================================================

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

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val token = CancellationTokenSource()
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
                .addOnSuccessListener { loc ->
                    loc?.let {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(GmsLatLng(it.latitude, it.longitude), 15f)
                        )
                    }
                }
        }
    }

    LaunchedEffect(Unit) {
        if (checkLocationPermission(context)) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(GmsLatLng(it.latitude, it.longitude), 15f)
                    )
                }
            }
        } else {
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
                    onMapClick = { gms -> pickedGms = gms },
                    properties = MapProperties(isMyLocationEnabled = checkLocationPermission(context))
                ) {
                    pickedGms?.let { gms ->
                        Marker(state = rememberMarkerState(position = gms), title = "Punto de recogida")
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
                            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                                Text("Cancelar")
                            }
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

// =============================================================================
// Utilidades
// =============================================================================

private fun crearUriTemporal(context: Context): Uri {
    val archivo = File.createTempFile("material_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}