package com.ssk.weatherapp.utils

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.compose.CloudyBlue
import com.example.compose.RainyGrey
import com.example.compose.SunnyGreen
import com.ssk.weatherapp.R
import com.ssk.weatherapp.ui.screens.uistates.CurrentWeatherUIState
import com.ssk.weatherapp.ui.screens.uistates.WeatherForecastUiState
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@DrawableRes
internal fun selectWeatherImage(uiState: CurrentWeatherUIState?): Int {
    return when (uiState != null) {
        uiState?.isRaining -> R.drawable.forest_rainy
        uiState?.isCloudy -> R.drawable.forest_cloudy
        uiState?.isSunny -> R.drawable.forest_sunny
        else -> R.drawable.clear // Fallback image
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@DrawableRes
internal fun selectWeatherIcon(forecast: WeatherForecastUiState?): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDateTime = LocalDateTime.parse(forecast?.dateTime, formatter)
    val hour = localDateTime.hour
    val isDay = isDayTime(hour)
    return when (forecast != null) {
        forecast?.weatherDescription?.contains(
            "rain",
            ignoreCase = true
        ) -> if (isDay) R.drawable.rain_day else R.drawable.rain_night

        forecast?.weatherDescription?.contains(
            "cloud",
            ignoreCase = true
        ) -> if (isDay) R.drawable.cloudy_day else R.drawable.clear_night

        forecast?.weatherDescription?.contains(
            "clear",
            ignoreCase = true
        ) -> if (isDay) R.drawable.day_clear else R.drawable.clear_night

        forecast?.weatherDescription?.contains(
            "Thunderstorm",
            ignoreCase = true
        ) -> R.drawable.thunderstorm_night

        forecast?.weatherDescription?.contains(
            "Drizzle",
            ignoreCase = true
        ) -> R.drawable.shower_rain

        forecast?.weatherDescription?.contains(
            "freezing rain",
            ignoreCase = true
        ) -> R.drawable.snowfall

        (forecast?.id in 700..799) -> R.drawable.mist
        else -> if (isDay) R.drawable.clear else R.drawable.clear_night // Fallback image
    }
}

internal fun backgroundColour(condition: String?): Color {
    return when (condition != null) {
        condition?.contains("cloud", ignoreCase = true) -> CloudyBlue
        condition?.contains("rain", ignoreCase = true) -> RainyGrey
        else -> SunnyGreen
    }
}

@Composable
internal fun Double.formatTemperature(): String {
    return stringResource(R.string.temperature_degrees, this.roundToInt())
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun String.dayOfWeek(): DayOfWeek {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDateTime = LocalDateTime.parse(this, formatter)
    return localDateTime.dayOfWeek
}

fun Double.roundToInt(): Int = Math.round(this).toInt()

fun isDayTime(hour: Int): Boolean {
    return hour in 6..18
}