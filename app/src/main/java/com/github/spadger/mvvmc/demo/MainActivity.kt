package com.github.spadger.mvvmc.demo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.github.spadger.mvvmc.BaseActivity
import com.github.spadger.mvvmc.demo.vm.UserDetailViewModel
import com.github.spadger.mvvmc.ext.observeResult
import com.github.spadger.mvvmc.util.DataStoreHolder
import com.github.spadger.mvvmc.util.DataStoreKeys
import com.github.spadger.mvvmc.util.DialogBuilder
import com.github.spadger.mvvmc.util.PermissionCallback
import com.github.spadger.mvvmc.util.PermissionConstants
import com.github.spadger.mvvmc.util.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 演示 Activity
 * 展示如何使用 MVVMCore 框架的各种封装
 */
class MainActivity : BaseActivity<UserDetailViewModel>() {

    private lateinit var tvUserName: TextView

    /**
     * 初始化视图（必须实现）
     */
    override fun initView() {
        setContentView(R.layout.activity_main)
        tvUserName = findViewById(R.id.tv_user_name)
    }

    /**
     * 设置数据观察（可重写）
     */
    override fun setupObservers() {
        // 观察 ViewModel 状态
        viewModel.state.observeResult(
            owner = this,
            onSuccess = { user -> handleUserLoadSuccess(user) },
            onError = { exception -> handleUserLoadError(exception) },
            onLoading = { showLoading() }
        )

        // 监听 DataStore 数据变化
        DataStoreHolder.getInstance().getStringFlow(DataStoreKeys.USER_NAME)
            .observe(this) { name ->
                tvUserName.text = name ?: "未登录"
            }
    }

    /**
     * 初始化事件监听（可重写）
     */
    override fun initListener() {
        // 设置按钮点击事件
        findViewById<View>(R.id.btn_load_data).setOnClickListener {
            loadUserData()
        }
        
        findViewById<View>(R.id.btn_show_dialog).setOnClickListener {
            showSampleDialog()
        }
        
        findViewById<View>(R.id.btn_request_permission).setOnClickListener {
            requestPermissions()
        }
    }

    /**
     * 初始化数据（可重写）
     */
    override fun initData() {
        logD("初始化数据...")
        loadUserData()
        initDataStore()
        requestPermissions()
    }

    /**
     * 创建 ViewModel（必须实现）
     */
    override fun createViewModel(): UserDetailViewModel {
        return UserDetailViewModel()
    }

    /**
     * Activity 进入前台
     */
    override fun onActivityResume() {
        super.onActivityResume()
        logD("Activity resumed")
    }

    /**
     * 加载用户数据
     */
    private fun loadUserData() {
        viewModel.loadUserDetail("123")
    }

    /**
     * 处理用户加载成功
     */
    private fun handleUserLoadSuccess(user: com.github.spadger.mvvmc.demo.model.UserDetail) {
        hideLoading()
        tvUserName.text = user.name
        showSuccess("用户加载成功")
    }

    /**
     * 处理用户加载失败
     */
    private fun handleUserLoadError(exception: Exception) {
        hideLoading()
        showError("加载失败: ${exception.message}")
    }

    /**
     * 初始化 DataStore
     */
    private fun initDataStore() {
        CoroutineScope(Dispatchers.IO).launch {
            // 写入测试数据
            DataStoreHolder.getInstance().putString(DataStoreKeys.USER_TOKEN, "test_token_123")
            DataStoreHolder.getInstance().putBoolean(DataStoreKeys.IS_LOGGED_IN, true)
            DataStoreHolder.getInstance().putLong(DataStoreKeys.LAST_LOGIN_TIME, System.currentTimeMillis())
            
            // 读取数据
            val token = DataStoreHolder.getInstance().getString(DataStoreKeys.USER_TOKEN)
            runOnUiThread {
                logD("Token from DataStore: $token")
            }
        }
    }

    /**
     * 请求权限
     */
    private fun requestPermissions() {
        val permissionHelper = PermissionHelper(this, object : PermissionCallback {
            override fun onGranted() {
                showSuccess("权限已授予")
            }

            override fun onDenied(deniedPermissions: List<String>, shouldShowRationale: Boolean) {
                showWarning("权限被拒绝")
            }
        })

        if (permissionHelper.areAllPermissionsGranted(PermissionConstants.STORAGE_PERMISSIONS)) {
            logD("存储权限已授予")
        } else {
            permissionHelper.requestPermissions(PermissionConstants.STORAGE_PERMISSIONS)
        }
    }

    /**
     * 显示示例对话框
     */
    private fun showSampleDialog() {
        DialogBuilder.showConfirm(this, "测试对话框", "这是一个测试对话框",
            { showSuccess("确认") },
            { showInfo("取消") }
        )
    }

    /**
     * 显示加载中
     */
    private fun showLoading() {
        logD("显示加载中...")
    }

    /**
     * 隐藏加载中
     */
    private fun hideLoading() {
        logD("隐藏加载中...")
    }
}

/**
 * 登录演示 Activity
 */
class LoginActivity : BaseActivity<com.github.spadger.mvvmc.demo.vm.LoginViewModel>() {

    override fun initView() {
        setContentView(R.layout.activity_login)
    }

    override fun setupObservers() {
        viewModel.state.observeResult(
            this,
            onSuccess = { loginResponse ->
                // 登录成功，保存 token
                CoroutineScope(Dispatchers.IO).launch {
                    DataStoreHolder.getInstance().putString(DataStoreKeys.USER_TOKEN, loginResponse.token)
                    DataStoreHolder.getInstance().putBoolean(DataStoreKeys.IS_LOGGED_IN, true)
                }
                showSuccess("登录成功")
            },
            onError = { exception ->
                showError("登录失败: ${exception.message}")
            },
            onLoading = {
                logD("正在登录...")
            }
        )
    }

    override fun createViewModel(): com.github.spadger.mvvmc.demo.vm.LoginViewModel {
        return com.github.spadger.mvvmc.demo.vm.LoginViewModel()
    }

    // 模拟登录按钮点击
    fun onLoginClick(username: String, password: String) {
        viewModel.login(username, password)
    }
}