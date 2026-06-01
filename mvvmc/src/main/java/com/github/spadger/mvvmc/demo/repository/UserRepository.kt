package com.github.spadger.mvvmc.demo.repository

import com.github.spadger.mvvmc.BaseRepository
import com.github.spadger.mvvmc.demo.model.*
import com.github.spadger.mvvmc.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 用户 Repository
 * 负责用户相关的数据请求
 */
class UserRepository : BaseRepository() {
    
    private val api: UserApi by lazy {
        createApi(UserApi::class.java)
    }
    
    /**
     * 用户登录
     */
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(username, password))
                if (response.isSuccess() && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(Exception(response.getErrorMessage()))
                }
            } catch (e: Exception) {
                LogUtil.e("UserRepository", "Login failed", e)
                Result.Error(ExceptionHandler.handleException(e))
            }
        }
    }
    
    /**
     * 注册用户
     */
    suspend fun register(username: String, password: String, email: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.register(RegisterRequest(username, password, email))
                if (response.isSuccess() && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(Exception(response.getErrorMessage()))
                }
            } catch (e: Exception) {
                LogUtil.e("UserRepository", "Register failed", e)
                Result.Error(ExceptionHandler.handleException(e))
            }
        }
    }
    
    /**
     * 获取用户详情
     */
    suspend fun getUserDetail(userId: String): Result<UserDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUserDetail(userId)
                if (response.isSuccess() && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(Exception(response.getErrorMessage()))
                }
            } catch (e: Exception) {
                LogUtil.e("UserRepository", "Get user detail failed", e)
                Result.Error(ExceptionHandler.handleException(e))
            }
        }
    }
    
    /**
     * 获取用户列表
     */
    suspend fun getUserList(page: Int, pageSize: Int = 20): Result<PageData<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUserList(page, pageSize)
                if (response.isSuccess() && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(Exception(response.getErrorMessage()))
                }
            } catch (e: Exception) {
                LogUtil.e("UserRepository", "Get user list failed", e)
                Result.Error(ExceptionHandler.handleException(e))
            }
        }
    }
    
    /**
     * 更新用户信息
     */
    suspend fun updateUser(userId: String, request: UpdateUserRequest): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.updateUser(userId, request)
                if (response.isSuccess() && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(Exception(response.getErrorMessage()))
                }
            } catch (e: Exception) {
                LogUtil.e("UserRepository", "Update user failed", e)
                Result.Error(ExceptionHandler.handleException(e))
            }
        }
    }
}

/**
 * 用户 API 接口
 * 定义用户相关的网络请求
 */
interface UserApi {
    
    suspend fun login(request: LoginRequest): LoginResponseType
    
    suspend fun register(request: RegisterRequest): UserResponse
    
    suspend fun getUserDetail(userId: String): UserDetailResponse
    
    suspend fun getUserList(page: Int, pageSize: Int): UserListResponse
    
    suspend fun updateUser(userId: String, request: UpdateUserRequest): UserResponse
    
    suspend fun logout(): EmptyResponseType
}