/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Fragment基类，提供ViewModel支持、懒加载和通用UI操作方法
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
     * Fragment创建视图时调用，初始化ViewModel
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = createViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * 视图创建完成后调用，开始观察数据和执行懒加载
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        if (isFirstLoad && !isHidden) {
            isFirstLoad = false
            lazyLoad()
        }
    }

    /**
     * Fragment显示/隐藏状态变化时调用
     * 用于ViewPager等场景下的懒加载
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isFirstLoad) {
            isFirstLoad = false
            lazyLoad()
        }
    }

    /**
     * 抽象方法，子类必须实现以创建对应的ViewModel实例
     * @return VM类型的ViewModel实例
     */
    protected abstract fun createViewModel(): VM

    /**
     * 观察ViewModel数据变化的方法，子类可重写
     */
    protected open fun observeViewModel() {}

    /**
     * 懒加载方法，子类可重写实现延迟数据加载
     * 只在第一次显示时执行
     */
    protected open fun lazyLoad() {}

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