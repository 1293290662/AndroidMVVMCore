package com.github.spadger.mvvmc.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.spadger.mvvmc.BaseViewModel
import com.github.spadger.mvvmc.Result
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        viewModel = UserViewModel()
        
        viewModel.userLiveData.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading()
                is Result.Success -> handleSuccess(result.data)
                is Result.Error -> handleError(result.exception)
                is Result.Idle -> {}
            }
        }
        
        viewModel.loadUser("123")
    }
    
    private fun showLoading() {
    }
    
    private fun handleSuccess(data: UserResponse) {
    }
    
    private fun handleError(exception: Exception) {
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
            userLiveData.value = Result.Success(UserResponse(userId, "Test User", "test@example.com"))
        }
    }
}