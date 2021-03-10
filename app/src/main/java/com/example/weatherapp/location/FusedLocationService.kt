package com.example.weatherapp.location

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.Result
import com.example.weatherapp.extension.isPermissionGranted
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class FusedLocationService constructor(private val context: Context?) {

    companion object {
        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    /**
     * The current location.
     */

    private val mLastLocation = MutableLiveData<Location>()

    fun getLocation(): Location? {
        return mLastLocation.value
    }

    fun getLocationLiveData(): LiveData<Location?> {
        return mLastLocation
    }

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? =
        context?.let { LocationServices.getFusedLocationProviderClient(it) }

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            locationResult.lastLocation ?: return
            mLastLocation.postValue(locationResult.lastLocation)
//            mFusedLocationClient?.removeLocationUpdates(this)
//            Timber.e("[${this@FusedLocationService}]: Location updates removed...")
        }
    }

    private val mLocationRequest = LocationRequest().apply {
        interval = UPDATE_INTERVAL_IN_MILLISECONDS
        fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressWarnings("MissingPermission")
    private suspend fun startLocationUpdates() {
        withContext(Dispatchers.IO) {
            try {
                if (isPermissionGranted(context)) {
                    mFusedLocationClient?.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.getMainLooper()
                    )
                    Timber.e("[${this@FusedLocationService}]: Location updating...")
                }
                return@withContext

            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun requestLocationUpdates(): Result<Task<Location>> = withContext(Dispatchers.IO) {
        try {
            if (isPermissionGranted(context)) {
                startLocationUpdates()
                return@withContext Result.Success(mFusedLocationClient?.lastLocation)
            }
            return@withContext Result.Error(Exception("Permission required"))
        } catch (e: SecurityException) {
            return@withContext Result.Error(e)
        }
    }
}