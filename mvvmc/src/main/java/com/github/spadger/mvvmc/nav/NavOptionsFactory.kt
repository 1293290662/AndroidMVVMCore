/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 导航选项配置类，提供常用的导航动画配置
 */
package com.github.spadger.mvvmc.nav

import androidx.navigation.NavOptions
import com.github.spadger.mvvmc.R

/**
 * 导航选项配置类
 */
object NavOptionsFactory {

    /**
     * 默认导航选项（无动画）
     */
    fun default(): NavOptions {
        return NavOptions.Builder().build()
    }

    /**
     * 从右侧进入的动画
     */
    fun slideInFromRight(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(android.R.anim.slide_in_left)
            .setExitAnim(android.R.anim.slide_out_right)
            .setPopEnterAnim(android.R.anim.slide_in_left)
            .setPopExitAnim(android.R.anim.slide_out_right)
            .build()
    }

    /**
     * 从底部进入的动画（适用于底部弹窗）
     */
    fun slideInFromBottom(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_up)
            .setExitAnim(R.anim.slide_out_down)
            .setPopEnterAnim(R.anim.slide_in_up)
            .setPopExitAnim(R.anim.slide_out_down)
            .build()
    }

    /**
     * 淡入淡出动画
     */
    fun fadeInFadeOut(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(android.R.anim.fade_in)
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(android.R.anim.fade_in)
            .setPopExitAnim(android.R.anim.fade_out)
            .build()
    }

    /**
     * 缩放动画
     */
    fun scaleIn(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.scale_in)
            .setExitAnim(R.anim.scale_out)
            .setPopEnterAnim(R.anim.scale_in)
            .setPopExitAnim(R.anim.scale_out)
            .build()
    }

    /**
     * 清除当前栈并导航（适用于登录后跳转首页）
     */
    fun clearBackStack(): NavOptions {
        return NavOptions.Builder()
            .setPopUpTo(0, true)
            .build()
    }

    /**
     * 清除当前栈并导航（带有动画）
     */
    fun clearBackStackWithAnim(): NavOptions {
        return NavOptions.Builder()
            .setPopUpTo(0, true)
            .setEnterAnim(android.R.anim.slide_in_left)
            .setExitAnim(android.R.anim.slide_out_right)
            .build()
    }

    /**
     * 自定义导航选项构建器
     */
    fun custom(): NavOptions.Builder {
        return NavOptions.Builder()
    }
}
