package com.lairui.easy.utils.tool

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView

/**
 * 富文本  TextView
 */
class TextViewUtils {

    private var mTextView1: TextView? = null
    private var ss: SpannableString? = null
    fun init(content: String, mTextView: TextView) {
        mTextView1 = mTextView
        ss = SpannableString(content)
    }

    /**
     * 设置点击事件
     * @param start
     * @param end
     */
    fun setTextClick(start: Int, end: Int, callBack: ClickCallBack?) {
        ss!!.setSpan(object: ClickableSpan() {
            override fun onClick(widget: View) {
                callBack?.onClick()
            }
            override fun updateDrawState(ds: TextPaint?) {

            }

        },start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        mTextView1!!.movementMethod = LinkMovementMethod.getInstance()
    }


    /**
     * 设置颜色
     * @param start
     * @param end
     * @param color
     */
    fun setTextColor(start: Int, end: Int, color: Int) {
        ss!!.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    /**
     * 设置大小
     * @param start
     * @param end
     * @param size
     */
    fun setTextSize(start: Int, end: Int, size: Float) {
        ss!!.setSpan(RelativeSizeSpan(size), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }


    /**
     * 设置字体风格
     * @param start
     * @param end
     */
    fun setTextStyle(start: Int, end: Int, style: Int) {
        ss!!.setSpan(StyleSpan(style), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

    }


    /**
     * 设置字体背景
     * @param start
     * @param end
     */
    fun setTextBackgound(start: Int, end: Int, color: Int) {
        ss!!.setSpan(BackgroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }


    /**
     * 设置删除线
     * @param start
     * @param end
     */
    fun setTextDeleteLine(start: Int, end: Int) {
        ss!!.setSpan(StrikethroughSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    /**
     * 设置下划线
     * @param start
     * @param end
     */
    fun setTextUnderLine(start: Int, end: Int) {
        ss!!.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    /**
     * 设置左上角上标
     * @param start
     * @param end
     */
    fun setTextTopTip(start: Int, end: Int) {
        ss!!.setSpan(SuperscriptSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }


    /**
     * 设置右下角下标
     * @param start
     * @param end
     */
    fun setTextUnderTip(start: Int, end: Int) {
        ss!!.setSpan(SubscriptSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }


    /**
     * 向文本中添加表情
     * @param start
     * @param end
     */
    fun setTextAddEmoji(start: Int, end: Int, drawable: Drawable) {
        ss!!.setSpan(ImageSpan(drawable), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }


    interface ClickCallBack {
        fun onClick()
    }


    /**
     * 为TextView 设置效果
     */
    fun build() {
        mTextView1!!.text = ss
    }


}
