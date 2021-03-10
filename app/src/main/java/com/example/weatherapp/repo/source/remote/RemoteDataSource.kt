package com.example.weatherapp.repo.source.remote

import com.example.weatherapp.data.WeatherResponse

interface RemoteDataSource {
    suspend fun fetch(callback: WeatherRemoteDataSource.RemoteCallback)
    suspend fun fetchByCityID(cityId: Int, callback: WeatherRemoteDataSource.RemoteCallback)
    suspend fun fetchByCityName(cityName: String, callback: WeatherRemoteDataSource.RemoteCallback)

    suspend fun fetch(handler: (WeatherResponse?, Throwable?) -> Unit)
    suspend fun fetchByCityID(cityId: Int, handler: (WeatherResponse?, Throwable?) -> Unit)
    suspend fun fetchByCityName(cityName: String, handler: (WeatherResponse?, Throwable?) -> Unit)
}