package com.example.weatherapp.retrofit

import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Simplify implementation and initializing of Retrofit
 */
class RetrofitFactory {

    companion object {
        const val API_KEY = "183e4da4b53bd0da907de82a1fac5181"
        const val URL = "https://api.openweathermap.org/data/2.5/"

        private val instance: RetrofitFactory? = null
        fun getInstance(): RetrofitFactory {
            return instance ?: RetrofitFactory()
        }
    }


    private val _units: MutableLiveData<String> = MutableLiveData<String>(Units.METRIC.type)
    private val _lang: MutableLiveData<String> = MutableLiveData<String>(Lang.VIETNAMESE.type)
    val units = _units
    val lang = _lang

    fun setLang(langType: Lang) {
        _lang.value = langType.type
    }

    fun setUnits(unit: Units) {
        _units.value = unit.type
    }

    private val service by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherService::class.java)
    }

    private val httpService by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(SimpleCallAdapterFactory.create())
            .build()
            .create(IHttpService::class.java)
    }

    suspend fun process(
        lat: Double,
        lon: Double,
        responseHandler: (WeatherResponse?, Throwable?) -> Unit
    ): Unit =
        withContext(Dispatchers.IO) {
            return@withContext try {
                httpService.getCurrentWeather(
                    lat,
                    lon,
                    _lang.value!!,
                    _units.value!!,
                    API_KEY
                ).process { response, throwable ->
                    handle(response, throwable, responseHandler)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                responseHandler(null, e)
            }
        }

    suspend fun process(
        cityId: Int,
        responseHandler: (WeatherResponse?, Throwable?) -> Unit
    ): Unit =
        withContext(Dispatchers.IO) {
            return@withContext try {
                httpService.getWeatherByCityId(
                    cityId, _lang.value!!,
                    _units.value!!, API_KEY
                ).process { response, throwable ->
                    handle(response, throwable, responseHandler)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                responseHandler(null, e)
            }
        }

    suspend fun process(
        cityName: String,
        responseHandler: (WeatherResponse?, Throwable?) -> Unit
    ): Unit =
        withContext(Dispatchers.IO) {
            return@withContext try {
                httpService.getWeatherByCityName(
                    cityName, _lang.value!!,
                    _units.value!!, API_KEY
                )
                    .process { response, throwable ->
                        handle(response, throwable, responseHandler)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                responseHandler(null, e)
            }
        }

    suspend fun run(
        lat: Double,
        lon: Double,
        responseHandler: (WeatherResponse?, Throwable?) -> Unit
    ): Unit =
        withContext(Dispatchers.IO) {
            return@withContext try {
                httpService.getCurrentWeather(
                    lat,
                    lon,
                    _lang.value!!,
                    _units.value!!,
                    API_KEY
                ).run { response, throwable ->
                    responseHandler(response, throwable)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                responseHandler(null, e)
            }
        }

    /**
     * Not used at the moment
     */

    /**
     * [enqueue] run asynchronously behave like Call.enqueue
     */
    suspend fun enqueue(lat: Double, lon: Double): Result<WeatherResponse> =
        withContext(Dispatchers.IO) {
            try {
                if (lat.isNaN() || lat.isInfinite() || lon.isNaN() || lon.isInfinite()) {
                    return@withContext Result.Error(Exception("Coordinate is not appropriate!"))
                }

                service.getCurrentWeather(
                    lat, lon, _lang.value!!,
                    _units.value!!, API_KEY
                ).let {
                    if (!it.isSuccessful) {
                        return@withContext Result.Error(Exception(it.message()))
                    } else
                        return@withContext Result.Success(it.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Result.Error(e)
            } catch (e: HttpException) {
                e.printStackTrace()
                return@withContext Result.Error(e)
            } catch (e: java.net.UnknownHostException) {
                e.printStackTrace()
                return@withContext Result.Error(e)
            }
        }

    private fun handle(
        response: WeatherResponse?,
        throwable: Throwable?,
        handler: (WeatherResponse?, Throwable?) -> Unit
    ) {
        if (response != null) {
            handler(response, null)
        } else {
            handler(null, throwable)
        }
    }
}