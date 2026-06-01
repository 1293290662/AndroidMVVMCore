package com.github.spadger.mvvmc.demo.model

import com.github.spadger.mvvmc.model.BaseResponse
import com.github.spadger.mvvmc.model.PageResponse
import com.github.spadger.mvvmc.model.PageData

/**
 * 用户相关的数据模型
 */

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null,
    val phone: String? = null,
    val status: Int = 1
)

data class UserDetail(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null,
    val phone: String? = null,
    val gender: Int = 0,
    val birthday: String? = null,
    val address: String? = null,
    val bio: String? = null,
    val createTime: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)

data class UpdateUserRequest(
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val gender: Int? = null,
    val birthday: String? = null,
    val address: String? = null,
    val bio: String? = null
)

typealias UserResponse = BaseResponse<User>
typealias UserDetailResponse = BaseResponse<UserDetail>
typealias LoginResponseType = BaseResponse<LoginResponse>
typealias UserListResponse = PageResponse<User>
typealias EmptyResponseType = BaseResponse<Unit>