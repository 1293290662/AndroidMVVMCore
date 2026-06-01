package com.github.spadger.mvvmc.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.spadger.mvvmc.BaseActivity
import com.github.spadger.mvvmc.Result
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<UserViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 方式1：使用 observeResult（推荐）- 自动处理 Result 状态
        viewModel.userLiveData.observeResult(
            this,
            onSuccess = { user -> handleSuccess(user) },
            onError = { exception -> handleError(exception) },
            onLoading = { showLoading() }
        )
        
        // 方式2：使用 observeSimple - 只处理成功和错误
        // viewModel.userLiveData.observeSimple(this,
        //     onSuccess = { user -> handleSuccess(user) },
        //     onError = { exception -> handleError(exception) }
        // )
        
        // 方式3：使用 observeWithLoading - 带加载状态
        // viewModel.userLiveData.observeWithLoading(this,
        //     onSuccess = { user -> handleSuccess(user) },
        //     onError = { exception -> handleError(exception) },
        //     onLoading = { showLoading() }
        // )
        
        viewModel.loadUser("123")
    }
    
    override fun createViewModel(): UserViewModel {
        return UserViewModel()
    }
    
    private fun showLoading() {
        logD("Loading...")
    }
    
    private fun handleSuccess(data: UserResponse) {
        logD("User loaded: ${data.name}")
        showSuccess("加载成功")
    }
    
    private fun handleError(exception: Exception) {
        logE("Load failed: ${exception.message}", exception)
        showError("加载失败: ${exception.message}")
    }
}

data class UserResponse(
    val id: String,
    val name: String,
    val email: String
)

class UserViewModel : BaseViewModel() {
    val userLiveData = MutableLiveData<Result<UserResponse>>()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            userLiveData.value = Result.Loading
            // 模拟网络请求
            userLiveData.value = Result.Success(UserResponse(userId, "Test User", "test@example.com"))
        }
    }
}