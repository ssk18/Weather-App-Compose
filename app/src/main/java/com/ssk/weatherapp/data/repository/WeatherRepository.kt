package com.ssk.weatherapp.data.repository

import com.ssk.weatherapp.data.model.CurrentWeather
import com.ssk.weatherapp.data.model.ForecastResponse
import com.ssk.weatherapp.data.remote.WeatherService
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService,
) {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Response<CurrentWeather> =
        weatherService.getCurrentWeather(lat, lon, apiKey)

    suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Response<ForecastResponse> =
        weatherService.getWeatherForecast(lat, lon, apiKey)

    suspend fun getCurrentWeather(
        cityName: String,
        apiKey: String
    ): Response<CurrentWeather> =
        weatherService.getCityWeather(cityName, apiKey)

    suspend fun getWeatherForecast(
        cityName: String,
        apiKey: String
    ): Response<ForecastResponse> =
        weatherService.getCityForecast(cityName, apiKey)

}