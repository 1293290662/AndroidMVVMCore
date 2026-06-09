package com.github.spadger.mvvmc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseBindingFragment<VB : ViewBinding, VM : ViewModel> : BaseFragment() {

    private var _binding: VB? = null
    protected val mBinding: VB
        get() = _binding ?: throw IllegalStateException(
            "Binding can not be accessed before onCreateView() or after onDestroyView()"
        )

    protected lateinit var mVM: VM

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val superClass = javaClass.genericSuperclass as ParameterizedType

        val classVB = superClass.actualTypeArguments[0] as Class<VB>
        val vbMethod = classVB.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        _binding = vbMethod.invoke(null, inflater, container, false) as VB

        val classVM = superClass.actualTypeArguments[1] as Class<VM>
        mVM = ViewModelProvider(requireActivity()).get(classVM)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserve()
        initListener()
    }

    abstract fun initView()

    abstract fun initObserve()

    open fun initListener() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}