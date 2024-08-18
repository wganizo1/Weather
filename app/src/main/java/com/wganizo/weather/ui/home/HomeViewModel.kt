package com.wganizo.weather.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _weatherText = MutableLiveData<String>().apply {
        value = "Weather information will appear here."
    }
    val weatherText: LiveData<String> = _weatherText

    private fun updateWeatherText(info: String) {
        _weatherText.value = info
    }

    fun fetchWeatherData(lat: Double, lon: Double, callback: (Weather) -> Unit) {
        viewModelScope.launch {
            val weather = weatherRepository.getWeather(lat, lon)
            weather?.let {
                updateWeatherText("Temperature: ${it.temp}°\nHumidity: ${it.humidity}%")
                callback(it)
            }
        }
    }
}
