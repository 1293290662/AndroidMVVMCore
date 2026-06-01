/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * 日志工具类，统一日志管理，支持开关控制
 */
package com.github.spadger.mvvmc.util

import android.util.Log

object LogUtil {
    /**
     * 是否开启日志输出
     * 建议在release版本中关闭
     */
    var isDebug = true

    /**
     * 日志标签前缀
     */
    private const val TAG_PREFIX = "MVVMCore_"

    /**
     * 输出详细日志
     * @param tag 标签
     * @param message 消息
     */
    fun v(tag: String, message: String) {
        if (isDebug) {
            Log.v(TAG_PREFIX + tag, message)
        }
    }

    /**
     * 输出详细日志（自动生成标签）
     * @param message 消息
     */
    fun v(message: String) {
        if (isDebug) {
            Log.v(TAG_PREFIX + getCallerTag(), message)
        }
    }

    /**
     * 输出调试日志
     * @param tag 标签
     * @param message 消息
     */
    fun d(tag: String, message: String) {
        if (isDebug) {
            Log.d(TAG_PREFIX + tag, message)
        }
    }

    /**
     * 输出调试日志（自动生成标签）
     * @param message 消息
     */
    fun d(message: String) {
        if (isDebug) {
            Log.d(TAG_PREFIX + getCallerTag(), message)
        }
    }

    /**
     * 输出信息日志
     * @param tag 标签
     * @param message 消息
     */
    fun i(tag: String, message: String) {
        if (isDebug) {
            Log.i(TAG_PREFIX + tag, message)
        }
    }

    /**
     * 输出信息日志（自动生成标签）
     * @param message 消息
     */
    fun i(message: String) {
        if (isDebug) {
            Log.i(TAG_PREFIX + getCallerTag(), message)
        }
    }

    /**
     * 输出警告日志
     * @param tag 标签
     * @param message 消息
     */
    fun w(tag: String, message: String) {
        if (isDebug) {
            Log.w(TAG_PREFIX + tag, message)
        }
    }

    /**
     * 输出警告日志（自动生成标签）
     * @param message 消息
     */
    fun w(message: String) {
        if (isDebug) {
            Log.w(TAG_PREFIX + getCallerTag(), message)
        }
    }

    /**
     * 输出错误日志
     * @param tag 标签
     * @param message 消息
     */
    fun e(tag: String, message: String) {
        if (isDebug) {
            Log.e(TAG_PREFIX + tag, message)
        }
    }

    /**
     * 输出错误日志（自动生成标签）
     * @param message 消息
     */
    fun e(message: String) {
        if (isDebug) {
            Log.e(TAG_PREFIX + getCallerTag(), message)
        }
    }

    /**
     * 输出错误日志（包含异常）
     * @param tag 标签
     * @param message 消息
     * @param throwable 异常
     */
    fun e(tag: String, message: String, throwable: Throwable) {
        if (isDebug) {
            Log.e(TAG_PREFIX + tag, message, throwable)
        }
    }

    /**
     * 输出错误日志（包含异常，自动生成标签）
     * @param message 消息
     * @param throwable 异常
     */
    fun e(message: String, throwable: Throwable) {
        if (isDebug) {
            Log.e(TAG_PREFIX + getCallerTag(), message, throwable)
        }
    }

    /**
     * 输出JSON格式日志（美化输出）
     * @param tag 标签
     * @param json JSON字符串
     */
    fun json(tag: String, json: String) {
        if (isDebug) {
            val prettyJson = formatJson(json)
            Log.d(TAG_PREFIX + tag, "\n" + prettyJson)
        }
    }

    /**
     * 输出JSON格式日志（美化输出，自动生成标签）
     * @param json JSON字符串
     */
    fun json(json: String) {
        if (isDebug) {
            val prettyJson = formatJson(json)
            Log.d(TAG_PREFIX + getCallerTag(), "\n" + prettyJson)
        }
    }

    /**
     * 获取调用者类名作为标签
     * @return 类名
     */
    private fun getCallerTag(): String {
        val stackTrace = Thread.currentThread().stackTrace
        // 0: getStackTrace()
        // 1: getCallerTag()
        // 2: 本类的调用方法 (如 d(), e() 等)
        // 3: 外部调用者
        val callerIndex = 4
        if (stackTrace.size > callerIndex) {
            val className = stackTrace[callerIndex].className
            return className.substring(className.lastIndexOf('.') + 1)
        }
        return "Unknown"
    }

    /**
     * 格式化JSON字符串，使其更易读
     * @param json JSON字符串
     * @return 格式化后的JSON字符串
     */
    private fun formatJson(json: String): String {
        val sb = StringBuilder()
        var indentLevel = 0
        var inString = false
        var inEscape = false

        for (char in json) {
            when {
                inEscape -> {
                    sb.append(char)
                    inEscape = false
                }
                char == '\\' && inString -> {
                    sb.append(char)
                    inEscape = true
                }
                char == '"' -> {
                    sb.append(char)
                    inString = !inString
                }
                char == '{' || char == '[' -> {
                    sb.append(char)
                    if (!inString) {
                        indentLevel++
                        sb.append("\n")
                        sb.append("  ".repeat(indentLevel))
                    }
                }
                char == '}' || char == ']' -> {
                    if (!inString) {
                        indentLevel--
                        sb.append("\n")
                        sb.append("  ".repeat(indentLevel))
                    }
                    sb.append(char)
                }
                char == ',' -> {
                    sb.append(char)
                    if (!inString) {
                        sb.append("\n")
                        sb.append("  ".repeat(indentLevel))
                    }
                }
                else -> {
                    sb.append(char)
                }
            }
        }
        return sb.toString()
    }
}