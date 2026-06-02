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
     * 当前配置
     */
    private var currentConfig: Config = Config("")

    /**
     * 配置类
     */
    data class Config(
        val baseUrl: String,
        val skipSslVerification: Boolean = false,
        val connectTimeout: Long = 30L,
        val readTimeout: Long = 30L,
        val writeTimeout: Long = 30L
    )

    /**
     * 初始化RetrofitProvider（简化版本，仅传入baseUrl）
     * @param baseUrl API基础URL
     */
    fun init(baseUrl: String) {
        this.baseUrl = baseUrl
        this.currentConfig = Config(baseUrl)
    }

    /**
     * 初始化RetrofitProvider（完整版本，传入配置）
     * @param config 配置参数
     */
    fun init(config: Config) {
        this.baseUrl = config.baseUrl
        this.currentConfig = config
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
        
        val okHttpConfig = OkHttpProvider.Config(
            connectTimeout = currentConfig.connectTimeout,
            readTimeout = currentConfig.readTimeout,
            writeTimeout = currentConfig.writeTimeout,
            skipSslVerification = currentConfig.skipSslVerification
        )
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpProvider.create(okHttpConfig))
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