/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Activity基类，提供完整的生命周期管理和通用UI操作方法
 * 
 * @param VM ViewModel类型参数
 */
package com.github.spadger.mvvmc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.spadger.mvvmc.ext.LiveDataExt
import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.util.ToastUtil

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {
    /**
     * 泛型ViewModel实例，由createViewModel()方法初始化
     */
    protected lateinit var viewModel: VM

    /**
     * Activity创建时调用，按顺序执行初始化流程
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化流程
        beforeInit()
        viewModel = createViewModel()
        initView()
        setupObservers()
        initListener()
        initData()
        afterInit()
    }

    /**
     * 在初始化之前调用，可用于设置主题、全屏等
     */
    protected open fun beforeInit() {}

    /**
     * 初始化视图，子类必须实现
     * 通常在这里调用 setContentView() 和初始化 View
     */
    protected abstract fun initView()

    /**
     * 设置数据观察，子类可重写
     * 用于观察 ViewModel 中的 LiveData
     */
    protected open fun setupObservers() {}

    /**
     * 初始化事件监听，子类可重写
     * 用于设置点击事件等
     */
    protected open fun initListener() {}

    /**
     * 初始化数据，子类可重写
     * 用于加载初始数据
     */
    protected open fun initData() {}

    /**
     * 在初始化之后调用，可用于执行延迟操作
     */
    protected open fun afterInit() {}

    /**
     * 抽象方法，子类必须实现以创建对应的ViewModel实例
     * @return VM类型的ViewModel实例
     */
    protected abstract fun createViewModel(): VM

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
    protected open fun showLoading() {}

    /**
     * 隐藏加载状态，子类可重写实现具体UI
     */
    protected open fun hideLoading() {}

    /**
     * 显示错误信息，子类可重写实现具体UI
     * @param message 错误信息
     */
    protected fun showError(message: String) {
        ToastUtil.showError(this, message)
    }

    // ==================== Toast 便捷方法 ====================

    /**
     * 显示短时间Toast
     * @param message 消息内容
     */
    protected fun showToast(message: String) {
        ToastUtil.showShort(this, message)
    }

    /**
     * 显示长时间Toast
     * @param message 消息内容
     */
    protected fun showToastLong(message: String) {
        ToastUtil.showLong(this, message)
    }

    /**
     * 显示成功提示Toast
     * @param message 消息内容
     */
    protected fun showSuccess(message: String) {
        ToastUtil.showSuccess(this, message)
    }

    /**
     * 显示警告提示Toast
     * @param message 消息内容
     */
    protected fun showWarning(message: String) {
        ToastUtil.showWarning(this, message)
    }

    /**
     * 显示信息提示Toast
     * @param message 消息内容
     */
    protected fun showInfo(message: String) {
        ToastUtil.showInfo(this, message)
    }

    // ==================== Log 便捷方法 ====================

    /**
     * 输出调试日志
     * @param message 消息内容
     */
    protected fun logD(message: String) {
        LogUtil.d(javaClass.simpleName, message)
    }

    /**
     * 输出信息日志
     * @param message 消息内容
     */
    protected fun logI(message: String) {
        LogUtil.i(javaClass.simpleName, message)
    }

    /**
     * 输出警告日志
     * @param message 消息内容
     */
    protected fun logW(message: String) {
        LogUtil.w(javaClass.simpleName, message)
    }

    /**
     * 输出错误日志
     * @param message 消息内容
     */
    protected fun logE(message: String) {
        LogUtil.e(javaClass.simpleName, message)
    }

    /**
     * 输出错误日志（包含异常）
     * @param message 消息内容
     * @param throwable 异常
     */
    protected fun logE(message: String, throwable: Throwable) {
        LogUtil.e(javaClass.simpleName, message, throwable)
    }

    // ==================== 生命周期回调 ====================

    override fun onStart() {
        super.onStart()
        onActivityStart()
    }

    override fun onResume() {
        super.onResume()
        onActivityResume()
    }

    override fun onPause() {
        onActivityPause()
        super.onPause()
    }

    override fun onStop() {
        onActivityStop()
        super.onStop()
    }

    override fun onDestroy() {
        onActivityDestroy()
        super.onDestroy()
    }

    /**
     * Activity 进入前台时调用
     */
    protected open fun onActivityStart() {}

    /**
     * Activity 恢复时调用
     */
    protected open fun onActivityResume() {}

    /**
     * Activity 暂停时调用
     */
    protected open fun onActivityPause() {}

    /**
     * Activity 停止时调用
     */
    protected open fun onActivityStop() {}

    /**
     * Activity 销毁时调用
     */
    protected open fun onActivityDestroy() {}
}