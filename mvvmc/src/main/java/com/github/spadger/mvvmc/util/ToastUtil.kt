/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Toast封装类，统一Toast显示管理
 */
package com.github.spadger.mvvmc.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ToastUtil {
    /**
     * Toast实例，用于防止重复显示
     */
    private var toast: Toast? = null

    /**
     * Handler用于确保在主线程显示Toast
     */
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 显示短时间Toast
     * @param context 上下文
     * @param message 消息内容
     */
    fun showShort(context: Context, message: String) {
        showToast(context, message, Toast.LENGTH_SHORT)
    }

    /**
     * 显示短时间Toast（资源ID）
     * @param context 上下文
     * @param resId 字符串资源ID
     */
    fun showShort(context: Context, resId: Int) {
        try {
            val message = context.getString(resId)
            showToast(context, message, Toast.LENGTH_SHORT)
        } catch (e: Exception) {
            LogUtil.e("ToastUtil", "Invalid string resource ID: $resId", e)
        }
    }

    /**
     * 显示长时间Toast
     * @param context 上下文
     * @param message 消息内容
     */
    fun showLong(context: Context, message: String) {
        showToast(context, message, Toast.LENGTH_LONG)
    }

    /**
     * 显示长时间Toast（资源ID）
     * @param context 上下文
     * @param resId 字符串资源ID
     */
    fun showLong(context: Context, resId: Int) {
        try {
            val message = context.getString(resId)
            showToast(context, message, Toast.LENGTH_LONG)
        } catch (e: Exception) {
            LogUtil.e("ToastUtil", "Invalid string resource ID: $resId", e)
        }
    }

    /**
     * 在UI线程显示短时间Toast
     * @param context 上下文
     * @param message 消息内容
     */
    fun showShortOnUiThread(context: Context, message: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showShort(context, message)
        } else {
            handler.post { showShort(context, message) }
        }
    }

    /**
     * 在UI线程显示长时间Toast
     * @param context 上下文
     * @param message 消息内容
     */
    fun showLongOnUiThread(context: Context, message: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showLong(context, message)
        } else {
            handler.post { showLong(context, message) }
        }
    }

    /**
     * 显示成功提示Toast（绿色主题）
     * @param context 上下文
     * @param message 消息内容
     */
    fun showSuccess(context: Context, message: String) {
        showShort(context, message)
    }

    /**
     * 显示错误提示Toast（红色主题）
     * @param context 上下文
     * @param message 消息内容
     */
    fun showError(context: Context, message: String) {
        showShort(context, message)
    }

    /**
     * 显示警告提示Toast（黄色主题）
     * @param context 上下文
     * @param message 消息内容
     */
    fun showWarning(context: Context, message: String) {
        showShort(context, message)
    }

    /**
     * 显示信息提示Toast（蓝色主题）
     * @param context 上下文
     * @param message 消息内容
     */
    fun showInfo(context: Context, message: String) {
        showShort(context, message)
    }

    /**
     * 取消当前显示的Toast
     */
    fun cancel() {
        toast?.cancel()
        toast = null
    }

    /**
     * 内部方法：显示Toast
     * @param context 上下文
     * @param message 消息内容
     * @param duration 显示时长
     */
    private fun showToast(context: Context, message: String, duration: Int) {
        // 取消之前的Toast，避免重复显示
        cancel()

        // 使用Application Context防止内存泄漏
        val appContext = context.applicationContext
        toast = Toast.makeText(appContext, message, duration)
        toast?.show()
    }
}