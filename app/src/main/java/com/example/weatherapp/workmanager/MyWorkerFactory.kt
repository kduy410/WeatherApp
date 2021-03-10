package com.example.weatherapp.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

open class MyWorkerFactory : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            FetchLocationWorker::class.java.name -> FetchLocationWorker(
                appContext,
                workerParameters
            )
            AsyncAPIWorker::class.java.name -> AsyncAPIWorker(appContext, workerParameters)

            DailyWorker::class.java.name -> DailyWorker(appContext, workerParameters)

            StorageWorker::class.java.name -> StorageWorker(
                appContext,
                workerParameters
            )

            // Return null, so that the base class can delegate to the default WorkerFactory.
            else -> null
        }
    }
}