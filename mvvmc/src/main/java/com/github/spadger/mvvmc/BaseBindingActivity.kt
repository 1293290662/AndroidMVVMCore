/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * Activity基类，支持 View Binding
 *
 * @param VM ViewModel类型参数
 * @param VB ViewBinding类型参数
 */
package com.github.spadger.mvvmc

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseBindingActivity<VM : ViewModel, VB : ViewBinding> : BaseActivity<VM>() {
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = createViewBinding()
        setContentView(binding.root)
        onViewReady()
    }

    protected abstract fun createViewBinding(): VB
}
