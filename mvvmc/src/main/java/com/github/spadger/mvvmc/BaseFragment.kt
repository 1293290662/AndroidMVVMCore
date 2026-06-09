package com.github.spadger.mvvmc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.github.spadger.mvvmc.nav.Navigator
import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.util.ToastUtil

abstract class BaseFragment : Fragment() {
    protected lateinit var mContext: Context

    private var isFirstLoad = true
    private var isViewCreated = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        
        if (isFirstLoad && !isHidden) {
            isFirstLoad = false
            initData()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isViewCreated && isFirstLoad) {
            isFirstLoad = false
            initData()
        }
    }

    protected open fun initData() {}

    protected fun showLoading() {
        showLoadingDialog()
    }

    protected fun showLoading(message: String) {
        showLoadingDialog(message)
    }

    protected fun hideLoading() {
        dismissLoadingDialog()
    }

    var mDialog: androidx.appcompat.app.AlertDialog? = null

    fun showLoadingDialog(message: String = "加载中") {
        mDialog?.dismiss()
        mDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it)
                .setMessage(message)
                .setCancelable(false)
                .create()
        }
        mDialog?.show()
    }

    fun dismissLoadingDialog() {
        mDialog?.dismiss()
        mDialog = null
    }

    protected fun showError(message: String) {
        context?.let { ToastUtil.showError(it, message) }
    }

    fun showToast(message: String) {
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
}