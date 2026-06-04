/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * ViewModel 基类，提供协程调度、网络请求安全处理和生命周期管理
 */
package com.github.spadger.mvvmc.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.spadger.mvvmc.Result
import com.github.spadger.mvvmc.model.BaseData
import com.github.spadger.mvvmc.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.spadger.mvvmc.ext.VmLiveData
import com.github.spadger.mvvmc.ext.paresVmException
import com.github.spadger.mvvmc.ext.paresVmResult

open class BaseViewModel : ViewModel() {
    private val jobs = mutableListOf<Job>()

    protected fun launchOnUI(block: suspend CoroutineScope.() -> Unit): Job {
        val job = viewModelScope.launch(Dispatchers.Main) {
            block()
        }
        jobs.add(job)
        job.invokeOnCompletion {
            jobs.remove(job)
        }
        return job
    }

    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return launchOnUI {
            withContext(Dispatchers.IO) {
                block()
            }
        }
    }

    protected suspend fun <T> safeApiCall(call: suspend () -> Result<T>): Result<T> {
        return try {
            call()
        } catch (e: Exception) {
            LogUtil.e("BaseViewModel", "API call failed: ${e.message}", e)
            Result.Error(e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.cancel() }
        jobs.clear()
    }
}

fun <T> BaseViewModel.launchVmRequest(
    request: suspend () -> BaseData<T>,
    viewState: VmLiveData<T>
) {
    viewModelScope.launch {
        runCatching {
            viewState.value = Result.Loading
            request()
        }.onSuccess {
            viewState.paresVmResult(it)
        }.onFailure {
            LogUtil.d("BaseViewModel", "Request failed: ${it.message}")
            viewState.paresVmException(it)
        }
    }
}