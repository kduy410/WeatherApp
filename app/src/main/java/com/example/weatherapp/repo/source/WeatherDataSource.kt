package com.example.weatherapp.repo.source

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.repo.source.local.Entities.*

/**
 * MainEntity entry point for accessing weatherEntities data.
 */

interface WeatherDataSource {

    fun observeWeatherResponses(): LiveData<List<WeatherResponseEntity>>

    fun observeWeatherResponse(id: Int): LiveData<WeatherResponseEntity>

    suspend fun getListWeatherResponse(): Result<List<WeatherResponse>>

    suspend fun getWeatherResponse(id: Int): Result<WeatherResponse>

    suspend fun getCoord(id: Int): CoordEntity

    suspend fun getWeathers(id: Int): List<WeatherEntity>

    suspend fun getMain(id: Int): MainEntity

    suspend fun getWind(id: Int): WindEntity

    suspend fun getClouds(id: Int): CloudsEntity

    suspend fun getSys(id: Int): SysEntity

    suspend fun getWeatherResponseEntityById(id: Int): WeatherResponseEntity

    suspend fun save(weather: WeatherResponse): Result<Int>

    suspend fun deleteWeatherEntitiesById(weatherId: Int)

    suspend fun deleteCoordById(weatherId: Int)

    suspend fun deleteMainById(weatherId: Int)

    suspend fun deleteCloudsById(weatherId: Int)

    suspend fun deleteSysById(weatherId: Int)

    suspend fun deleteWindById(weatherId: Int)

    suspend fun deleteWeatherById(weatherId: Int)

    suspend fun deleteAll()

    suspend fun getLastResponse(): Result<Int>

}