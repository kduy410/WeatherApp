package com.example.weatherapp.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import timber.log.Timber

/**
 * For receiving network status
 */
class NetworkReceiver(private val listener: NetworkReceiverListener) : BroadcastReceiver() {

    private lateinit var connectivityManager: ConnectivityManager

    interface NetworkReceiverListener {
        fun onNetworkChangedListener(isConnected: Boolean)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.e("onReceive: [$context]")
        val binder = peekService(context, Intent(context, NetworkScheduleService::class.java))

        listener.onNetworkChangedListener(isNetworkAvailable(context))
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var result = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val networkCapabilities = connectivityManager.activeNetwork ?: return false

            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
            return result

        } else {
            connectivityManager.run {
                this.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
            return result
        }
    }

}