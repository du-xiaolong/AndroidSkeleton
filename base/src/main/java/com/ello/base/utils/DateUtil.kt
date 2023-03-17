package com.ello.base.utils

import com.blankj.utilcode.util.TimeUtils
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

const val DATE_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
const val DATE_TIME_MS_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val DATE_FORMAT = "yyyy-MM-dd"
const val DATE_MONTH_DAY_FORMAT = "MM-dd"
const val DATE_YEAR_MONTH = "yyyy-MM"
const val DATE_MONTH_DAY_HOUR_MINUTE = "MM-dd HH:mm"
const val MONTH = "MM"
const val TIME_FORMAT = "HH:mm:ss"
const val TIME_FORMAT2 = "HH:mm"


/**
 * 将Date格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
 *
 * @param date   要转换的日期。
 * @param format 日期格式。如："yyyy-MM-dd"
 * @return 返回转换后的字符串，格式为yyyy-MM-dd HH:mm:ss
 */
fun Date.format(format: String = DATE_TIME_FORMAT): String =
    SimpleDateFormat(format, Locale.getDefault()).format(this)

/**
 * 将Date日期格式时间转换为字符串 yyyy-MM-dd
 *
 * @param date 要转换的日期。
 * @return 返回转换后的字符串，格式为yyyy-MM-dd
 */
fun Date.formatDateString() = format(DATE_FORMAT)

/**
 * 将Date日期格式时间转换为字符串 HH:mm:ss
 *
 * @param date
 * @return
 */
fun Date.formatTimeString() = format(TIME_FORMAT)


/**
 * 将日期字符串解析为日期Date。
 *
 * @param dateString 要转换的日期字符串，可能为长日期、短日期、带时区的日期。
 * @param format     日期格式。
 * @return 返回转换后的日期。
 */
fun String.parseDate(format: String = DATE_TIME_FORMAT): Date? =
    SimpleDateFormat(format, Locale.getDefault()).parse(this, ParsePosition(0))

/**
 * 将日期字符串解析为日期Date(自动推断日期格式)
 *
 * @return
 */
fun String.parseDateAuto(): Date? =
    kotlin.runCatching { parseDate(this.getDateFormat()) }.getOrNull()

/**
 * 将日期字符串格式化为可读的日期
 * 如2022-10-10 11:30:51 格式化为  4分钟前、6天前等
 *
 * @return
 */
fun String?.parseReadTime(): String {
    this ?: return ""
    val date = this.parseDateAuto() ?: return ""
    val nowDate = Date()
    //时间距离当前时间间隔(秒)
    val l = (nowDate.time - date.time) / 1000
    if (l < 60) {
        //一分钟以内
        return "刚刚"
    }
    if (l < 60 * 60) {
        //一小时以内
        return "${l / 60}分钟前"
    }
    if (l < 24 * 60 * 60) {
        //一天以内
        return "${l / (60 * 60)}小时前"
    }
    if (l < 7 * 24 * 60 * 60) {
        //七天内
        return "${l / (24 * 60 * 60)}天前"
    }
    return date.formatDateString()
}

/**
 * 常规自动日期格式识别
 * @param str 时间字符串
 * @return Date
 * @author dc
 */
fun String.getDateFormat(): String {

    var year = false
    val pattern = Pattern.compile("^[-\\+]?[\\d]*$");
    if (pattern.matcher(this.substring(0, 4)).matches()) {
        year = true
    }
    val sb = StringBuilder()
    var index = 0
    if (!year) {
        if (this.contains("月") || this.contains("-") || this.contains("/")) {
            if (Character.isDigit(this[0])) {
                index = 1;
            }
        } else {
            index = 3;
        }
    }
    for (i in 0 until this.length) {
        val chr = this[i]
        if (Character.isDigit(chr)) {
            if (index == 0) {
                sb.append("y")
            }
            if (index == 1) {
                sb.append("M")
            }
            if (index == 2) {
                sb.append("d")
            }
            if (index == 3) {
                sb.append("H")
            }
            if (index == 4) {
                sb.append("m")
            }
            if (index == 5) {
                sb.append("s")
            }
            if (index == 6) {
                sb.append("S")
            }
        } else {
            if (i > 0) {
                val lastChar = this[i - 1]
                if (Character.isDigit(lastChar)) {
                    index++;
                }
            }
            sb.append(chr);
        }
    }
    return sb.toString()
}

/**
 * 获取星期几(1~7)
 */
fun Date.getWeek(): Int = Calendar.getInstance().run {
    time = this@getWeek
    get(Calendar.DAY_OF_WEEK).run {
        if (this == Calendar.SUNDAY) 7 else this - 1
    }
}

fun Date.getWeekString(): String =
    listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")[this.getWeek() - 1]

fun Date.getMonthOfYear(): Int = Calendar.getInstance().run {
    time = this@getMonthOfYear
    get(Calendar.MONTH) + 1
}

fun Date.getDayOfMonth(): Int = Calendar.getInstance().run {
    time = this@getDayOfMonth
    get(Calendar.DAY_OF_MONTH)
}


/**
 * 获取当前周的周一的日期
 * @param date 传入当前日期
 * @return
 */
fun Date.getThisWeekMonday(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    // 获得当前日期是一个星期的第几天
    val dayWeek = cal[Calendar.DAY_OF_WEEK]
    if (1 == dayWeek) {
        cal.add(Calendar.DAY_OF_MONTH, -1)
    }
    // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
    cal.firstDayOfWeek = Calendar.MONDAY
    // 获得当前日期是一个星期的第几天
    val day = cal[Calendar.DAY_OF_WEEK]
    cal.add(Calendar.DATE, cal.firstDayOfWeek - day)
    return cal.time
}

/**
 * 获取间隔天数的日期
 */
fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_YEAR, days)
    return calendar.time
}

fun Date.addMonths(months: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.MONTH, months)
    return calendar.time
}


/**
 * 是否同一天
 */
fun Date.isSameDayTo(date: Date?): Boolean {
    if (date == null) return false
    val calendar1 = Calendar.getInstance().apply { time = this@isSameDayTo }
    val calendar2 = Calendar.getInstance().apply { time = date }
    return calendar1.get(Calendar.ERA) == calendar2.get(Calendar.ERA) && calendar1.get(Calendar.YEAR) == calendar2.get(
        Calendar.YEAR
    ) && calendar1.get(
        Calendar.DAY_OF_YEAR
    ) == calendar2.get(Calendar.DAY_OF_YEAR)
}

fun Date.isToday(): Boolean = isSameDayTo(Date())


object DateUtil {
    /**
     * 获取当前日期（不含时分秒）
     *
     * @return 返回当前日期yyyy-MM-dd
     */
    fun getTodayString() = Date().formatDateString()

    /**
     * 获取现在时间字符串
     *
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    fun getNowDateTimeString() = Date().format()

    /**
     * 获取一年第一天
     */
    fun getFirstDayOfYear(): Date {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        return calendar.time
    }

    /**
     * 将秒数格式化为01：22：33 时间格式，如果没有时，就是00：22：33
     */
    fun formatToHms(seconds: Long): String {
        val h = seconds / (60 * 60)
        val m = (seconds - h * 60 * 60) / 60
        val s = seconds % 60
        val hStr = if (h < 10) "0$h" else h.toString()
        val mStr = if (m < 10) "0$m" else m.toString()
        val sStr = if (s < 10) "0$s" else s.toString()
        return "$hStr:$mStr:$sStr"
    }

    /**
     * 将秒数格式化为22：33 时间格式
     */
    fun formatToMs(seconds: Long): String {
        val s = seconds % 60
        val m = seconds / 60

        val mStr = if (m < 10) "0$m" else m.toString()
        val sStr = if (s < 10) "0$s" else s.toString()
        return "$mStr:$sStr"
    }

    /**
     * 将时间（秒）转换 为可读的时间
     */
    fun formatReadTime(seconds: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = seconds * 1000
        val calendarNow = Calendar.getInstance()
        //不是今年的，显示年月日时分
        if (calendar[Calendar.YEAR] != calendarNow.get(Calendar.YEAR) || calendar.get(Calendar.ERA) != calendarNow.get(
                Calendar.ERA
            )
        ) {
            return SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
            ).format(Date(seconds * 1000))
        }
        //今天的
        if (calendar[Calendar.DAY_OF_YEAR] == calendarNow.get(Calendar.DAY_OF_YEAR)) {
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(seconds * 1000))
        }

        if (calendar[Calendar.DAY_OF_YEAR] == calendarNow.get(Calendar.DAY_OF_YEAR) - 1) {
            //昨天的
            return SimpleDateFormat(
                "昨天 HH:mm",
                Locale.getDefault()
            ).format(Date(seconds * 1000 - 24 * 60 * 60L))
        }
        //今年的
        return SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(seconds * 1000))
    }

    /**
     * 将秒数格式化为01：22：33 时间格式  如果没有时，就是22：33
     */
    fun formatToHms2(seconds: Int): String {
        val h = seconds / (60 * 60)
        val m = (seconds - h * 60 * 60) / 60
        val s = seconds % 60
        val hStr = if (h < 10) "0$h" else h.toString()
        val mStr = if (m < 10) "0$m" else m.toString()
        val sStr = if (s < 10) "0$s" else s.toString()
        if (h == 0) {
            return "$mStr:$sStr"
        }
        return "$hStr:$mStr:$sStr"
    }


    /**
     * 根据时间戳(秒)生成年月日
     */
    fun getYearMonthDayByStamp(timeSeconds: Long): List<Int> {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = Date(timeSeconds * 1000)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return listOf(year, month, day)
    }

    //当前秒
    val currentSeconds: Int
        get() = (System.currentTimeMillis() / 1000).toInt()

    fun getWeekString(dayOfWeek: Int): String =
        listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")[dayOfWeek - 1]

}


