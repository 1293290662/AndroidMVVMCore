/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 网络响应封装类，统一处理API返回数据
 * 
 * @param T 数据类型参数
 */
package com.github.spadger.mvvmc

data class NetworkResponse<T>(
    /**
     * 状态码
     */
    val code: Int,
    /**
     * 响应消息
     */
    val message: String,
    /**
     * 响应数据
     */
    val data: T?
) {
    /**
     * 判断请求是否成功（状态码为200）
     * @return 是否成功
     */
    fun isSuccess(): Boolean = code == 200

    /**
     * 将NetworkResponse转换为Result类型
     * @return Result包装的结果
     */
    fun toResult(): Result<T> {
        return if (isSuccess()) {
            data?.let { Result.Success(it) } ?: Result.Error(AppException.ParseException())
        } else {
            Result.Error(AppException.BusinessException(code, message))
        }
    }
}