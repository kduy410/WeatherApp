package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.dagger.component.DaggerAppComponent
import com.example.weatherapp.location.FusedLocationService
import com.example.weatherapp.repo.DefaultWeatherRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherApplication : Application() {
    @Inject
    lateinit var fusedLocationService: FusedLocationService

    @Inject
    lateinit var repository: DefaultWeatherRepository

    // appComponent lives in the Application class to share its lifecycle
    // Reference to the application graph that is used across the whole app
    var appComponent = DaggerAppComponent.builder()

    override fun onCreate() {
        super.onCreate()

        appComponent.application(this@WeatherApplication).build().inject(this@WeatherApplication)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

//        if (checkAllPermissions(this)) {
//            val request = OneTimeWorkRequestBuilder<DailyWorker>().build()
//            WorkManager.getInstance(this).enqueue(request)
//        }
    }
}


