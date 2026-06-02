/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 通用结果封装类，用于统一处理成功、失败、加载中等状态
 * 
 * @param T 数据类型参数
 */
package com.github.spadger.mvvmc

sealed class Result<out T> {
    /**
     * 成功状态，包含数据
     */
    data class Success<out T>(val data: T) : Result<T>()
    
    /**
     * 错误状态，包含异常信息
     */
    data class Error(val err: Exception) : Result<Nothing>()
    
    /**
     * 加载中状态
     */
    object Loading : Result<Nothing>()
    
    /**
     * 空闲状态（未开始）
     */
    object Idle : Result<Nothing>()

    fun isSuccess(): Boolean = this is Success
    
    fun isError(): Boolean = this is Error
    
    fun isLoading(): Boolean = this is Loading
    
    fun isIdle(): Boolean = this is Idle

    fun getOrNull(): T? = (this as? Success)?.data

    fun getException(): Exception? = (this as? Error)?.err

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(err)
        return this
    }

    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}
