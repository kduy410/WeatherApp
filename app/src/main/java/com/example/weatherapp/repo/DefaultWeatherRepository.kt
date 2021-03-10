package com.example.weatherapp.repo

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.repo.source.WeatherDataSource
import com.example.weatherapp.repo.source.local.Entities.WeatherResponseEntity
import com.example.weatherapp.repo.source.remote.RemoteDataSource
import com.example.weatherapp.repo.source.remote.WeatherRemoteDataSource
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class DefaultWeatherRepository constructor(
    private val localDataSource: WeatherDataSource,
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {

    private val ioDispatcher = Dispatchers.IO
    private val mainDispatcher = Dispatchers.Main
    private val backGroundScope = CoroutineScope(ioDispatcher)


    override fun observeWeathers(): LiveData<List<WeatherResponseEntity>> {
        return localDataSource.observeWeatherResponses()
    }

    override fun observeWeather(weatherId: Int): LiveData<WeatherResponseEntity> {
        return localDataSource.observeWeatherResponse(weatherId)
    }

    override suspend fun convertEntitiesToPOJOs(entities: List<WeatherResponseEntity?>): List<WeatherResponse?> =
        withContext(ioDispatcher) {
            try {
                val list = ArrayList<WeatherResponse?>()
                if (entities.isEmpty()) {
                    return@withContext list.toList()
                }
                entities.forEach {
                    if (it != null) {
                        val response = localDataSource.getWeatherResponse(it.id)
                        if (response is Result.Success && response.data != null) {
                            list.add(response.data)
                        }
                    }
                }
                list.toList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList<WeatherResponse?>()
            }
        }

    override suspend fun convertEntityToPOJO(entity: WeatherResponseEntity?): WeatherResponse? =
        withContext(ioDispatcher) {
            try {
                if (entity != null) {
                    val response = localDataSource.getWeatherResponse(entity.id)
                    if (response is Result.Success && response.data != null) {
                        return@withContext response.data
                    }
                }
                return@withContext null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


    override suspend fun getWeathers(): List<WeatherResponse?> =
        withContext(ioDispatcher) {
            try {
                Timber.e("FETCH RESOURCE...")
                val list = localDataSource.getListWeatherResponse()
                if (list is Result.Success && !list.data.isNullOrEmpty()) {
                    Timber.e("FETCH RESOURCE...SUCCESS")
                    list.data
                } else {
                    Timber.e("FETCH RESOURCE...FAIL")
                    emptyList<WeatherResponse?>()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList<WeatherResponse?>()
            }
        }

    /**
     * Get current location's Weather data and save it to database
     */
    override suspend fun updateWeathers() {
        withContext(ioDispatcher) {
            try {
                Timber.e("UPDATE RESOURCE...")
                remoteDataSource.fetch(object : WeatherRemoteDataSource.RemoteCallback {
                    override fun onSuccess(response: WeatherResponse?, t: Throwable?) {
                        if (response != null) {
                            backGroundScope.launch {
                                val id = save(response)
                                if (id != 0) {
                                    Timber.e("UPDATE RESOURCE[${response.id}]...SUCCESS")
                                } else {
                                    Timber.e("UPDATE RESOURCE[${response.id}]...FAIL")
                                }
                            }
                        } else {
                            t?.printStackTrace()
                            Timber.e("UPDATE RESOURCE...FAIL")
                        }
                    }

                    override fun onFailure(t: Throwable?) {
                        t?.printStackTrace()
                        if (t is NullPointerException) {

                        }
                        Timber.e("UPDATE RESOURCE...FAIL")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("UPDATE RESOURCE...FAIL")
            }
        }
    }

    /**
     * Get current location's Weather data and save it to database
     */
    override suspend fun updateWeathers(handler: (WeatherResponse?, Throwable?) -> Unit) {
        withContext(ioDispatcher) {
            try {
                remoteDataSource.fetch { response, throwable ->
                    backGroundScope.launch {
                        handleUpdate(response, throwable, handler)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler(null, e)
            }
        }
    }

    override suspend fun getWeatherById(weatherId: Int): WeatherResponse? =
        withContext(ioDispatcher) {
            try {
                Timber.e("FETCH RESOURCE...")
                val response = localDataSource.getWeatherResponse(weatherId)
                if (response is Result.Success && response.data != null) {
                    Timber.e("FETCH RESOURCE...SUCCESS")
                    response.data
                } else {
                    Timber.e("FETCH RESOURCE...FAIL")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


    override suspend fun updateWeatherById(weatherId: Int) {
        withContext(ioDispatcher) {
            try {
                Timber.e("UPDATE RESOURCE[$weatherId]...")
                remoteDataSource.fetchByCityID(weatherId,
                    object : WeatherRemoteDataSource.RemoteCallback {
                        override fun onSuccess(response: WeatherResponse?, t: Throwable?) {
                            if (response != null) {
                                backGroundScope.launch {
                                    val result = save(response)
                                    if (result != 0) {
                                        Timber.e("UPDATE RESOURCE[${response.id}]...SUCCESS")
                                    } else {
                                        Timber.e("UPDATE RESOURCE[${response.id}]...FAIL")
                                    }
                                }
                            } else {
                                Timber.e("UPDATE RESOURCE[$weatherId]...FAIL")
                                t?.printStackTrace()
                            }
                        }

                        override fun onFailure(t: Throwable?) {
                            Timber.e("UPDATE RESOURCE[$weatherId]...FAIL")
                            t?.printStackTrace()
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun updateWeatherById(
        weatherId: Int,
        handler: (WeatherResponse?, Throwable?) -> Unit
    ) {
        withContext(ioDispatcher) {
            try {
                remoteDataSource.fetchByCityID(weatherId) { response, throwable ->
                    backGroundScope.launch {
                        handleUpdate(response, throwable, handler)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler(null, e)
            }
        }
    }

    override suspend fun updateWeatherByName(
        cityName: String,
        handler: (WeatherResponse?, Throwable?) -> Unit
    ) {
        withContext(ioDispatcher) {
            try {
                remoteDataSource.fetchByCityName(cityName) { response, throwable ->
                    backGroundScope.launch {
                        handleUpdate(response, throwable, handler)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler(null, e)
            }
        }
    }

    override suspend fun refresh() {
        backGroundScope.launch {
            updateWeathers()
        }
    }

    override suspend fun refresh(cityId: Int) {
        backGroundScope.launch {
            updateWeatherById(cityId)
        }
    }

    override suspend fun getLastResponseId(): Int? {
        return backGroundScope.async {
            try {
                Timber.e("FETCH LAST RESPONSE...")
                val result = withContext(ioDispatcher) {
                    localDataSource.getLastResponse()
                }
                if (result is Result.Success && result.data != null) {
                    Timber.e("FETCH LAST RESPONSE[${result.data}]...SUCCESS")
                    return@async result.data
                }
                Timber.e("FETCH LAST RESPONSE... FAIL")
                return@async null
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("FETCH LAST RESPONSE... FAIL")
                return@async null
            }
        }.await()
    }

    override suspend fun deleteWeather(weatherId: Int) {
        backGroundScope.launch {
            localDataSource.deleteWeatherEntitiesById(weatherId)
        }
    }

    override suspend fun deleteAll() {
        backGroundScope.launch {
            localDataSource.deleteAll()

        }
    }

    override suspend fun save(weather: WeatherResponse): Int =
        withContext(ioDispatcher) {
            try {
                val result = backGroundScope.async {
                    localDataSource.save(weather)
                }.await()

                if (result is Result.Success && result.data != null) {
                    return@withContext result.data
                }

                return@withContext 0
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext 0
            }
        }

    private suspend fun handleUpdate(
        response: WeatherResponse?,
        throwable: Throwable?,
        handler: (WeatherResponse?, Throwable?) -> Unit
    ) {
        withContext(ioDispatcher) {
            try {
                Timber.e("UPDATE RESOURCE...")
                if (response != null) {
                    backGroundScope.async {
                        val id = save(response)
                        if (id != 0) {
                            Timber.e("UPDATE RESOURCE[${response.id}]...SUCCESS")
                            handler(response, null)
                        } else {
                            Timber.e("UPDATE RESOURCE[${response.id}]...FAIL")
                            handler(null, Exception("SAVING...FAIL"))
                        }
                    }
                } else {
                    Timber.e("UPDATE RESOURCE[]...FAIL")
                    throwable?.printStackTrace()
                    handler(null, throwable)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("UPDATE RESOURCE[]...FAIL")
                handler(null, e)
            }
        }
    }
}
