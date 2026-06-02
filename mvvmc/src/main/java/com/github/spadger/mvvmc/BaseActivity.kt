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

import android.Manifest
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.core.app.ActivityCompat

import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.util.SystemBarHelper
import com.github.spadger.mvvmc.util.ToastUtil

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {
    protected open val isEdgeToEdge: Boolean = true

    protected lateinit var mViewModel: VM

    protected val viewModel: VM
        get() = mViewModel

    /**
     * 权限请求回调接口
     */
    interface PermissionCallback {
        fun onGranted()
        fun onDenied(deniedPermissions: List<String>, shouldShowRationale: Boolean)
    }

    /**
     * 权限请求 launcher（在 onCreate 中注册）
     */
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var currentPermissionCallback: PermissionCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 必须在 onCreate 期间注册 ActivityResultLauncher
        registerPermissionLauncher()
        
        beforeInit()
        if (isEdgeToEdge) {
            SystemBarHelper.enableEdgeToEdge(this)
        }
        mViewModel = createViewModel()
    }

    /**
     * 完成视图设置后的初始化（子类调用）
     */
    protected fun onViewReady() {
        initView()
        setupObservers()
        initListener()
        initData()
        afterInit()
    }

    /**
     * 注册权限请求 launcher
     */
    private fun registerPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handlePermissionResult(permissions)
        }
    }

    /**
     * 请求权限
     * @param permissions 要请求的权限数组
     * @param callback 权限回调
     */
    protected fun requestPermissions(permissions: Array<String>, callback: PermissionCallback?) {
        currentPermissionCallback = callback
        
        val ungrantedPermissions = permissions.filter { 
            ActivityCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED 
        }.toTypedArray()
        
        if (ungrantedPermissions.isEmpty()) {
            callback?.onGranted()
            return
        }
        
        permissionLauncher.launch(ungrantedPermissions)
    }

    /**
     * 处理权限请求结果
     */
    private fun handlePermissionResult(permissions: Map<String, Boolean>) {
        val grantedPermissions = permissions.filter { it.value }.keys.toList()
        val deniedPermissions = permissions.filter { !it.value }.keys.toList()
        
        if (deniedPermissions.isEmpty()) {
            currentPermissionCallback?.onGranted()
        } else {
            val shouldShowRationale = deniedPermissions.any { 
                ActivityCompat.shouldShowRequestPermissionRationale(this, it) 
            }
            currentPermissionCallback?.onDenied(deniedPermissions, shouldShowRationale)
        }
        
        currentPermissionCallback = null
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
        ToastUtil.showError(this, message)
    }

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

    protected fun enableEdgeToEdge() {
        SystemBarHelper.enableEdgeToEdge(this)
    }

    protected fun setStatusBarColor(@androidx.annotation.ColorInt color: Int) {
        SystemBarHelper.setStatusBarColor(this, color)
    }

    protected fun setNavigationBarColor(@androidx.annotation.ColorInt color: Int) {
        SystemBarHelper.setNavigationBarColor(this, color)
    }

    protected fun setTransparentStatusBar() {
        SystemBarHelper.setTransparentStatusBar(this)
    }

    protected fun setTransparentNavigationBar() {
        SystemBarHelper.setTransparentNavigationBar(this)
    }

    protected fun setTransparentSystemBars() {
        SystemBarHelper.setTransparentSystemBars(this)
    }

    protected fun hideSystemBars() {
        SystemBarHelper.hideSystemBars(this)
    }

    protected fun showSystemBars() {
        SystemBarHelper.showSystemBars(this)
    }

    protected fun setLightStatusBar(isLight: Boolean) {
        SystemBarHelper.setLightStatusBar(this, isLight)
    }

    protected fun setLightNavigationBar(isLight: Boolean) {
        SystemBarHelper.setLightNavigationBar(this, isLight)
    }

    protected fun setLightSystemBars(isLight: Boolean) {
        SystemBarHelper.setLightSystemBars(this, isLight)
    }
}
