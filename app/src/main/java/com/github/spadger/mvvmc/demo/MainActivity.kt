package com.github.spadger.mvvmc.demo

import android.os.Bundle
import com.github.spadger.mvvmc.BaseActivity
import com.github.spadger.mvvmc.BaseBindingActivity
import com.github.spadger.mvvmc.demo.databinding.ActivityMainBinding
import com.github.spadger.mvvmc.demo.vm.UserDetailViewModel
import com.github.spadger.mvvmc.ext.observeResult
import com.github.spadger.mvvmc.util.DataStoreHolder
import com.github.spadger.mvvmc.util.DataStoreKeys
import com.github.spadger.mvvmc.util.DialogBuilder
import com.github.spadger.mvvmc.util.PermissionConstants
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 演示 Activity - 使用 View Binding
 * 
 * View Binding 是 Android Studio 3.6 引入的官方视图绑定方式，
 * 替代 findViewById，类型安全且空安全
 */
class MainActivity : BaseBindingActivity<UserDetailViewModel, ActivityMainBinding>() {

    /**
     * 创建 View Binding（必须实现）
     */
    override fun createViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    /**
     * 初始化视图（可重写）
     */
    override fun initView() {
        // 使用 binding 直接访问视图，无需 findViewById
        binding.tvUserName.text = "未登录"
    }

    /**
     * 设置数据观察
     */
    override fun setupObservers() {
        viewModel.state.observeResult(
            owner = this,
            onSuccess = { user -> handleUserLoadSuccess(user) },
            onError = { exception -> handleUserLoadError(exception) },
            onLoading = { showLoading() }
        )

        // 监听 DataStore 数据变化
        lifecycleScope.launch {
            DataStoreHolder.getInstance().getStringFlow(DataStoreKeys.USER_NAME)
                .collect { name ->
                    binding.tvUserName.text = name ?: "未登录"
                }
        }
    }

    /**
     * 初始化事件监听
     */
    override fun initListener() {
        binding.btnLoadData.setOnClickListener {
            loadUserData()
        }

        binding.btnShowDialog.setOnClickListener {
            showSampleDialog()
        }

        binding.btnRequestPermission.setOnClickListener {
            requestPermissions()
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        logD("初始化数据...")
        loadUserData()
        initDataStore()
        requestPermissions()
    }

    /**
     * 创建 ViewModel
     */
    override fun createViewModel(): UserDetailViewModel {
        return UserDetailViewModel()
    }

    private fun loadUserData() {
        viewModel.loadUserDetail("123")
    }

    private fun handleUserLoadSuccess(user: com.github.spadger.mvvmc.demo.model.UserDetail) {
        hideLoading()
        // 使用 View Binding 直接访问视图
        binding.tvUserName.text = user.name
        binding.tvUserEmail.text = user.email
        showSuccess("用户加载成功")
    }

    private fun handleUserLoadError(exception: Exception) {
        hideLoading()
        showError("加载失败: ${exception.message}")
    }

    private fun initDataStore() {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreHolder.getInstance().putString(DataStoreKeys.USER_TOKEN, "test_token_123")
            DataStoreHolder.getInstance().putBoolean(DataStoreKeys.IS_LOGGED_IN, true)
        }
    }

    private fun requestPermissions() {
        requestPermissions(PermissionConstants.STORAGE_PERMISSIONS, object : BaseActivity.PermissionCallback {
            override fun onGranted() {
                showSuccess("权限已授予")
            }

            override fun onDenied(deniedPermissions: List<String>, shouldShowRationale: Boolean) {
                showWarning("权限被拒绝")
            }
        })
    }

    private fun showSampleDialog() {
        DialogBuilder.showConfirm(this, "测试对话框", "这是一个测试对话框",
            { showSuccess("确认") },
            { showInfo("取消") }
        )
    }

    /*private fun showLoading() {
        binding.progressBar.visibility = android.view.View.VISIBLE
    }*/

    /*private fun hideLoading() {
        binding.progressBar.visibility = android.view.View.GONE
    }*/
}