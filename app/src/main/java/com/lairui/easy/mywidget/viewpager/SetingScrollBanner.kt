package com.lairui.easy.mywidget.viewpager

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by yukuo on 2016/5/10.
 * 一个可以自己设置是否可以滚动的viewpager
 */
class SetingScrollBanner : ViewPager {

    private var scrollable = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}


    /**
     * 设置viewpager是否可以滚动
     *
     * @param enable
     */
    fun setScrollable(enable: Boolean) {
        scrollable = enable
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (scrollable) {
            super.onInterceptTouchEvent(event)
        } else {
            false
        }
    }
}
