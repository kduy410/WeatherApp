package com.example.weatherapp.workmanager

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.repo.DefaultWeatherRepository
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

open class StorageWorker(
    context: Context,
    private val params: WorkerParameters
) :
    ListenableWorker(context, params) {
    private val ioDispatcher = Dispatchers.IO
    private val defaultDispatcher = Dispatchers.Default
    private val repository: DefaultWeatherRepository = (context as WeatherApplication).repository

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
                Timber.e("SAVING DATA TO DATABASE...")
                val stringResponse = params.inputData.getString(WEATHER_RESPONSE)
                if (stringResponse == null) {
                    Timber.e("FINISHED SAVING DATA TO DATABASE...[FAILED: response is null!]")
                    completer.set(Result.failure(createOutputData(false)))
                }
                launch(defaultDispatcher) {
                    try {
                        val response =
                            GsonBuilder().create().fromJson<WeatherResponse>(
                                stringResponse, WeatherResponse::class.java
                            )
                        launch(ioDispatcher) {
                            val result = repository.save(response)
//                            if (result is com.example.weatherapp.data.Result.Success) {
//                                Timber.e("FINISHED SAVING DATA TO DATABASE...[SUCCESS]")
//                                completer.set(Result.success(createOutputData(true)))
//                            } else {
//                                Timber.e("FINISHED SAVING DATA TO DATABASE...[FAILED: $result]")
//                                completer.set(Result.failure(createOutputData(false)))
//                            }
                            if (result != 0) {
                                Timber.e("FINISHED SAVING DATA TO DATABASE...[SUCCESS]")
                                completer.set(Result.success(createOutputData(true)))
                            } else {
                                Timber.e("FINISHED SAVING DATA TO DATABASE...[FAILED: $result]")
                                completer.set(Result.failure(createOutputData(false)))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        completer.set(Result.failure())
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                completer.set(Result.failure())
            }
        }

    private fun createOutputData(result: Boolean): Data =
        Data.Builder()
            .putBoolean(SAVE_RESULT, result)
            .build()
}