package com.github.spadger.mvvmc.demo.repository

import com.github.spadger.mvvmc.BaseRepository
import com.github.spadger.mvvmc.ExceptionHandler
import com.github.spadger.mvvmc.Result
import com.github.spadger.mvvmc.demo.model.*
import com.github.spadger.mvvmc.model.BaseResponse
import com.github.spadger.mvvmc.model.PageData
import com.github.spadger.mvvmc.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.*

/**
 * 用户 Repository
 * 负责用户相关的数据请求
 */
class UserRepository : BaseRepository() {
    
    private val api: UserApi by lazy {
        createApi()
    }
    
    /**
     * 用户登录
     */
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(username, password))
                if (response.isSuccess() && response.data != null) {
                    Result.Success(response.data!!)
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
                    Result.Success(response.data!!)
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
                    Result.Success(response.data!!)
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
                    Result.Success(response.data!!)
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
                    Result.Success(response.data!!)
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
    
    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponseType
    
    @POST("api/user/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse
    
    @GET("api/user/{userId}")
    suspend fun getUserDetail(@Path("userId") userId: String): UserDetailResponse
    
    @GET("api/user/list")
    suspend fun getUserList(@Query("page") page: Int, @Query("pageSize") pageSize: Int): UserListResponse
    
    @PUT("api/user/{userId}")
    suspend fun updateUser(@Path("userId") userId: String, @Body request: UpdateUserRequest): UserResponse
    
    @POST("api/user/logout")
    suspend fun logout(): EmptyResponseType
}

typealias LoginResponseType = BaseResponse<LoginResponse>
typealias UserResponse = BaseResponse<User>
typealias UserDetailResponse = BaseResponse<UserDetail>
typealias UserListResponse = BaseResponse<PageData<User>>
typealias EmptyResponseType = BaseResponse<Unit>
