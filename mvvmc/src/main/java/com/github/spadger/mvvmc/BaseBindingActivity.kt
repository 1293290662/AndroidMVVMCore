package com.github.spadger.mvvmc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.github.spadger.mvvmc.util.SystemBarHelper
import java.lang.reflect.ParameterizedType

abstract class BaseBindingActivity<VB : ViewBinding, VM : ViewModel> : BaseActivity() {

    private var _binding: VB? = null
    protected val mBinding: VB
        get() = _binding ?: throw IllegalStateException(
            "Binding can not be accessed before onCreate() or after onDestroy()"
        )

    protected lateinit var mVM: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isFullWindow()) {
            setupImmersionBar()
        }

        initBinding()

        setContentView(mBinding.root)

        initView()
        initObserve()
        initData()
        initListener()
    }

    @Suppress("UNCHECKED_CAST")
    private fun initBinding() {
        val superClass = javaClass.genericSuperclass as ParameterizedType
        
        val classVB = superClass.actualTypeArguments[0] as Class<VB>
        val vbMethod = classVB.getMethod("inflate", LayoutInflater::class.java)
        _binding = vbMethod.invoke(null, layoutInflater) as VB

        val classVM = superClass.actualTypeArguments[1] as Class<VM>
        mVM = ViewModelProvider(this).get(classVM)
    }

    protected open fun setupImmersionBar() {
        SystemBarHelper.setTransparentStatusBar(this)
        SystemBarHelper.setLightStatusBar(this, statusBarDarkFont())
    }

    open fun isFullWindow(): Boolean = false

    open fun statusBarDarkFont(): Boolean = true

    open fun setFullWindow(@ColorRes color: Int = android.R.color.white) {
        SystemBarHelper.setStatusBarColor(this, resources.getColor(color, theme))
        SystemBarHelper.setLightStatusBar(this, color == android.R.color.white)
    }

    abstract fun initView()

    abstract fun initObserve()

    abstract fun initData()

    abstract fun initListener()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}