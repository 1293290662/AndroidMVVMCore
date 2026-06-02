package com.github.spadger.mvvmc.demo

import android.app.Application
import com.github.spadger.mvvmc.network.RetrofitProvider
import com.github.spadger.mvvmc.util.DataStoreHolder
import com.github.spadger.mvvmc.util.LogUtil

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 Retrofit（设置 Base URL）
        RetrofitProvider.init(
            RetrofitProvider.Config(
                baseUrl = "https://test-api.example.com/",
                skipSslVerification = true,  // 跳过 SSL 证书验证
                connectTimeout = 30L,
                readTimeout = 30L,
                writeTimeout = 30L
            )
        )
        
        // 初始化 DataStore
        DataStoreHolder.init(this)

        
        // 开启日志（发布版本建议关闭）
        LogUtil.isDebug = true
    }
}