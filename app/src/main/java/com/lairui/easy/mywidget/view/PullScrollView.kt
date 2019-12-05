package com.lairui.easy.mywidget.view


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.ScrollView

import com.github.jdsjlzx.interfaces.IRefreshHeader
import com.github.jdsjlzx.recyclerview.AppBarStateChangeListener
import com.github.jdsjlzx.view.ArrowRefreshHeader
import com.google.android.material.appbar.AppBarLayout

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat


/**
 * 自定义下拉刷新ScrollView
 *
 *
 */
class PullScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ScrollView(context, attrs, defStyle) {
    private var mRefreshListener: RefreshListener? = null
    private var scrollViewListener: OnScrollChangeListener? = null

    private var mRefreshHeader: IRefreshHeader? = null
    private var isRefreshEnabled = true    //设置下拉刷新是否可用
    private var dragRate = 2f       //下拉刷新滑动阻力系数，越大需要手指下拉的距离越大才能刷新

    private var isRefreshing: Boolean = false   //是否正在刷新
    private var mLastY = -1f      //上次触摸的的Y值
    private var topY: Int = 0
    private var sumOffSet: Float = 0.toFloat()
    private var isAdded: Boolean = false

    private var appbarState: AppBarStateChangeListener.State = AppBarStateChangeListener.State.EXPANDED

    val refreshHeaderView: View
        get() = mRefreshHeader!!.headerView

    /**
     * 如果在HeaderView已经被添加到布局中，说明已经到顶部
     */
    private val isOnTop: Boolean
        get() = topY == 0

    init {
        init()
    }

    private fun init() {
        if (isRefreshEnabled) {
            mRefreshHeader = ArrowRefreshHeader(context)
        }

        viewTreeObserver.addOnGlobalLayoutListener {
            if (!isAdded) {
                isAdded = true

                //解决和AppBarLayout冲突的问题
                var p: ViewParent? = parent
                while (p != null) {
                    if (p is CoordinatorLayout) {
                        break
                    }
                    p = p.parent
                }

                if (p != null) {
                    var appBarLayout: AppBarLayout? = null
                    val coordinatorLayout = p as CoordinatorLayout?
                    val childCount = coordinatorLayout!!.childCount
                    for (i in childCount - 1 downTo 0) {
                        val child = coordinatorLayout.getChildAt(i)
                        if (child is AppBarLayout) {
                            appBarLayout = child
                            break
                        }
                    }

                    appBarLayout?.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
                        override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {
                            appbarState = state
                        }
                    })
                }

                setLayout()
            }
        }
    }

    private fun setLayout() {
        val group = parent as ViewGroup
        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        val index = group.indexOfChild(this)
        group.removeView(this)
        group.addView(container, index, layoutParams)

        container.addView(mRefreshHeader!!.headerView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        container.addView(this, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    /**
     * 设置下拉刷新上拉加载回调
     */
    fun setRefreshListener(listener: RefreshListener) {
        mRefreshListener = listener
    }



    /**
     * 设置自定义的header
     */
    fun setRefreshHeader(mRefreshHeader: IRefreshHeader) {
        this.mRefreshHeader = mRefreshHeader
    }

    /**
     * 下拉刷新是否可用
     */
    fun setPullRefreshEnabled(enabled: Boolean) {
        isRefreshEnabled = enabled
    }

    /**
     * 下拉刷新滑动阻力系数，越大需要手指下拉的距离越大才能刷新
     */
    fun setDragRate(dragRate: Int) {
        this.dragRate = dragRate.toFloat()
    }

    /**
     * 设置下拉刷新的进度条风格
     */
    fun setRefreshProgressStyle(style: Int) {
        if (mRefreshHeader != null && mRefreshHeader is ArrowRefreshHeader) {
            (mRefreshHeader as ArrowRefreshHeader).setProgressStyle(style)
        }
    }

    /**
     * 设置下拉刷新的箭头图标
     */
    fun setArrowImageView(resId: Int) {
        if (mRefreshHeader != null && mRefreshHeader is ArrowRefreshHeader) {
            (mRefreshHeader as ArrowRefreshHeader).setArrowImageView(resId)
        }
    }

    /**
     * 设置颜色
     * @param indicatorColor Only call the method setRefreshProgressStyle(int style) to take effect
     * @param hintColor
     * @param backgroundColor
     */
    fun setHeaderViewColor(indicatorColor: Int, hintColor: Int, backgroundColor: Int) {
        if (mRefreshHeader != null && mRefreshHeader is ArrowRefreshHeader) {
            val arrowRefreshHeader = mRefreshHeader as ArrowRefreshHeader?
            arrowRefreshHeader?.setIndicatorColor(ContextCompat.getColor(context, indicatorColor))
            arrowRefreshHeader?.setHintTextColor(hintColor)
            arrowRefreshHeader?.setViewBackgroundColor(backgroundColor)
        }

    }


    /**
     * 手动调用直接刷新，无下拉效果
     */
    fun refresh() {
        if (mRefreshListener != null) {
            isRefreshing = true
            mRefreshListener!!.onRefresh()
        }
    }

    /**
     * 手动调用下拉刷新，有下拉效果
     */
    fun refreshWithPull() {
        setRefreshing(true)
        refresh()
    }

    /**
     * 下拉刷新和到底加载完成
     */
    fun setRefreshCompleted() {
        if (isRefreshing) {
            isRefreshing = false
            mRefreshHeader!!.refreshComplete()
        }
    }

    /**
     * 手动调用加载状态，此函数不会调用 [RefreshListener.onRefresh]加载数据
     * 如果需要加载数据和状态显示调用 [.refreshWithPull]
     */
    fun setRefreshing(refreshing: Boolean) {
        if (refreshing && isRefreshEnabled) {
            isRefreshing = true
            mRefreshHeader!!.onRefreshing()

            val offSet = mRefreshHeader!!.headerView
                    .measuredHeight
            mRefreshHeader!!.onMove(offSet.toFloat(), offSet.toFloat())
        }
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mLastY == -1f) {
            mLastY = ev.rawY
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = ev.rawY
                sumOffSet = 0f
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = (ev.rawY - mLastY) / dragRate
                mLastY = ev.rawY
                sumOffSet += deltaY
                if (isOnTop && isRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    mRefreshHeader!!.onMove(deltaY, sumOffSet)
                    if (mRefreshHeader!!.visibleHeight > 0 && !isRefreshing) {
                        return false
                    }
                }
            }
            else -> {
                mLastY = -1f // reset
                if (isOnTop && isRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (mRefreshHeader!!.onRelease()) {
                        if (mRefreshListener != null) {
                            isRefreshing = true
                            mRefreshListener!!.onRefresh()
                        }
                    }
                } else if (isRefreshing && mRefreshHeader!!.onRelease()) {
                    mRefreshListener!!.onRefresh()
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        topY = t
        if (scrollViewListener != null) {
            scrollViewListener!!.onScrollChange(this, l, t, oldl, oldt)
        }
    }

    fun setScrollViewListener(scrollViewListener: OnScrollChangeListener) {
        this.scrollViewListener = scrollViewListener
    }


    interface RefreshListener {
        fun onRefresh()
    }

    interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param v The view whose scroll position has changed.
         * @param scrollX Current horizontal scroll origin.
         * @param scrollY Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        fun onScrollChange(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
    }

}