package com.example.weatherapp.retrofit

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class SimpleCallAdapterFactory private constructor() : CallAdapter.Factory() {
    /**
     * [returnType]: This is the return type of the calling function,
     * for instance, the method getCurrentWeather of IHttpService, is Simple<WeatherResponse>
     *
     * [annotations]: This is a list of annotations that the calling method has, for instance,
     * the method getCurrentWeather of IHttpService, will have one annotation, which is the retrofit GET annotations.
     *
     * [retrofit]: This is the retrofit instance that created the http-service containing the calling method.
     */

    override fun get(
        returnType: Type?,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): CallAdapter<*, *>? {
        return returnType?.let {
            return try {
                // get enclosing type
                val enclosingType = (it as ParameterizedType)

                // ensure enclosing type is 'Simple'
                if (enclosingType.rawType != Simple::class.java) {
                    null
                } else {
                    val type = enclosingType.actualTypeArguments[0]
                    SimpleCallAdapter<Any>(type)
                }
            } catch (e: ClassCastException) {
                null
            }
        }
    }

    companion object {
        @JvmStatic
        fun create() = SimpleCallAdapterFactory()
    }
}