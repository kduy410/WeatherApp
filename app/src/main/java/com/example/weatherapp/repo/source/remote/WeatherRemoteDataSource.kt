package com.example.weatherapp.repo.source.remote

import android.app.Application
import android.location.Location
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.retrofit.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainEntity entry point for accessing remote data source
 */

class WeatherRemoteDataSource internal constructor(
    application: Application
) : RemoteDataSource {
    private val ioDispatcher = Dispatchers.IO

    // Either used backGroundScope or withContext(ioDispatcher)
    private val backGroundScope = CoroutineScope(ioDispatcher)
    private val fused = (application as WeatherApplication).fusedLocationService

    interface RemoteCallback {
        fun onSuccess(response: WeatherResponse?, t: Throwable?)
        fun onFailure(t: Throwable?)
    }

    override suspend fun fetch(callback: RemoteCallback) {
        backGroundScope.launch {
            try {
                val deferred = withContext(ioDispatcher) { fused.requestLocationUpdates() }
                if (deferred is Result.Success && deferred.data != null) {
                    (deferred).data.let {
                        it.addOnSuccessListener { location: Location? ->
                            // Call when the Task completes successfully
                            if (location != null) {
                                backGroundScope.launch {
                                    RetrofitFactory.getInstance().process(
                                        location.latitude,
                                        location.longitude
                                    ) { response, throwable ->
                                        handle(response, throwable, callback)
                                    }
                                }
                            } else {
                                callback.onFailure(NullPointerException("Location is null!"))
                            }
                        }
                        it.addOnFailureListener { e ->
                            // Call when the task fails
                            callback.onFailure(e)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onFailure(e)
            }
        }
    }

    override suspend fun fetch(handler: (WeatherResponse?, Throwable?) -> Unit) {
        backGroundScope.launch {
            try {
                val locationDeferred = withContext(ioDispatcher) { fused.requestLocationUpdates() }
                if (locationDeferred is Result.Success && locationDeferred.data != null) {
                    locationDeferred.data.let {
                        it.addOnSuccessListener { location: Location? ->
                            // Call when the Task completes successfully
                            if (location != null) {
                                backGroundScope.launch {
                                    RetrofitFactory.getInstance().process(
                                        location.latitude,
                                        location.longitude
                                    ) { response, throwable ->
                                        handle(response, throwable, handler)
                                    }
                                }
                            } else {
                                handler(null, NullPointerException("Location is null!"))
                            }
                        }
                        it.addOnFailureListener { e ->
                            // Call when the task fails
                            handler(null, e)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler(null, e)
            }
        }
    }


    override suspend fun fetchByCityID(cityId: Int, callback: RemoteCallback) {
        backGroundScope.launch {
            try {
                RetrofitFactory.getInstance().process(cityId) { response, throwable ->
                    handle(response, throwable, callback)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onFailure(e)
            }
        }
    }

    override suspend fun fetchByCityID(
        cityId: Int,
        handler: (WeatherResponse?, Throwable?) -> Unit
    ) {
        backGroundScope.launch {
            try {
                RetrofitFactory.getInstance().process(cityId) { response, throwable ->
                    handle(response, throwable, handler)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler(null, e)
            }
        }
    }

    override suspend fun fetchByCityName(cityName: String, callback: RemoteCallback) {
        backGroundScope.launch {
            try {
                RetrofitFactory.getInstance().process(cityName) { response, throwable ->
                    handle(response, throwable, callback)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onFailure(e)
            }
        }
    }

    override suspend fun fetchByCityName(
        cityName: String,
        handler: (WeatherResponse?, Throwable?) -> Unit
    ) {
        backGroundScope.launch {
            try {
                RetrofitFactory.getInstance().process(cityName) { response, throwable ->
                    handle(response, throwable, handler)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler(null, e)
            }
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

    private fun handle(
        response: WeatherResponse?,
        throwable: Throwable?,
        callback: RemoteCallback
    ) {
        if (response != null) {
            callback.onSuccess(response, null)
        } else {
            callback.onFailure(throwable)
        }
    }
}