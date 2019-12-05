package com.lairui.easy.utils.tool

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

/**
 * 动画工具类
 * UpdateListener： 动画过程中通过添加此监听来回调数据
 * EndListener： 动画结束的时候通过此监听器来做一些处理
 */
class AnimUtil {

    private var valueAnimator: ValueAnimator? = null
    private var updateListener: UpdateListener? = null
    private var endListener: EndListener? = null
    private var duration: Long = 0
    private var start: Float = 0.toFloat()
    private var end: Float = 0.toFloat()
    private var interpolator: Interpolator = LinearInterpolator()

    init {
        duration = 1000 //默认动画时常1s
        start = 0.0f
        end = 1.0f
        interpolator = LinearInterpolator()// 匀速的插值器
    }


    fun setDuration(timeLength: Int) {
        duration = timeLength.toLong()
    }

    fun setValueAnimator(start: Float, end: Float, duration: Long) {

        this.start = start
        this.end = end
        this.duration = duration

    }

    fun setInterpolator(interpolator: Interpolator) {
        this.interpolator = interpolator
    }

    fun startAnimator() {
        if (valueAnimator != null) {
            valueAnimator = null
        }
        valueAnimator = ValueAnimator.ofFloat(start, end)
        valueAnimator!!.duration = duration
        valueAnimator!!.interpolator = interpolator
        valueAnimator!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            if (updateListener == null) {
                return@AnimatorUpdateListener
            }

            val cur = valueAnimator.animatedValue as Float
            updateListener!!.progress(cur)
        })
        valueAnimator!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                if (endListener == null) {
                    return
                }
                endListener!!.endUpdate(animator)
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        valueAnimator!!.start()
    }

    fun addUpdateListener(updateListener: UpdateListener) {

        this.updateListener = updateListener
    }

    fun addEndListner(endListener: EndListener) {
        this.endListener = endListener
    }

    interface EndListener {
        fun endUpdate(animator: Animator)
    }

    interface UpdateListener {

        fun progress(progress: Float)
    }

}
