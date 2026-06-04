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
    var isDebug = true
    private const val TAG_PREFIX = "MVVMCore_"
    private const val MAX_TAG_LENGTH = 23
    private const val MAX_MESSAGE_LENGTH = 4000

    fun v(tag: String, message: String) {
        if (isDebug) Log.v(shortenTag(tag), message)
    }

    fun v(message: String) {
        if (isDebug) Log.v(shortenTag(getCallerTag()), message)
    }

    fun d(tag: String, message: String) {
        if (isDebug) Log.d(shortenTag(tag), message)
    }

    fun d(message: String) {
        if (isDebug) Log.d(shortenTag(getCallerTag()), message)
    }

    fun i(tag: String, message: String) {
        if (isDebug) Log.i(shortenTag(tag), message)
    }

    fun i(message: String) {
        if (isDebug) Log.i(shortenTag(getCallerTag()), message)
    }

    fun w(tag: String, message: String) {
        if (isDebug) Log.w(shortenTag(tag), message)
    }

    fun w(message: String) {
        if (isDebug) Log.w(shortenTag(getCallerTag()), message)
    }

    fun e(tag: String, message: String) {
        if (isDebug) Log.e(shortenTag(tag), message)
    }

    fun e(message: String) {
        if (isDebug) Log.e(shortenTag(getCallerTag()), message)
    }

    fun e(tag: String, message: String, throwable: Throwable) {
        if (isDebug) Log.e(shortenTag(tag), message, throwable)
    }

    fun e(message: String, throwable: Throwable) {
        if (isDebug) Log.e(shortenTag(getCallerTag()), message, throwable)
    }

    fun json(tag: String, json: String) {
        if (isDebug) {
            val prettyJson = formatJson(json)
            logLongMessage(shortenTag(tag), prettyJson)
        }
    }

    fun json(json: String) {
        if (isDebug) {
            val prettyJson = formatJson(json)
            logLongMessage(shortenTag(getCallerTag()), prettyJson)
        }
    }

    fun obj(tag: String, obj: Any?) {
        if (isDebug) {
            val json = try {
                com.google.gson.Gson().toJson(obj)
            } catch (e: Exception) {
                obj?.toString() ?: "null"
            }
            json(tag, json)
        }
    }

    fun obj(obj: Any?) {
        obj(getCallerTag(), obj)
    }

    inline fun <reified T> T.log(tag: String = "") {
        if (isDebug) {
            val actualTag = if (tag.isEmpty()) getCallerTagFromStack(5) else tag
            val json = try {
                com.google.gson.Gson().toJson(this)
            } catch (e: Exception) {
                this.toString()
            }
            json(actualTag, json)
        }
    }

    private fun shortenTag(tag: String): String {
        val fullTag = TAG_PREFIX + tag
        return if (fullTag.length > MAX_TAG_LENGTH) {
            fullTag.substring(0, MAX_TAG_LENGTH)
        } else {
            fullTag
        }
    }

    fun getCallerTag(): String {
        return getCallerTagFromStack(4)
    }

    fun getCallerTagFromStack(stackIndex: Int): String {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size > stackIndex) {
            val className = stackTrace[stackIndex].className
            return className.substring(className.lastIndexOf('.') + 1)
        }
        return "Unknown"
    }

    private fun formatJson(json: String): String {
        return try {
            val parser = com.google.gson.JsonParser.parseString(json)
            val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
            gson.toJson(parser)
        } catch (e: Exception) {
            json
        }
    }

    private fun logLongMessage(tag: String, message: String) {
        if (message.length <= MAX_MESSAGE_LENGTH) {
            Log.d(tag, "\n$message")
            return
        }

        var offset = 0
        while (offset < message.length) {
            val end = minOf(offset + MAX_MESSAGE_LENGTH, message.length)
            Log.d(tag, message.substring(offset, end))
            offset = end
        }
    }
}