package com.wganizo.weather.ui.home

data class WeatherResponse(
    val main: Main
) {
    fun toWeather(lat: Double, lon: Double): Weather {
        return Weather(lat, lon, main.temp, main.humidity)
    }
}

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Weather(
    val latitude: Double,
    val longitude: Double,
    val temp: Double,
    val humidity: Int
)
