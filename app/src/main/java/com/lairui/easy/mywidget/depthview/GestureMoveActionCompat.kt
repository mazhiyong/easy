package com.lairui.easy.mywidget.depthview

import android.view.MotionEvent

/**
 * @Description:
 * @author: lll
 * @date: 2019-07-19
 */
class GestureMoveActionCompat(private val gestureMoveListener: OnGestureMoveListener?) {
    /**
     * 本次 ACTION_DOWN 事件的坐标 x
     */
    private var lastMotionX = 0f
    /**
     * 本次 ACTION_DOWN 事件的坐标 y
     */
    private var lastMotionY = 0f
    /**
     * 当前滑动的方向。0 无滑动（视为点击）；1 垂直滑动；2 横向滑动
     */
    private var interceptStatus = 0
    /**
     * 是否响应点击事件
     *
     * 因为有手指抖动的影响，有时候会产生少量的 ACTION_MOVE 事件，造成程序识别错误。
     */
    private var mEnableClick = true
    /**
     * 避免程序识别错误的一个阀值。只有触摸移动的距离大于这个阀值时，才认为是一个有效的移动。
     */
    private var touchSlop = 20
    var isDragging = false
        private set

    fun enableClick(enableClick: Boolean) {
        mEnableClick = enableClick
    }

    fun setTouchSlop(touchSlop: Int) {
        this.touchSlop = touchSlop
    }

    /**
     * @param e 事件 e
     * @param x 本次事件的坐标 x。可以是 e.getRawX() 或是 e.getX()，具体看情况
     * @param y 本次事件的坐标 y。可以是 e.getRawY() 或是 e.getY()，具体看情况
     *
     * @return 事件是否是横向滑动
     */
    fun onTouchEvent(e: MotionEvent, x: Float, y: Float): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                lastMotionY = y
                lastMotionX = x
                interceptStatus = 0
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = Math.abs(y - lastMotionY)
                val deltaX = Math.abs(x - lastMotionX)
                /**
                 * 如果之前是垂直滑动，即使现在是横向滑动，仍然认为它是垂直滑动的
                 * 如果之前是横向滑动，即使现在是垂直滑动，仍然认为它是横向滑动的
                 * 防止在一个方向上来回滑动时，发生垂直滑动和横向滑动的频繁切换，造成识别错误
                 */
                if (interceptStatus != 1 &&
                        (isDragging || deltaX > deltaY && deltaX > touchSlop)) {
                    interceptStatus = 2
                    isDragging = true
                    gestureMoveListener?.onHorizontalMove(e, x, y)
                } else if (interceptStatus != 2 &&
                        (isDragging || deltaX < deltaY && deltaY > touchSlop)) {
                    interceptStatus = 1
                    isDragging = true
                    gestureMoveListener?.onVerticalMove(e, x, y)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (interceptStatus == 0) {
                    if (mEnableClick && gestureMoveListener != null) {
                        gestureMoveListener.onClick(e, x, y)
                    }
                }
                interceptStatus = 0
                isDragging = false
            }
        }
        return interceptStatus == 2
    }

    interface OnGestureMoveListener {
        /**
         * 横向移动
         */
        fun onHorizontalMove(e: MotionEvent?, x: Float, y: Float)

        /**
         * 垂直移动
         */
        fun onVerticalMove(e: MotionEvent?, x: Float, y: Float)

        /**
         * 点击事件
         */
        fun onClick(e: MotionEvent?, x: Float, y: Float)
    }

}