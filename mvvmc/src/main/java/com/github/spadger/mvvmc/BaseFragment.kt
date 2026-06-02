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

import com.github.spadger.mvvmc.nav.Navigator
import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.util.ToastUtil

abstract class BaseFragment<VM : ViewModel> : Fragment() {
    protected lateinit var mViewModel: VM
    
    protected val viewModel: VM
        get() = mViewModel
    
    private var isFirstLoad = true
    private var isViewCreated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beforeInit()
        mViewModel = createViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
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

    protected abstract fun initView()

    protected open fun setupObservers() {}

    protected open fun initListener() {}

    protected open fun initData() {}

    protected open fun afterInit() {}

    protected abstract fun createViewModel(): VM

    protected inline fun <reified T : ViewModel> obtainViewModel(): T {
        return ViewModelProvider(this)[T::class.java]
    }

    protected open fun showLoading() {}

    protected open fun hideLoading() {}

    protected fun showError(message: String) {
        context?.let { ToastUtil.showError(it, message) }
    }

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

    protected fun navigate(@androidx.annotation.IdRes resId: Int) {
        Navigator.navigate(this, resId)
    }

    protected fun navigate(@androidx.annotation.IdRes resId: Int, args: android.os.Bundle?) {
        Navigator.navigate(this, resId, args)
    }

    protected fun goBack() {
        Navigator.popBackStack(this)
    }

    protected fun goBackToRoot() {
        Navigator.popToRoot(this)
    }

    protected fun canGoBack(): Boolean {
        return Navigator.canGoBack(this)
    }

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
