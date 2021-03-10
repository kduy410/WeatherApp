package com.example.weatherapp.repo.source.local

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.extension.toModel
import com.example.weatherapp.extension.toPOJOs
import com.example.weatherapp.repo.source.WeatherDataSource
import com.example.weatherapp.repo.source.local.Entities.*
import kotlinx.coroutines.*
import timber.log.Timber

class WeatherLocalDataSource internal constructor(private val weatherDAO: WeatherDAO) :
    WeatherDataSource {

    private val ioDispatcher = Dispatchers.IO
    private val defaultDispatcher = Dispatchers.Default

    private val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        Timber.e("Coroutine context: $coroutineContext,Exception: $throwable")
    }

    override fun observeWeatherResponses(): LiveData<List<WeatherResponseEntity>> {
        return weatherDAO.observeListWeatherResponseEntity()
    }

    override fun observeWeatherResponse(id: Int): LiveData<WeatherResponseEntity> {
        return weatherDAO.observeWeatherResponseEntityById(id)
    }

    override suspend fun getLastResponse(): Result<Int> =
        withContext(ioDispatcher) {
            try {
                val list = weatherDAO.getLatestWeather()
                if (list.isEmpty()) {
                    return@withContext Result.Error(Exception("Empty list!"))
                }
                return@withContext Result.Success(list.last().weatherResponseDataId)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(Exception("Unable to fetch last response!"))
            }
        }


    override suspend fun getListWeatherResponse(): Result<List<WeatherResponse>> =
        withContext(ioDispatcher) {
            try {
                val list = ArrayList<WeatherResponse>()
                val weatherResponses = weatherDAO.getWeathers()
                weatherResponses.forEach {
                    try {
                        val response = getWeatherResponse(it.id)
                        if (response is Result.Success) {
                            response.data?.let {
                                list.add(response.data)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Result.Success(list.toList())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }


    override suspend fun getWeatherResponse(id: Int): Result<WeatherResponse> =
        withContext(ioDispatcher) {
            // withContext waits for all children coroutines
            return@withContext try {
                // Tiep tuc cac task khac du mot trong so chung failed
                supervisorScope {
                    val response = WeatherResponse()

                    val weatherResponseEntityDeferred = async { getWeatherResponseEntityById(id) }
                    val coordDeferred = async { getCoord(id) }
                    val weathersDeferred = async { getWeathers(id) }
                    val mainDeferred = async { getMain(id) }
                    val windDeferred = async { getWind(id) }
                    val cloudsDeferred = async { getClouds(id) }
                    val sysDeferred = async { getSys(id) }

                    val weatherResponseEntity = try {
                        weatherResponseEntityDeferred.await()
                    } catch (e: Exception) {
                        null
                    }.let {
                        response.base = it?.base
                        response.visibility = it?.visibility
                        response.dt = it?.dt
                        response.id = it?.id
                        response.name = it?.name
                        response.cod = it?.cod
                        response.timezone = it?.timezone
                    }

                    try {
                        coordDeferred.await()
                    } catch (e: Exception) {
                        null
                    }.let {
                        response.coord = it?.toPOJOs()
                    }

                    try {
                        weathersDeferred.await()
                    } catch (e: Exception) {
                        emptyList<WeatherEntity>()
                    }.let {
                        response.weather = it.toPOJOs()
                    }

                    try {
                        mainDeferred.await()
                    } catch (e: Exception) {
                        null
                    }.let {
                        response.main = it?.toPOJOs()
                    }

                    try {
                        windDeferred.await()
                    } catch (e: Exception) {
                        null
                    }.let {
                        response.wind = it?.toPOJOs()
                    }

                    try {
                        cloudsDeferred.await()
                    } catch (e: Exception) {
                        null
                    }.let {
                        response.clouds = it?.toPOJOs()
                    }

                    try {
                        sysDeferred.await()
                    } catch (e: Exception) {
                        null
                    }.let {
                        response.sys = it?.toPOJOs()
                    }

                    return@supervisorScope Result.Success(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(Exception("Error while fetching data!"))
            }
        }

    override suspend fun getWeatherResponseEntityById(id: Int): WeatherResponseEntity =
        weatherDAO.getWeatherResponseById(id)

    override suspend fun getCoord(id: Int): CoordEntity {
        return weatherDAO.getWeatherResponseAndCoord(id).last().coordEntity
    }

    override suspend fun getWeathers(id: Int): List<WeatherEntity> {
        return weatherDAO.getWeatherResponseWithWeathers(id).last().weatherEntities
    }

    override suspend fun getMain(id: Int): MainEntity {
        return weatherDAO.getWeatherResponseAndMain(id).last().mainEntity
    }

    override suspend fun getWind(id: Int): WindEntity {
        return weatherDAO.getWeatherResponseAndWind(id).last().windEntity
    }

    override suspend fun getClouds(id: Int): CloudsEntity {
        return weatherDAO.getWeatherResponseAndClouds(id).last().cloudsEntity
    }

    override suspend fun getSys(id: Int): SysEntity {
        return weatherDAO.getWeatherResponseAndSys(id).last().sysEntity
    }

    override suspend fun save(weather: WeatherResponse): Result<Int> =
        withContext(ioDispatcher) {
            try {
                supervisorScope {
                    Timber.e("SAVING...ID:[${weather.id}]")

                    val id = async { weatherDAO.insertWeatherResponseEntity(weather.toModel()) }

                    launch {
                        weather.coord?.let {
                            weatherDAO.insertCoordEntity(it.toModel(weather.id!!))
                        }
                    }

                    launch {
                        weather.weather?.let {
                            weatherDAO.insertWeather(it.toModel(weather.id!!))
                        }
                    }

                    launch {
                        weather.main?.let {
                            weatherDAO.insertMainEntity(it.toModel(weather.id!!))
                        }

                    }

                    launch {
                        weather.wind?.let {
                            weatherDAO.insertWindEntity(it.toModel(weather.id!!))
                        }
                    }

                    launch {
                        weather.clouds?.let {
                            weatherDAO.insertCloudsEntity(it.toModel(weather.id!!))
                        }
                    }

                    launch {
                        weather.sys?.let {
                            weatherDAO.insertSysEntity(it.toModel(weather.id!!))
                        }
                    }

                    Timber.e("FINISHED SAVING...ID:[${weather.id}]")
                    return@supervisorScope Result.Success(id.await().toInt())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Result.Error(Exception("Error while saving data!"))
            }
        }

    override suspend fun deleteWeatherEntitiesById(weatherId: Int) {
        withContext(ioDispatcher + handler) {
            Timber.e("DELETE WEATHER...ID:[$weatherId]")
            deleteCloudsById(weatherId)
            deleteCoordById(weatherId)
            deleteMainById(weatherId)
            deleteSysById(weatherId)
            deleteWindById(weatherId)
            deleteWeatherById(weatherId)
            val row = weatherDAO.deleteById(weatherId)
            Timber.e("ROW AFFECTED BY WEATHER ENTITIES: $row")
            Timber.e("DELETE WEATHER FINISHED...ID: [$weatherId]")
        }
    }


    override suspend fun deleteCoordById(weatherId: Int) {
        val row = weatherDAO.deleteCoordById(weatherId)
        Timber.e("ROW AFFECTED BY COORD ENTITY:${row}")
    }

    override suspend fun deleteMainById(weatherId: Int) {
        val row = weatherDAO.deleteMainById(weatherId)
        Timber.e("ROW AFFECTED BY MAIN ENTITY:${row}")
    }

    override suspend fun deleteCloudsById(weatherId: Int) {
        val row = weatherDAO.deleteCloudsById(weatherId)
        Timber.e("ROW AFFECTED BY CLOUDS ENTITY:${row}")
    }

    override suspend fun deleteSysById(weatherId: Int) {
        val row = weatherDAO.deleteSysById(weatherId)
        Timber.e("ROW AFFECTED BY SYS ENTITY:${row}")
    }

    override suspend fun deleteWindById(weatherId: Int) {
        val row = weatherDAO.deleteWindById(weatherId)
        Timber.e("ROW AFFECTED BY WIND ENTITY:${row}")
    }

    override suspend fun deleteWeatherById(weatherId: Int) {
        val row = weatherDAO.deleteWeatherById(weatherId)
        Timber.e("ROW AFFECTED BY WEATHER ENTITY:${row}")
    }

    override suspend fun deleteAll() {
        withContext(ioDispatcher + handler) {

            Timber.e("ERASING ALL DATA")
            val row = weatherDAO.deleteCloudsEntity() +
                    weatherDAO.deleteCoordEntity() +
                    weatherDAO.deleteWeatherEntity() +
                    weatherDAO.deleteMainEntity() +
                    weatherDAO.deleteWindEntity() +
                    weatherDAO.deleteSysEntity() +
                    weatherDAO.deleteWeatherEntities()
            Timber.e("FINISHED, No. Row affected:[$row]")
        }
    }
}