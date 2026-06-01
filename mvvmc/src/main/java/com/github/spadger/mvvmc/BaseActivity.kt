/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Activity基类，提供ViewModel支持和通用UI操作方法
 * 
 * @param VM ViewModel类型参数
 */
package com.github.spadger.mvvmc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {
    /**
     * 泛型ViewModel实例，由createViewModel()方法初始化
     */
    protected lateinit var viewModel: VM

    /**
     * Activity创建时调用，初始化ViewModel并开始观察数据
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        observeViewModel()
    }

    /**
     * 抽象方法，子类必须实现以创建对应的ViewModel实例
     * @return VM类型的ViewModel实例
     */
    protected abstract fun createViewModel(): VM

    /**
     * 观察ViewModel数据变化的方法，子类可重写
     * 在onCreate中被调用，用于设置数据观察
     */
    protected open fun observeViewModel() {}

    /**
     * 获取ViewModel的便捷方法，使用泛型推断
     * @return 指定类型的ViewModel实例
     */
    protected inline fun <reified T : ViewModel> getViewModel(): T {
        return ViewModelProvider(this)[T::class.java]
    }

    /**
     * 显示加载状态，子类可重写实现具体UI
     */
    protected fun showLoading() {}

    /**
     * 隐藏加载状态，子类可重写实现具体UI
     */
    protected fun hideLoading() {}

    /**
     * 显示错误信息，子类可重写实现具体UI
     * @param message 错误信息
     */
    protected fun showError(message: String) {}
}