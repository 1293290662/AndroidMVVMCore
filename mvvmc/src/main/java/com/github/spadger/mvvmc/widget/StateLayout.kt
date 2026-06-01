/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 状态管理布局，支持加载中、空状态、错误状态、网络异常等状态切换
 */
package com.github.spadger.mvvmc.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.github.spadger.mvvmc.R

class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 状态枚举
     */
    enum class State {
        CONTENT,      // 内容状态
        LOADING,      // 加载中
        EMPTY,        // 空状态
        ERROR,        // 错误状态
        NO_NETWORK    // 无网络
    }

    private lateinit var contentView: View
    private lateinit var loadingView: View
    private lateinit var emptyView: View
    private lateinit var errorView: View
    private lateinit var noNetworkView: View

    private var currentState: State = State.CONTENT

    init {
        initViews()
    }

    private fun initViews() {
        // 创建加载中视图
        loadingView = LayoutInflater.from(context).inflate(R.layout.layout_state_loading, this, false)
        addView(loadingView)

        // 创建空状态视图
        emptyView = LayoutInflater.from(context).inflate(R.layout.layout_state_empty, this, false)
        addView(emptyView)

        // 创建错误视图
        errorView = LayoutInflater.from(context).inflate(R.layout.layout_state_error, this, false)
        addView(errorView)

        // 创建无网络视图
        noNetworkView = LayoutInflater.from(context).inflate(R.layout.layout_state_no_network, this, false)
        addView(noNetworkView)

        // 默认隐藏所有状态视图
        loadingView.visibility = View.GONE
        emptyView.visibility = View.GONE
        errorView.visibility = View.GONE
        noNetworkView.visibility = View.GONE
    }

    /**
     * 设置内容视图
     */
    fun setContentView(view: View) {
        contentView = view
        addView(view)
    }

    /**
     * 切换到指定状态
     */
    fun setState(state: State) {
        currentState = state
        updateVisibility()
    }

    /**
     * 获取当前状态
     */
    fun getState(): State = currentState

    /**
     * 显示内容
     */
    fun showContent() {
        setState(State.CONTENT)
    }

    /**
     * 显示加载中
     */
    fun showLoading() {
        setState(State.LOADING)
    }

    /**
     * 显示空状态
     */
    fun showEmpty() {
        setState(State.EMPTY)
    }

    /**
     * 显示空状态（带自定义文案）
     */
    fun showEmpty(message: String) {
        setState(State.EMPTY)
        emptyView.findViewById<TextView>(R.id.tv_empty_message)?.text = message
    }

    /**
     * 显示错误状态
     */
    fun showError() {
        setState(State.ERROR)
    }

    /**
     * 显示错误状态（带自定义文案）
     */
    fun showError(message: String) {
        setState(State.ERROR)
        errorView.findViewById<TextView>(R.id.tv_error_message)?.text = message
    }

    /**
     * 显示无网络状态
     */
    fun showNoNetwork() {
        setState(State.NO_NETWORK)
    }

    /**
     * 设置空状态的图标
     */
    fun setEmptyIcon(@DrawableRes resId: Int) {
        emptyView.findViewById<ImageView>(R.id.iv_empty_icon)?.setImageResource(resId)
    }

    /**
     * 设置空状态的提示文字
     */
    fun setEmptyMessage(@StringRes resId: Int) {
        emptyView.findViewById<TextView>(R.id.tv_empty_message)?.setText(resId)
    }

    /**
     * 设置空状态的提示文字
     */
    fun setEmptyMessage(message: String) {
        emptyView.findViewById<TextView>(R.id.tv_empty_message)?.text = message
    }

    /**
     * 设置错误状态的图标
     */
    fun setErrorIcon(@DrawableRes resId: Int) {
        errorView.findViewById<ImageView>(R.id.iv_error_icon)?.setImageResource(resId)
    }

    /**
     * 设置错误状态的提示文字
     */
    fun setErrorMessage(@StringRes resId: Int) {
        errorView.findViewById<TextView>(R.id.tv_error_message)?.setText(resId)
    }

    /**
     * 设置错误状态的提示文字
     */
    fun setErrorMessage(message: String) {
        errorView.findViewById<TextView>(R.id.tv_error_message)?.text = message
    }

    /**
     * 设置错误重试按钮点击事件
     */
    fun setOnErrorRetryClickListener(listener: OnClickListener) {
        errorView.findViewById<View>(R.id.btn_error_retry)?.setOnClickListener(listener)
    }

    /**
     * 设置空状态点击事件
     */
    fun setOnEmptyClickListener(listener: OnClickListener) {
        emptyView.setOnClickListener(listener)
    }

    /**
     * 设置无网络重试按钮点击事件
     */
    fun setOnNoNetworkRetryClickListener(listener: OnClickListener) {
        noNetworkView.findViewById<View>(R.id.btn_no_network_retry)?.setOnClickListener(listener)
    }

    private fun updateVisibility() {
        if (::contentView.isInitialized) {
            contentView.visibility = if (currentState == State.CONTENT) View.VISIBLE else View.GONE
        }
        loadingView.visibility = if (currentState == State.LOADING) View.VISIBLE else View.GONE
        emptyView.visibility = if (currentState == State.EMPTY) View.VISIBLE else View.GONE
        errorView.visibility = if (currentState == State.ERROR) View.VISIBLE else View.GONE
        noNetworkView.visibility = if (currentState == State.NO_NETWORK) View.VISIBLE else View.GONE
    }
}