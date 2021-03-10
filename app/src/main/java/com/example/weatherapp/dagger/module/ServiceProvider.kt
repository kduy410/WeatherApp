package com.example.weatherapp.dagger.module

import androidx.annotation.NonNull
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.location.FusedLocationService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceProvider {
    @Provides
    @Singleton
    fun provideService(@NonNull application: WeatherApplication): FusedLocationService =
        FusedLocationService(application)
}