package com.ssk.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssk.weatherapp.R
import com.ssk.weatherapp.ui.screens.uistates.CurrentWeatherUIState
import com.ssk.weatherapp.ui.screens.uistates.WeatherForecastUiState
import com.ssk.weatherapp.ui.theme.WeatherAppTheme
import com.ssk.weatherapp.utils.backgroundColour
import com.ssk.weatherapp.utils.formatTemperature
import com.ssk.weatherapp.utils.roundToInt
import com.ssk.weatherapp.utils.selectWeatherIcon
import com.ssk.weatherapp.utils.selectWeatherImage

@Composable
fun WeatherScreen(
    innerPadding: PaddingValues = PaddingValues()
) {
    val viewModel = hiltViewModel<WeatherViewModel>()
    val combinedState by viewModel.combinedWeatherState.collectAsStateWithLifecycle()
    val currentWeatherState = combinedState?.currentWeather
    val forecastState = combinedState?.weatherForecast
    val weatherImage = selectWeatherImage(currentWeatherState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        if (combinedState == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = backgroundColour(currentWeatherState?.weatherDescription))
            ) {
                WeatherTopSection(
                    currentTemperature = { currentWeatherState?.temperature?.toInt() },
                    weatherCondition = { currentWeatherState?.weatherDescription },
                    weatherIcon = weatherImage,
                    city = { currentWeatherState?.city }
                )
                WeatherMiddleSection(
                    minTemperature = { currentWeatherState?.main?.temp_min?.toInt() },
                    currentTemperature = { currentWeatherState?.main?.temp?.toInt() },
                    maxTemperature = { currentWeatherState?.main?.temp_max?.toInt() },
                    condition = { currentWeatherState?.weatherDescription }
                )
                HorizontalDivider(color = Color.White)
                WeatherBottomSection(
                    dailyWeather = { forecastState ?: emptyList() },
                    condition = { currentWeatherState?.weatherDescription },
                )
            }
        }
    }
}

@Composable
fun WeatherTopSection(
    currentTemperature: () -> Int?,
    weatherCondition: () -> String?,
    city: () -> String?,
    weatherIcon: Int
) {
    Box {
        Image(
            painter = painterResource(id = weatherIcon),
            contentDescription = "Weather Icon",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "${currentTemperature() ?: "N/A"}°",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 40.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = weatherCondition() ?: "N/A",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = city() ?: "N/A",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun WeatherMiddleSection(
    currentTemperature: () -> Int?,
    minTemperature: () -> Int?,
    maxTemperature: () -> Int?,
    condition: () -> String?
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(color = backgroundColour(condition()))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${minTemperature() ?: "N/A"}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = "min",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${currentTemperature() ?: "N/A"}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = "Current",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${maxTemperature() ?: "N/A"}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = "max",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun WeatherBottomSection(
    dailyWeather: () -> List<WeatherForecastUiState>,
    condition: () -> String?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(color = backgroundColour(condition()))
    ) {
        items(dailyWeather()) { weather ->
            val weatherIcon = selectWeatherIcon(weather)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weather.day,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = weatherIcon),
                    contentDescription = "Weather Icon",
                    modifier = Modifier
                        .size(32.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = weather.temperature.formatTemperature(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherTopPreview() {
    WeatherAppTheme {
        WeatherTopSection(
            currentTemperature = { 25 },
            weatherCondition = { "Sunny" },
            weatherIcon = R.drawable.forest_rainy,
            city = { "Dhule" }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherMidPreview(@PreviewParameter(WeatherUIStateProvider::class) uiState: CurrentWeatherUIState) {
    WeatherAppTheme {
        WeatherMiddleSection(
            currentTemperature = { 25 },
            minTemperature = { 10 },
            maxTemperature = { 45 },
            condition = { "cloud" }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherBottomPreview() {
    WeatherAppTheme {
        WeatherBottomSection(
            dailyWeather = {
                listOf(
                    WeatherForecastUiState(
                        day = "Monday",
                        weatherDescription = "Rain",
                        temperature = 20.0,
                        dateTime = "",
                        id = 1
                    ),
                    WeatherForecastUiState(
                        day = "Tuesday",
                        weatherDescription = "Cloudy",
                        temperature = 22.0,
                        dateTime = "",
                        id = 1
                    ),
                    WeatherForecastUiState(
                        day = "Wednesday",
                        weatherDescription = "Sunny",
                        temperature = 25.0,
                        dateTime = "",
                        id = 1
                    )
                )
            },
            condition = { "Sunny" },
        )
    }
}