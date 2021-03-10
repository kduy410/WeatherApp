package com.example.weatherapp.repo

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.repo.source.local.Entities.WeatherResponseEntity

/**
 * Interface to the data layer.
 */

interface WeatherRepository {

    fun observeWeathers(): LiveData<List<WeatherResponseEntity>>

    fun observeWeather(weatherId: Int): LiveData<WeatherResponseEntity>

    suspend fun getWeathers(): List<WeatherResponse?>

    suspend fun updateWeathers()

    suspend fun updateWeathers(handler: (WeatherResponse?, Throwable?) -> Unit)

    suspend fun getWeatherById(weatherId: Int): WeatherResponse?

    suspend fun updateWeatherById(weatherId: Int)

    suspend fun updateWeatherById(weatherId: Int, handler: (WeatherResponse?, Throwable?) -> Unit)

    suspend fun updateWeatherByName(
        cityName: String,
        handler: (WeatherResponse?, Throwable?) -> Unit
    )

    suspend fun getLastResponseId(): Int?

    suspend fun refresh()

    suspend fun refresh(cityId: Int)

    suspend fun save(weather: WeatherResponse): Int

    suspend fun deleteWeather(weatherId: Int)

    suspend fun deleteAll()

    suspend fun convertEntitiesToPOJOs(entities: List<WeatherResponseEntity?>): List<WeatherResponse?>

    suspend fun convertEntityToPOJO(entity: WeatherResponseEntity?): WeatherResponse?
}