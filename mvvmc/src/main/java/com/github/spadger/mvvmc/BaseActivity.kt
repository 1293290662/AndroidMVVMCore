package com.github.spadger.mvvmc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        observeViewModel()
    }

    protected abstract fun createViewModel(): VM

    protected open fun observeViewModel() {}

    protected inline fun <reified T : ViewModel> getViewModel(): T {
        return ViewModelProvider(this)[T::class.java]
    }

    protected fun showLoading() {}

    protected fun hideLoading() {}

    protected fun showError(message: String) {}
}