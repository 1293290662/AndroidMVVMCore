package com.github.spadger.mvvmc

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class AppException(message: String) : Exception(message) {
    class NetworkException(message: String = "网络连接失败") : AppException(message)
    class TimeoutException(message: String = "请求超时") : AppException(message)
    class ServerException(val code: Int, message: String = "服务器错误") : AppException(message)
    class ParseException(message: String = "数据解析失败") : AppException(message)
    class UnknownException(message: String = "未知错误") : AppException(message)
    class BusinessException(val code: Int, message: String) : AppException(message)
}

object ExceptionHandler {
    fun handleException(e: Exception): AppException {
        return when (e) {
            is UnknownHostException -> AppException.NetworkException()
            is SocketTimeoutException -> AppException.TimeoutException()
            is IOException -> AppException.NetworkException()
            is AppException -> e
            else -> AppException.UnknownException()
        }
    }

    fun getErrorMessage(e: Exception): String {
        return handleException(e).message ?: "未知错误"
    }
}