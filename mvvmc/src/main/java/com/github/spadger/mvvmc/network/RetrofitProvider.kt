package com.github.spadger.mvvmc.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private lateinit var baseUrl: String

    fun init(baseUrl: String) {
        RetrofitProvider.baseUrl = baseUrl
    }

    fun create(): Retrofit {
        if (!::baseUrl.isInitialized) {
            throw IllegalStateException("RetrofitProvider not initialized. Call init() first with a base URL.")
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpProvider.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return create().create(serviceClass)
    }
}