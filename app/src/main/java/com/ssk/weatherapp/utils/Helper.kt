package com.ssk.weatherapp.utils

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.compose.CloudyBlue
import com.example.compose.RainyGrey
import com.example.compose.SunnyGreen
import com.ssk.weatherapp.R
import com.ssk.weatherapp.ui.screens.WeatherUIState
import kotlin.math.roundToInt


@DrawableRes
internal fun selectWeatherImage(uiState: WeatherUIState?): Int {
    return when (uiState != null) {
        uiState?.isRaining -> R.drawable.forest_rainy
        uiState?.isCloudy -> R.drawable.forest_cloudy
        uiState?.isSunny -> R.drawable.forest_sunny
        else -> R.drawable.clear // Fallback image
    }
}

internal fun backgroundColour(condition:String?): Color {
    return when (condition != null) {
        condition?.contains("cloud", ignoreCase = true) -> CloudyBlue
        condition?.contains("rain", ignoreCase = true) -> RainyGrey
        else -> SunnyGreen
    }
}

@Composable
internal fun formatTemperature(temperature: Double): String {
    return stringResource(R.string.temperature_degrees, temperature.roundToInt())
}