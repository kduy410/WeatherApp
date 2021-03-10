package com.example.weatherapp.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.data.Result.*
import com.example.weatherapp.extension.isPermissionGranted
import com.example.weatherapp.location.FusedLocationService
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.*
import timber.log.Timber

/**
 * Class handles location request in background and call API => Currently don't work because it's do work
 * return before onComplete finished
 */
/**
 * For ideas about adding extra parameters to Worker's [constructor]
 * => YOU CAN'T
 * https://stackoverflow.com/questions/52639001/how-to-create-a-worker-with-parameters-for-in-workmanager-for-android
 * => Instead You can use [setInputData] method to send data just like [Bundle].
 */
open class FetchLocationWorker(
    context: Context,
    params: WorkerParameters
) :
    ListenableWorker(context, params) {
    private val ioDispatcher = Dispatchers.IO
    private var fusedService: FusedLocationService? =
        (context as WeatherApplication).fusedLocationService
    /**
     * [applicationContext] & [context] is the same
     */


    /**
     * Provides access to the Fused Location Provider API.
     */

    override fun startWork(): ListenableFuture<Result> {

        return CallbackToFutureAdapter.getFuture { completer ->
            CoroutineScope(ioDispatcher).launch {
                // Getting out of MAIN Thread to set up location listener.
                // This scope will be finished after setting up the listeners (Not wait through the final response).
                doWork(completer)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun doWork(completer: CallbackToFutureAdapter.Completer<Result>) =
        withContext(ioDispatcher) {
            try {
                if (isPermissionGranted(applicationContext)) {
                    Timber.e("FETCHING LOCATION...")
                    val deferred = async { fusedService?.requestLocationUpdates() }
                    if (deferred.await() is Success) {
                        (deferred.await() as Success<Task<Location>>).data?.apply {
                            addOnSuccessListener { location: Location? ->
                                // Got last known location. In some rare situations this can be null.
                                // Adds a listener that is called if the Task completes successfully.
                                // MAIN THREAD - MINIMAL WORK.
                                // YOU MIGHT WANT TO PASS AN EXECUTOR TO THE ONSUCCESSLISTENER IF YOU HAVE A LOT OF WORK TO DO
                                if (location != null) {
                                    Timber.e("FINISHED FETCHING LOCATION...[SUCCESS]")
                                    completer.set(Result.success(createOutputData(location)))
                                } else {
                                    Timber.e("FINISHED FETCHING LOCATION...[FAILED: Location is null - Make sure you had used location before - Try on real phone.]")
                                    completer.set(Result.success(Data.EMPTY))
                                }
                            }
//                    addOnCompleteListener { task: Task<Location> ->
//                        // Adds a listener that is called when the Task completes.
//                        // It's successful or not is unknown.
//                        if (task.result != null && task.isSuccessful) {
//                            completer.set(
//                                Result.success(
//                                    createOutputData(task.result)
//                                )
//                            )
//                        } else
//                            completer.set(Result.success(Data.EMPTY))
//
//                    }
                            addOnFailureListener { exception ->
                                // Adds a listener that is called if the Task fails.
                                Timber.e("FINISHED FETCHING LOCATION...[FAILED: $exception]")
                                completer.set(Result.failure(Data.EMPTY))
                            }

                        }

                    } else {
                        Timber.e("FINISHED FETCHING LOCATION...[FAILED: ${deferred.await()}]")
                        completer.set(Result.failure(Data.EMPTY))
                    }
                } else {
                    Timber.e("FINISHED FETCHING LOCATION...[FAILED: Permission is not granted!]")
                    completer.set(Result.failure(Data.EMPTY))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                completer.set(Result.failure())
            }
        }

    private fun createOutputData(location: Location?): Data =
        if (location == null) Data.EMPTY else
            Data.Builder()
                .putDouble(LOCATION_LAT, location.latitude)
                .putDouble(LOCATION_LONG, location.longitude)
                .putLong(LOCATION_TIME, location.time)
                .build()
}
