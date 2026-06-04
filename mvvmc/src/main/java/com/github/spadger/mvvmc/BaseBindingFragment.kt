/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * Fragment基类，支持 View Binding（自动绑定）和懒加载
 */
package com.github.spadger.mvvmc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseBindingFragment<VM : ViewModel, VB : ViewBinding> : BaseFragment<VM>() {

    protected lateinit var mContext: Context
    protected lateinit var mActivity: BaseActivity<*>

    private var _binding: VB? = null
    protected val mBinding: VB
        get() = _binding ?: throw IllegalStateException(
            "Binding can not be accessed before onCreateView() or after onDestroyView()"
        )

    protected lateinit var mVM: VM

    private var isFirstLoad = true
    private var isViewCreated = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as BaseActivity<*>
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beforeInit()

        val superClass = javaClass.genericSuperclass as ParameterizedType

        val classVB = superClass.actualTypeArguments[1] as Class<VB>
        val vbMethod = classVB.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        _binding = vbMethod.invoke(null, inflater, container, false) as VB

        return mBinding.root
    }

    @Suppress("UNCHECKED_CAST")
    override fun createViewModel(): VM {
        val superClass = javaClass.genericSuperclass as ParameterizedType
        val classVM = superClass.actualTypeArguments[0] as Class<VM>
        mVM = ViewModelProvider(requireActivity()).get(classVM)
        return mVM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true

        initView()
        initObserve()
        initListener()

        if (isFirstLoad && !isHidden) {
            isFirstLoad = false
            initData()
        }

        afterInit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isViewCreated && isFirstLoad) {
            isFirstLoad = false
            initData()
        }
    }

    override fun initView() {}

    abstract fun initObserve()

    override fun initData() {}

    override fun initListener() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}