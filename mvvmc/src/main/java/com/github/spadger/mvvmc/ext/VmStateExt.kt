/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * ViewModel 状态封装
 */
package com.github.spadger.mvvmc.ext

import androidx.lifecycle.MutableLiveData
import com.github.spadger.mvvmc.model.BaseData
import com.github.spadger.mvvmc.Result

typealias VmLiveData<T> = MutableLiveData<Result<T>>

fun <T> VmLiveData<T>.paresVmResult(data: BaseData<T>) {
    if (data.isSuccess) {
        val resultData = data.data
        if (resultData != null) {
            this.value = Result.Success(resultData)
        } else {
            this.value = Result.Error(Exception(data.message ?: "数据为空"))
        }
    } else {
        this.value = Result.Error(Exception(data.message ?: "请求失败"))
    }
}

fun <T> VmLiveData<T>.paresVmException(e: Throwable) {
    val exception = if (e is Exception) e else Exception(e.message ?: "未知错误", e)
    this.value = Result.Error(exception)
}