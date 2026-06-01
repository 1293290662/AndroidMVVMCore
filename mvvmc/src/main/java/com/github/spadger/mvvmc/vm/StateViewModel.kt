/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * ViewModel 基类，带泛型状态管理
 * 提供更简洁的数据状态管理方式
 * 
 * @param T 数据类型
 */
package com.github.spadger.mvvmc.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.spadger.mvvmc.Result
import com.github.spadger.mvvmc.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel 状态包装类
 * 提供更简洁的状态管理 API
 */
class StateViewModel<T> : ViewModel() {
    private val jobs = mutableListOf<Job>()
    
    private val _state = MutableLiveData<Result<T>>()
    val state: LiveData<Result<T>> = _state
    
    private val _data = MutableLiveData<T>()
    val data: LiveData<T> = _data
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> = _error
    
    protected fun setLoading() {
        _state.value = Result.Loading
        _loading.value = true
    }
    
    protected fun setSuccess(data: T) {
        _state.value = Result.Success(data)
        _data.value = data
        _loading.value = false
        _error.value = null
    }
    
    protected fun setError(exception: Exception) {
        _state.value = Result.Error(exception)
        _loading.value = false
        _error.value = exception
    }
    
    protected fun setIdle() {
        _state.value = Result.Idle
        _loading.value = false
    }
    
    protected fun launchOnUI(block: suspend CoroutineScope.() -> Unit): Job {
        val job = viewModelScope.launch(Dispatchers.Main) {
            block()
        }
        jobs.add(job)
        job.invokeOnCompletion { jobs.remove(job) }
        return job
    }
    
    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return launchOnUI {
            withContext(Dispatchers.IO) {
                block()
            }
        }
    }
    
    protected suspend fun <R> safeApiCall(call: suspend () -> R): Result<R> {
        return try {
            Result.Success(call())
        } catch (e: Exception) {
            Result.Error(ExceptionHandler.handleException(e))
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.cancel() }
        jobs.clear()
    }
}

/**
 * 列表 ViewModel 基类
 * 提供分页、刷新等列表常用功能
 */
abstract class ListViewModel<T> : ViewModel() {
    private val jobs = mutableListOf<Job>()
    
    private val _listData = MutableLiveData<MutableList<T>>(mutableListOf())
    val listData: LiveData<MutableList<T>> = _listData
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = _refreshing
    
    private val _loadMore = MutableLiveData<Boolean>()
    val loadMore: LiveData<Boolean> = _loadMore
    
    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> = _error
    
    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean> = _empty
    
    protected var currentPage = 1
    protected var pageSize = 20
    protected var hasMore = true
    
    val isEmpty: Boolean
        get() = _listData.value?.isEmpty() == true
    
    protected fun setRefreshing() {
        _refreshing.value = true
        _loading.value = true
    }
    
    protected fun setLoadMore() {
        _loadMore.value = true
    }
    
    protected fun setListSuccess(list: List<T>, hasMore: Boolean = false) {
        this.hasMore = hasMore
        _refreshing.value = false
        _loadMore.value = false
        _loading.value = false
        _empty.value = list.isEmpty()
        
        if (currentPage == 1) {
            _listData.value = list.toMutableList()
        } else {
            _listData.value?.addAll(list)
            _listData.value = _listData.value
        }
    }
    
    protected fun setListError(exception: Exception) {
        _refreshing.value = false
        _loadMore.value = false
        _loading.value = false
        _error.value = exception
    }
    
    protected fun clearList() {
        currentPage = 1
        _listData.value = mutableListOf()
        _empty.value = true
    }
    
    protected fun nextPage(): Int {
        return ++currentPage
    }
    
    protected fun resetPage() {
        currentPage = 1
    }
    
    protected fun launchOnUI(block: suspend CoroutineScope.() -> Unit): Job {
        val job = viewModelScope.launch(Dispatchers.Main) {
            block()
        }
        jobs.add(job)
        job.invokeOnCompletion { jobs.remove(job) }
        return job
    }
    
    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return launchOnUI {
            withContext(Dispatchers.IO) {
                block()
            }
        }
    }
    
    protected suspend fun <R> safeApiCall(call: suspend () -> R): Result<R> {
        return try {
            Result.Success(call())
        } catch (e: Exception) {
            Result.Error(ExceptionHandler.handleException(e))
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.cancel() }
        jobs.clear()
    }
}

/**
 * 异常处理工具类
 */
object ExceptionHandler {
    fun handleException(e: Exception): Exception {
        LogUtil.e("ExceptionHandler", "Error occurred: ${e.message}", e)
        return when (e) {
            is java.net.UnknownHostException -> Exception("网络连接失败，请检查网络设置")
            is java.net.SocketTimeoutException -> Exception("网络请求超时，请稍后重试")
            is java.io.IOException -> Exception("网络异常，请稍后重试")
            is com.google.gson.JsonParseException -> Exception("数据解析失败")
            is retrofit2.HttpException -> {
                when (e.code()) {
                    400 -> Exception("请求参数错误")
                    401 -> Exception("未授权，请重新登录")
                    403 -> Exception("没有权限访问")
                    404 -> Exception("请求的资源不存在")
                    500 -> Exception("服务器内部错误")
                    else -> Exception("请求失败: ${e.code()}")
                }
            }
            else -> e
        }
    }
}