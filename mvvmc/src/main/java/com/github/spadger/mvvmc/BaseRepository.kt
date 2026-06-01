package com.github.spadger.mvvmc

import com.github.spadger.mvvmc.network.RetrofitProvider

open class BaseRepository {
    protected inline fun <reified T> createApi(): T {
        return RetrofitProvider.createService(T::class.java)
    }

    protected suspend fun <T> apiCall(call: suspend () -> NetworkResponse<T>): Result<T> {
        return try {
            val response = call()
            response.toResult()
        } catch (e: Exception) {
            Result.Error(ExceptionHandler.handleException(e))
        }
    }
}