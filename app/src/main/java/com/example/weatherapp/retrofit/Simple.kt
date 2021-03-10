package com.example.weatherapp.retrofit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
 * Simple class to handle return type of retrofit
 * It is a Transformer
 * https://android.jlelse.eu/building-your-own-retrofit-call-adapter-b198169bab69
 */
class Simple<R>(private val call: Call<R>) {
    /**
     * First, it takes a call object in its constructor,
     * which is the object containing the HTTP request to be issued out.
     */

    /**
     * The run method is used to synchronously issue out network requests, and pass results to the response handler
     */
    fun run(responseHandler: (R?, Throwable?) -> Unit) {
        // run in the same thread
        try {
            // call and handle response
            val response = call.execute()
            handleResponse(response, responseHandler)
        } catch (t: IOException) {
            responseHandler(null, t)
        }
    }

    /**
     * The enqueue method is used to asynchronously issue out network requests,
     * and pass the results to the response handler
     */
    fun process(responseHandler: (R?, Throwable?) -> Unit) {
        // define callback
        val callback = object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                handleResponse(response, responseHandler)
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                responseHandler(null, t)
            }
        }
        /**
         * call.execute() // works in the foreground.
         * call.enqueue() // works in the background.
         */
        // enqueue network call
        call.enqueue(callback)
        // return subscription
    }

    private fun handleResponse(response: Response<R>?, handler: (R?, Throwable?) -> Unit) {
        if (response?.isSuccessful == true) {
            handler(response.body(), null)
        } else {
            Timber.e("ResponseCode: ${response?.code()}, ResponseBody: ${response?.body()}")
            if (response?.code() in 400..511) {
                handler(null, HttpException(response!!))
            } else
                handler(response?.body(), null)
        }
    }

}