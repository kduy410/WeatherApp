package com.example.weatherapp.repo.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.repo.source.local.Entities.*

@Database(
    entities = [WeatherResponseEntity::class,
        WeatherEntity::class,
        CoordEntity::class,
        MainEntity::class,
        WindEntity::class,
        CloudsEntity::class,
        SysEntity::class],
    version = 1,
    exportSchema = false
)

abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDAO(): WeatherDAO
}

class Converters