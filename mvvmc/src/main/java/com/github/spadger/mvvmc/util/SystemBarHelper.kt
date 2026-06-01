/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * System Bar (Status Bar & Navigation Bar) helper utilities
 */
package com.github.spadger.mvvmc.util

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object SystemBarHelper {

    /**
     * Enable Edge-to-Edge mode
     */
    fun enableEdgeToEdge(activity: Activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    }

    /**
     * Set status bar color
     */
    fun setStatusBarColor(activity: Activity, @ColorInt color: Int) {
        activity.window.statusBarColor = color
    }

    /**
     * Set navigation bar color
     */
    fun setNavigationBarColor(activity: Activity, @ColorInt color: Int) {
        activity.window.navigationBarColor = color
    }

    /**
     * Set transparent status bar
     */
    fun setTransparentStatusBar(activity: Activity) {
        setStatusBarColor(activity, Color.TRANSPARENT)
    }

    /**
     * Set transparent navigation bar
     */
    fun setTransparentNavigationBar(activity: Activity) {
        setNavigationBarColor(activity, Color.TRANSPARENT)
    }

    /**
     * Set both status bar and navigation bar transparent
     */
    fun setTransparentSystemBars(activity: Activity) {
        setTransparentStatusBar(activity)
        setTransparentNavigationBar(activity)
    }

    /**
     * Hide system bars (status bar & navigation bar)
     */
    fun hideSystemBars(activity: Activity) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     * Show system bars
     */
    fun showSystemBars(activity: Activity) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)?.show(
            WindowInsetsCompat.Type.systemBars()
        )
    }

    /**
     * Set light status bar (dark icons)
     */
    fun setLightStatusBar(activity: Activity, isLight: Boolean) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)?.let {
            it.isAppearanceLightStatusBars = isLight
        }
    }

    /**
     * Set light navigation bar (dark icons)
     */
    fun setLightNavigationBar(activity: Activity, isLight: Boolean) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)?.let {
            it.isAppearanceLightNavigationBars = isLight
        }
    }

    /**
     * Set light system bars
     */
    fun setLightSystemBars(activity: Activity, isLight: Boolean) {
        setLightStatusBar(activity, isLight)
        setLightNavigationBar(activity, isLight)
    }

    /**
     * Get status bar height in pixels
     */
    fun getStatusBarHeight(activity: Activity): Int {
        val windowInsets = WindowCompat.getInsetsController(activity.window, activity.window.decorView)?.let {
            WindowInsetsCompat.toWindowInsetsCompat(activity.window.decorView.rootWindowInsets)
        }
        return windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
    }

    /**
     * Get navigation bar height in pixels
     */
    fun getNavigationBarHeight(activity: Activity): Int {
        val windowInsets = WindowCompat.getInsetsController(activity.window, activity.window.decorView)?.let {
            WindowInsetsCompat.toWindowInsetsCompat(activity.window.decorView.rootWindowInsets)
        }
        return windowInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
    }
}
