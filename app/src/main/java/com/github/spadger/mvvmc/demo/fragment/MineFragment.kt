package com.github.spadger.mvvmc.demo.fragment

import com.github.spadger.mvvmc.BaseBindingFragment
import com.github.spadger.mvvmc.demo.R
import com.github.spadger.mvvmc.demo.databinding.FragmentMineBinding
import com.github.spadger.mvvmc.demo.vm.DemoViewModel

class MineFragment : BaseBindingFragment<FragmentMineBinding, DemoViewModel>() {

    override fun initView() {
        mBinding.tvTitle.text = "我的"
    }

    override fun initObserve() {
        
    }
}
