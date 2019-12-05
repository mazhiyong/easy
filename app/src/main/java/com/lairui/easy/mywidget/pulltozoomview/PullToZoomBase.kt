package com.lairui.easy.mywidget.pulltozoomview

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.LinearLayout

import com.lairui.easy.R
/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2014/11/7  14:18.
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2014/11/7        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
abstract class PullToZoomBase<T : View> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), IPullToZoom<T> {
    override var pullRootView: T? = null
        set(value: T?) {
            field = value
            field = value
        }
    protected var mHeaderView: View? = null//头部View
    protected var mZoomView: View? = null//缩放拉伸View

    protected var mScreenHeight: Int = 0
    protected var mScreenWidth: Int = 0

    override var isPullToZoomEnabled = true
           set(value: Boolean) {
            field = value
            field = value
        }
    override var isParallax = true
    override var isZooming = false
            set(value: Boolean) {
            field = value
            field = value
        }
    override//header显示才能Zoom
    var isHideHeader = false

    private var mTouchSlop: Int = 0
    private var mIsBeingDragged = false
    private var mLastMotionY: Float = 0.toFloat()
    private var mLastMotionX: Float = 0.toFloat()
    private var mInitialMotionY: Float = 0.toFloat()
    private var mInitialMotionX: Float = 0.toFloat()
    private var onPullZoomListener: OnPullZoomListener? = null

    override var zoomView: View? = null
        get() = mZoomView


    override var headerView: View? = null
        get() = mHeaderView

    protected abstract val isReadyForPullStart: Boolean

    init {

        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        gravity = Gravity.CENTER

        val config = ViewConfiguration.get(context)
        mTouchSlop = config.scaledTouchSlop

        val localDisplayMetrics = DisplayMetrics()
        (getContext() as Activity).windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        mScreenHeight = localDisplayMetrics.heightPixels
        mScreenWidth = localDisplayMetrics.widthPixels

        // Refreshable View
        // By passing the attrs, we can add ListView/GridView params via XML
        pullRootView = createRootView(context, attrs)

        if (attrs != null) {
            val mLayoutInflater = LayoutInflater.from(getContext())
            //初始化状态View
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.PullToZoomView)

            val zoomViewResId = a.getResourceId(R.styleable.PullToZoomView_zoomView, 0)
            if (zoomViewResId > 0) {
                mZoomView = mLayoutInflater.inflate(zoomViewResId, null, false)
            }

            val headerViewResId = a.getResourceId(R.styleable.PullToZoomView_headerView, 0)
            if (headerViewResId > 0) {
                mHeaderView = mLayoutInflater.inflate(headerViewResId, null, false)
            }

            isParallax = a.getBoolean(R.styleable.PullToZoomView_isHeaderParallax, true)

            // Let the derivative classes have a go at handling attributes, then
            // recycle them...
            handleStyledAttributes(a)
            a.recycle()
        }
        addView(pullRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun setOnPullZoomListener(onPullZoomListener: OnPullZoomListener) {
        this.onPullZoomListener = onPullZoomListener
    }

    fun setZoomEnabled(isZoomEnabled: Boolean) {
        this.isPullToZoomEnabled = isZoomEnabled
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!isPullToZoomEnabled || isHideHeader) {
            return false
        }

        val action = event.action

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false
            return false
        }

        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true
        }
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                if (isReadyForPullStart) {
                    val y = event.y
                    val x = event.x
                    val diff: Float
                    val oppositeDiff: Float
                    val absDiff: Float

                    // We need to use the correct values, based on scroll
                    // direction
                    diff = y - mLastMotionY
                    oppositeDiff = x - mLastMotionX
                    absDiff = Math.abs(diff)

                    if (absDiff > mTouchSlop && absDiff > Math.abs(oppositeDiff)) {
                        if (diff >= 1f && isReadyForPullStart) {
                            mLastMotionY = y
                            mLastMotionX = x
                            mIsBeingDragged = true
                        }
                    }
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (isReadyForPullStart) {
                    mInitialMotionY = event.y
                    mLastMotionY = mInitialMotionY
                    mInitialMotionX = event.x
                    mLastMotionX = mInitialMotionX
                    mIsBeingDragged = false
                }
            }
        }

        return mIsBeingDragged
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isPullToZoomEnabled || isHideHeader) {
            return false
        }

        if (event.action == MotionEvent.ACTION_DOWN && event.edgeFlags != 0) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (mIsBeingDragged) {
                    mLastMotionY = event.y
                    mLastMotionX = event.x
                    pullEvent()
                    isZooming = true
                    return true
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (isReadyForPullStart) {
                    mInitialMotionY = event.y
                    mLastMotionY = mInitialMotionY
                    mInitialMotionX = event.x
                    mLastMotionX = mInitialMotionX
                    return true
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (mIsBeingDragged) {
                    mIsBeingDragged = false
                    // If we're already refreshing, just scroll back to the top
                    if (isZooming) {
                        smoothScrollToTop()
                        if (onPullZoomListener != null) {
                            onPullZoomListener!!.onPullZoomEnd()
                        }
                        isZooming = false
                        return true
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun pullEvent() {
        val newScrollValue: Int
        val initialMotionValue: Float
        val lastMotionValue: Float

        initialMotionValue = mInitialMotionY
        lastMotionValue = mLastMotionY

        newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0f) / FRICTION)

        pullHeaderToZoom(newScrollValue)
        if (onPullZoomListener != null) {
            onPullZoomListener!!.onPullZooming(newScrollValue)
        }
    }

    protected abstract fun pullHeaderToZoom(newScrollValue: Int)

    protected abstract fun createRootView(context: Context, attrs: AttributeSet?): T

    protected abstract fun smoothScrollToTop()

    interface OnPullZoomListener {
        fun onPullZooming(newScrollValue: Int)

        fun onPullZoomEnd()
    }

    companion object {
        private val FRICTION = 2.0f
    }
}
