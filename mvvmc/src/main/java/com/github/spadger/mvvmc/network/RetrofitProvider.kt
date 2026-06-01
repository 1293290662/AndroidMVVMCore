/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Retrofit提供者，负责创建和管理Retrofit实例
 */
package com.github.spadger.mvvmc.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    /**
     * 基础URL，需要通过init()方法初始化
     */
    private lateinit var baseUrl: String

    /**
     * 初始化RetrofitProvider
     * @param baseUrl API基础URL
     */
    fun init(baseUrl: String) {
        RetrofitProvider.baseUrl = baseUrl
    }

    /**
     * 创建Retrofit实例
     * @return Retrofit实例
     * @throws IllegalStateException 如果未调用init()方法
     */
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

    /**
     * 创建API服务接口实例
     * @param serviceClass 服务接口类
     * @return 服务接口实例
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return create().create(serviceClass)
    }
}