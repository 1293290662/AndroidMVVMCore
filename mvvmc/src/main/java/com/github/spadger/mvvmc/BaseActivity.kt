package com.github.spadger.mvvmc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.util.SystemBarHelper
import com.github.spadger.mvvmc.util.ToastUtil

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var mContext: Context

    interface PermissionCallback {
        fun onGranted()
        fun onDenied(deniedPermissions: List<String>, shouldShowRationale: Boolean)
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var currentPermissionCallback: PermissionCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        registerPermissionLauncher()
    }

    private fun registerPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handlePermissionResult(permissions)
        }
    }

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

    fun startActivity(clz: Class<*>) {
        val intent = Intent(this, clz)
        startActivity(intent)
    }

    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val intent = Intent()
        intent.setClass(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    fun startActivityForResult(clz: Class<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent()
        intent.setClass(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }

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
        mDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(false)
            .create()
        mDialog?.show()
    }

    fun dismissLoadingDialog() {
        mDialog?.dismiss()
        mDialog = null
    }

    protected fun showError(message: String) {
        ToastUtil.showError(this, message)
    }

    fun showToast(message: String) {
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