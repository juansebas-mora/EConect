package com.econect.app.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

data class LocationPermissionState(
    val isGranted: Boolean,
    val requestPermission: () -> Unit
)

fun checkLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

@Composable
fun rememberLocationPermissionState(
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
): LocationPermissionState {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    var showSettingsRedirect by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            showSettingsRedirect = true
            onDenied()
        }
    }

    if (showRationale) {
        LocationPermissionRationaleDialog(
            onConfirm = {
                showRationale = false
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            onDismiss = {
                showRationale = false
                onDenied()
            }
        )
    }

    if (showSettingsRedirect) {
        LocationSettingsRedirectDialog(
            onConfirm = {
                showSettingsRedirect = false
                openAppSettings(context)
            },
            onDismiss = { showSettingsRedirect = false }
        )
    }

    val isGranted = checkLocationPermission(context)
    return LocationPermissionState(
        isGranted = isGranted,
        requestPermission = {
            if (isGranted) {
                onGranted()
            } else {
                showRationale = true
            }
        }
    )
}

@Composable
private fun LocationPermissionRationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permiso de ubicación") },
        text = {
            Text(
                "EConect necesita acceso a tu ubicación para mostrar " +
                    "recicladores cercanos y centros de acopio. " +
                    "Tu ubicación nunca se comparte sin tu consentimiento."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Permitir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Ahora no") }
        }
    )
}

@Composable
private fun LocationSettingsRedirectDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permiso requerido") },
        text = {
            Text(
                "El permiso de ubicación fue denegado. Para habilitarlo, " +
                    "ve a Configuración > Permisos > Ubicación."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Abrir configuración") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}
