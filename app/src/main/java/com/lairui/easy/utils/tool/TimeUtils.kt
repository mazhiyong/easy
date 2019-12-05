package com.lairui.easy.utils.tool


import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/02
 * desc  : 时间相关工具类
</pre> *
 */
class TimeUtils  {

    companion object {
        private val DEFAULT_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        /**
         * 将时间戳转为时间字符串
         *
         * 格式为format
         *
         * @param millis 毫秒时间戳
         * @param format 时间格式
         * @return 时间字符串
         */
        @JvmOverloads
       public fun millis2String(millis: Long, format: DateFormat = DEFAULT_FORMAT): String {
            return format.format(Date(millis))
        }

        /**
         * 将时间字符串转为时间戳
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 毫秒时间戳
         */
        @JvmOverloads
        fun string2Millis(time: String, format: DateFormat = DEFAULT_FORMAT): Long {
            try {
                return format.parse(time).time
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return -1
        }

        /**
         * 将时间字符串转为Date类型
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return Date类型
         */
        @JvmOverloads
        fun string2Date(time: String, format: DateFormat = DEFAULT_FORMAT): Date? {
            try {
                return format.parse(time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * 将Date类型转为时间字符串
         *
         * 格式为format
         *
         * @param date   Date类型时间
         * @param format 时间格式
         * @return 时间字符串
         */
        @JvmOverloads
        fun date2String(date: Date, format: DateFormat = DEFAULT_FORMAT): String {
            return format.format(date)
        }

        /**
         * 将Date类型转为时间戳
         *
         * @param date Date类型时间
         * @return 毫秒时间戳
         */
        fun date2Millis(date: Date): Long {
            return date.time
        }

        /**
         * 将时间戳转为Date类型
         *
         * @param millis 毫秒时间戳
         * @return Date类型时间
         */
        fun millis2Date(millis: Long): Date {
            return Date(millis)
        }

        /**
         * 获取两个时间差（单位：unit）
         *
         * time0和time1格式都为yyyy-MM-dd HH:mm:ss
         *
         * @param time0 时间字符串0
         * @param time1 时间字符串1
         * @param unit  单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return unit时间戳
         */
        fun getTimeSpan(time0: String, time1: String, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return getTimeSpan(time0, time1, DEFAULT_FORMAT, unit)
        }

        /**
         * 获取两个时间差（单位：unit）
         *
         * time0和time1格式都为format
         *
         * @param time0  时间字符串0
         * @param time1  时间字符串1
         * @param format 时间格式
         * @param unit   单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return unit时间戳
         */
        fun getTimeSpan(time0: String, time1: String, format: DateFormat, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return millis2TimeSpan(Math.abs(string2Millis(time0, format) - string2Millis(time1, format)), unit)
        }

        /**
         * 获取两个时间差（单位：unit）
         *
         * @param date0 Date类型时间0
         * @param date1 Date类型时间1
         * @param unit  单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return unit时间戳
         */
        fun getTimeSpan(date0: Date, date1: Date, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return millis2TimeSpan(Math.abs(date2Millis(date0) - date2Millis(date1)), unit)
        }

        /**
         * 获取两个时间差（单位：unit）
         *
         * @param millis0 毫秒时间戳0
         * @param millis1 毫秒时间戳1
         * @param unit    单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return unit时间戳
         */
        fun getTimeSpan(millis0: Long, millis1: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return millis2TimeSpan(Math.abs(millis0 - millis1), unit)
        }

        /**
         * 获取合适型两个时间差
         *
         * time0和time1格式都为yyyy-MM-dd HH:mm:ss
         *
         * @param time0     时间字符串0
         * @param time1     时间字符串1
         * @param precision 精度
         *
         * precision = 0，返回null
         *
         * precision = 1，返回天
         *
         * precision = 2，返回天和小时
         *
         * precision = 3，返回天、小时和分钟
         *
         * precision = 4，返回天、小时、分钟和秒
         *
         * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         * @return 合适型两个时间差
         */
        fun getFitTimeSpan(time0: String, time1: String, precision: Int): String? {
            return millis2FitTimeSpan(Math.abs(string2Millis(time0, DEFAULT_FORMAT) - string2Millis(time1, DEFAULT_FORMAT)), precision)
        }

        /**
         * 获取合适型两个时间差
         *
         * time0和time1格式都为format
         *
         * @param time0     时间字符串0
         * @param time1     时间字符串1
         * @param format    时间格式
         * @param precision 精度
         *
         * precision = 0，返回null
         *
         * precision = 1，返回天
         *
         * precision = 2，返回天和小时
         *
         * precision = 3，返回天、小时和分钟
         *
         * precision = 4，返回天、小时、分钟和秒
         *
         * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         * @return 合适型两个时间差
         */
        fun getFitTimeSpan(time0: String, time1: String, format: DateFormat, precision: Int): String? {
            return millis2FitTimeSpan(Math.abs(string2Millis(time0, format) - string2Millis(time1, format)), precision)
        }

        /**
         * 获取合适型两个时间差
         *
         * @param date0     Date类型时间0
         * @param date1     Date类型时间1
         * @param precision 精度
         *
         * precision = 0，返回null
         *
         * precision = 1，返回天
         *
         * precision = 2，返回天和小时
         *
         * precision = 3，返回天、小时和分钟
         *
         * precision = 4，返回天、小时、分钟和秒
         *
         * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         * @return 合适型两个时间差
         */
        fun getFitTimeSpan(date0: Date, date1: Date, precision: Int): String? {
            return millis2FitTimeSpan(Math.abs(date2Millis(date0) - date2Millis(date1)), precision)
        }

        /**
         * 获取合适型两个时间差
         *
         * @param millis0   毫秒时间戳1
         * @param millis1   毫秒时间戳2
         * @param precision 精度
         *
         * precision = 0，返回null
         *
         * precision = 1，返回天
         *
         * precision = 2，返回天和小时
         *
         * precision = 3，返回天、小时和分钟
         *
         * precision = 4，返回天、小时、分钟和秒
         *
         * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         * @return 合适型两个时间差
         */
        fun getFitTimeSpan(millis0: Long, millis1: Long, precision: Int): String? {
            return millis2FitTimeSpan(Math.abs(millis0 - millis1), precision)
        }

        /**
         * 获取当前毫秒时间戳
         *
         * @return 毫秒时间戳
         */
        val nowMills: Long
            get() = System.currentTimeMillis()

        /**
         * 获取当前时间字符串
         *
         * 格式为yyyy-MM-dd HH:mm:ss
         *
         * @return 时间字符串
         */
        val nowString: String
            get() = millis2String(System.currentTimeMillis(), DEFAULT_FORMAT)

        /**
         * 获取当前时间字符串
         *
         * 格式为format
         *
         * @param format 时间格式
         * @return 时间字符串
         */
        fun getNowString(format: DateFormat): String {
            return millis2String(System.currentTimeMillis(), format)
        }

        /**
         * 获取当前Date
         *
         * @return Date类型时间
         */
        val nowDate: Date
            get() = Date()

        /**
         * 获取与当前时间的差（单位：unit）
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @param unit 单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return unit时间戳
         */
        fun getTimeSpanByNow(time: String, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return getTimeSpan(nowString, time, DEFAULT_FORMAT, unit)
        }

        /**
         * 获取与当前时间的差（单位：unit）
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @param unit   单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return unit时间戳
         */
        fun getTimeSpanByNow(time: String, format: DateFormat, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return getTimeSpan(getNowString(format), time, format, unit)
        }

        /**
         * 获取与当前时间的差（单位：unit）
         *
         * @param date Date类型时间
         * @param unit 单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return unit时间戳
         */
        fun getTimeSpanByNow(date: Date, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return getTimeSpan(Date(), date, unit)
        }

        /**
         * 获取与当前时间的差（单位：unit）
         *
         * @param millis 毫秒时间戳
         * @param unit   单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return unit时间戳
         */
        fun getTimeSpanByNow(millis: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return getTimeSpan(System.currentTimeMillis(), millis, unit)
        }

        /**
         * 获取合适型与当前时间的差
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time      时间字符串
         * @param precision 精度
         *
         *  * precision = 0，返回null
         *  * precision = 1，返回天
         *  * precision = 2，返回天和小时
         *  * precision = 3，返回天、小时和分钟
         *  * precision = 4，返回天、小时、分钟和秒
         *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         *
         * @return 合适型与当前时间的差
         */
        fun getFitTimeSpanByNow(time: String, precision: Int): String? {
            return getFitTimeSpan(nowString, time, DEFAULT_FORMAT, precision)
        }

        /**
         * 获取合适型与当前时间的差
         *
         * time格式为format
         *
         * @param time      时间字符串
         * @param format    时间格式
         * @param precision 精度
         *
         *  * precision = 0，返回null
         *  * precision = 1，返回天
         *  * precision = 2，返回天和小时
         *  * precision = 3，返回天、小时和分钟
         *  * precision = 4，返回天、小时、分钟和秒
         *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         *
         * @return 合适型与当前时间的差
         */
        fun getFitTimeSpanByNow(time: String, format: DateFormat, precision: Int): String? {
            return getFitTimeSpan(getNowString(format), time, format, precision)
        }

        /**
         * 获取合适型与当前时间的差
         *
         * @param date      Date类型时间
         * @param precision 精度
         *
         *  * precision = 0，返回null
         *  * precision = 1，返回天
         *  * precision = 2，返回天和小时
         *  * precision = 3，返回天、小时和分钟
         *  * precision = 4，返回天、小时、分钟和秒
         *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         *
         * @return 合适型与当前时间的差
         */
        fun getFitTimeSpanByNow(date: Date, precision: Int): String? {
            return getFitTimeSpan(nowDate, date, precision)
        }

        /**
         * 获取合适型与当前时间的差
         *
         * @param millis    毫秒时间戳
         * @param precision 精度
         *
         *  * precision = 0，返回null
         *  * precision = 1，返回天
         *  * precision = 2，返回天和小时
         *  * precision = 3，返回天、小时和分钟
         *  * precision = 4，返回天、小时、分钟和秒
         *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         *
         * @return 合适型与当前时间的差
         */
        fun getFitTimeSpanByNow(millis: Long, precision: Int): String? {
            return getFitTimeSpan(System.currentTimeMillis(), millis, precision)
        }

        /**
         * 获取友好型与当前时间的差
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 友好型与当前时间的差
         *
         *  * 如果小于1秒钟内，显示刚刚
         *  * 如果在1分钟内，显示XXX秒前
         *  * 如果在1小时内，显示XXX分钟前
         *  * 如果在1小时外的今天内，显示今天15:32
         *  * 如果是昨天的，显示昨天15:32
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        @JvmOverloads
        fun getFriendlyTimeSpanByNow(time: String, format: DateFormat = DEFAULT_FORMAT): String {
            return getFriendlyTimeSpanByNow(string2Millis(time, format))
        }

        /**
         * 获取友好型与当前时间的差
         *
         * @param date Date类型时间
         * @return 友好型与当前时间的差
         *
         *  * 如果小于1秒钟内，显示刚刚
         *  * 如果在1分钟内，显示XXX秒前
         *  * 如果在1小时内，显示XXX分钟前
         *  * 如果在1小时外的今天内，显示今天15:32
         *  * 如果是昨天的，显示昨天15:32
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        fun getFriendlyTimeSpanByNow(date: Date): String {
            return getFriendlyTimeSpanByNow(date.time)
        }

        /**
         * 获取友好型与当前时间的差
         *
         * @param millis 毫秒时间戳
         * @return 友好型与当前时间的差
         *
         *  * 如果小于1秒钟内，显示刚刚
         *  * 如果在1分钟内，显示XXX秒前
         *  * 如果在1小时内，显示XXX分钟前
         *  * 如果在1小时外的今天内，显示今天15:32
         *  * 如果是昨天的，显示昨天15:32
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        fun getFriendlyTimeSpanByNow(millis: Long): String {
            val now = System.currentTimeMillis()
            val span = now - millis
            if (span < 0)
                return String.format("%tc", millis)// U can read http://www.apihome.cn/api/java/Formatter.html to understand it.
            if (span < 1000) {
                return "刚刚"
            } else if (span < com.lairui.easy.basic.MbsConstans.MIN) {
                return String.format(Locale.getDefault(), "%d秒前", span / com.lairui.easy.basic.MbsConstans.SEC)
            } else if (span < com.lairui.easy.basic.MbsConstans.HOUR) {
                return String.format(Locale.getDefault(), "%d分钟前", span / com.lairui.easy.basic.MbsConstans.MIN)
            }
            // 获取当天00:00
            val wee = weeOfToday
            return if (millis >= wee) {
                String.format("今天%tR", millis)
            } else if (millis >= wee - com.lairui.easy.basic.MbsConstans.DAY) {
                String.format("昨天%tR", millis)
            } else {
                String.format("%tF", millis)
            }
        }

        private val weeOfToday: Long
            get() {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.MILLISECOND, 0)
                return cal.timeInMillis
            }

        /**
         * 获取与给定时间等于时间差的时间戳
         *
         * @param millis   给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间戳
         */
        fun getMillis(millis: Long, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return millis + timeSpan2Millis(timeSpan, unit)
        }

        /**
         * 获取与给定时间等于时间差的时间戳
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time     给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间戳
         */
        fun getMillis(time: String, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return getMillis(time, DEFAULT_FORMAT, timeSpan, unit)
        }

        /**
         * 获取与给定时间等于时间差的时间戳
         *
         * time格式为format
         *
         * @param time     给定时间
         * @param format   时间格式
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间戳
         */
        fun getMillis(time: String, format: DateFormat, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return string2Millis(time, format) + timeSpan2Millis(timeSpan, unit)
        }

        /**
         * 获取与给定时间等于时间差的时间戳
         *
         * @param date     给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间戳
         */
        fun getMillis(date: Date, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return date2Millis(date) + timeSpan2Millis(timeSpan, unit)
        }

        /**
         * 获取与给定时间等于时间差的时间字符串
         *
         * 格式为yyyy-MM-dd HH:mm:ss
         *
         * @param millis   给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间字符串
         */
        fun getString(millis: Long, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): String {
            return getString(millis, DEFAULT_FORMAT, timeSpan, unit)
        }

        /**
         * 获取与给定时间等于时间差的时间字符串
         *
         * 格式为format
         *
         * @param millis   给定时间
         * @param format   时间格式
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间字符串
         */
        fun getString(millis: Long, format: DateFormat, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): String {
            return millis2String(millis + timeSpan2Millis(timeSpan, unit), format)
        }

        /**
         * 获取与给定时间等于时间差的时间字符串
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time     给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间字符串
         */
        fun getString(time: String, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): String {
            return getString(time, DEFAULT_FORMAT, timeSpan, unit)
        }

        /**
         * 获取与给定时间等于时间差的时间字符串
         *
         * 格式为format
         *
         * @param time     给定时间
         * @param format   时间格式
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间字符串
         */
        fun getString(time: String, format: DateFormat, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): String {
            return millis2String(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit), format)
        }

        /**
         * 获取与给定时间等于时间差的时间字符串
         *
         * 格式为yyyy-MM-dd HH:mm:ss
         *
         * @param date     给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间字符串
         */
        fun getString(date: Date, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): String {
            return getString(date, DEFAULT_FORMAT, timeSpan, unit)
        }

        /**
         * 获取与给定时间等于时间差的时间字符串
         *
         * 格式为format
         *
         * @param date     给定时间
         * @param format   时间格式
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的时间字符串
         */
        fun getString(date: Date, format: DateFormat, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): String {
            return millis2String(date2Millis(date) + timeSpan2Millis(timeSpan, unit), format)
        }

        /**
         * 获取与给定时间等于时间差的Date
         *
         * @param millis   给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的Date
         */
        fun getDate(millis: Long, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Date {
            return millis2Date(millis + timeSpan2Millis(timeSpan, unit))
        }

        /**
         * 获取与给定时间等于时间差的Date
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time     给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的Date
         */
        fun getDate(time: String, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Date {
            return getDate(time, DEFAULT_FORMAT, timeSpan, unit)
        }

        /**
         * 获取与给定时间等于时间差的Date
         *
         * 格式为format
         *
         * @param time     给定时间
         * @param format   时间格式
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的Date
         */
        fun getDate(time: String, format: DateFormat, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Date {
            return millis2Date(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit))
        }

        /**
         * 获取与给定时间等于时间差的Date
         *
         * @param date     给定时间
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与给定时间等于时间差的Date
         */
        fun getDate(date: Date, timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Date {
            return millis2Date(date2Millis(date) + timeSpan2Millis(timeSpan, unit))
        }

        /**
         * 获取与当前时间等于时间差的时间戳
         *
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与当前时间等于时间差的时间戳
         */
        fun getMillisByNow(timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return getMillis(nowMills, timeSpan, unit)
        }

        /**
         * 获取与当前时间等于时间差的时间字符串
         *
         * 格式为yyyy-MM-dd HH:mm:ss
         *
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与当前时间等于时间差的时间字符串
         */
        fun getStringByNow(timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): String {
            return getStringByNow(timeSpan, DEFAULT_FORMAT, unit)
        }

        /**
         * 获取与当前时间等于时间差的时间字符串
         *
         * 格式为format
         *
         * @param timeSpan 时间差的毫秒时间戳
         * @param format   时间格式
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与当前时间等于时间差的时间字符串
         */
        fun getStringByNow(timeSpan: Long, format: DateFormat, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): String {
            return getString(nowMills, format, timeSpan, unit)
        }

        /**
         * 获取与当前时间等于时间差的Date
         *
         * @param timeSpan 时间差的毫秒时间戳
         * @param unit     单位类型
         *
         *  * [com.lairui.easy.basic.MbsConstans.MSEC]: 毫秒
         *  * [com.lairui.easy.basic.MbsConstans.SEC]: 秒
         *  * [com.lairui.easy.basic.MbsConstans.MIN]: 分
         *  * [com.lairui.easy.basic.MbsConstans.HOUR]: 小时
         *  * [com.lairui.easy.basic.MbsConstans.DAY]: 天
         *
         * @return 与当前时间等于时间差的Date
         */
        fun getDateByNow(timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Date {
            return getDate(nowMills, timeSpan, unit)
        }

        /**
         * 判断是否今天
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isToday(time: String): Boolean {
            return isToday(string2Millis(time, DEFAULT_FORMAT))
        }

        /**
         * 判断是否今天
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isToday(time: String, format: DateFormat): Boolean {
            return isToday(string2Millis(time, format))
        }

        /**
         * 判断是否今天
         *
         * @param date Date类型时间
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isToday(date: Date): Boolean {
            return isToday(date.time)
        }

        /**
         * 判断是否今天
         *
         * @param millis 毫秒时间戳
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isToday(millis: Long): Boolean {
            val wee = weeOfToday
            return millis >= wee && millis < wee + com.lairui.easy.basic.MbsConstans.DAY
        }

        /**
         * 判断是否闰年
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return `true`: 闰年<br></br>`false`: 平年
         */
        fun isLeapYear(time: String): Boolean {
            return isLeapYear(string2Date(time, DEFAULT_FORMAT))
        }

        /**
         * 判断是否闰年
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return `true`: 闰年<br></br>`false`: 平年
         */
        fun isLeapYear(time: String, format: DateFormat): Boolean {
            return isLeapYear(string2Date(time, format))
        }

        /**
         * 判断是否闰年
         *
         * @param date Date类型时间
         * @return `true`: 闰年<br></br>`false`: 平年
         */
        fun isLeapYear(date: Date?): Boolean {
            val cal = Calendar.getInstance()
            cal.time = date
            val year = cal.get(Calendar.YEAR)
            return isLeapYear(year)
        }

        /**
         * 判断是否闰年
         *
         * @param millis 毫秒时间戳
         * @return `true`: 闰年<br></br>`false`: 平年
         */
        fun isLeapYear(millis: Long): Boolean {
            return isLeapYear(millis2Date(millis))
        }

        /**
         * 判断是否闰年
         *
         * @param year 年份
         * @return `true`: 闰年<br></br>`false`: 平年
         */
        fun isLeapYear(year: Int): Boolean {
            return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
        }

        /**
         * 获取中式星期
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return 中式星期
         */
        fun getChineseWeek(time: String): String {
            return getChineseWeek(string2Date(time, DEFAULT_FORMAT))
        }

        /**
         * 获取中式星期
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 中式星期
         */
        fun getChineseWeek(time: String, format: DateFormat): String {
            return getChineseWeek(string2Date(time, format))
        }

        /**
         * 获取中式星期
         *
         * @param date Date类型时间
         * @return 中式星期
         */
        fun getChineseWeek(date: Date?): String {
            return SimpleDateFormat("E", Locale.CHINA).format(date)
        }

        /**
         * 获取中式星期
         *
         * @param millis 毫秒时间戳
         * @return 中式星期
         */
        fun getChineseWeek(millis: Long): String {
            return getChineseWeek(Date(millis))
        }

        /**
         * 获取美式星期
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return 美式星期
         */
        fun getUSWeek(time: String): String {
            return getUSWeek(string2Date(time, DEFAULT_FORMAT))
        }

        /**
         * 获取美式星期
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 美式星期
         */
        fun getUSWeek(time: String, format: DateFormat): String {
            return getUSWeek(string2Date(time, format))
        }

        /**
         * 获取美式星期
         *
         * @param date Date类型时间
         * @return 美式星期
         */
        fun getUSWeek(date: Date?): String {
            return SimpleDateFormat("EEEE", Locale.US).format(date)
        }

        /**
         * 获取美式星期
         *
         * @param millis 毫秒时间戳
         * @return 美式星期
         */
        fun getUSWeek(millis: Long): String {
            return getUSWeek(Date(millis))
        }

        /**
         * 获取星期索引
         *
         * 注意：周日的Index才是1，周六为7
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return 1...7
         * @see Calendar.SUNDAY
         *
         * @see Calendar.MONDAY
         *
         * @see Calendar.TUESDAY
         *
         * @see Calendar.WEDNESDAY
         *
         * @see Calendar.THURSDAY
         *
         * @see Calendar.FRIDAY
         *
         * @see Calendar.SATURDAY
         */
        fun getWeekIndex(time: String): Int {
            return getWeekIndex(string2Date(time, DEFAULT_FORMAT))
        }

        /**
         * 获取星期索引
         *
         * 注意：周日的Index才是1，周六为7
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 1...7
         * @see Calendar.SUNDAY
         *
         * @see Calendar.MONDAY
         *
         * @see Calendar.TUESDAY
         *
         * @see Calendar.WEDNESDAY
         *
         * @see Calendar.THURSDAY
         *
         * @see Calendar.FRIDAY
         *
         * @see Calendar.SATURDAY
         */
        fun getWeekIndex(time: String, format: DateFormat): Int {
            return getWeekIndex(string2Date(time, format))
        }

        /**
         * 获取星期索引
         *
         * 注意：周日的Index才是1，周六为7
         *
         * @param date Date类型时间
         * @return 1...7
         * @see Calendar.SUNDAY
         *
         * @see Calendar.MONDAY
         *
         * @see Calendar.TUESDAY
         *
         * @see Calendar.WEDNESDAY
         *
         * @see Calendar.THURSDAY
         *
         * @see Calendar.FRIDAY
         *
         * @see Calendar.SATURDAY
         */
        fun getWeekIndex(date: Date?): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal.get(Calendar.DAY_OF_WEEK)
        }

        /**
         * 获取星期索引
         *
         * 注意：周日的Index才是1，周六为7
         *
         * @param millis 毫秒时间戳
         * @return 1...7
         * @see Calendar.SUNDAY
         *
         * @see Calendar.MONDAY
         *
         * @see Calendar.TUESDAY
         *
         * @see Calendar.WEDNESDAY
         *
         * @see Calendar.THURSDAY
         *
         * @see Calendar.FRIDAY
         *
         * @see Calendar.SATURDAY
         */
        fun getWeekIndex(millis: Long): Int {
            return getWeekIndex(millis2Date(millis))
        }

        /**
         * 获取月份中的第几周
         *
         * 注意：国外周日才是新的一周的开始
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return 1...5
         */
        fun getWeekOfMonth(time: String): Int {
            return getWeekOfMonth(string2Date(time, DEFAULT_FORMAT))
        }

        /**
         * 获取月份中的第几周
         *
         * 注意：国外周日才是新的一周的开始
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 1...5
         */
        fun getWeekOfMonth(time: String, format: DateFormat): Int {
            return getWeekOfMonth(string2Date(time, format))
        }

        /**
         * 获取月份中的第几周
         *
         * 注意：国外周日才是新的一周的开始
         *
         * @param date Date类型时间
         * @return 1...5
         */
        fun getWeekOfMonth(date: Date?): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal.get(Calendar.WEEK_OF_MONTH)
        }

        /**
         * 获取月份中的第几周
         *
         * 注意：国外周日才是新的一周的开始
         *
         * @param millis 毫秒时间戳
         * @return 1...5
         */
        fun getWeekOfMonth(millis: Long): Int {
            return getWeekOfMonth(millis2Date(millis))
        }

        /**
         * 获取年份中的第几周
         *
         * 注意：国外周日才是新的一周的开始
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return 1...54
         */
        fun getWeekOfYear(time: String): Int {
            return getWeekOfYear(string2Date(time, DEFAULT_FORMAT))
        }

        /**
         * 获取年份中的第几周
         *
         * 注意：国外周日才是新的一周的开始
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 1...54
         */
        fun getWeekOfYear(time: String, format: DateFormat): Int {
            return getWeekOfYear(string2Date(time, format))
        }

        /**
         * 获取年份中的第几周
         *
         * 注意：国外周日才是新的一周的开始
         *
         * @param date Date类型时间
         * @return 1...54
         */
        fun getWeekOfYear(date: Date?): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal.get(Calendar.WEEK_OF_YEAR)
        }

        /**
         * 获取年份中的第几周
         *
         * 注意：国外周日才是新的一周的开始
         *
         * @param millis 毫秒时间戳
         * @return 1...54
         */
        fun getWeekOfYear(millis: Long): Int {
            return getWeekOfYear(millis2Date(millis))
        }

        private val CHINESE_ZODIAC = arrayOf("猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊")

        /**
         * 获取生肖
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return 生肖
         */
        fun getChineseZodiac(time: String): String {
            return getChineseZodiac(string2Date(time, DEFAULT_FORMAT))
        }

        /**
         * 获取生肖
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 生肖
         */
        fun getChineseZodiac(time: String, format: DateFormat): String {
            return getChineseZodiac(string2Date(time, format))
        }

        /**
         * 获取生肖
         *
         * @param date Date类型时间
         * @return 生肖
         */
        fun getChineseZodiac(date: Date?): String {
            val cal = Calendar.getInstance()
            cal.time = date
            return CHINESE_ZODIAC[cal.get(Calendar.YEAR) % 12]
        }

        /**
         * 获取生肖
         *
         * @param millis 毫秒时间戳
         * @return 生肖
         */
        fun getChineseZodiac(millis: Long): String {
            return getChineseZodiac(millis2Date(millis))
        }

        /**
         * 获取生肖
         *
         * @param year 年
         * @return 生肖
         */
        fun getChineseZodiac(year: Int): String {
            return CHINESE_ZODIAC[year % 12]
        }

        private val ZODIAC = arrayOf("水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座")
        private val ZODIAC_FLAGS = intArrayOf(20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22)

        /**
         * 获取星座
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return 生肖
         */
        fun getZodiac(time: String): String {
            return getZodiac(string2Date(time, DEFAULT_FORMAT))
        }

        /**
         * 获取星座
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 生肖
         */
        fun getZodiac(time: String, format: DateFormat): String {
            return getZodiac(string2Date(time, format))
        }

        /**
         * 获取星座
         *
         * @param date Date类型时间
         * @return 星座
         */
        fun getZodiac(date: Date?): String {
            val cal = Calendar.getInstance()
            cal.time = date
            val month = cal.get(Calendar.MONTH) + 1
            val day = cal.get(Calendar.DAY_OF_MONTH)
            return getZodiac(month, day)
        }

        /**
         * 获取星座
         *
         * @param millis 毫秒时间戳
         * @return 星座
         */
        fun getZodiac(millis: Long): String {
            return getZodiac(millis2Date(millis))
        }

        /**
         * 获取星座
         *
         * @param month 月
         * @param day   日
         * @return 星座
         */
        fun getZodiac(month: Int, day: Int): String {
            return ZODIAC[if (day >= ZODIAC_FLAGS[month - 1])
                month - 1
            else
                (month + 10) % 12]
        }

        private fun timeSpan2Millis(timeSpan: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return timeSpan * unit
        }

        private fun millis2TimeSpan(millis: Long, @com.lairui.easy.basic.MbsConstans.Unit unit: Int): Long {
            return millis / unit
        }

        private fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
            var millis = millis
            var precision = precision
            if (millis < 0 || precision <= 0) return null
            precision = Math.min(precision, 5)
            val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
            if (millis == 0L) return 0.toString() + units[precision - 1]
            val sb = StringBuilder()
            val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
            for (i in 0 until precision) {
                if (millis >= unitLen[i]) {
                    val mode = millis / unitLen[i]
                    millis -= mode * unitLen[i]
                    sb.append(mode).append(units[i])
                }
            }
            return sb.toString()
        }
    }
}
