/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 请求头拦截器，为所有请求添加默认的Content-Type和Accept头
 */
package com.github.spadger.mvvmc.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    /**
     * 拦截请求并添加默认请求头
     * @param chain 请求链
     * @return 响应
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 构建新的请求，添加默认请求头
        val requestBuilder: Request.Builder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}