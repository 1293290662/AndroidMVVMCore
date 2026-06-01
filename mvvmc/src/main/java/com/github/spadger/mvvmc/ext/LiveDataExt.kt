/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * LiveData 扩展函数封装，提供更简洁的观察API
 */
package com.github.spadger.mvvmc.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.spadger.mvvmc.Result
import com.github.spadger.mvvmc.util.LogUtil

/**
 * 简化的 LiveData 观察方法
 * @param owner LifecycleOwner
 * @param onChanged 数据变化回调
 */
fun <T> LiveData<T>.observe(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    observe(owner, Observer { onChanged(it) })
}

/**
 * 简化的 LiveData 观察方法（可空数据）
 * @param owner LifecycleOwner
 * @param onChanged 数据变化回调
 */
fun <T> LiveData<T?>.observeNullable(owner: LifecycleOwner, onChanged: (T?) -> Unit) {
    observe(owner, Observer { onChanged(it) })
}

/**
 * 观察 Result 类型的 LiveData
 * 自动处理 Success、Error、Loading 状态
 * @param owner LifecycleOwner
 * @param onSuccess 成功回调
 * @param onError 错误回调（可选）
 * @param onLoading 加载中回调（可选）
 * @param onIdle 空闲状态回调（可选）
 */
fun <T> LiveData<Result<T>>.observeResult(
    owner: LifecycleOwner,
    onSuccess: (T) -> Unit,
    onError: ((Exception) -> Unit)? = null,
    onLoading: (() -> Unit)? = null,
    onIdle: (() -> Unit)? = null
) {
    observe(owner) { result ->
        when (result) {
            is Result.Success -> {
                LogUtil.d("LiveData", "Success: ${result.data}")
                onSuccess(result.data)
            }
            is Result.Error -> {
                LogUtil.e("LiveData", "Error: ${result.exception.message}", result.exception)
                onError?.invoke(result.exception)
            }
            is Result.Loading -> {
                LogUtil.d("LiveData", "Loading...")
                onLoading?.invoke()
            }
            is Result.Idle -> {
                LogUtil.d("LiveData", "Idle")
                onIdle?.invoke()
            }
        }
    }
}

/**
 * 观察 Result 类型的 LiveData（简化版，只处理成功和错误）
 * @param owner LifecycleOwner
 * @param onSuccess 成功回调
 * @param onError 错误回调
 */
fun <T> LiveData<Result<T>>.observeSimple(
    owner: LifecycleOwner,
    onSuccess: (T) -> Unit,
    onError: (Exception) -> Unit
) {
    observeResult(owner, onSuccess, onError)
}

/**
 * 观察 Result 类型的 LiveData（带加载状态）
 * @param owner LifecycleOwner
 * @param onSuccess 成功回调
 * @param onError 错误回调
 * @param onLoading 加载中回调
 */
fun <T> LiveData<Result<T>>.observeWithLoading(
    owner: LifecycleOwner,
    onSuccess: (T) -> Unit,
    onError: (Exception) -> Unit,
    onLoading: () -> Unit
) {
    observeResult(owner, onSuccess, onError, onLoading)
}

/**
 * 观察非空数据的 LiveData
 * 如果数据为 null 则不回调
 * @param owner LifecycleOwner
 * @param onChanged 数据变化回调（数据非空时）
 */
fun <T> LiveData<T?>.observeNotNull(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    observe(owner) { data ->
        data?.let { onChanged(it) }
    }
}

/**
 * 观察非空数据的 LiveData（带默认值）
 * 如果数据为 null 则使用默认值
 * @param owner LifecycleOwner
 * @param defaultValue 默认值
 * @param onChanged 数据变化回调
 */
fun <T> LiveData<T?>.observeWithDefault(owner: LifecycleOwner, defaultValue: T, onChanged: (T) -> Unit) {
    observe(owner) { data ->
        onChanged(data ?: defaultValue)
    }
}

/**
 * 只观察一次数据变化
 * @param owner LifecycleOwner
 * @param onChanged 数据变化回调
 */
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            onChanged(value)
            removeObserver(this)
        }
    }
    observe(owner, observer)
}

/**
 * 过滤重复数据的观察
 * 只有当数据真正变化时才回调
 * @param owner LifecycleOwner
 * @param onChanged 数据变化回调
 */
fun <T> LiveData<T>.observeDistinct(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    var previousValue: T? = null
    observe(owner) { currentValue ->
        if (previousValue != currentValue) {
            previousValue = currentValue
            onChanged(currentValue)
        }
    }
}

/**
 * 观察数据并自动处理日志
 * @param owner LifecycleOwner
 * @param tag 日志标签
 * @param onChanged 数据变化回调
 */
fun <T> LiveData<T>.observeWithLog(owner: LifecycleOwner, tag: String, onChanged: (T) -> Unit) {
    observe(owner) { data ->
        LogUtil.d(tag, "Data changed: $data")
        onChanged(data)
    }
}