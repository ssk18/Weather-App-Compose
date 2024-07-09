package com.ssk.weatherapp.ui.screens.uistates


data class CombinedWeatherState(
    val currentWeather: CurrentWeatherUIState?,
    val weatherForecast: List<WeatherForecastUiState>?
)
