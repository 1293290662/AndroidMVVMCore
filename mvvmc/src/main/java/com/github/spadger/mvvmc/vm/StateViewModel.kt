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
import com.github.spadger.mvvmc.ExceptionHandler
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
open class StateViewModel<T> : ViewModel() {
    private val jobs = mutableListOf<Job>()
    
    private val _state = MutableLiveData<Result<T>>()
    val state: LiveData<Result<T>> = _state
    
    private val _data = MutableLiveData<T>()
    val data: LiveData<T> = _data
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> = _error
    
    open fun setLoading() {
        _state.postValue(Result.Loading)
        _loading.postValue(true)
    }
    
    open fun setSuccess(data: T) {
        _state.postValue(Result.Success(data))
        _data.postValue(data)
        _loading.postValue(false)
        _error.postValue(null)
    }
    
    open fun setError(exception: Exception) {
        _state.postValue(Result.Error(exception))
        _loading.postValue(false)
        _error.postValue(exception)
    }
    
    open fun setIdle() {
        _state.postValue(Result.Idle)
        _loading.postValue(false)
    }
    
    open fun launchOnUI(block: suspend CoroutineScope.() -> Unit): Job {
        val job = viewModelScope.launch(Dispatchers.Main) {
            block()
        }
        jobs.add(job)
        job.invokeOnCompletion { jobs.remove(job) }
        return job
    }
    
    open fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
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
    
    open fun setRefreshing() {
        _refreshing.postValue(true)
        _loading.postValue(true)
    }
    
    open fun setLoadMore() {
        _loadMore.postValue(true)
    }
    
    open fun setListSuccess(list: List<T>, hasMore: Boolean = false) {
        this.hasMore = hasMore
        _refreshing.postValue(false)
        _loadMore.postValue(false)
        _loading.postValue(false)
        _empty.postValue(list.isEmpty())
        
        if (currentPage == 1) {
            _listData.postValue(list.toMutableList())
        } else {
            val currentList = _listData.value?.toMutableList() ?: mutableListOf()
            currentList.addAll(list)
            _listData.postValue(currentList)
        }
    }
    
    open fun setListError(exception: Exception) {
        _refreshing.postValue(false)
        _loadMore.postValue(false)
        _loading.postValue(false)
        _error.postValue(exception)
    }
    
    open fun clearList() {
        currentPage = 1
        _listData.postValue(mutableListOf())
        _empty.postValue(true)
    }
    
    open fun nextPage(): Int {
        return ++currentPage
    }
    
    open fun resetPage() {
        currentPage = 1
    }
    
    open fun launchOnUI(block: suspend CoroutineScope.() -> Unit): Job {
        val job = viewModelScope.launch(Dispatchers.Main) {
            block()
        }
        jobs.add(job)
        job.invokeOnCompletion { jobs.remove(job) }
        return job
    }
    
    open fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
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
