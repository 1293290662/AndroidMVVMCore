/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Fragment基类，提供完整的生命周期管理、懒加载和通用UI操作方法
 * 
 * @param VM ViewModel类型参数
 */
package com.github.spadger.mvvmc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.spadger.mvvmc.ext.LiveDataExt
import com.github.spadger.mvvmc.nav.Navigator
import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.util.ToastUtil

abstract class BaseFragment<VM : ViewModel> : Fragment() {
    /**
     * 泛型ViewModel实例
     */
    protected lateinit var viewModel: VM
    
    /**
     * 懒加载标记，确保数据只加载一次
     */
    private var isFirstLoad = true
    
    /**
     * 视图是否创建完成
     */
    private var isViewCreated = false

    /**
     * Fragment创建视图时调用
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beforeInit()
        viewModel = createViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * 视图创建完成后调用
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        
        initView()
        setupObservers()
        initListener()
        
        // 检查是否需要立即加载数据
        if (isFirstLoad && !isHidden) {
            isFirstLoad = false
            initData()
        }
        
        afterInit()
    }

    /**
     * Fragment显示/隐藏状态变化时调用
     * 用于ViewPager等场景下的懒加载
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isViewCreated && isFirstLoad) {
            isFirstLoad = false
            initData()
        }
    }

    /**
     * 在初始化之前调用
     */
    protected open fun beforeInit() {}

    /**
     * 初始化视图，子类必须实现
     */
    protected abstract fun initView()

    /**
     * 设置数据观察，子类可重写
     */
    protected open fun setupObservers() {}

    /**
     * 初始化事件监听，子类可重写
     */
    protected open fun initListener() {}

    /**
     * 懒加载数据，子类可重写
     * 只在第一次显示时执行
     */
    protected open fun initData() {}

    /**
     * 在初始化之后调用
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
        context?.let { ToastUtil.showError(it, message) }
    }

    // ==================== Toast 便捷方法 ====================

    /**
     * 显示短时间Toast
     * @param message 消息内容
     */
    protected fun showToast(message: String) {
        context?.let { ToastUtil.showShort(it, message) }
    }

    /**
     * 显示长时间Toast
     * @param message 消息内容
     */
    protected fun showToastLong(message: String) {
        context?.let { ToastUtil.showLong(it, message) }
    }

    /**
     * 显示成功提示Toast
     * @param message 消息内容
     */
    protected fun showSuccess(message: String) {
        context?.let { ToastUtil.showSuccess(it, message) }
    }

    /**
     * 显示警告提示Toast
     * @param message 消息内容
     */
    protected fun showWarning(message: String) {
        context?.let { ToastUtil.showWarning(it, message) }
    }

    /**
     * 显示信息提示Toast
     * @param message 消息内容
     */
    protected fun showInfo(message: String) {
        context?.let { ToastUtil.showInfo(it, message) }
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

    // ==================== 导航便捷方法 ====================

    /**
     * 导航到指定目的地
     * @param resId 目标目的地 ID
     */
    protected fun navigate(@androidx.annotation.IdRes resId: Int) {
        Navigator.navigate(this, resId)
    }

    /**
     * 导航到指定目的地（带参数）
     * @param resId 目标目的地 ID
     * @param args 传递的参数
     */
    protected fun navigate(@androidx.annotation.IdRes resId: Int, args: android.os.Bundle?) {
        Navigator.navigate(this, resId, args)
    }

    /**
     * 返回上一级
     */
    protected fun goBack() {
        Navigator.popBackStack(this)
    }

    /**
     * 返回栈底
     */
    protected fun goBackToRoot() {
        Navigator.popToRoot(this)
    }

    /**
     * 判断是否可以返回
     */
    protected fun canGoBack(): Boolean {
        return Navigator.canGoBack(this)
    }

    // ==================== 生命周期回调 ====================

    override fun onStart() {
        super.onStart()
        onFragmentStart()
    }

    override fun onResume() {
        super.onResume()
        onFragmentResume()
    }

    override fun onPause() {
        onFragmentPause()
        super.onPause()
    }

    override fun onStop() {
        onFragmentStop()
        super.onStop()
    }

    override fun onDestroyView() {
        onFragmentDestroyView()
        isViewCreated = false
        super.onDestroyView()
    }

    override fun onDestroy() {
        onFragmentDestroy()
        super.onDestroy()
    }

    protected open fun onFragmentStart() {}
    protected open fun onFragmentResume() {}
    protected open fun onFragmentPause() {}
    protected open fun onFragmentStop() {}
    protected open fun onFragmentDestroyView() {}
    protected open fun onFragmentDestroy() {}
}