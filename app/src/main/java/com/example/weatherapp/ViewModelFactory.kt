package com.example.weatherapp

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.weatherapp.repo.WeatherRepository
import com.example.weatherapp.ui.home.HomeViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory internal constructor(
    private val application: Application,
    private val defaultRepository: WeatherRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return with(modelClass) {
            when {
                isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(
                    application,
                    defaultRepository,
                    handle
                )
                isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(
                    application,
                    defaultRepository,
                    handle
                )
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }
}