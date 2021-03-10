package com.example.weatherapp.extension

import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.ViewModelFactory
import com.example.weatherapp.WeatherApplication

fun AppCompatActivity.getViewModelFactory(): ViewModelFactory {
    val application = (applicationContext as WeatherApplication)

    return ViewModelFactory(
        application,
        application.repository,
        this,

        )
}