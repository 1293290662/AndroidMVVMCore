/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 演示数据模型
 */
package com.github.spadger.mvvmc.demo.model

data class UserDetail(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null,
    val phone: String? = null,
    val status: Int = 1
)

data class LoginRequest(
    val User_Name: String,
    val Pwd: String
)

data class LoginResponse(
    val User_Name: String,
    val User_Phone: String,
    val Truck_Num: String
)
