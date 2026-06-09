package com.github.spadger.mvvmc.demo

import com.github.spadger.mvvmc.BaseBindingActivity
import com.github.spadger.mvvmc.demo.databinding.ActivityMainBinding
import com.github.spadger.mvvmc.demo.fragment.HomeFragment
import com.github.spadger.mvvmc.demo.fragment.MineFragment
import com.github.spadger.mvvmc.demo.vm.DemoViewModel

class MainActivity : BaseBindingActivity<ActivityMainBinding, DemoViewModel>() {

    private val homeFragment = HomeFragment()
    private val mineFragment = MineFragment()

    override fun initView() {
        switchFragment(0)
    }

    override fun initObserve() {
        
    }

    override fun initData() {
        
    }

    override fun initListener() {
        mBinding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    switchFragment(0)
                    true
                }
                R.id.nav_mine -> {
                    switchFragment(1)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(index: Int) {
        val fragment = when (index) {
            0 -> homeFragment
            1 -> mineFragment
            else -> homeFragment
        }

        if (!fragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }

        listOf(homeFragment, mineFragment).forEach { f ->
            supportFragmentManager.beginTransaction()
                .hide(f)
                .commit()
        }

        supportFragmentManager.beginTransaction()
            .show(fragment)
            .commit()
    }
}
