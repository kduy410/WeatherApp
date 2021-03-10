package com.example.weatherapp.retrofit

import com.example.weatherapp.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IHttpService {
    /**
     * If you want suspend function => you will have to wrap WeatherResponse in Response of Retrofit2
     */
    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Simple<WeatherResponse>

    @GET("weather")
    fun getWeatherByCityId(
        @Query("id") id: Int,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Simple<WeatherResponse>

    @GET("weather")
    fun getWeatherByCityName(
        @Query("q") cityName: String,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Simple<WeatherResponse>

    /**
     * Use difference types of WeatherResponse
     */
    @GET("onecall")
    fun getOnecall(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Simple<WeatherResponse>
}