/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Repository基类，提供API服务创建和网络请求封装
 */
package com.github.spadger.mvvmc

import com.github.spadger.mvvmc.network.RetrofitProvider

open class BaseRepository {
    /**
     * 创建API服务接口实例
     * @return 指定类型的API服务实例
     */
    protected inline fun <reified T> createApi(): T {
        return RetrofitProvider.createService(T::class.java)
    }

    /**
     * 封装网络请求，自动处理异常并转换为Result类型
     * @param call 网络请求调用
     * @return Result<T>包装的结果
     */
    protected suspend fun <T> apiCall(call: suspend () -> NetworkResponse<T>): Result<T> {
        return try {
            val response = call()
            response.toResult()
        } catch (e: Exception) {
            Result.Error(ExceptionHandler.handleException(e))
        }
    }
}