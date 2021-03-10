package com.example.weatherapp.repo.source.local.Entities

import androidx.room.*
import com.google.gson.annotations.SerializedName

/**
 * Class for Room model
 */
@Entity(tableName = "weatherEntities")
data class WeatherResponseEntity constructor(

    @ColumnInfo(name = "base")
    val base: String?,

    @ColumnInfo(name = "visibility")
    val visibility: Double?,

    @ColumnInfo(name = "dt")
    val dt: Long?,

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "cod")
    @SerializedName("cod")
    val cod: Int?,

    @ColumnInfo(name = "timezone")
    val timezone: Long?,
)

@Entity(tableName = "coordEntity")
data class CoordEntity constructor(
    val weatherResponseDataId: Int,
    /**
     * If the field type is long or int (or its TypeConverter converts it to a long or int),
     * Insert methods treat 0 as not-set while inserting the item.
     */
    @PrimaryKey(autoGenerate = true)
    val coordEntityId: Int = 0,

    @ColumnInfo(name = "lon")
    val lon: Double?,

    @ColumnInfo(name = "lat")
    val lat: Double?
)

/**
 * one-to-one relationship
 */

data class WeatherResponseAndCoord(
    @Embedded val weatherResponseEntity: WeatherResponseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherResponseDataId"
    )
    val coordEntity: CoordEntity,
)

/**
 *  one-to-many relationships
 */

data class WeatherResponseWithWeathers(
    @Embedded val weatherResponseEntity: WeatherResponseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherResponseDataId"
    )
    val weatherEntities: List<WeatherEntity>
)


@Entity(tableName = "weatherEntity")
data class WeatherEntity constructor(

    val weatherResponseDataId: Int,
    @PrimaryKey(autoGenerate = true)
    val weatherEntityId: Int = 0,

    @ColumnInfo(name = "id")
    val id: Int?,

    @ColumnInfo(name = "main")
    val main: String?,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "icon")
    val icon: String?
)

data class WeatherResponseAndMain(
    @Embedded val weatherResponseEntity: WeatherResponseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherResponseDataId"
    )
    val mainEntity: MainEntity
)

@Entity(tableName = "mainEntity")
data class MainEntity constructor(

    val weatherResponseDataId: Int,

    @PrimaryKey(autoGenerate = true)
    val mainEntityId: Int = 0,

    @ColumnInfo(name = "temp")
    val temp: Double?,

    @ColumnInfo(name = "feels_like")
    val feelsLike: Double?,

    @ColumnInfo(name = "pressure")
    val pressure: Double?,

    @ColumnInfo(name = "humidity")
    val humidity: Double?,

    @ColumnInfo(name = "temp_min")
    val temp_min: Double?,

    @ColumnInfo(name = "temp_max")
    val temp_max: Double?

)

data class WeatherResponseAndWind(
    @Embedded val weatherResponseEntity: WeatherResponseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherResponseDataId"
    )
    val windEntity: WindEntity
)

@Entity(tableName = "windEntity")
data class WindEntity constructor(
    val weatherResponseDataId: Int,
    @PrimaryKey(autoGenerate = true)
    val windEntityId: Int = 0,

    @ColumnInfo(name = "speed")
    val speed: Double?,

    @ColumnInfo(name = "deg")
    val deg: Int?
)

data class WeatherResponseAndClouds(
    @Embedded val weatherResponseEntity: WeatherResponseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherResponseDataId"
    )
    val cloudsEntity: CloudsEntity
)

@Entity(tableName = "cloudsEntity")
data class CloudsEntity constructor(
    val weatherResponseDataId: Int,
    @PrimaryKey(autoGenerate = true)
    val cloudsEntityId: Int = 0,

    @ColumnInfo(name = "all")
    val all: Int?
)

data class WeatherResponseAndSys(
    @Embedded val weatherResponseEntity: WeatherResponseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherResponseDataId"
    )
    val sysEntity: SysEntity,
)

@Entity(tableName = "sysEntity")
data class SysEntity constructor(
    val weatherResponseDataId: Int,

    @PrimaryKey(autoGenerate = true)
    val sysEntityId: Int = 0,

    @ColumnInfo(name = "type")
    val type: Int?,

    @ColumnInfo(name = "id")
    val id: Int?,

    @ColumnInfo(name = "country")
    val country: String?,

    @ColumnInfo(name = "sunrise")
    val sunrise: Long?,

    @ColumnInfo(name = "sunset")
    val sunset: Long?
)
