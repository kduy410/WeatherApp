package com.example.weatherapp.retrofit

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * To let retrofit know of this transformer we defined Simple,
 * we have to create an implementation of the retrofit CallAdapter interface like so:
 */
class SimpleCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Any> {
    /**
     * Providing a Type object that will be the type of the request’s response,
     * that is, the type of the object in the HTTP response body. (i.e. ‘List<User>’ in Simple<List<User>>)
     */
    override fun responseType(): Type = responseType

    /**
     * providing the transformer that will
     * transform the retrofit call object: which is what we had defined earlier (Simple<R>).
     */
    override fun adapt(call: Call<R>): Any = Simple(call)
}