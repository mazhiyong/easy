package com.lairui.easy.mywidget.pulltozoomview

import android.content.Context
import android.content.res.TypedArray
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListAdapter
import android.widget.ListView

class PullToZoomListViewEx @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PullToZoomBase<ListView>(context, attrs), AbsListView.OnScrollListener {
    private var mHeaderContainer: FrameLayout? = null
    private var mHeaderHeight: Int = 0
    private val mScalingRunnable: ScalingRunnable?

    /**
     * 是否显示headerView
     *
     * @param isHideHeader true: show false: hide
     */
    override var isHideHeader: Boolean
        get() = super.isHideHeader
        set(isHideHeader) {
            if (isHideHeader != isHideHeader) {
                super.isHideHeader = isHideHeader
                if (isHideHeader) {
                    removeHeaderView()
                } else {
                    updateHeaderView()
                }
            }
        }

    override var headerView: View?
        get() = super.headerView
        set(headerView) {
            if (headerView != null) {
                this.mHeaderView = headerView
                updateHeaderView()
            }
        }

    override var zoomView: View?
        get() = super.zoomView
        set(zoomView) {
            if (zoomView != null) {
                this.mZoomView = zoomView
                updateHeaderView()
            }
        }

    protected override val isReadyForPullStart: Boolean
        get() = isFirstItemVisible

    private
            /**
             * This check should really just be:
             * mRootView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
    val isFirstItemVisible: Boolean
        get() {
            val adapter = pullRootView?.adapter

            if (null == adapter || adapter.isEmpty) {
                return true
            } else {
                if (pullRootView!!.firstVisiblePosition <= 1) {
                    val firstVisibleChild = pullRootView?.getChildAt(0)
                    if (firstVisibleChild != null) {
                        return firstVisibleChild.top >= pullRootView!!.top
                    }
                }
            }

            return false
        }

    init {
        pullRootView?.setOnScrollListener(this)
        mScalingRunnable = ScalingRunnable()
    }

    /**
     * 移除HeaderView
     * 如果要兼容API 9,需要修改此处逻辑，API 11以下不支持动态添加header
     */
    private fun removeHeaderView() {
        if (mHeaderContainer != null) {
            pullRootView?.removeHeaderView(mHeaderContainer)
        }
    }

    /**
     * 更新HeaderView  先移除-->再添加zoomView、HeaderView -->然后添加到listView的head
     * 如果要兼容API 9,需要修改此处逻辑，API 11以下不支持动态添加header
     */
    private fun updateHeaderView() {
        if (mHeaderContainer != null) {
            pullRootView?.removeHeaderView(mHeaderContainer)

            mHeaderContainer!!.removeAllViews()

            if (mZoomView != null) {
                mHeaderContainer!!.addView(mZoomView)
            }

            if (mHeaderView != null) {
                mHeaderContainer!!.addView(mHeaderView)
            }

            mHeaderHeight = mHeaderContainer!!.height
            pullRootView?.addHeaderView(mHeaderContainer)
        }
    }

    fun setAdapter(adapter: ListAdapter) {
        pullRootView?.adapter = adapter
    }

    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener) {
        pullRootView?.onItemClickListener = listener
    }

    /**
     * 创建listView 如果要兼容API9,需要修改此处
     *
     * @param context 上下文
     * @param attrs   AttributeSet
     * @return ListView
     */
    override fun createRootView(context: Context, attrs: AttributeSet?): ListView {
        val lv = ListView(context, attrs)
        // Set it to this so it can be used in ListActivity/ListFragment
        lv.id = android.R.id.list
        return lv
    }

    /**
     * 重置动画，自动滑动到顶部
     */
    override fun smoothScrollToTop() {
        Log.d(TAG, "smoothScrollToTop --> ")
        mScalingRunnable!!.startAnimation(200L)
    }

    /**
     * zoomView动画逻辑
     *
     * @param newScrollValue 手指Y轴移动距离值
     */
    override fun pullHeaderToZoom(newScrollValue: Int) {
        Log.d(TAG, "pullHeaderToZoom --> newScrollValue = $newScrollValue")
        Log.d(TAG, "pullHeaderToZoom --> mHeaderHeight = $mHeaderHeight")
        if (mScalingRunnable != null && !mScalingRunnable.isFinished) {
            mScalingRunnable.abortAnimation()
        }

        val localLayoutParams = mHeaderContainer!!.layoutParams
        localLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight
        mHeaderContainer!!.layoutParams = localLayoutParams
    }

    override fun handleStyledAttributes(a: TypedArray) {
        mHeaderContainer = FrameLayout(context)
        if (mZoomView != null) {
            mHeaderContainer!!.addView(mZoomView)
        }
        if (mHeaderView != null) {
            mHeaderContainer!!.addView(mHeaderView)
        }

        pullRootView?.addHeaderView(mHeaderContainer)
    }

    /**
     * 设置HeaderView高度
     *
     * @param width  宽
     * @param height 高
     */
    fun setHeaderViewSize(width: Int, height: Int) {
        if (mHeaderContainer != null) {
            var localObject: Any? = mHeaderContainer!!.layoutParams
            if (localObject == null) {
                localObject = AbsListView.LayoutParams(width, height)
            }
            (localObject as ViewGroup.LayoutParams).width = width
            localObject.height = height
            mHeaderContainer!!.layoutParams = localObject
            mHeaderHeight = height
        }
    }

    fun setHeaderLayoutParams(layoutParams: AbsListView.LayoutParams) {
        if (mHeaderContainer != null) {
            mHeaderContainer!!.layoutParams = layoutParams
            mHeaderHeight = layoutParams.height
        }
    }

    override fun onLayout(paramBoolean: Boolean, paramInt1: Int, paramInt2: Int,
                          paramInt3: Int, paramInt4: Int) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4)
        Log.d(TAG, "onLayout --> ")
        if (mHeaderHeight == 0 && mHeaderContainer != null) {
            mHeaderHeight = mHeaderContainer!!.height
        }
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        Log.d(TAG, "onScrollStateChanged --> ")
    }

    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (mZoomView != null && !isHideHeader && isPullToZoomEnabled) {
            val f = (mHeaderHeight - mHeaderContainer!!.bottom).toFloat()
            Log.d(TAG, "onScroll --> f = $f")
            if (isParallax) {
                if (f > 0.0f && f < mHeaderHeight) {
                    val i = (0.65 * f).toInt()
                    mHeaderContainer!!.scrollTo(0, -i)
                } else if (mHeaderContainer!!.scrollY != 0) {
                    mHeaderContainer!!.scrollTo(0, 0)
                }
            }
        }
    }


    internal inner class ScalingRunnable : Runnable {
        protected var mDuration: Long = 0
        var isFinished = true
            protected set
        protected var mScale: Float = 0.toFloat()
        protected var mStartTime: Long = 0

        fun abortAnimation() {
            isFinished = true
        }

        override fun run() {
            if (mZoomView != null) {
                val f2: Float
                val localLayoutParams: ViewGroup.LayoutParams
                if (!isFinished && mScale > 1.0) {
                    val f1 = (SystemClock.currentThreadTimeMillis().toFloat() - mStartTime.toFloat()) / mDuration.toFloat()
                    f2 = mScale - (mScale - 1.0f) * PullToZoomListViewEx.sInterpolator.getInterpolation(f1)
                    localLayoutParams = mHeaderContainer!!.layoutParams
                    Log.d(TAG, "ScalingRunnable --> f2 = $f2")
                    if (f2 > 1.0f) {
                        localLayoutParams.height = (f2 * mHeaderHeight).toInt()
                        mHeaderContainer!!.layoutParams = localLayoutParams
                        post(this)
                        return
                    }
                    isFinished = true
                }
            }
        }

        fun startAnimation(paramLong: Long) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis()
                mDuration = paramLong
                mScale = mHeaderContainer!!.bottom.toFloat() / mHeaderHeight
                isFinished = false
                post(this)
            }
        }
    }

    companion object {
        private val TAG = PullToZoomListViewEx::class.java.simpleName

        private val sInterpolator = Interpolator { paramAnonymousFloat ->
            val f = paramAnonymousFloat - 1.0f
            1.0f + f * (f * (f * (f * f)))
        }
    }
}
