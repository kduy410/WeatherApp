package com.example.weatherapp.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.app.ActivityCompat
import com.example.weatherapp.data.*
import com.example.weatherapp.repo.source.local.Entities.*
import java.text.SimpleDateFormat
import java.util.*

fun isNetworkAvailable(context: Context?): Boolean {
    val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var result = false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    } else {
        connectivityManager.run {
            this.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return result
    }
}

fun isPermissionGranted(context: Context?): Boolean {
    if (context != null) {
        return !(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED)
    } else throw NullPointerException("Context is null!")
}

const val TIME_ZONE_FORMAT = "EEEE, dd.MM.yyyy, hh:mm aa zz"
const val SUNRISE_FORMAT = "hh:mm aa zz"

fun toDate(
    millis: Long?,
    dateFormat: String = TIME_ZONE_FORMAT
): String {
    // millis is in Seconds -> convert to miliseconds -> to Date
    val date =
        if (millis != null) Date(millis * 1000L) else Calendar.getInstance().time // *1000 is to convert seconds to milliseconds

    val formatter =
        SimpleDateFormat(dateFormat, Locale.getDefault()).apply { timeZone = TimeZone.getDefault() }
    return formatter.format(date)
}

fun toSunrise(
    millis: Long?,
    dateFormat: String = SUNRISE_FORMAT
): String {
    // millis is in Seconds -> convert to miliseconds -> to Date
    val date =
        if (millis != null) Date(millis * 1000L) else Calendar.getInstance().time // *1000 is to convert seconds to milliseconds

    val formatter =
        SimpleDateFormat(dateFormat, Locale.getDefault()).apply { timeZone = TimeZone.getDefault() }
    return formatter.format(date)
}

fun toSunriseWithTimeZone(
    millis: Long?,
    timeZone: Long?,
    dateFormat: String = SUNRISE_FORMAT
): String {
    // millis is in Seconds -> convert to miliseconds -> to Date
    val date =
        if (millis != null) Date(millis * 1000L) else Calendar.getInstance().time // *1000 is to convert seconds to milliseconds
    // GMT-08:00
    val offset =
        (if (timeZone != null) timeZone / 60 / 60 else TimeZone.getDefault().rawOffset / 60 / 60).toLong()

    val sign = if (offset > 0L) "+" else ""
    val utc = "GMT${sign}${offset}:00"

    val formatter =
        SimpleDateFormat(dateFormat, Locale.getDefault()).apply {
            this.timeZone = TimeZone.getTimeZone(utc)
        }

    return formatter.format(date)
}

fun toDateWithTimeZone(
    millis: Long?,
    timeZone: Long?,
    dateFormat: String = TIME_ZONE_FORMAT
): String {
    // millis is in Seconds -> convert to miliseconds -> to Date
    val date =
        if (millis != null) Date(millis * 1000L) else Calendar.getInstance().time // *1000 is to convert seconds to milliseconds
    // GMT-08:00
    val offset =
        (if (timeZone != null) timeZone / 60 / 60 else TimeZone.getDefault().rawOffset / 60 / 60).toLong()
    val sign = if (offset > 0L) "+" else ""
    val utc = "GMT${sign}${offset}:00"

    val formatter =
        SimpleDateFormat(dateFormat, Locale.getDefault()).apply {
            this.timeZone = TimeZone.getTimeZone(utc)
        }

    return formatter.format(date)
}

fun WeatherResponse.toModel(): WeatherResponseEntity {
    return WeatherResponseEntity(
        base = this.base,
        visibility = this.visibility,
        dt = this.dt,
        id = this.id!!,
        name = this.name,
        cod = this.cod,
        timezone = this.timezone
    )
}

fun CoordEntity.toPOJOs(): Coord {
    return Coord(this.lon, this.lat)
}

fun Coord.toModel(id: Int): CoordEntity {
    return CoordEntity(weatherResponseDataId = id, lon = this.lon, lat = this.lat)
}

fun WeatherEntity.toPOJOs(): Weather {
    return Weather(this.id, this.main, this.description, this.icon)
}

fun Weather.toModel(id: Int): WeatherEntity {
    return WeatherEntity(
        weatherResponseDataId = id,
        id = this.id,
        main = this.main,
        description = this.description,
        icon = this.icon
    )
}

fun List<WeatherEntity>.toPOJOs(): List<Weather> {
    return this.map {
        it.toPOJOs()
    }
}

fun List<Weather>.toModel(id: Int): List<WeatherEntity> {
    return this.map {
        it.toModel(id)
    }
}

@JvmName("toModelWeatherNull")
fun List<Weather?>.toModel(id: Int): List<WeatherEntity> {
    val list = arrayListOf<WeatherEntity>()
    this.forEach {
        if (it != null) list.add(it.toModel(id))
    }
    return list.toList()
}

fun MainEntity.toPOJOs(): Main {
    return Main(
        temp = this.temp,
        feelsLike = this.feelsLike,
        pressure = this.pressure,
        humidity = this.humidity,
        temp_min = this.temp_min,
        temp_max = this.temp_max
    )
}

fun Main.toModel(id: Int): MainEntity {
    return MainEntity(
        weatherResponseDataId = id,
        temp = this.temp,
        feelsLike = this.feelsLike,
        pressure = this.pressure,
        humidity = this.humidity,
        temp_min = this.temp_min,
        temp_max = this.temp_max,
    )
}

fun WindEntity.toPOJOs(): Wind {
    return Wind(this.speed, this.deg)
}

fun Wind.toModel(id: Int): WindEntity {
    return WindEntity(weatherResponseDataId = id, speed = this.speed, deg = this.deg)
}

fun CloudsEntity.toPOJOs(): Clouds {
    return Clouds(this.all)
}

fun Clouds.toModel(id: Int): CloudsEntity {
    return CloudsEntity(weatherResponseDataId = id, all = this.all)
}

fun SysEntity.toPOJOs(): Sys {
    return Sys(this.type, this.id, this.country, this.sunrise, this.sunset)
}

fun Sys.toModel(id: Int): SysEntity {
    return SysEntity(
        weatherResponseDataId = id,
        type = this.type,
        id = this.id,
        country = this.country,
        sunrise = this.sunrise,
        sunset = this.sunset
    )
}

fun <K, V> lazyMap(initializer: (K) -> V): Map<K, V> {
    val map = mutableMapOf<K, V>()
    return map.withDefault { key ->
        val newValue = initializer(key)
        map[key] = newValue
        return@withDefault newValue
    }
}


