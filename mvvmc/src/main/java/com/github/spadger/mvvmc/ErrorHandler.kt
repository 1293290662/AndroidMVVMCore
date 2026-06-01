/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 异常处理类，统一处理各种异常类型并转换为AppException
 */
package com.github.spadger.mvvmc

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 应用异常密封类，定义各种业务异常类型
 */
sealed class AppException(message: String) : Exception(message) {
    /**
     * 网络连接异常
     */
    class NetworkException(message: String = "网络连接失败") : AppException(message)
    
    /**
     * 请求超时异常
     */
    class TimeoutException(message: String = "请求超时") : AppException(message)
    
    /**
     * 服务器错误异常
     */
    class ServerException(val code: Int, message: String = "服务器错误") : AppException(message)
    
    /**
     * 数据解析异常
     */
    class ParseException(message: String = "数据解析失败") : AppException(message)
    
    /**
     * 未知错误异常
     */
    class UnknownException(message: String = "未知错误") : AppException(message)
    
    /**
     * 业务逻辑异常
     */
    class BusinessException(val code: Int, message: String) : AppException(message)
}

/**
 * 异常处理器，负责将各种异常转换为统一的AppException
 */
object ExceptionHandler {
    /**
     * 处理异常，将原始异常转换为AppException
     * @param e 原始异常
     * @return AppException类型的异常
     */
    fun handleException(e: Exception): AppException {
        return when (e) {
            is UnknownHostException -> AppException.NetworkException()
            is SocketTimeoutException -> AppException.TimeoutException()
            is IOException -> AppException.NetworkException()
            is AppException -> e
            else -> AppException.UnknownException()
        }
    }

    /**
     * 获取异常的错误消息
     * @param e 异常
     * @return 错误消息字符串
     */
    fun getErrorMessage(e: Exception): String {
        return handleException(e).message ?: "未知错误"
    }
}