package com.example.weatherapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.weatherapp.repo.WeatherRepository
import timber.log.Timber

class MainActivityViewModel(
    application: Application,
    defaultRepository: WeatherRepository,
    private val handle: SavedStateHandle
) : AndroidViewModel(application) {
    private val _permissionCount = MutableLiveData(0)
    private val _isPermissionGranted = MutableLiveData(false)

    fun isPermissionGrated() = _isPermissionGranted.value
    fun setPermissionGranted(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }

    fun observePermissionState() = _isPermissionGranted

    fun setPermissionState(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }

    fun getPermissionCount() = _permissionCount.value ?: 0

    fun setPermissionCount(count: Int) {
        _permissionCount.value?.plus(count)
    }

    fun savedInstanceState() {
        handle.set(KEY_PERMISSIONS_REQUEST_COUNT, _permissionCount.value)
        Timber.e("SAVED: ${handle.get<Int>(KEY_PERMISSIONS_REQUEST_COUNT)}")
    }
}