package com.ssk.weatherapp.data.repository

import com.ssk.weatherapp.data.model.CurrentWeather
import com.ssk.weatherapp.data.remote.WeatherService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService,
) {
    fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): Flow<CurrentWeather> = flow {
        val response = weatherService.getCurrentWeather(lat, lon, apiKey)
        if (response.isSuccessful) {
            response.body()?.let { emit(it) }
            Timber.d("Weather response: ${response.body()}")
        } else {
            throw Exception("Error fetching weather data: ${response.message()}")
        }
    }
}