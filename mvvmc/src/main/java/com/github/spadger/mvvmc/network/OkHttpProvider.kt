/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * OkHttp提供者，负责创建配置好的OkHttpClient实例
 */
package com.github.spadger.mvvmc.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpProvider {
    /**
     * 连接超时时间（秒）
     */
    private const val CONNECT_TIMEOUT = 30L
    
    /**
     * 读取超时时间（秒）
     */
    private const val READ_TIMEOUT = 30L
    
    /**
     * 写入超时时间（秒）
     */
    private const val WRITE_TIMEOUT = 30L

    /**
     * 创建配置好的OkHttpClient实例
     * @return OkHttpClient实例
     */
    fun create(): OkHttpClient {
        // 创建日志拦截器，输出完整的请求和响应信息
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)          // 添加日志拦截器
            .addInterceptor(HeaderInterceptor())         // 添加请求头拦截器
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)    // 设置连接超时
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)          // 设置读取超时
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)        // 设置写入超时
            .build()
    }
}