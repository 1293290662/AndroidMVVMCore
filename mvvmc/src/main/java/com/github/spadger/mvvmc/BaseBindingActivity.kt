/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * Activity基类，支持 View Binding
 *
 * @param VM ViewModel类型参数
 * @param VB ViewBinding类型参数
 */
package com.github.spadger.mvvmc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.util.SystemBarHelper
import com.github.spadger.mvvmc.util.ToastUtil

abstract class BaseBindingActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity() {
    /**
     * 是否启用 Edge-to-Edge 模式（默认启用）
     */
    protected open val isEdgeToEdge: Boolean = true

    /**
     * 泛型ViewModel实例
     */
    protected lateinit var viewModel: VM

    /**
     * View Binding 实例
     */
    protected lateinit var binding: VB

    /**
     * Activity创建时调用
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beforeInit()
        if (isEdgeToEdge) {
            SystemBarHelper.enableEdgeToEdge(this)
        }
        viewModel = createViewModel()
        binding = createViewBinding()
        setContentView(binding.root)
        initView()
        setupObservers()
        initListener()
        initData()
        afterInit()
    }

    /**
     * 在初始化之前调用
     */
    protected open fun beforeInit() {}

    /**
     * 创建 View Binding，子类必须实现
     * @return ViewBinding 实例
     */
    protected abstract fun createViewBinding(): VB

    /**
     * 初始化视图，子类可重写
     */
    protected open fun initView() {}

    /**
     * 设置数据观察，子类可重写
     */
    protected open fun setupObservers() {}

    /**
     * 初始化事件监听，子类可重写
     */
    protected open fun initListener() {}

    /**
     * 初始化数据，子类可重写
     */
    protected open fun initData() {}

    /**
     * 在初始化之后调用
     */
    protected open fun afterInit() {}

    /**
     * 创建 ViewModel，子类必须实现
     * @return VM类型的ViewModel实例
     */
    protected abstract fun createViewModel(): VM

    /**
     * 获取ViewModel的便捷方法
     */
    protected inline fun <reified T : ViewModel> getViewModel(): T {
        return ViewModelProvider(this)[T::class.java]
    }

    protected open fun showLoading() {}
    protected open fun hideLoading() {}

    protected fun showError(message: String) {
        ToastUtil.showError(this, message)
    }

    // ==================== Toast 便捷方法 ====================
    protected fun showToast(message: String) {
        ToastUtil.showShort(this, message)
    }

    protected fun showToastLong(message: String) {
        ToastUtil.showLong(this, message)
    }

    protected fun showSuccess(message: String) {
        ToastUtil.showSuccess(this, message)
    }

    protected fun showWarning(message: String) {
        ToastUtil.showWarning(this, message)
    }

    protected fun showInfo(message: String) {
        ToastUtil.showInfo(this, message)
    }

    // ==================== Log 便捷方法 ====================
    protected fun logD(message: String) {
        LogUtil.d(javaClass.simpleName, message)
    }

    protected fun logI(message: String) {
        LogUtil.i(javaClass.simpleName, message)
    }

    protected fun logW(message: String) {
        LogUtil.w(javaClass.simpleName, message)
    }

    protected fun logE(message: String) {
        LogUtil.e(javaClass.simpleName, message)
    }

    protected fun logE(message: String, throwable: Throwable) {
        LogUtil.e(javaClass.simpleName, message, throwable)
    }

    // ==================== System Bar 便捷方法 ====================

    /**
     * 启用 Edge-to-Edge 模式
     */
    protected fun enableEdgeToEdge() {
        SystemBarHelper.enableEdgeToEdge(this)
    }

    /**
     * 设置状态栏颜色
     */
    protected fun setStatusBarColor(@androidx.annotation.ColorInt color: Int) {
        SystemBarHelper.setStatusBarColor(this, color)
    }

    /**
     * 设置导航栏颜色
     */
    protected fun setNavigationBarColor(@androidx.annotation.ColorInt color: Int) {
        SystemBarHelper.setNavigationBarColor(this, color)
    }

    /**
     * 设置透明状态栏
     */
    protected fun setTransparentStatusBar() {
        SystemBarHelper.setTransparentStatusBar(this)
    }

    /**
     * 设置透明导航栏
     */
    protected fun setTransparentNavigationBar() {
        SystemBarHelper.setTransparentNavigationBar(this)
    }

    /**
     * 设置透明系统栏
     */
    protected fun setTransparentSystemBars() {
        SystemBarHelper.setTransparentSystemBars(this)
    }

    /**
     * 隐藏系统栏
     */
    protected fun hideSystemBars() {
        SystemBarHelper.hideSystemBars(this)
    }

    /**
     * 显示系统栏
     */
    protected fun showSystemBars() {
        SystemBarHelper.showSystemBars(this)
    }

    /**
     * 设置浅色状态栏（深色图标）
     */
    protected fun setLightStatusBar(isLight: Boolean) {
        SystemBarHelper.setLightStatusBar(this, isLight)
    }

    /**
     * 设置浅色导航栏（深色图标）
     */
    protected fun setLightNavigationBar(isLight: Boolean) {
        SystemBarHelper.setLightNavigationBar(this, isLight)
    }

    /**
     * 设置浅色系统栏
     */
    protected fun setLightSystemBars(isLight: Boolean) {
        SystemBarHelper.setLightSystemBars(this, isLight)
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

    protected open fun onActivityStart() {}
    protected open fun onActivityResume() {}
    protected open fun onActivityPause() {}
    protected open fun onActivityStop() {}
    protected open fun onActivityDestroy() {}
}
