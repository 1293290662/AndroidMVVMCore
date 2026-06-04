/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 可组合的分页管理器
 * 可以在任何 ViewModel 中使用，支持多个分页数据源
 */
package com.github.spadger.mvvmc.pager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 分页数据结果
 */
data class PagedData<T>(
    val data: List<T>,
    val page: Int,
    val totalPage: Int,
    val totalCount: Int,
    val hasMore: Boolean
)

/**
 * 分页加载状态
 */
sealed class PagingLoadState {
    object Idle : PagingLoadState()
    object LoadingFirst : PagingLoadState()
    object LoadingMore : PagingLoadState()
    object Refreshing : PagingLoadState()
    data class Success(val page: Int, val totalCount: Int, val hasMore: Boolean) : PagingLoadState()
    data class Error(val exception: Exception, val message: String? = null) : PagingLoadState()
}

/**
 * 可组合的分页管理器
 * 
 * 使用示例：
 * ```kotlin
 * class MyViewModel : BaseViewModel() {
 *     // 用户列表分页
 *     val userPager = Pager<User>(pageSize = 20) { page, size ->
 *         repository.getUserList(page, size)
 *     }
 *     
 *     // 订单列表分页
 *     val orderPager = Pager<Order>(pageSize = 10) { page, size ->
 *         repository.getOrderList(page, size)
 *     }
 *     
 *     // 不分页的用户详情
 *     val userDetail = MutableLiveData<Result<UserDetail>>()
 * }
 * ```
 */
class Pager<T>(
    private val pageSize: Int = 20,
    private val scope: CoroutineScope,
    private val fetchData: suspend (page: Int, pageSize: Int) -> PagedData<T>
) {
    /** 当前页码 */
    private var currentPage = 1

    /** 是否还有更多数据 */
    private var hasMore = true

    /** 是否正在加载 */
    private var isLoading = false

    /** 分页状态 */
    private val _loadState = MutableLiveData<PagingLoadState>()
    val loadState: LiveData<PagingLoadState> = _loadState

    /** 数据列表 */
    private val _dataList = MutableLiveData<List<T>>()
    val dataList: LiveData<List<T>> = _dataList

    /** 当前页码（只读） */
    val currentPageNumber: Int get() = currentPage

    /** 是否还有更多数据（只读） */
    val hasMoreData: Boolean get() = hasMore

    /** 是否正在加载中（只读） */
    val isLoadingData: Boolean get() = isLoading

    /**
     * 是否可以加载更多
     * @return true 表示可以加载更多，false 表示不能（没有更多数据或正在加载）
     */
    fun canLoadMore(): Boolean = hasMore && !isLoading

    /**
     * 加载第一页数据
     */
    fun loadData() {
        if (isLoading) return
        
        currentPage = 1
        hasMore = true
        isLoading = true
        _loadState.value = PagingLoadState.LoadingFirst
        com.github.spadger.mvvmc.util.LogUtil.d("Pager", "loadData called, currentPage=$currentPage, pageSize=$pageSize")

        scope.launch {
            try {
                val result = fetchData(currentPage, pageSize)
                hasMore = result.hasMore
                _dataList.postValue(result.data)
                _loadState.postValue(PagingLoadState.Success(currentPage, result.totalCount, result.hasMore))
            } catch (e: Exception) {
                _loadState.postValue(PagingLoadState.Error(e, e.message))
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * 加载下一页数据
     */
    fun loadMore() {
        if (!hasMore || isLoading) return

        currentPage++
        isLoading = true
        _loadState.value = PagingLoadState.LoadingMore

        scope.launch {
            try {
                val result = fetchData(currentPage, pageSize)
                hasMore = result.hasMore

                // 追加数据
                val currentData = _dataList.value ?: emptyList()
                val newData = currentData + result.data
                _dataList.postValue(newData)
                _loadState.postValue(PagingLoadState.Success(currentPage, result.totalCount, result.hasMore))
            } catch (e: Exception) {
                // 加载失败，页码回退
                currentPage--
                _loadState.postValue(PagingLoadState.Error(e, e.message))
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * 刷新数据（重新加载第一页）
     */
    fun refresh() {
        if (isLoading) return
        
        isLoading = true
        _loadState.value = PagingLoadState.Refreshing

        scope.launch {
            try {
                currentPage = 1
                hasMore = true
                val result = fetchData(currentPage, pageSize)
                hasMore = result.hasMore
                _dataList.postValue(result.data)
                _loadState.postValue(PagingLoadState.Success(currentPage, result.totalCount, result.hasMore))
            } catch (e: Exception) {
                _loadState.postValue(PagingLoadState.Error(e, e.message))
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * 重置分页状态
     */
    fun reset() {
        currentPage = 1
        hasMore = true
        isLoading = false
        _dataList.value = emptyList()
        _loadState.value = PagingLoadState.Idle
    }

    /**
     * 清空数据
     */
    fun clear() {
        _dataList.value = emptyList()
        _loadState.value = PagingLoadState.Idle
    }
}

/**
 * Pager 工厂函数
 * 方便在 ViewModel 中创建 Pager
 */
inline fun <reified T> CoroutineScope.createPager(
    pageSize: Int = 20,
    crossinline fetchData: suspend (page: Int, pageSize: Int) -> PagedData<T>
): Pager<T> {
    return Pager(pageSize, this) { page, size ->
        fetchData(page, size)
    }
}

/**
 * ViewModel 扩展函数
 * 在 ViewModel 中直接创建 Pager，自动使用 viewModelScope
 */
inline fun <reified T> ViewModel.createPager(
    pageSize: Int = 20,
    crossinline fetchData: suspend (page: Int, pageSize: Int) -> PagedData<T>
): Pager<T> {
    return Pager(pageSize, viewModelScope) { page, size ->
        fetchData(page, size)
    }
}