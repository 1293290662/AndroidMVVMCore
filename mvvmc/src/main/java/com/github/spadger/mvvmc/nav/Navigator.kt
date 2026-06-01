/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 导航管理类，封装 Navigation 组件的常用操作
 */
package com.github.spadger.mvvmc.nav

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

/**
 * 导航管理类
 */
object Navigator {

    /**
     * 导航到指定目标
     * @param fragment 当前 Fragment
     * @param resId 目标目的地 ID
     * @param args 传递的参数
     * @param navOptions 导航选项
     */
    fun navigate(
        fragment: Fragment,
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null
    ) {
        try {
            fragment.findNavController().navigate(resId, args, navOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 使用 NavDirections 导航
     * @param fragment 当前 Fragment
     * @param directions 导航方向
     * @param navOptions 导航选项
     */
    fun navigate(
        fragment: Fragment,
        directions: NavDirections,
        navOptions: NavOptions? = null
    ) {
        try {
            fragment.findNavController().navigate(directions, navOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 返回上一级
     * @param fragment 当前 Fragment
     */
    fun popBackStack(fragment: Fragment) {
        try {
            fragment.findNavController().popBackStack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 返回指定目的地
     * @param fragment 当前 Fragment
     * @param destinationId 目标目的地 ID
     * @param inclusive 是否包含目标目的地
     */
    fun popBackStack(
        fragment: Fragment,
        @IdRes destinationId: Int,
        inclusive: Boolean
    ) {
        try {
            fragment.findNavController().popBackStack(destinationId, inclusive)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 返回栈底
     * @param fragment 当前 Fragment
     */
    fun popToRoot(fragment: Fragment) {
        try {
            fragment.findNavController().popBackStack(
                fragment.findNavController().graph.startDestinationId,
                false
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取 NavController
     * @param fragment 当前 Fragment
     * @return NavController 实例
     */
    fun getNavController(fragment: Fragment): NavController? {
        return try {
            fragment.findNavController()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 判断是否可以返回
     * @param fragment 当前 Fragment
     * @return 是否可以返回
     */
    fun canGoBack(fragment: Fragment): Boolean {
        return try {
            fragment.findNavController().previousBackStackEntry != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}