package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

/**
 * Class for Retrofit response
 */
data class WeatherResponse constructor(
    @SerializedName("coord")
    var coord: Coord? = null,

    @SerializedName("weather")
    var weather: List<Weather?>? = null,

    @SerializedName("base")
    var base: String? = null,

    @SerializedName("main")
    var main: Main? = null,

    @SerializedName("visibility")
    var visibility: Double? = null,

    @SerializedName("wind")
    var wind: Wind? = null,

    @SerializedName("clouds")
    var clouds: Clouds? = null,

    @SerializedName("dt")
    var dt: Long? = null,

    @SerializedName("sys")
    var sys: Sys? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("cod")
    var cod: Int? = null,

    @SerializedName("timezone")
    var timezone: Long? = null,
)

data class Coord constructor(

    @SerializedName("lon")
    var lon: Double? = null,

    @SerializedName("lat")
    var lat: Double? = null
)

data class Weather constructor(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("main")
    var main: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("icon")
    var icon: String? = null
)

data class Main constructor(
    @SerializedName("temp")
    var temp: Double? = null,

    @SerializedName("feels_like")
    var feelsLike: Double? = null,

    @SerializedName("pressure")
    var pressure: Double? = null,

    @SerializedName("humidity")
    var humidity: Double? = null,

    @SerializedName("temp_min")
    var temp_min: Double? = null,

    @SerializedName("temp_max")
    var temp_max: Double? = null
)

data class Wind constructor(

    @SerializedName("speed")
    var speed: Double? = null,

    @SerializedName("deg")
    var deg: Int? = null
)

data class Clouds constructor(
    @SerializedName("all")
    var all: Int? = null
)

data class Sys constructor(

    @SerializedName("type")
    var type: Int? = null,

    @SerializedName("id")
    var id: Int? = null,

//    @SerializedName("message")
//    var message: Double?,

    @SerializedName("country")
    var country: String? = null,

    @SerializedName("sunrise")
    var sunrise: Long? = null,

    @SerializedName("sunset")
    var sunset: Long? = null
)
