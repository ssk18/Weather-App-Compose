package com.ssk.weatherapp.ui.screens

import com.ssk.weatherapp.data.model.CurrentWeather

data class WeatherUIState(
    val temperature: Double,
    val weatherDescription: String,
    val weatherIcon: String,
    val isRaining: Boolean,
    val isCloudy: Boolean,
    val isSunny: Boolean,
    val city: String,
    val main: CurrentWeather.Main?,
) {
    companion object {
        fun fromCurrentWeather(currentWeather: CurrentWeather): WeatherUIState {
            val weatherDescription = currentWeather.weather.firstOrNull()?.description ?: ""
            val weatherIcon = currentWeather.weather.firstOrNull()?.icon ?: ""
            val isRaining = (currentWeather.rain?.`1h` ?: 0.0) > 0
            val isCloudy = currentWeather.clouds.all > 30
            val isSunny = !isRaining && !isCloudy
            val city = currentWeather.name
            val main = currentWeather.main

            return WeatherUIState(
                temperature = currentWeather.main.temp,
                weatherDescription = weatherDescription,
                weatherIcon = weatherIcon,
                isRaining = isRaining,
                isCloudy = isCloudy,
                isSunny = isSunny,
                city = city,
                main = main
            )
        }
    }
}
