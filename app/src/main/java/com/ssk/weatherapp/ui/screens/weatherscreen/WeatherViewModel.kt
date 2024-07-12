package com.ssk.weatherapp.ui.screens.weatherscreen

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.ssk.weatherapp.BuildConfig
import com.ssk.weatherapp.data.repository.WeatherRepository
import com.ssk.weatherapp.ui.screens.uistates.CombinedWeatherState
import com.ssk.weatherapp.ui.screens.uistates.CurrentWeatherUIState
import com.ssk.weatherapp.ui.screens.uistates.WeatherForecastUiState
import com.ssk.weatherapp.ui.screens.uistates.toWeatherForecastUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val weatherRepository: WeatherRepository,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _locationUpdates = MutableStateFlow<Location?>(null)
    val locationUpdates: StateFlow<Location?> get() = _locationUpdates.asStateFlow()

    private val _combinedWeatherState = MutableStateFlow<CombinedWeatherState?>(null)
    val combinedWeatherState: StateFlow<CombinedWeatherState?> = _combinedWeatherState.asStateFlow()

    private val _autoCompleteSuggestions =
        MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val autoCompleteSuggestions: StateFlow<List<AutocompletePrediction>> =
        _autoCompleteSuggestions.asStateFlow()

    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(locationResult: LocationResult) {
            _locationUpdates.value = locationResult.lastLocation
            Timber.d("Location updated: ${locationResult.locations}")
            fetchWeatherData()
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

    fun clearSuggestions() {
        _autoCompleteSuggestions.tryEmit(emptyList())
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private suspend fun getCurrentWeather(): CurrentWeatherUIState? {
        val location = _locationUpdates.value ?: return null
        var currentWeatherState: CurrentWeatherUIState? = null
        weatherRepository.getCurrentWeather(
            location.latitude,
            location.longitude,
            BuildConfig.openWeatherApiKey
        ).takeIf { it.isSuccessful }?.body()?.let { cWeather ->
            currentWeatherState = CurrentWeatherUIState.fromCurrentWeather(cWeather)
        }
        return currentWeatherState
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getWeatherForecast(): List<WeatherForecastUiState>? {
        val location = _locationUpdates.value ?: return null
        var forecastList: List<WeatherForecastUiState> = emptyList()
        weatherRepository.getWeatherForecast(
            location.latitude,
            location.longitude,
            BuildConfig.openWeatherApiKey
        ).takeIf { it.isSuccessful }?.body()?.let { mforecastResponse ->
            forecastList = mforecastResponse.toWeatherForecastUiState()
        }
        return forecastList
    }

    private suspend fun getCurrentCityWeather(cityName: String): CurrentWeatherUIState? {
        var currentWeatherState: CurrentWeatherUIState? = null
        weatherRepository.getCurrentWeather(
            cityName,
            BuildConfig.openWeatherApiKey
        ).takeIf { it.isSuccessful }?.body()?.let { cWeather ->
            currentWeatherState = CurrentWeatherUIState.fromCurrentWeather(cWeather)
        }
        return currentWeatherState
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getCityWeatherForecast(cityName: String): List<WeatherForecastUiState>? {
        var forecastList: List<WeatherForecastUiState> = emptyList()
        weatherRepository.getWeatherForecast(
            cityName,
            BuildConfig.openWeatherApiKey
        ).takeIf { it.isSuccessful }?.body()?.let { mforecastResponse ->
            forecastList = mforecastResponse.toWeatherForecastUiState()
        }
        return forecastList
    }

    private suspend fun <T> fetchWeatherReport(
        repositoryCall: (latitude: Double, longitude: Double, apiKey: String) -> T
    ): T? {
        return runCatching {
            val location = _locationUpdates.value ?: return null
            repositoryCall(location.latitude, location.longitude, BuildConfig.openWeatherApiKey)
        }.getOrNull()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentWeatherDeferred = async { getCurrentWeather() }
            val weatherForecastDeferred = async { getWeatherForecast() }

            try {
                val currentWeather = currentWeatherDeferred.await()
                val weatherForecast = weatherForecastDeferred.await()

                if (currentWeather != null && weatherForecast != null) {
                    _combinedWeatherState.value = CombinedWeatherState(
                        currentWeather = currentWeather,
                        weatherForecast = weatherForecast
                    )
                } else {
                    Timber.e("Failed to fetch one of the weather data components")
                }
            } catch (e: Exception) {
                Timber.e("Error fetching weather data concurrently: ${e.message}")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchCityWeatherData(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentWeatherDeferred = async { getCurrentCityWeather(cityName = cityName) }
            val weatherForecastDeferred = async { getCityWeatherForecast(cityName = cityName) }

            try {
                val currentWeather = currentWeatherDeferred.await()
                val weatherForecast = weatherForecastDeferred.await()

                if (currentWeather != null && weatherForecast != null) {
                    _combinedWeatherState.value = CombinedWeatherState(
                        currentWeather = currentWeather,
                        weatherForecast = weatherForecast
                    )
                } else {
                    Timber.e("Failed to fetch one of the weather data components")
                }
            } catch (e: Exception) {
                Timber.e("Error fetching weather data concurrently: ${e.message}")
            }
        }
    }


    fun fetchAutoCompleteSuggestions(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _autoCompleteSuggestions.value = response.autocompletePredictions
                }.addOnFailureListener { exception ->
                    Timber.e("Error fetching auto-complete suggestions: ${exception.message}")
                }
        }
    }

}