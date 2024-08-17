package com.wganizo.weather.ui.forecast

import com.wganizo.weather.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

class WeatherForecastRepository {

    private val weatherApi: WeatherApi
    private val constants = Constants()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(constants.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherApi = retrofit.create(WeatherApi::class.java)
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherForecastResponse? {
        return try {
            val response = weatherApi.getWeather(lat, lon, constants.apiKey)
            if (response.isSuccessful) {
                response.body()
            } else null
        } catch (e: Exception) {
            null
        }
    }


    interface WeatherApi {
        @GET("forecast")
        suspend fun getWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): Response<WeatherForecastResponse>
    }
}
