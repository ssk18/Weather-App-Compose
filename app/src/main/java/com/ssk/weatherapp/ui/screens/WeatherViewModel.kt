package com.ssk.weatherapp.ui.screens

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.ssk.weatherapp.BuildConfig
import com.ssk.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _locationUpdates = MutableStateFlow<Location?>(null)
    val locationUpdates: StateFlow<Location?> get() = _locationUpdates.asStateFlow()

    private val _weatherUIState = MutableStateFlow<WeatherUIState?>(null)
    val weatherUIState: StateFlow<WeatherUIState?> get() = _weatherUIState.asStateFlow()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            _locationUpdates.value = locationResult.lastLocation
            Timber.d("Location updated: ${locationResult.locations}")
            getCurrentWeather()
            Timber.e("Latest Weather: ${_weatherUIState.value?.weatherDescription}")
        }
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
            .setMinUpdateIntervalMillis(5000L)
            .setMinUpdateDistanceMeters(50f)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            .addOnFailureListener { exception ->
                Timber.e("Error requesting location updates: ${exception.message}")
            }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun getCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            while (_weatherUIState.value == null) {
                try {
                    val location = _locationUpdates.value ?: continue
                    weatherRepository.getCurrentWeather(
                        location.latitude,
                        location.longitude,
                        BuildConfig.openWeatherApiKey
                    ).onEach { weatherData ->
                        val newState = WeatherUIState.fromCurrentWeather(weatherData)
                        _weatherUIState.value = newState
                    }.firstOrNull()
                } catch (e: Exception) {
                    Timber.e("Error fetching weather data: ${e.message}")
                }
                delay(5000)  // Delay to prevent spamming the API
            }
        }
    }
}