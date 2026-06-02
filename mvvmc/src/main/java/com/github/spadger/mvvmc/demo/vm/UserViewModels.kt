package com.github.spadger.mvvmc.demo.vm

import com.github.spadger.mvvmc.Result
import com.github.spadger.mvvmc.demo.model.*
import com.github.spadger.mvvmc.demo.repository.UserRepository
import com.github.spadger.mvvmc.model.PageData
import com.github.spadger.mvvmc.vm.ListViewModel
import com.github.spadger.mvvmc.vm.StateViewModel

/**
 * 用户列表 ViewModel
 * 演示如何使用 ListViewModel 基类
 */
class UserListViewModel : ListViewModel<User>() {
    
    private val repository = UserRepository()
    
    fun refresh() {
        resetPage()
        setRefreshing()
        loadUsers()
    }
    
    fun loadMore() {
        if (!hasMore) return
        setLoadMore()
        loadUsers()
    }
    
    private fun loadUsers() {
        launchOnIO {
            val result = repository.getUserList(currentPage)
            when (result) {
                is Result.Success -> {
                    val pageData = result.data
                    setListSuccess(
                        pageData.list ?: emptyList(),
                        pageData.hasMore
                    )
                    nextPage()
                }
                is Result.Error -> {
                    setListError(result.err)
                }
                else -> {}
            }
        }
    }
}

/**
 * 用户详情 ViewModel
 * 演示如何使用 StateViewModel 基类
 */
class UserDetailViewModel : StateViewModel<UserDetail>() {
    
    private val repository = UserRepository()
    
    fun loadUserDetail(userId: String) {
        setLoading()
        launchOnIO {
            val result = repository.getUserDetail(userId)
            when (result) {
                is Result.Success -> {
                    setSuccess(result.data)
                }
                is Result.Error -> {
                    setError(result.err)
                }
                else -> {}
            }
        }
    }
}

/**
 * 更新用户信息 ViewModel
 */
class UpdateUserViewModel : StateViewModel<User>() {
    
    private val repository = UserRepository()
    
    fun updateUser(userId: String, name: String? = null, avatar: String? = null) {
        setLoading()
        launchOnIO {
            val result = repository.updateUser(userId, UpdateUserRequest(name, avatar))
            when (result) {
                is Result.Success -> {
                    setSuccess(result.data)
                }
                is Result.Error -> {
                    setError(result.err)
                }
                else -> {}
            }
        }
    }
}

/**
 * 登录 ViewModel
 * 演示简单的状态管理
 */
class LoginViewModel : StateViewModel<LoginResponse>() {
    
    private val repository = UserRepository()
    
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            setError(Exception("用户名和密码不能为空"))
            return
        }
        
        setLoading()
        launchOnIO {
            val result = repository.login(username, password)
            when (result) {
                is Result.Success -> {
                    setSuccess(result.data)
                }
                is Result.Error -> {
                    setError(result.err)
                }
                else -> {}
            }
        }
    }
}

/**
 * 注册 ViewModel
 */
class RegisterViewModel : StateViewModel<User>() {
    
    private val repository = UserRepository()
    
    fun register(username: String, password: String, email: String) {
        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            setError(Exception("请填写完整信息"))
            return
        }
        
        setLoading()
        launchOnIO {
            val result = repository.register(username, password, email)
            when (result) {
                is Result.Success -> {
                    setSuccess(result.data)
                }
                is Result.Error -> {
                    setError(result.err)
                }
                else -> {}
            }
        }
    }
}
