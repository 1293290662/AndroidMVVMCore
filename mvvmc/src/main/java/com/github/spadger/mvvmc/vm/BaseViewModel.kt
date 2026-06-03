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
import com.github.spadger.mvvmc.model.BaseData
import com.github.spadger.mvvmc.ExceptionHandler
import com.github.spadger.mvvmc.Result
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
    /**
     * 存储所有协程Job，用于ViewModel销毁时取消所有任务
     */
    private val jobs = mutableListOf<Job>()

    /**
     * 在UI线程执行协程任务
     * @param block 协程执行的代码块
     * @return Job实例，可用于取消任务
     */
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

    /**
     * 在IO线程执行协程任务，自动切换到主线程
     * @param block 协程执行的代码块
     * @return Job实例，可用于取消任务
     */
    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return launchOnUI {
            withContext(Dispatchers.IO) {
                block()
            }
        }
    }

    /**
     * 安全的API调用方法，自动捕获异常并转换为Result类型
     * @param call 网络请求调用
     * @return Result<T>包装的结果
     */
    protected suspend fun <T> safeApiCall(call: suspend () -> Result<T>): Result<T> {
        return try {
            call()
        } catch (e: Exception) {
            Result.Error(ExceptionHandler.handleException(e))
        }
    }

    /**
     * ViewModel销毁时调用，取消所有正在执行的协程任务
     */
    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.cancel() }
        jobs.clear()
    }

}

/**
 * BaseViewModel 开启协程扩展
 * 简化网络请求调用，自动处理 Loading/Success/Error 状态
 *
 * @param request 网络请求挂起函数
 * @param viewState 用于接收结果的 LiveData
 */
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
