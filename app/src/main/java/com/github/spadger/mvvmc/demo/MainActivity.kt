package com.github.spadger.mvvmc.demo

import android.os.Bundle
import com.github.spadger.mvvmc.BaseActivity
import com.github.spadger.mvvmc.demo.vm.UserDetailViewModel
import com.github.spadger.mvvmc.ext.observeResult

/**
 * 演示如何使用封装好的基类和 ViewModel
 */
class MainActivity : BaseActivity<UserDetailViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupObservers()
        loadData()
    }
    
    override fun createViewModel(): UserDetailViewModel {
        return UserDetailViewModel()
    }
    
    private fun setupObservers() {
        viewModel.state.observeResult(
            owner = this,
            onSuccess = { user ->
                logD("用户加载成功: ${user.name}")
                showSuccess("加载成功")
            },
            onError = { exception ->
                logE("加载失败: ${exception.message}", exception)
                showError("加载失败: ${exception.message}")
            },
            onLoading = {
                logD("正在加载...")
                showLoading()
            }
        )
    }
    
    private fun loadData() {
        viewModel.loadUserDetail("123")
    }
    
    private fun showLoading() {
        logD("显示加载中...")
    }
}