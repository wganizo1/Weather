package com.wganizo.weather.ui.forecast

data class WeatherForecastResponse(
    val list: List<WeatherListItem>
)

data class WeatherListItem(
    val dt_txt: String,
    val main: Main,
    val weather: List<WeatherDetails>
)

data class Main(
    val temp: Double,
    val humidity: String
)

data class WeatherDetails(
    val icon: String,
    val description: String,
    val tempMax: Double,
    val tempMin: Double,
    val humidity: String
)

data class Weather(
    val date: String,
    val tempMax: Double,
    val tempMin: Double,
    val temp: Double,
    val description: String,
    val icon: String,
)

