/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * ViewModel基类，提供协程调度、网络请求安全处理和生命周期管理
 */
package com.github.spadger.mvvmc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.spadger.mvvmc.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    // ==================== Log 便捷方法 ====================

    /**
     * 输出调试日志
     * @param message 消息内容
     */
    protected fun logD(message: String) {
        LogUtil.d(javaClass.simpleName, message)
    }

    /**
     * 输出信息日志
     * @param message 消息内容
     */
    protected fun logI(message: String) {
        LogUtil.i(javaClass.simpleName, message)
    }

    /**
     * 输出警告日志
     * @param message 消息内容
     */
    protected fun logW(message: String) {
        LogUtil.w(javaClass.simpleName, message)
    }

    /**
     * 输出错误日志
     * @param message 消息内容
     */
    protected fun logE(message: String) {
        LogUtil.e(javaClass.simpleName, message)
    }

    /**
     * 输出错误日志（包含异常）
     * @param message 消息内容
     * @param throwable 异常
     */
    protected fun logE(message: String, throwable: Throwable) {
        LogUtil.e(javaClass.simpleName, message, throwable)
    }
}