package com.example.weatherapp.repo.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherapp.repo.source.local.Entities.*

@Dao
interface WeatherDAO {
    @Query("SELECT * FROM weatherEntities")
    fun observeListWeatherResponseEntity(): LiveData<List<WeatherResponseEntity>>

    @Query("SELECT * FROM weatherEntities WHERE id=:weatherId")
    fun observeWeatherResponseEntityById(weatherId: Int): LiveData<WeatherResponseEntity>

    @Query("SELECT * FROM weatherEntity WHERE weatherResponseDataId=:weatherId")
    fun observeWeatherEntityById(weatherId: Int): LiveData<List<WeatherEntity>>

    @Query("SELECT * FROM cloudsEntity WHERE weatherResponseDataId=:weatherId")
    fun observeCloudsEntityById(weatherId: Int): LiveData<CloudsEntity>

    @Query("SELECT * FROM coordEntity WHERE weatherResponseDataId=:weatherId")
    fun observeCoordEntityById(weatherId: Int): LiveData<CoordEntity>

    @Query("SELECT * FROM mainEntity WHERE weatherResponseDataId=:weatherId")
    fun observeMainEntityById(weatherId: Int): LiveData<MainEntity>

    @Query("SELECT * FROM sysEntity WHERE weatherResponseDataId=:weatherId")
    fun observeSysEntityById(weatherId: Int): LiveData<SysEntity>

    @Query("SELECT * FROM windEntity WHERE weatherResponseDataId=:weatherId")
    fun observeWindEntityById(weatherId: Int): LiveData<WindEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherResponseEntity(weatherResponseEntity: WeatherResponseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoordEntity(coordEntity: CoordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: List<WeatherEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMainEntity(mainEntity: MainEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWindEntity(windEntity: WindEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloudsEntity(cloudsEntity: CloudsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSysEntity(sysEntity: SysEntity): Long

    @Update
    suspend fun updateWeatherResponseEntity(weatherResponse: WeatherResponseEntity): Int

    @Update
    suspend fun updateCoordEntity(coordEntity: CoordEntity): Int

    @Update
    suspend fun updateWeatherEntity(weather: List<WeatherEntity>): Int

    @Update
    suspend fun updateMainEntity(mainEntity: MainEntity): Int

    @Update
    suspend fun updateWindEntity(windEntity: WindEntity): Int

    @Update
    suspend fun updateCloudsEntity(cloudsEntity: CloudsEntity): Int

    @Update
    suspend fun updateSysEntity(sysEntity: SysEntity): Int

    @Query("DELETE FROM weatherEntities WHERE id = :weatherId")
    suspend fun deleteById(weatherId: Int): Int

    @Query("DELETE FROM weatherEntity WHERE weatherResponseDataId = :weatherId")
    suspend fun deleteWeatherById(weatherId: Int): Int

    @Query("DELETE FROM cloudsEntity WHERE weatherResponseDataId = :weatherId")
    suspend fun deleteCloudsById(weatherId: Int): Int

    @Query("DELETE FROM coordEntity WHERE weatherResponseDataId = :weatherId")
    suspend fun deleteCoordById(weatherId: Int): Int

    @Query("DELETE FROM mainEntity WHERE weatherResponseDataId = :weatherId")
    suspend fun deleteMainById(weatherId: Int): Int

    @Query("DELETE FROM sysEntity WHERE weatherResponseDataId = :weatherId")
    suspend fun deleteSysById(weatherId: Int): Int

    @Query("DELETE FROM windEntity WHERE weatherResponseDataId = :weatherId")
    suspend fun deleteWindById(weatherId: Int): Int

    @Query("DELETE FROM weatherEntities")
    suspend fun deleteWeatherEntities(): Int

    @Query("DELETE FROM windEntity")
    suspend fun deleteWindEntity(): Int

    @Query("DELETE FROM sysEntity")
    suspend fun deleteSysEntity(): Int

    @Query("DELETE FROM mainEntity")
    suspend fun deleteMainEntity(): Int

    @Query("DELETE FROM coordEntity")
    suspend fun deleteCoordEntity(): Int

    @Query("DELETE FROM cloudsEntity")
    suspend fun deleteCloudsEntity(): Int

    @Query("DELETE FROM weatherEntity")
    suspend fun deleteWeatherEntity(): Int

    @Query("SELECT * FROM weatherEntities")
    suspend fun getWeathers(): List<WeatherResponseEntity>

    @Query("SELECT * FROM weatherEntity ORDER BY ROWID DESC LIMIT 1")
    suspend fun getLatestWeather(): List<WeatherEntity>

    @Query("SELECT * FROM weatherEntities WHERE id=:id")
    suspend fun getWeatherResponseById(id: Int): WeatherResponseEntity

    /**
     * This method requires Room to run two queries, so add the @Transaction annotation
     * to this method to ensure that the whole operation is performed atomically.
     */
    @Transaction
    @Query("SELECT * FROM weatherEntities WHERE id=:id")
    suspend fun getWeatherResponseAndCoord(id: Int): List<WeatherResponseAndCoord>

    @Transaction
    @Query("SELECT * FROM weatherEntities WHERE id=:id")
    suspend fun getWeatherResponseWithWeathers(id: Int): List<WeatherResponseWithWeathers>

    @Transaction
    @Query("SELECT * FROM weatherEntities WHERE id=:id")
    suspend fun getWeatherResponseAndMain(id: Int): List<WeatherResponseAndMain>

    @Transaction
    @Query("SELECT * FROM weatherEntities WHERE id=:id")
    suspend fun getWeatherResponseAndWind(id: Int): List<WeatherResponseAndWind>

    @Transaction
    @Query("SELECT * FROM weatherEntities WHERE id=:id")
    suspend fun getWeatherResponseAndClouds(id: Int): List<WeatherResponseAndClouds>

    @Transaction
    @Query("SELECT * FROM weatherEntities WHERE id=:id")
    suspend fun getWeatherResponseAndSys(id: Int): List<WeatherResponseAndSys>

}