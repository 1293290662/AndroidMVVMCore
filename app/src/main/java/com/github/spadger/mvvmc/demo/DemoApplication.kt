package com.github.spadger.mvvmc.demo

import android.app.Application
import com.github.spadger.mvvmc.network.RetrofitProvider

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitProvider.init("https://api.example.com/")
    }
}