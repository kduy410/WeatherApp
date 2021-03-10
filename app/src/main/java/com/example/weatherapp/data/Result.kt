package com.example.weatherapp.data

sealed class Result<out T> {

    data class Success<T>(val data: T?) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            /**
             * Star-projections
             * https://kotlinlang.org/docs/reference/generics.html#star-projections
             */
            is Success<*> -> "Success=[data=$data]"
            is Error -> "Error=[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

val Result<*>.succeeded get() = this is Result.Success && data != null
