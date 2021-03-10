package com.example.weatherapp.retrofit

/**
 * use this class to share the state of subscription between the process function and the enclosing class
 *  We do this by creating a subscription and returning it from the enqueue function
 *  Used in asynchronous function
 */
class Subscription {
    private var disposed = false

    fun isDisposed() = disposed

    fun dispose() {
        disposed = true
    }
}