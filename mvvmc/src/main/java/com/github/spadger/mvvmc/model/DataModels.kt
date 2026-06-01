/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 通用数据模型封装，提供请求/响应基类和分页模型
 */
package com.github.spadger.mvvmc.model

import com.google.gson.annotations.SerializedName

/**
 * 基础请求模型
 */
open class BaseRequest {
    @SerializedName("page")
    var page: Int = 1
    
    @SerializedName("page_size")
    var pageSize: Int = 20
}

/**
 * 基础响应模型
 */
open class BaseResponse<T>(
    @SerializedName("code")
    open val code: Int = 0,
    
    @SerializedName("msg")
    open val message: String? = null,
    
    @SerializedName("data")
    open val data: T? = null
) {
    fun isSuccess(): Boolean = code == 0 || code == 200
    
    fun getErrorMessage(): String = message ?: "请求失败"
}

/**
 * 分页响应模型
 */
data class PageResponse<T>(
    @SerializedName("code")
    override val code: Int = 0,
    
    @SerializedName("msg")
    override val message: String? = null,
    
    @SerializedName("data")
    override val data: PageData<T>? = null
) : BaseResponse<PageData<T>>(code, message, data)

/**
 * 分页数据模型
 */
data class PageData<T>(
    @SerializedName("list")
    val list: List<T>? = null,
    
    @SerializedName("total")
    val total: Int = 0,
    
    @SerializedName("page")
    val page: Int = 1,
    
    @SerializedName("page_size")
    val pageSize: Int = 20,
    
    @SerializedName("has_more")
    val hasMore: Boolean = false
) {
    fun isEmpty(): Boolean = list.isNullOrEmpty()
    
    fun isNotEmpty(): Boolean = !isEmpty()
}

/**
 * 空响应模型（用于不需要返回数据的接口）
 */
class EmptyResponse

/**
 * 简单的布尔响应
 */
data class BooleanResponse(
    @SerializedName("code")
    val code: Int = 0,
    
    @SerializedName("msg")
    val message: String? = null,
    
    @SerializedName("data")
    val data: Boolean = false
)

/**
 * 简单的字符串响应
 */
data class StringResponse(
    @SerializedName("code")
    val code: Int = 0,
    
    @SerializedName("msg")
    val message: String? = null,
    
    @SerializedName("data")
    val data: String? = null
)

/**
 * ID 请求模型
 */
class IdRequest(
    @SerializedName("id")
    val id: String
)

/**
 * IDs 请求模型（批量操作）
 */
class IdsRequest(
    @SerializedName("ids")
    val ids: List<String>
)

/**
 * 搜索请求模型
 */
open class SearchRequest(
    @SerializedName("keyword")
    var keyword: String = "",
    
    @SerializedName("page")
    override var page: Int = 1,
    
    @SerializedName("page_size")
    override var pageSize: Int = 20
) : BaseRequest()

/**
 * 通用结果封装
 */
data class ApiResult<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val errorCode: Int?
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResult<T> {
            return ApiResult(true, message, data, null)
        }
        
        fun <T> error(message: String, errorCode: Int? = null): ApiResult<T> {
            return ApiResult(false, message, null, errorCode)
        }
    }
}