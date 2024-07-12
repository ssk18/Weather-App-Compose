package com.ssk.weatherapp.ui.screens.weatherscreen
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ssk.weatherapp.ui.screens.uistates.CurrentWeatherUIState

class WeatherUIStateProvider : PreviewParameterProvider<CurrentWeatherUIState> {
    override val values: Sequence<CurrentWeatherUIState> = sequenceOf(
        CurrentWeatherUIState(
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