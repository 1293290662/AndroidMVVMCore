package com.github.spadger.mvvmc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<VM : ViewModel> : Fragment() {
    protected lateinit var viewModel: VM
    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = createViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        if (isFirstLoad && !isHidden) {
            isFirstLoad = false
            lazyLoad()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isFirstLoad) {
            isFirstLoad = false
            lazyLoad()
        }
    }

    protected abstract fun createViewModel(): VM

    protected open fun observeViewModel() {}

    protected open fun lazyLoad() {}

    protected inline fun <reified T : ViewModel> getViewModel(): T {
        return ViewModelProvider(this)[T::class.java]
    }

    protected fun showLoading() {}

    protected fun hideLoading() {}

    protected fun showError(message: String) {}
}