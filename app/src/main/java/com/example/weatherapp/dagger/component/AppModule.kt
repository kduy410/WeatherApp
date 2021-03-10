package com.example.weatherapp.dagger.component

import android.content.Context
import com.example.weatherapp.WeatherApplication
import dagger.Binds
import dagger.Module

@Module
interface AppModule {

    @Binds
    fun bindContext(application: WeatherApplication): Context
}