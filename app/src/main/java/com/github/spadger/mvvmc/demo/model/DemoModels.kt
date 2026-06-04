/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 演示数据模型
 */
package com.github.spadger.mvvmc.demo.model

import com.github.spadger.mvvmc.model.BasePageResponse

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

data class ContractResponse(
    val rowId: Int = 0,
    val Id: Int = 0,
    val Contract_Num: String = "",
    val Load_Address: String = "",
    val Load_Street: String = "",
    val UnLoad_Address: String = "",
    val Unload_Street: String = "",
    val Load_Date_Start: String? = null,
    val Load_Date_End: String? = null,
    val Person_Send: String = "",
    val Person_Receive: String = "",
    val Goods_Type: String = "",
    val Truck_Fee: String = "",
    val Line_Name: String = "",
    val Unit_Name: String = "",
    val Person_Send_Phone: String = "",
    val Person_Receive_Phone: String = ""
)
