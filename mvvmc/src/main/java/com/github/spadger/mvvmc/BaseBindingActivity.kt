/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * Activity基类，支持 View Binding（自动绑定）和沉浸式状态栏
 */
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

abstract class BaseBindingActivity<VM : ViewModel, VB : ViewBinding> : BaseActivity<VM>() {

    protected lateinit var mActivity: BaseBindingActivity<VM, VB>
    protected lateinit var mContext: Context

    private var _binding: VB? = null
    protected val mBinding: VB
        get() = _binding ?: throw IllegalStateException(
            "Binding can not be accessed before onCreate() or after onDestroy()"
        )

    protected lateinit var mVM: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        mActivity = this

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
        val classVB = superClass.actualTypeArguments[1] as Class<VB>
        val vbMethod = classVB.getMethod("inflate", LayoutInflater::class.java)
        _binding = vbMethod.invoke(null, layoutInflater) as VB
    }

    @Suppress("UNCHECKED_CAST")
    override fun createViewModel(): VM {
        val superClass = javaClass.genericSuperclass as ParameterizedType
        val classVM = superClass.actualTypeArguments[0] as Class<VM>
        mVM = ViewModelProvider(this).get(classVM)
        return mVM
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

    override fun initView() {}

    abstract fun initObserve()

    override fun initData() {}

    override fun initListener() {}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}