package com.example.weatherapp.workmanager

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.retrofit.RetrofitFactory
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

open class AsyncAPIWorker(context: Context, private val params: WorkerParameters) :
    ListenableWorker(context, params) {
    private val ioDispatcher = Dispatchers.IO
    private val defaultDispatcher = Dispatchers.Default
    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            CoroutineScope(ioDispatcher).launch {
                doWork(completer)
            }
        }

    }

    private suspend fun doWork(completer: CallbackToFutureAdapter.Completer<Result>) =
        withContext(ioDispatcher) {
            try {
                Timber.e("FETCHING DATA FROM API...")
                val lat = params.inputData.getDouble(LOCATION_LAT, 0.0)
                val lon = params.inputData.getDouble(LOCATION_LONG, 0.0)

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    Timber.e("NETWORK: [${params.network}]")
                }

                if (lat != 0.0 && lon != 0.0) {
                    RetrofitFactory.getInstance().process(lat, lon) { response, t ->
                        run {
                            if (response != null) {
                                CoroutineScope(defaultDispatcher).launch {
                                    Timber.e("FINISHED FETCHING DATA FROM API...[SUCCESS]")
                                    completer.set(Result.success(createOutputData(response)))
                                }
                            } else {
                                Timber.e("FINISHED FETCHING LOCATION...[FAILED: $response, $t]")
                                completer.set(Result.failure(Data.EMPTY))
                            }
                        }
                    }
                } else {
                    Timber.e("FINISHED FETCHING LOCATION...[FAILED: LAT: $lat,LON: $lon]")
                    completer.set(Result.failure(Data.EMPTY))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                completer.set(Result.failure())
            }
        }

    private suspend fun createOutputData(response: WeatherResponse?): Data =
        withContext(defaultDispatcher) {
            try {
                if (response == null) Data.EMPTY else
                    Data.Builder()
                        .putString(WEATHER_RESPONSE, GsonBuilder().create().toJson(response))
                        .build()
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Data.EMPTY
            }
        }
}