package com.example.weatherapp.network

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.IntentFilter
import com.example.weatherapp.R
import timber.log.Timber

/**
 * Implementing broadcast receiver as a job service for notify about network
 */

class NetworkScheduleService : JobService(),
    NetworkReceiver.NetworkReceiverListener {

    private lateinit var mNetworkReceiver: NetworkReceiver

    override fun onCreate() {
        super.onCreate()
        mNetworkReceiver = NetworkReceiver(this)
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.e("onStartJob")
        registerReceiver(mNetworkReceiver, IntentFilter(CONNECTIVITY_ACTION))
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Timber.e("onStopJob")
        unregisterReceiver(mNetworkReceiver)
        return true
    }

    override fun onNetworkChangedListener(isConnected: Boolean) {
        val message: Int = if (isConnected) R.string.connected else R.string.not_connected
        Timber.e("onNetworkChangedListener:[${this}]")
    }


}

const val CONNECTIVITY_ACTION = "CONNECTIVITY_ACTION"