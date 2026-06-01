/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * View padding and WindowInsets extension functions
 */
package com.github.spadger.mvvmc.ext

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Apply system bar padding to view
 */
fun View.applySystemBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            left = systemBars.left,
            top = systemBars.top,
            right = systemBars.right,
            bottom = systemBars.bottom
        )
        insets
    }
}

/**
 * Apply status bar padding to view
 */
fun View.applyStatusBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        view.updatePadding(top = statusBars.top)
        insets
    }
}

/**
 * Apply navigation bar padding to view
 */
fun View.applyNavigationBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        view.updatePadding(bottom = navigationBars.bottom)
        insets
    }
}

/**
 * Apply system bar margin to view
 */
fun View.applySystemBarMargin() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
        layoutParams?.setMargins(
            systemBars.left,
            systemBars.top,
            systemBars.right,
            systemBars.bottom
        )
        view.layoutParams = layoutParams
        insets
    }
}

/**
 * Set padding for all sides
 */
fun View.setPadding(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

/**
 * Get status bar height for this view's context
 */
fun View.getStatusBarHeight(): Int {
    val windowInsets = ViewCompat.getRootWindowInsets(this)
    return windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
}

/**
 * Get navigation bar height for this view's context
 */
fun View.getNavigationBarHeight(): Int {
    val windowInsets = ViewCompat.getRootWindowInsets(this)
    return windowInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
}

/**
 * Add padding top equals to status bar height
 */
fun View.addStatusBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        view.updatePadding(
            top = view.paddingTop + statusBars.top
        )
        insets
    }
}

/**
 * Add padding bottom equals to navigation bar height
 */
fun View.addNavigationBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        view.updatePadding(
            bottom = view.paddingBottom + navigationBars.bottom
        )
        insets
    }
}
