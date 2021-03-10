package com.example.weatherapp.workmanager

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkManager
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.dagger.component.DaggerAppComponent

@SuppressLint("LogNotTimber")
class MyWorkManagerInitializer : DummyContentProvider(), Configuration.Provider {
    /**
     * This [fusedLocationService] will be initialized after [DaggerAppComponent]
     * finishes instantiated -> after [onCreate] method finished
     * BUT [WorkManager] is also created in [onCreate] method AND
     * IT demands [workManagerConfiguration] while [workManagerConfiguration] required
     * [fusedLocationService] => ORIGIN OF ERROR => BECAUSE [fusedLocationService] is not initialized yet.
     *
     * SOLUTION => delete [MyWorkerFactory] constructor that required [fusedLocationService] and
     * get [fusedLocationService] directly from [WeatherApplication]
     */
//    @Inject
//    lateinit var fusedLocationService: FusedLocationService

    override fun onCreate(): Boolean {
//        DaggerAppComponent.builder().application((context as WeatherApplication)).build()
//            .inject(this)
        try {
            WorkManager.initialize(context!!, workManagerConfiguration)
            Log.i(INITIALIZER_TAG, "CONTEXT: $context")
        } catch (e: Exception) {
            throw e
        }
        return true
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val myWorkerFactory = DelegatingWorkerFactory().apply {
            addFactory(MyWorkerFactory())
        }
//         Add here other factories that you may need in your application
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(myWorkerFactory)
            .build()
    }
}

const val INITIALIZER_TAG = "INITIALIZER_TAG"

@SuppressLint("LogNotTimber")
abstract class DummyContentProvider : ContentProvider() {

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}