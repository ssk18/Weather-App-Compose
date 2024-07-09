package com.ssk.weatherapp.data.remote

import com.ssk.weatherapp.data.model.CurrentWeather
import com.ssk.weatherapp.data.model.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather?units=metric")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appId") appId: String,
    ): Response<CurrentWeather>

    @GET("forecast?units=metric")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appId") appId: String,
    ): Response<ForecastResponse>

}
