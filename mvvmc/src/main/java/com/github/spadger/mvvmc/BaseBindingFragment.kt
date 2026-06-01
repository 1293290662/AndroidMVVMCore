/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Fragment基类，支持 View Binding 和懒加载
 * 
 * @param VM ViewModel类型参数
 * @param VB ViewBinding类型参数
 */
package com.github.spadger.mvvmc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.util.ToastUtil

abstract class BaseBindingFragment<VM : ViewModel, VB : ViewBinding> : Fragment() {
    /**
     * 泛型ViewModel实例
     */
    protected lateinit var viewModel: VM
    
    /**
     * View Binding 实例
     */
    protected lateinit var binding: VB
    
    /**
     * 懒加载标记
     */
    private var isFirstLoad = true
    
    /**
     * 视图是否创建完成
     */
    private var isViewCreated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beforeInit()
        viewModel = createViewModel()
        binding = createViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        
        initView()
        setupObservers()
        initListener()
        
        if (isFirstLoad && !isHidden) {
            isFirstLoad = false
            initData()
        }
        
        afterInit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isViewCreated && isFirstLoad) {
            isFirstLoad = false
            initData()
        }
    }

    protected open fun beforeInit() {}

    /**
     * 创建 View Binding，子类必须实现
     */
    protected abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected open fun initView() {}
    protected open fun setupObservers() {}
    protected open fun initListener() {}
    protected open fun initData() {}
    protected open fun afterInit() {}

    protected abstract fun createViewModel(): VM

    protected inline fun <reified T : ViewModel> getViewModel(): T {
        return ViewModelProvider(this)[T::class.java]
    }

    protected open fun showLoading() {}
    protected open fun hideLoading() {}

    protected fun showError(message: String) {
        context?.let { ToastUtil.showError(it, message) }
    }

    // ==================== Toast 便捷方法 ====================
    protected fun showToast(message: String) {
        context?.let { ToastUtil.showShort(it, message) }
    }

    protected fun showToastLong(message: String) {
        context?.let { ToastUtil.showLong(it, message) }
    }

    protected fun showSuccess(message: String) {
        context?.let { ToastUtil.showSuccess(it, message) }
    }

    protected fun showWarning(message: String) {
        context?.let { ToastUtil.showWarning(it, message) }
    }

    protected fun showInfo(message: String) {
        context?.let { ToastUtil.showInfo(it, message) }
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