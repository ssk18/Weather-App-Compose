package com.ssk.weatherapp.ui.screens
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class WeatherUIStateProvider : PreviewParameterProvider<WeatherUIState> {
    override val values: Sequence<WeatherUIState> = sequenceOf(
        WeatherUIState(
            temperature = 25.0,
            weatherDescription = "Sunny",
            isCloudy = true,
            isSunny = false,
            weatherIcon = "2",
            isRaining = false,
            main = null,
            city = "QueensTown"
        )
    )
}