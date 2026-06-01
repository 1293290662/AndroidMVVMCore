package com.github.spadger.mvvmc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            Result.Error(ExceptionHandler.handleException(e))
        }
    }

    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.cancel() }
        jobs.clear()
    }
}