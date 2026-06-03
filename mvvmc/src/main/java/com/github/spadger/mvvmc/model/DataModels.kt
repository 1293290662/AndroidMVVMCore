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
    open var page: Int = 1

    @SerializedName("page_size")
    open var pageSize: Int = 20
}

/**
 * 基础响应模型
 */
open class BaseResponse<T>(
    @SerializedName("Active")
    open val active: Boolean,

    @SerializedName("code")
    open val code: Int = 0,

    @SerializedName("msg")
    open override val message: String? = null,

    @SerializedName("Obj")
    open override val data: T? = null
) : BaseData<T> {
    override val isSuccess: Boolean get() = code == 0 || code == 200 || active

    fun getErrorMessage(): String = message ?: "请求失败"
}

/**
 * 分页响应模型
 */
data class PageResponse<T>(
    @SerializedName("Active")
    override val active: Boolean,

    @SerializedName("code")
    override val code: Int = 0,

    @SerializedName("msg")
    override val message: String? = null,

    @SerializedName("Obj")
    override val data: PageData<T>? = null
) : BaseResponse<PageData<T>>(active, code, message, data)

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
 * ID 请求模型
 */
class IdRequest(
    @SerializedName("id")
    val id: String
)

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
