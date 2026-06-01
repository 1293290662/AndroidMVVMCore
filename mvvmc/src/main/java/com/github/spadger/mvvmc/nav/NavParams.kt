/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 导航参数传递工具类，提供便捷的 Bundle 创建方法
 */
package com.github.spadger.mvvmc.nav

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * Bundle 构建器扩展
 */
class BundleBuilder {
    private val bundle = Bundle()

    fun putString(key: String, value: String?): BundleBuilder {
        bundle.putString(key, value)
        return this
    }

    fun putInt(key: String, value: Int): BundleBuilder {
        bundle.putInt(key, value)
        return this
    }

    fun putLong(key: String, value: Long): BundleBuilder {
        bundle.putLong(key, value)
        return this
    }

    fun putFloat(key: String, value: Float): BundleBuilder {
        bundle.putFloat(key, value)
        return this
    }

    fun putDouble(key: String, value: Double): BundleBuilder {
        bundle.putDouble(key, value)
        return this
    }

    fun putBoolean(key: String, value: Boolean): BundleBuilder {
        bundle.putBoolean(key, value)
        return this
    }

    fun putParcelable(key: String, value: Parcelable?): BundleBuilder {
        bundle.putParcelable(key, value)
        return this
    }

    fun putSerializable(key: String, value: Serializable?): BundleBuilder {
        bundle.putSerializable(key, value)
        return this
    }

    fun putStringArray(key: String, value: Array<String>?): BundleBuilder {
        bundle.putStringArray(key, value)
        return this
    }

    fun putIntArray(key: String, value: IntArray?): BundleBuilder {
        bundle.putIntArray(key, value)
        return this
    }

    fun putBundle(key: String, value: Bundle?): BundleBuilder {
        bundle.putBundle(key, value)
        return this
    }

    fun build(): Bundle {
        return bundle
    }
}

/**
 * 便捷创建 Bundle 的方法
 */
fun bundleOf(init: BundleBuilder.() -> Unit): Bundle {
    return BundleBuilder().apply(init).build()
}

/**
 * 常用导航参数 Key 常量
 */
object NavParams {
    const val ID = "id"
    const val NAME = "name"
    const val DATA = "data"
    const val TYPE = "type"
    const val TITLE = "title"
    const val URL = "url"
    const val POSITION = "position"
    const val EXTRA = "extra"
}