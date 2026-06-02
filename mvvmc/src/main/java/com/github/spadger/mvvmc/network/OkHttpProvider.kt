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
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

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
     * 网络配置类
     */
    data class Config(
        val connectTimeout: Long = CONNECT_TIMEOUT,
        val readTimeout: Long = READ_TIMEOUT,
        val writeTimeout: Long = WRITE_TIMEOUT,
        val enableLogging: Boolean = true,
        val skipSslVerification: Boolean = false
    )

    /**
     * 创建配置好的OkHttpClient实例
     * @return OkHttpClient实例
     */
    fun create(): OkHttpClient {
        return create(Config())
    }

    /**
     * 创建配置好的OkHttpClient实例（带配置参数）
     * @param config 配置参数
     * @return OkHttpClient实例
     */
    fun create(config: Config): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(config.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeout, TimeUnit.SECONDS)

        if (config.enableLogging) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        builder.addInterceptor(HeaderInterceptor())

        if (config.skipSslVerification) {
            builder.sslSocketFactory(createInsecureSslSocketFactory(), createTrustAllManager())
            builder.hostnameVerifier { _, _ -> true }
        }

        return builder.build()
    }

    /**
     * 创建不验证SSL证书的SSLSocketFactory
     */
    private fun createInsecureSslSocketFactory(): SSLSocketFactory {
        return try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(createTrustAllManager()), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException("Failed to create insecure SSL socket factory", e)
        }
    }

    /**
     * 创建信任所有证书的TrustManager
     */
    private fun createTrustAllManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }
    }
}