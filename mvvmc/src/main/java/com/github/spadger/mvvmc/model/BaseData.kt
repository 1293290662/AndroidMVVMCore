/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 基础数据接口，用于统一处理网络响应
 */
package com.github.spadger.mvvmc.model

interface BaseData<T> {
    val isSuccess: Boolean
    val message: String?
    val data: T?
}