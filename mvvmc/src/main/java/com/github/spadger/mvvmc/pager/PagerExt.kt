package com.github.spadger.mvvmc.pager

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun <T> Pager<T>.observeData(
    owner: LifecycleOwner,
    onDataChanged: (List<T>) -> Unit
) {
    dataList.observe(owner) { onDataChanged(it) }
}

fun <T> Pager<T>.observeState(
    owner: LifecycleOwner,
    onLoading: (() -> Unit)? = null,
    onLoadingMore: (() -> Unit)? = null,
    onRefreshing: (() -> Unit)? = null,
    onSuccess: ((page: Int, totalCount: Int, hasMore: Boolean) -> Unit)? = null,
    onError: ((message: String?) -> Unit)? = null
) {
    loadState.observe(owner) { state ->
        when (state) {
            is PagingLoadState.LoadingFirst -> onLoading?.invoke()
            is PagingLoadState.LoadingMore -> onLoadingMore?.invoke()
            is PagingLoadState.Refreshing -> onRefreshing?.invoke()
            is PagingLoadState.Success -> onSuccess?.invoke(state.page, state.totalCount, state.hasMore)
            is PagingLoadState.Error -> onError?.invoke(state.message)
            is PagingLoadState.Idle -> {}
        }
    }
}

fun <T> Pager<T>.setupRecyclerViewPaging(recyclerView: RecyclerView) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && canLoadMore()) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItemPosition >= totalItemCount - 2) {
                    loadMore()
                }
            }
        }
    })
}