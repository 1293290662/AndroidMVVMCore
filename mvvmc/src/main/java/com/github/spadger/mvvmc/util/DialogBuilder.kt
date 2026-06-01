/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 通用对话框封装类，支持高度自定义和拓展
 */
package com.github.spadger.mvvmc.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.github.spadger.mvvmc.R

/**
 * 通用对话框构建器
 * 支持链式调用，高度自定义
 */
class DialogBuilder(private val context: Context) {
    // 对话框配置
    private var title: String? = null
    private var message: String? = null
    private var positiveText: String = "确定"
    private var negativeText: String = "取消"
    private var neutralText: String? = null
    private var isCancelable: Boolean = true
    private var isCanceledOnTouchOutside: Boolean = true
    
    // 回调监听
    private var positiveListener: (() -> Unit)? = null
    private var negativeListener: (() -> Unit)? = null
    private var neutralListener: (() -> Unit)? = null
    private var dismissListener: (() -> Unit)? = null
    
    // 自定义视图
    private var customView: View? = null
    private var customViewProvider: ((LayoutInflater, ViewGroup) -> View)? = null
    
    // 样式配置
    private var titleColor: Int = Color.parseColor("#333333")
    private var messageColor: Int = Color.parseColor("#666666")
    private var positiveTextColor: Int = Color.parseColor("#1E88E5")
    private var negativeTextColor: Int = Color.parseColor("#999999")
    private var neutralTextColor: Int = Color.parseColor("#666666")
    private var backgroundColor: Int = Color.WHITE
    private var cornerRadius: Float = 12f
    
    // 是否显示按钮
    private var showPositiveButton: Boolean = true
    private var showNegativeButton: Boolean = true
    
    /**
     * 设置标题
     */
    fun setTitle(title: String): DialogBuilder {
        this.title = title
        return this
    }
    
    /**
     * 设置消息内容
     */
    fun setMessage(message: String): DialogBuilder {
        this.message = message
        return this
    }
    
    /**
     * 设置确认按钮文本
     */
    fun setPositiveText(text: String): DialogBuilder {
        this.positiveText = text
        return this
    }
    
    /**
     * 设置取消按钮文本
     */
    fun setNegativeText(text: String): DialogBuilder {
        this.negativeText = text
        return this
    }
    
    /**
     * 设置中立按钮文本（第三个按钮）
     */
    fun setNeutralText(text: String): DialogBuilder {
        this.neutralText = text
        return this
    }
    
    /**
     * 设置是否可取消
     */
    fun setCancelable(cancelable: Boolean): DialogBuilder {
        this.isCancelable = cancelable
        return this
    }
    
    /**
     * 设置点击外部是否可取消
     */
    fun setCanceledOnTouchOutside(canceled: Boolean): DialogBuilder {
        this.isCanceledOnTouchOutside = canceled
        return this
    }
    
    /**
     * 设置确认按钮点击监听
     */
    fun setPositiveListener(listener: () -> Unit): DialogBuilder {
        this.positiveListener = listener
        return this
    }
    
    /**
     * 设置取消按钮点击监听
     */
    fun setNegativeListener(listener: () -> Unit): DialogBuilder {
        this.negativeListener = listener
        return this
    }
    
    /**
     * 设置中立按钮点击监听
     */
    fun setNeutralListener(listener: () -> Unit): DialogBuilder {
        this.neutralListener = listener
        return this
    }
    
    /**
     * 设置对话框关闭监听
     */
    fun setDismissListener(listener: () -> Unit): DialogBuilder {
        this.dismissListener = listener
        return this
    }
    
    /**
     * 设置自定义视图
     */
    fun setCustomView(view: View): DialogBuilder {
        this.customView = view
        return this
    }
    
    /**
     * 设置自定义视图提供者
     */
    fun setCustomViewProvider(provider: (LayoutInflater, ViewGroup) -> View): DialogBuilder {
        this.customViewProvider = provider
        return this
    }
    
    /**
     * 设置标题颜色
     */
    fun setTitleColor(color: Int): DialogBuilder {
        this.titleColor = color
        return this
    }
    
    /**
     * 设置消息颜色
     */
    fun setMessageColor(color: Int): DialogBuilder {
        this.messageColor = color
        return this
    }
    
    /**
     * 设置确认按钮文本颜色
     */
    fun setPositiveTextColor(color: Int): DialogBuilder {
        this.positiveTextColor = color
        return this
    }
    
    /**
     * 设置取消按钮文本颜色
     */
    fun setNegativeTextColor(color: Int): DialogBuilder {
        this.negativeTextColor = color
        return this
    }
    
    /**
     * 设置背景颜色
     */
    fun setBackgroundColor(color: Int): DialogBuilder {
        this.backgroundColor = color
        return this
    }
    
    /**
     * 设置圆角半径
     */
    fun setCornerRadius(radius: Float): DialogBuilder {
        this.cornerRadius = radius
        return this
    }
    
    /**
     * 是否显示确认按钮
     */
    fun showPositiveButton(show: Boolean): DialogBuilder {
        this.showPositiveButton = show
        return this
    }
    
    /**
     * 是否显示取消按钮
     */
    fun showNegativeButton(show: Boolean): DialogBuilder {
        this.showNegativeButton = show
        return this
    }
    
    /**
     * 构建对话框
     */
    fun build(): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        
        // 设置布局
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_base, null)
        dialog.setContentView(view)
        
        // 设置窗口背景透明
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // 设置对话框属性
        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside)
        
        // 绑定视图
        val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
        val tvMessage = view.findViewById<TextView>(R.id.tv_dialog_message)
        val btnPositive = view.findViewById<Button>(R.id.btn_dialog_positive)
        val btnNegative = view.findViewById<Button>(R.id.btn_dialog_negative)
        val btnNeutral = view.findViewById<Button>(R.id.btn_dialog_neutral)
        val flCustomView = view.findViewById<ViewGroup>(R.id.fl_dialog_custom_view)
        
        // 设置标题
        if (title.isNullOrEmpty()) {
            tvTitle.visibility = View.GONE
        } else {
            tvTitle.text = title
            tvTitle.setTextColor(titleColor)
        }
        
        // 设置消息
        if (message.isNullOrEmpty()) {
            tvMessage.visibility = View.GONE
        } else {
            tvMessage.text = message
            tvMessage.setTextColor(messageColor)
        }
        
        // 设置确认按钮
        btnPositive.text = positiveText
        btnPositive.setTextColor(positiveTextColor)
        btnPositive.visibility = if (showPositiveButton) View.VISIBLE else View.GONE
        btnPositive.setOnClickListener {
            positiveListener?.invoke()
            dialog.dismiss()
        }
        
        // 设置取消按钮
        btnNegative.text = negativeText
        btnNegative.setTextColor(negativeTextColor)
        btnNegative.visibility = if (showNegativeButton) View.VISIBLE else View.GONE
        btnNegative.setOnClickListener {
            negativeListener?.invoke()
            dialog.dismiss()
        }
        
        // 设置中立按钮
        if (neutralText.isNullOrEmpty()) {
            btnNeutral.visibility = View.GONE
        } else {
            btnNeutral.text = neutralText
            btnNeutral.setTextColor(neutralTextColor)
            btnNeutral.visibility = View.VISIBLE
            btnNeutral.setOnClickListener {
                neutralListener?.invoke()
                dialog.dismiss()
            }
        }
        
        // 设置自定义视图
        customView?.let {
            flCustomView.addView(it)
        }
        customViewProvider?.let { provider ->
            val custom = provider.invoke(LayoutInflater.from(context), flCustomView)
            flCustomView.addView(custom)
        }
        
        // 设置关闭监听
        dialog.setOnDismissListener {
            dismissListener?.invoke()
        }
        
        return dialog
    }
    
    /**
     * 构建并显示对话框
     */
    fun show(): Dialog {
        val dialog = build()
        dialog.show()
        return dialog
    }
    
    // ==================== 静态便捷方法 ====================
    
    /**
     * 显示简单的确认对话框
     */
    companion object {
        /**
         * 显示简单消息对话框
         */
        fun showMessage(context: Context, message: String) {
            DialogBuilder(context)
                .setMessage(message)
                .showNegativeButton(false)
                .show()
        }
        
        /**
         * 显示标题+消息对话框
         */
        fun showMessage(context: Context, title: String, message: String) {
            DialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .showNegativeButton(false)
                .show()
        }
        
        /**
         * 显示确认对话框（只有确认按钮）
         */
        fun showConfirm(context: Context, message: String, listener: () -> Unit) {
            DialogBuilder(context)
                .setMessage(message)
                .showNegativeButton(false)
                .setPositiveListener(listener)
                .show()
        }
        
        /**
         * 显示确认对话框（有确认和取消按钮）
         */
        fun showConfirm(context: Context, message: String, positiveListener: () -> Unit, negativeListener: () -> Unit) {
            DialogBuilder(context)
                .setMessage(message)
                .setPositiveListener(positiveListener)
                .setNegativeListener(negativeListener)
                .show()
        }
        
        /**
         * 显示确认对话框（带标题）
         */
        fun showConfirm(context: Context, title: String, message: String, positiveListener: () -> Unit, negativeListener: () -> Unit) {
            DialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveListener(positiveListener)
                .setNegativeListener(negativeListener)
                .show()
        }
        
        /**
         * 显示警告对话框
         */
        fun showWarning(context: Context, message: String, positiveListener: () -> Unit) {
            DialogBuilder(context)
                .setTitle("警告")
                .setMessage(message)
                .setPositiveListener(positiveListener)
                .show()
        }
        
        /**
         * 显示错误对话框
         */
        fun showError(context: Context, message: String) {
            DialogBuilder(context)
                .setTitle("错误")
                .setMessage(message)
                .showNegativeButton(false)
                .show()
        }
    }
}