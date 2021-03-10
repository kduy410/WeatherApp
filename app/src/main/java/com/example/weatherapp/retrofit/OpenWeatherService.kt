package com.example.weatherapp.retrofit

import com.example.weatherapp.data.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {

    /**
     * https://stackoverflow.com/questions/58429501/unable-to-invoke-no-args-constructor-for-retrofit2-call
     *
     * Cannot return Call<WeatherResponse> within suspend fun
     * Therefore return [Response]
     * https://android.jlelse.eu/kotlin-coroutines-and-retrofit-e0702d0b8e8f
     *
     * You may notice that instead of Call<T>,
     * we now have a function with the suspend modifier defined in our interface function.
     * According to Retrofit documentation this function will,
     * behind the scenes behave as a normal [Call.enqueue] operation.
     * Also we wrap our response in a Response object to get metadata about our request response e.g.
     * information like response code.
     * We no longer have to await() anymore as this is handled automatically!
     * As with all networking on Android its done on the background.
     * And this is a very clean way of doing so!
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Response<WeatherResponse>

    /**
     * Use difference types of WeatherResponse
     */
    @GET("onecall")
    suspend fun getOnecall(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Response<WeatherResponse>


}
