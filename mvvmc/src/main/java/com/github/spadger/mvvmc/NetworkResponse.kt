package com.github.spadger.mvvmc

data class NetworkResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    fun isSuccess(): Boolean = code == 200

    fun toResult(): Result<T> {
        return if (isSuccess()) {
            data?.let { Result.Success(it) } ?: Result.Error(AppException.ParseException())
        } else {
            Result.Error(AppException.BusinessException(code, message))
        }
    }
}