package com.example.weatherapp.extension

import androidx.fragment.app.Fragment
import com.example.weatherapp.ViewModelFactory
import com.example.weatherapp.WeatherApplication

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val application = (requireContext().applicationContext as WeatherApplication)
//    Timber.e("${requireContext()}")                    -> return MainActivity
//    Timber.e("${requireContext().applicationContext}") -> return WeatherApplication
//    Timber.e("$application")                           -> return WeatherApplication
//    [activity!!.application] == [requireContext().applicationContext as WeatherApplication]
    return ViewModelFactory(application, application.repository, this)
}
