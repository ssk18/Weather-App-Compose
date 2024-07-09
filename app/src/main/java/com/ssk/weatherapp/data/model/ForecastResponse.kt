package com.ssk.weatherapp.data.model

data class ForecastResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Forecast>,
    val message: Int
) {
    data class Forecast(
        val clouds: Clouds,
        val dt: Int,
        val dt_txt: String,
        val main: MainForecast,
        val pop: Double,
        val rain: RainForecast,
        val sys: SysForecast,
        val visibility: Int,
        val weather: List<Weather>,
        val wind: Wind
    ) {
        data class Clouds(
            val all: Int
        )

        data class MainForecast(
            val feels_like: Double,
            val grnd_level: Int,
            val humidity: Int,
            val pressure: Int,
            val sea_level: Int,
            val temp: Double,
            val temp_kf: Double,
            val temp_max: Double,
            val temp_min: Double
        )

        data class RainForecast(
            val `3h`: Double
        )

        data class SysForecast(
            val pod: String
        )

        data class Weather(
            val description: String,
            val icon: String,
            val id: Int,
            val main: String
        )

        data class Wind(
            val deg: Int,
            val gust: Double,
            val speed: Double
        )
    }

    data class City(
        val coord: Coord,
        val country: String,
        val id: Int,
        val name: String,
        val population: Int,
        val sunrise: Int,
        val sunset: Int,
        val timezone: Int
    ) {
        data class Coord(
            val lat: Double,
            val lon: Double
        )
    }
}