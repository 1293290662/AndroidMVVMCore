package com.github.spadger.mvvmc.demo

import android.os.Bundle
import com.github.spadger.mvvmc.BaseActivity
import com.github.spadger.mvvmc.Result
import com.github.spadger.mvvmc.demo.vm.LoginViewModel
import com.github.spadger.mvvmc.demo.vm.UserDetailViewModel
import com.github.spadger.mvvmc.ext.observeResult
import com.github.spadger.mvvmc.util.DataStoreHolder
import com.github.spadger.mvvmc.util.DataStoreKeys
import com.github.spadger.mvvmc.util.DialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 演示 Activity
 * 展示如何使用 MVVMCore 框架的各种封装
 */
class MainActivity : BaseActivity<UserDetailViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 演示各种封装的使用
        demoLogUtil()
        demoToastUtil()
        demoDialogBuilder()
        demoDataStore()
        demoViewModel()
    }
    
    override fun createViewModel(): UserDetailViewModel {
        return UserDetailViewModel()
    }
    
    /**
     * 演示 LogUtil 使用
     */
    private fun demoLogUtil() {
        logD("Debug log - 调试信息")
        logI("Info log - 普通信息")
        logW("Warn log - 警告信息")
        logE("Error log - 错误信息")
        logE("Error with exception", Exception("测试异常"))
    }
    
    /**
     * 演示 ToastUtil 使用
     */
    private fun demoToastUtil() {
        showToast("短提示")
        showToastLong("长提示")
        showSuccess("操作成功")
        showWarning("警告信息")
        showError("错误信息")
        showInfo("提示信息")
    }
    
    /**
     * 演示 DialogBuilder 使用
     */
    private fun demoDialogBuilder() {
        // 简单消息对话框
        DialogBuilder.showMessage(this, "这是一个简单消息")
        
        // 确认对话框
        DialogBuilder.showConfirm(this, "确定要删除吗？",
            { showSuccess("已删除") },
            { showInfo("已取消") }
        )
        
        // 自定义对话框
        DialogBuilder(this)
            .setTitle("自定义标题")
            .setMessage("自定义消息内容")
            .setPositiveText("确认")
            .setNegativeText("取消")
            .setPositiveListener { showSuccess("点击了确认") }
            .show()
    }
    
    /**
     * 演示 DataStore 使用
     */
    private fun demoDataStore() {
        CoroutineScope(Dispatchers.IO).launch {
            // 写入数据
            DataStoreHolder.getInstance().putString(DataStoreKeys.USER_TOKEN, "test_token_123")
            DataStoreHolder.getInstance().putBoolean(DataStoreKeys.IS_LOGGED_IN, true)
            DataStoreHolder.getInstance().putLong(DataStoreKeys.LAST_LOGIN_TIME, System.currentTimeMillis())
            
            // 同步读取数据
            val token = DataStoreHolder.getInstance().getString(DataStoreKeys.USER_TOKEN)
            val isLoggedIn = DataStoreHolder.getInstance().getBoolean(DataStoreKeys.IS_LOGGED_IN)
            
            // 在主线程显示
            runOnUiThread {
                logD("Token from DataStore: $token")
                logD("Is logged in: $isLoggedIn")
            }
        }
        
        // 使用 Flow 监听数据变化
        DataStoreHolder.getInstance().getStringFlow(DataStoreKeys.USER_TOKEN)
            .observe(this) { token ->
                logD("Token changed: $token")
            }
    }
    
    /**
     * 演示 ViewModel 使用
     */
    private fun demoViewModel() {
        // 观察 ViewModel 状态
        viewModel.state.observeResult(
            owner = this,
            onSuccess = { user ->
                logD("用户加载成功: ${user.name}")
                showSuccess("用户加载成功")
            },
            onError = { exception ->
                logE("加载失败: ${exception.message}", exception)
                showError("加载失败: ${exception.message}")
            },
            onLoading = {
                logD("正在加载用户数据...")
            }
        )
        
        // 触发数据加载
        viewModel.loadUserDetail("123")
    }
}

/**
 * 登录演示 Activity
 */
class LoginActivity : BaseActivity<LoginViewModel>() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        setupObservers()
    }
    
    override fun createViewModel(): LoginViewModel {
        return LoginViewModel()
    }
    
    private fun setupObservers() {
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
    
    // 模拟登录按钮点击
    fun onLoginClick(username: String, password: String) {
        viewModel.login(username, password)
    }
}