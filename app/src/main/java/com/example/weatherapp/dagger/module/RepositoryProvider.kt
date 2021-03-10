package com.example.weatherapp.dagger.module

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.NonNull
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.dagger.component.PROVIDER_TAG
import com.example.weatherapp.repo.DefaultWeatherRepository
import com.example.weatherapp.repo.source.local.WeatherDAO
import com.example.weatherapp.repo.source.local.WeatherLocalDataSource
import com.example.weatherapp.repo.source.remote.WeatherRemoteDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@SuppressLint("LogNotTimber")
@Module
class RepositoryProvider {

    @Provides
    @Singleton
    fun provideLocalRepository(@NonNull weatherDAO: WeatherDAO): WeatherLocalDataSource {
        Log.i(PROVIDER_TAG, "Local repository provided...")
        return WeatherLocalDataSource(weatherDAO)
    }


    @Provides
    @Singleton
    fun provideRemoteDataSource(
        @NonNull application: WeatherApplication,
    ): WeatherRemoteDataSource {
        Log.i(PROVIDER_TAG, "Remote repository provided...")
        return WeatherRemoteDataSource(application)
    }

    @Provides
    @Singleton
    fun provideRepository(
        @NonNull localDataSource: WeatherLocalDataSource,
        @NonNull remoteDataSource: WeatherRemoteDataSource
    ): DefaultWeatherRepository {
        Log.i(PROVIDER_TAG, "Default repository provided...")

        return DefaultWeatherRepository(
            localDataSource,
            remoteDataSource
        )
    }
}