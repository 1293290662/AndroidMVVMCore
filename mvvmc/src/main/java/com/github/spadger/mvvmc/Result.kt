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
    data class Error(val exception: Exception) : Result<Nothing>()
    
    /**
     * 加载中状态
     */
    object Loading : Result<Nothing>()
    
    /**
     * 空闲状态（未开始）
     */
    object Idle : Result<Nothing>()

    /**
     * 判断是否为成功状态
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * 判断是否为错误状态
     */
    fun isError(): Boolean = this is Error
    
    /**
     * 判断是否为加载中状态
     */
    fun isLoading(): Boolean = this is Loading
    
    /**
     * 判断是否为空闲状态
     */
    fun isIdle(): Boolean = this is Idle

    /**
     * 获取成功数据，失败返回null
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * 获取异常信息，非错误状态返回null
     */
    fun getException(): Exception? = (this as? Error)?.exception

    /**
     * 成功时执行操作，链式调用
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * 错误时执行操作，链式调用
     */
    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }

    /**
     * 加载中时执行操作，链式调用
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}