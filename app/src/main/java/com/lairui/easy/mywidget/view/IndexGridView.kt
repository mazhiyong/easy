package com.lairui.easy.mywidget.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.GridView

/**
 * 此ViewPager解决与父容器ScrollView冲突的问题,无法完美解决.有卡顿
 * 此自定义组件和下拉刷新scrollview配合暂时小完美，有待改善
 *
 */

class IndexGridView : GridView {

    /**
     * 下面这段代码是解决girdView空白点击事件的处理
     */
    private var mTouchInvalidPosListener: OnTouchInvalidPositionListener? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2,
                View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

    interface OnTouchInvalidPositionListener {
        /**
         * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者 MotionEvent.ACTION_UP等来按需要进行判断
         * @return 是否要终止事件的路由
         */
        fun onTouchInvalidPosition(motionEvent: Int): Boolean
    }

    /**
     * 点击空白区域时的响应和处理接口
     */
    fun setOnTouchInvalidPositionListener(listener: OnTouchInvalidPositionListener) {
        mTouchInvalidPosListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mTouchInvalidPosListener == null) {
            return super.onTouchEvent(event)
        }
        if (!isEnabled) {
            return isClickable || isLongClickable
        }
        val motionPosition = pointToPosition(event.x.toInt(), event.y.toInt())
        if (motionPosition == AdapterView.INVALID_POSITION) {
            super.onTouchEvent(event)
            return mTouchInvalidPosListener!!.onTouchInvalidPosition(event.actionMasked)
        }
        return super.onTouchEvent(event)
    }
}
