package com.ssk.weatherapp.ui.screens.locationpermissions

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ssk.weatherapp.R
import com.ssk.weatherapp.ui.screens.splashscreen.SplashScreen
import com.ssk.weatherapp.ui.screens.weatherscreen.WeatherScreen
import com.ssk.weatherapp.ui.screens.weatherscreen.WeatherViewModel
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionLauncher(
    weatherViewModel: WeatherViewModel,
) {
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showDeniedDialog by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            weatherViewModel.startLocationUpdates()
        }
    }

    when {
        locationPermissionState.allPermissionsGranted -> {
            if (showLoading && weatherViewModel.combinedWeatherState.collectAsState().value == null) {
                SplashScreen()
            } else {
                WeatherScreen()
            }
        }

        locationPermissionState.shouldShowRationale || !locationPermissionState.allPermissionsGranted -> {
            showRationaleDialog = true
        }

        else -> {
            showDeniedDialog = true
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (showRationaleDialog) {
            RationaleDialog(
                onRequestPermission = {
                    coroutineScope.launch {
                        locationPermissionState.launchMultiplePermissionRequest()
                    }
                    if (locationPermissionState.allPermissionsGranted) {
                        showRationaleDialog = false
                    }
                },
                onDismiss = { showRationaleDialog = false }
            )
        }

        if (showDeniedDialog) {
            DeniedDialog(
                onDismiss = {
                    locationPermissionState.launchMultiplePermissionRequest()
                    if (locationPermissionState.allPermissionsGranted) {
                        showDeniedDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun RationaleDialog(onRequestPermission: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(stringResource(R.string.grant_permission))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = { Text(stringResource(R.string.the_app_needs_location_permissions_to_show_the_weather_information)) }
    )
}

@Composable
fun DeniedDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        },
        text = { Text(stringResource(R.string.location_permissions_are_denied_you_can_enable_them_in_the_app_settings)) }
    )
}

