package com.github.spadger.mvvmc.demo.vm

import com.github.spadger.mvvmc.BaseViewModel
import com.github.spadger.mvvmc.Result
import com.github.spadger.mvvmc.demo.model.*
import com.github.spadger.mvvmc.demo.repository.UserRepository
import com.github.spadger.mvvmc.model.PageData
import com.github.spadger.mvvmc.vm.ListViewModel

/**
 * 用户列表 ViewModel
 * 演示如何使用 ListViewModel 基类
 */
class UserListViewModel : ListViewModel<User>() {
    
    private val repository = UserRepository()
    
    /**
     * 刷新数据
     */
    fun refresh() {
        resetPage()
        setRefreshing()
        loadUsers()
    }
    
    /**
     * 加载更多
     */
    fun loadMore() {
        if (!hasMore) return
        setLoadMore()
        loadUsers()
    }
    
    /**
     * 加载用户列表
     */
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
                    setListError(result.exception)
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
class UserDetailViewModel : com.github.spadger.mvvmc.vm.StateViewModel<UserDetail>() {
    
    private val repository = UserRepository()
    
    /**
     * 加载用户详情
     */
    fun loadUserDetail(userId: String) {
        setLoading()
        launchOnIO {
            val result = repository.getUserDetail(userId)
            when (result) {
                is Result.Success -> {
                    setSuccess(result.data)
                }
                is Result.Error -> {
                    setError(result.exception)
                }
                else -> {}
            }
        }
    }
    
    /**
     * 更新用户信息
     */
    fun updateUser(userId: String, name: String? = null, avatar: String? = null) {
        setLoading()
        launchOnIO {
            val result = repository.updateUser(userId, UpdateUserRequest(name, avatar))
            when (result) {
                is Result.Success -> {
                    setSuccess(result.data)
                }
                is Result.Error -> {
                    setError(result.exception)
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
class LoginViewModel : com.github.spadger.mvvmc.vm.StateViewModel<LoginResponse>() {
    
    private val repository = UserRepository()
    
    /**
     * 执行登录
     */
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
                    setError(result.exception)
                }
                else -> {}
            }
        }
    }
    
    /**
     * 注册
     */
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
                    setError(result.exception)
                }
                else -> {}
            }
        }
    }
}