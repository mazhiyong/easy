package com.lairui.easy.utils.tool

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.Locale
import java.util.regex.Pattern

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

import com.lairui.easy.R
import com.lairui.easy.basic.MbsConstans


class ParseTextUtil(private val context: Context) {
    @SuppressLint("SimpleDateFormat")
    private val mParseSimpleDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm")
    @SuppressLint("SimpleDateFormat")
    private val mParseSimpleDate = SimpleDateFormat("yyyy-MM-dd")
    @SuppressLint("SimpleDateFormat")
    private val mSimpleDateFormat = SimpleDateFormat("MM-dd HH:mm")
    private val mDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.CHINA)

    /**
     * 解析并设置其字符串为指定的样式
     *
     * 要被设置的字符串数据
     * @return 已经被设置完成的<CODE>[SpannableString]</CODE>对象
     */
    fun parseValueColor(content: String): SpannableString {
        val mSpannableString = SpannableString(content)
        val values = this.getBeginAndEnd(Pattern.compile("\\d+"), content)
        for (value in values) {
            val begin = Integer.parseInt(value["BEGIN"]!!.toString())
            val end = Integer.parseInt(value["END"]!!.toString())
            mSpannableString.setSpan(AbsoluteSizeSpan(25, true), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            //mSpannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
            mSpannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.font_c)), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //设置前景色为洋红色
        }
        return mSpannableString
    }

    /**
     * 解析并设置其字符串为指定的样式
     *
     * 要被设置的字符串数据
     * @return 已经被设置完成的<CODE>[SpannableString]</CODE>对象
     */
    fun parseValueColorNum(content: String): Spannable {
        val mSpan = SpannableString(content)
        val values = this.getBeginAndEnd(Pattern.compile("\\d+"), content)
        for (value in values) {
            val begin = Integer.parseInt(value["BEGIN"]!!.toString())
            val end = Integer.parseInt(value["END"]!!.toString())


            //mSpan.setSpan(new AbsoluteSizeSpan(UtilTools.sp2px(context,16)), 0, begin, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // mSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.gray_normal)), 0, begin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
            mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 16)), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mSpan.setSpan(StyleSpan(android.graphics.Typeface.BOLD), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //粗体
            mSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //设置前景色为洋红色
            //mSpan.setSpan(new AbsoluteSizeSpan(UtilTools.sp2px(context,16)), end, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // mSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.gray_normal)), end, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        }
        return mSpan
    }

    /**
     * 解析并设置其字符串为指定的样式
     *
     * 要被设置的字符串数据
     * @return 已经被设置完成的<CODE>[SpannableString]</CODE>对象
     */
    fun parseValueColor(content: String, color: Int): SpannableString {
        val mSpannableString = SpannableString(content)
        val values = this.getBeginAndEnd(Pattern.compile("\\d+"), content)
        for (value in values) {
            val begin = Integer.parseInt(value["BEGIN"]!!.toString())
            val end = Integer.parseInt(value["END"]!!.toString())
            //mSpannableString.setSpan(new AbsoluteSizeSpan(25, true), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mSpannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //粗体
            mSpannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //设置前景色为洋红色
        }
        return mSpannableString
    }

    /**
     * 解析并设置其该字符串的指定位置为特定的样式
     *
     * 要被设置的字符串数据
     * @param begin
     * 要从该字符串的开始位置
     * @parsm end
     * 要到该字符串的结束位置
     * @return 已经被设置完成的<CODE>[SpannableString]</CODE>对象
     */
    fun parseValueColor(content: String, begin: Int, end: Int): SpannableString {
        val mSpannableString = SpannableString(content)
        mSpannableString.setSpan(AbsoluteSizeSpan(15, true), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //粗体
        mSpannableString.setSpan(ForegroundColorSpan(Color.rgb(141, 182, 205)), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return mSpannableString
    }

    /**
     * 解析并设置其该字符串的指定位置为特定的样式
     * @param content
     * 要被设置的字符串数据
     * @param begin
     * 要从该字符串的开始位置
     * @param end
     * 要到该字符串的结束位置
     * @param color
     * 指定要设置的最终颜色值
     * @return 已经被设置完成的<CODE>[SpannableString]</CODE>对象
     */
    fun parseValueColor(content: String, begin: Int, end: Int, color: Int): SpannableString {
        val mSpannableString = SpannableString(content)
        mSpannableString.setSpan(AbsoluteSizeSpan(15, true), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //粗体
        mSpannableString.setSpan(ForegroundColorSpan(color), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return mSpannableString
    }

    /**
     * 解析并设置其该字符串的指定位置为特定的样式
     *
     * @param content
     * 要被设置的字符串数据
     * @param size 要设置的文字大小
     * @param style 以何种字体风格显示，比如：`android.graphics.Typeface.BOLD`为粗体
     * @param begin
     * 要从该字符串的开始位置
     * @parsm end
     * 要到该字符串的结束位置
     * @param color
     * 指定要设置的最终颜色值
     * @return 已经被设置完成的<CODE>[SpannableString]</CODE>对象
     */
    fun parseValueColor(content: String, size: Int, style: Int, begin: Int, end: Int, color: Int): SpannableString {
        val mSpannableString = SpannableString(content)
        mSpannableString.setSpan(AbsoluteSizeSpan(size, true), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannableString.setSpan(StyleSpan(style), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannableString.setSpan(ForegroundColorSpan(color), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return mSpannableString
    }

    /**
     * 解析一个有规定格式的日期和时间的字符串，被解析的结果将按照不同的风格返回：
     * <UL>
     * <LI>如果参数时间距离当前系统时间不超过1分钟，则返回：xx秒前</LI>
     * <LI>如果参数时间距离当前系统时间不超过1小时，则返回：xx分钟前</LI>
     * <LI>如果参数时间距离当前系统时间不超过1天，则返回：xx小时前</LI>
     * <LI>如果参数时间距离当前该系统时间超过1天，则返回被格式化之后的日期简短形式：MM-dd HH:mm</LI>
    </UL> *
     * @param timeStr 具有规范化的日期字符串，该规范必须为：`yyyy-MM-dd HH:mm:ss`
     * @return 返回被解析完成后的日期简短形式字符串
     */
    fun parseDateTimeStr(timeStr: String): String {
        try {
            val mCalendar = Calendar.getInstance()
            mCalendar.time = Date() // 系统当前时间
            val nowHourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY) // 当前的小时
            val nowMinute = mCalendar.get(Calendar.MINUTE) // 当前的分
            val nowSecond = mCalendar.get(Calendar.SECOND) // 当前的秒
            val createDateTime = mParseSimpleDateTime.parse(timeStr) // 新闻创建时间
            mCalendar.time = createDateTime // 重新设置为新闻创建的时间
            val createDate = mParseSimpleDate.parse(mDateFormat.format(createDateTime))
            val nowDate = mParseSimpleDate.parse(mDateFormat.format(Date()))
            val timeDifferenceHour = nowHourOfDay - mCalendar.get(Calendar.HOUR_OF_DAY)
            val timeDifferenceMinute = nowMinute - mCalendar.get(Calendar.MINUTE)
            val timeDifferenceSecond = nowSecond - mCalendar.get(Calendar.SECOND)
            if (createDate.before(nowDate))
                return mSimpleDateFormat.format(createDateTime)
            else if (timeDifferenceHour > 0)
                return timeDifferenceHour.toString() + "小时前"
            else if (timeDifferenceMinute > 0)
                return timeDifferenceMinute.toString() + "分钟前"
            else if (timeDifferenceSecond > 0)
                return timeDifferenceSecond.toString() + "秒前"
        } catch (e: ParseException) {
            e.printStackTrace()
            return mSimpleDateFormat.format(Date())
        }

        return timeStr
    }

    fun getBeginAndEnd(pattern: Pattern,
                       content: String): List<MutableMap<String, Any>> {
        val values = ArrayList<MutableMap<String, Any>>()
        val matcher = pattern.matcher(content)
        while (matcher.find()) {
            val value = HashMap<String, Any>()
            value["VALUE"] = matcher.group()
            value["BEGIN"] = Integer.valueOf(matcher.start())
            value["END"] = Integer.valueOf(matcher.end())
            values.add(value)
        }
        return values
    }


    /**
     * 解析并设置其字符串为指定的样式
     *
     * 要被设置的字符串数据
     * @return 已经被设置完成的<CODE>[SpannableString]</CODE>对象
     */
    fun parseValueColor2(content: String): SpannableString {
        val mSpannableString = SpannableString(content)
        var values: List<MutableMap<String, Any>>? = this.getBeginAndEnd(
                Pattern.compile("\\d+\\.\\d%"), content)

        if (values == null || values.size <= 0) {
            values = this.getBeginAndEnd(Pattern.compile("\\d%"), content)
        }

        for (value in values) {
            val begin = Integer.parseInt(value["BEGIN"]!!.toString())
            val end = Integer.parseInt(value["END"]!!.toString())
            // mSpannableString.setSpan(new AbsoluteSizeSpan(16, true), begin,
            // end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // mSpannableString.setSpan(new
            // StyleSpan(android.graphics.Typeface.BOLD), begin, end,
            // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
            //			mSpannableString.setSpan(new ForegroundColorSpan(getResources()
            //							.getColor(R.color.chongzhi_color)), begin, end,
            //					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
        }
        return mSpannableString
    }

    /**
     * 解析并设置其字符串为指定的样式
     *
     * 要被设置的字符串数据
     * @return 已经被设置完成的<CODE>[SpannableString]</CODE>对象
     */
    fun parseValueColor3(content: String, mContext: Context): SpannableString {
        val mSpannableString = SpannableString(content)
        var values: List<MutableMap<String, Any>>? = this.getBeginAndEnd(
                Pattern.compile("《.*》"), content)

        if (values == null || values.size <= 0) {
            values = this.getBeginAndEnd(Pattern.compile("《.*》"), content)
        }

        for (value in values) {
            val begin = Integer.parseInt(value["BEGIN"]!!.toString())
            val end = Integer.parseInt(value["END"]!!.toString())
            // mSpannableString.setSpan(new AbsoluteSizeSpan(16, true), begin,
            // end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // mSpannableString.setSpan(new
            // StyleSpan(android.graphics.Typeface.BOLD), begin, end,
            // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
            mSpannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.font_c)), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // 设置前景色为洋红色
        }
        return mSpannableString
    }


    fun getDianType(money: String): Spannable {
        var dian = money.length
        var ff = 0
        if (money.contains(MbsConstans.RMB)) {
            ff = money.indexOf(MbsConstans.RMB)
            ff = ff + 1
        }
        if (money.contains(".")) {
            dian = money.indexOf(".")
        } else {
            dian = money.length
        }
        val mSpan = SpannableString(money)
        mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 26)), 0, ff, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 36)), ff, dian, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 16)), dian, money.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return mSpan
    }


    fun getTextSpan(contentStr: String, splitChar: String): SpannableString {
        var dian = contentStr.length
        if (contentStr.contains(splitChar)) {
            dian = contentStr.indexOf(splitChar)
            dian = dian + 1
        } else {
            dian = contentStr.length
        }
        val mSpan = SpannableString(contentStr)
        mSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), dian, contentStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // 设置前景色为洋红色

        return mSpan
    }


}
