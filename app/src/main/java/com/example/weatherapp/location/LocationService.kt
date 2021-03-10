package com.example.weatherapp.location

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber


class LocationService : Service() {
    // Binder given to clients
    private val binder = LocalBinder()

    private val locationManager: LocationManager by lazy {
        applicationContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    /**
     * https://developer.android.com/guide/components/bound-services.html#kotlin
     * Bound Service
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocationService so clients can call public methods
        fun getService(): LocationService = this@LocationService
    }

    inner class LocationListener(
        var provider: String,
        private val lastLocation: MutableLiveData<Location>
    ) :
        android.location.LocationListener {

        override fun onLocationChanged(location: Location) {
            lastLocation.value = location
            Timber.e("onLocationChanged: Location[$location]")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Timber.e("onStatusChanged: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Timber.e("onProviderEnabled: $provider")
        }

        override fun onProviderDisabled(provider: String) {
            Timber.e("onProviderDisabled: $provider")
        }
    }

    private val _locationListeners: Array<LocationListener> =
        arrayOf(LocationListener(LocationManager.PASSIVE_PROVIDER, _location))


    override fun onCreate() {
        try {
            // If don't set location again it won't display location
            locationManager.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                LOCATION_INTERVAL,
                LOCATION_DISTANCE,
                _locationListeners[0]
            )
            requestLocationUpdate()
        } catch (e: SecurityException) {
            Timber.e("Fail to request location update, ignore ${e.message}")
        } catch (e: IllegalArgumentException) {
            Timber.e("Network provider does not exist, ${e.message}")
        } catch (e: RuntimeException) {
            Timber.e("Permission is not granted, ${e.message}")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        try {
            requestLocationUpdate()
        } catch (e: SecurityException) {
            Timber.e("Fail to request location update, ignore ${e.message}")
        } catch (e: IllegalArgumentException) {
            Timber.e("Network provider does not exist, ${e.message}")
        } catch (e: RuntimeException) {
            Timber.e("Permission is not granted, ${e.message}")
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        _locationListeners.forEach {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                Timber.e("onDestroy: removeUpdates [$it]")
                locationManager.removeUpdates(it)
            } catch (e: Exception) {
                Timber.e("Fail to remove location listener, ignore $e")
                throw e
            }
        }

    }

    private fun requestLocationUpdate() {
        try {
            _location.value = locationManager.getLastKnownLocation(_locationListeners[0].provider)
        } catch (e: SecurityException) {
            Timber.e("Fail to request location update, ignore ${e.message}")
        } catch (e: IllegalArgumentException) {
            Timber.e("Network provider does not exist, ${e.message}")
        } catch (e: RuntimeException) {
            Timber.e("Permission is not granted, ${e.message}")
        }
    }

    companion object {
        private const val LOCATION_INTERVAL: Long = 1000
        private const val LOCATION_DISTANCE = 10F
    }
}
