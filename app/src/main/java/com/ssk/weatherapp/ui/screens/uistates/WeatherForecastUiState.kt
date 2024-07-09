package com.ssk.weatherapp.ui.screens.uistates

import android.os.Build
import androidx.annotation.RequiresApi
import com.ssk.weatherapp.data.model.ForecastResponse
import com.ssk.weatherapp.utils.dayOfWeek
import java.time.format.TextStyle
import java.util.Locale

data class WeatherForecastUiState(
    val day: String,
    val weatherDescription: String,
    val id: Int,
    val temperature: Double,
    val dateTime: String
)

@RequiresApi(Build.VERSION_CODES.O)
internal fun ForecastResponse.toWeatherForecastUiState(): List<WeatherForecastUiState> {
    val uniqueDays = mutableSetOf<String>()
    return this.list.map { forecast ->
        val dateTime = forecast.dt_txt
        val dayOfWeek =
            forecast.dt_txt.dayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())
        WeatherForecastUiState(
            day = dayOfWeek,
            weatherDescription = forecast.weather.firstOrNull()?.description ?: "Unknown",
            temperature = forecast.main.temp,
            dateTime = dateTime,
            id = forecast.weather.firstOrNull()?.id ?: 0
        )
    }.filter { uniqueDays.add(it.day) }
        .take(5)
}