/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 日期处理工具类
 */
package com.github.spadger.mvvmc.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    /**
     * 常用日期格式
     */
    const val FORMAT_YMD = "yyyy-MM-dd"
    const val FORMAT_YMD_HMS = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_YMD_HM = "yyyy-MM-dd HH:mm"
    const val FORMAT_HMS = "HH:mm:ss"
    const val FORMAT_HM = "HH:mm"
    const val FORMAT_YMD_CHINESE = "yyyy年MM月dd日"
    const val FORMAT_YMD_HMS_CHINESE = "yyyy年MM月dd日 HH时mm分ss秒"

    /**
     * 获取当前时间戳（毫秒）
     */
    fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    /**
     * 获取当前时间（指定格式）
     */
    fun getCurrentTime(format: String): String {
        return formatDate(getCurrentTimeMillis(), format)
    }

    /**
     * 将时间戳转换为指定格式的日期字符串
     */
    fun formatDate(timestamp: Long, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * 将日期字符串转换为时间戳
     */
    fun parseDate(dateStr: String, format: String): Long {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.parse(dateStr)?.time ?: 0
        } catch (e: ParseException) {
            0
        }
    }

    /**
     * 将日期字符串从一种格式转换为另一种格式
     */
    fun convertDate(dateStr: String, fromFormat: String, toFormat: String): String {
        val timestamp = parseDate(dateStr, fromFormat)
        return formatDate(timestamp, toFormat)
    }

    /**
     * 获取当前日期
     */
    fun getCurrentDate(): String {
        return getCurrentTime(FORMAT_YMD)
    }

    /**
     * 获取当前日期时间
     */
    fun getCurrentDateTime(): String {
        return getCurrentTime(FORMAT_YMD_HMS)
    }

    /**
     * 获取星期几
     */
    fun getDayOfWeek(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timestamp)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "星期日"
            Calendar.MONDAY -> "星期一"
            Calendar.TUESDAY -> "星期二"
            Calendar.WEDNESDAY -> "星期三"
            Calendar.THURSDAY -> "星期四"
            Calendar.FRIDAY -> "星期五"
            Calendar.SATURDAY -> "星期六"
            else -> ""
        }
    }

    /**
     * 获取当前星期几
     */
    fun getCurrentDayOfWeek(): String {
        return getDayOfWeek(getCurrentTimeMillis())
    }

    /**
     * 判断是否是今天
     */
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val target = Calendar.getInstance()
        target.time = Date(timestamp)
        return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == target.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 判断是否是昨天
     */
    fun isYesterday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val target = Calendar.getInstance()
        target.time = Date(timestamp)
        today.add(Calendar.DAY_OF_MONTH, -1)
        return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == target.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 判断是否是本周
     */
    fun isThisWeek(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeekStart = getWeekStart(calendar)
        calendar.time = Date(timestamp)
        val targetWeekStart = getWeekStart(calendar)
        return currentWeekStart.timeInMillis == targetWeekStart.timeInMillis
    }

    /**
     * 判断是否是本月
     */
    fun isThisMonth(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val target = Calendar.getInstance()
        target.time = Date(timestamp)
        return calendar.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == target.get(Calendar.MONTH)
    }

    /**
     * 获取本周开始时间
     */
    fun getWeekStart(calendar: Calendar): Calendar {
        val weekStart = calendar.clone() as Calendar
        weekStart.firstDayOfWeek = Calendar.MONDAY
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        weekStart.set(Calendar.HOUR_OF_DAY, 0)
        weekStart.set(Calendar.MINUTE, 0)
        weekStart.set(Calendar.SECOND, 0)
        weekStart.set(Calendar.MILLISECOND, 0)
        return weekStart
    }

    /**
     * 获取本月开始时间
     */
    fun getMonthStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * 获取本月结束时间
     */
    fun getMonthEnd(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    /**
     * 添加天数
     */
    fun addDays(timestamp: Long, days: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timestamp)
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar.timeInMillis
    }

    /**
     * 添加小时
     */
    fun addHours(timestamp: Long, hours: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timestamp)
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        return calendar.timeInMillis
    }

    /**
     * 计算两个时间戳之间的天数差
     */
    fun getDaysBetween(startTime: Long, endTime: Long): Long {
        val diff = endTime - startTime
        return diff / (1000 * 60 * 60 * 24)
    }

    /**
     * 计算两个时间戳之间的小时差
     */
    fun getHoursBetween(startTime: Long, endTime: Long): Long {
        val diff = endTime - startTime
        return diff / (1000 * 60 * 60)
    }

    /**
     * 格式化时长（秒）
     */
    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%d小时%d分钟%d秒", hours, minutes, secs)
        } else if (minutes > 0) {
            String.format("%d分钟%d秒", minutes, secs)
        } else {
            String.format("%d秒", secs)
        }
    }

    /**
     * 智能格式化时间（根据时间戳显示：刚刚、几分钟前、几小时前、昨天、日期）
     */
    fun smartFormat(timestamp: Long): String {
        val now = getCurrentTimeMillis()
        val diff = now - timestamp

        // 小于1分钟
        if (diff < 60 * 1000) {
            return "刚刚"
        }

        // 小于1小时
        if (diff < 60 * 60 * 1000) {
            val minutes = diff / (60 * 1000)
            return "${minutes}分钟前"
        }

        // 小于24小时
        if (diff < 24 * 60 * 60 * 1000) {
            val hours = diff / (60 * 60 * 1000)
            return "${hours}小时前"
        }

        // 昨天
        if (isYesterday(timestamp)) {
            return "昨天 " + formatDate(timestamp, FORMAT_HM)
        }

        // 本周内
        if (isThisWeek(timestamp)) {
            return getDayOfWeek(timestamp) + " " + formatDate(timestamp, FORMAT_HM)
        }

        // 本年
        val targetYear = Calendar.getInstance().apply { time = Date(timestamp) }.get(Calendar.YEAR)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return if (targetYear == currentYear) {
            formatDate(timestamp, FORMAT_YMD_HM)
        } else {
            formatDate(timestamp, FORMAT_YMD_HMS)
        }
    }
}