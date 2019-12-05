package com.lairui.easy.mywidget.pulltozoomview

import android.content.Context
import android.content.res.TypedArray
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.utils.tool.LogUtil


class PullToZoomScrollViewEx @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) : PullToZoomBase<ScrollView>(mContext, attrs) {
    private var isCustomHeaderHeight = false//自定义header高度之后可能导致zoomView拉伸不正确
    private var mHeaderContainer: FrameLayout? = null
    private var mRootContainer: LinearLayout? = null
    private var mContentView: View? = null
    private var mHeaderHeight: Int = 0
    private val mScalingRunnable: ScalingRunnable?

    private val i = 0

    var topLay: View? = null

    /**
     * 是否显示headerView
     *
     * @param isHideHeader true: show false: hide
     */
    override var isHideHeader: Boolean
        get() = super.isHideHeader
        set(isHideHeader) {
            if (isHideHeader != isHideHeader && mHeaderContainer != null) {
                super.isHideHeader = isHideHeader
                if (isHideHeader) {
                    mHeaderContainer!!.visibility = View.GONE
                } else {
                    mHeaderContainer!!.visibility = View.VISIBLE
                }
            }
        }

    override var headerView: View?
        get() = super.headerView
        set(headerView) {
            if (headerView != null) {
                mHeaderView = headerView
                updateHeaderView()
            }
        }

    override var zoomView: View?
        get() = super.zoomView
        set(zoomView) {
            if (zoomView != null) {
                mZoomView = zoomView
                updateHeaderView()
            }
        }

    protected override val isReadyForPullStart: Boolean
        get() = pullRootView?.scrollY == 0

    init {
        mScalingRunnable = ScalingRunnable()
        (pullRootView as InternalScrollView).setOnScrollViewChangedListener(object : OnScrollViewChangedListener {
            override fun onInternalScrollChanged(left: Int, top: Int, oldLeft: Int, oldTop: Int) {
                if (isPullToZoomEnabled && isParallax) {

                    if (topLay != null) {
                        if (pullRootView!!.scrollY <= 0) {
                            topLay!!.setBackgroundResource(R.color.title_bg)
                            topLay!!.background.mutate().alpha = 0//0~255透明度值 ，0为完全透明，255为不透明
                            (topLay!!.findViewById<View>(R.id.title_text) as TextView).text = ""
                        } else if (pullRootView!!.scrollY <= 440) {
                            topLay!!.setBackgroundResource(R.color.title_bg)
                            topLay!!.background.mutate().alpha = pullRootView!!.scrollY / 2//0~255透明度值 ，0为完全透明，255为不透明
                            (topLay!!.findViewById<View>(R.id.title_text) as TextView).text = ""
                        } else {
                            topLay!!.setBackgroundResource(R.color.title_bg)
                            topLay!!.background.mutate().alpha = 225
                            (topLay!!.findViewById<View>(R.id.title_text) as TextView).text = "person_title"
                        }
                    }

                    Log.d(TAG, "onScrollChanged --> getScrollY() = " + pullRootView!!.scrollY)
                    val f = (mHeaderHeight - mHeaderContainer!!.bottom + pullRootView!!.scrollY).toFloat()
                    Log.d(TAG, "onScrollChanged --> f = $f")
                    if (f > 0.0f && f < mHeaderHeight) {
                        val i = (0.65 * f).toInt()
                        mHeaderContainer!!.scrollTo(0, -i)


                    } else if (mHeaderContainer!!.scrollY != 0) {
                        mHeaderContainer!!.scrollTo(0, 0)
                    }
                }
            }
        })
    }

    override fun pullHeaderToZoom(newScrollValue: Int) {
        Log.d(TAG, "pullHeaderToZoom --> newScrollValue = $newScrollValue")
        Log.d(TAG, "pullHeaderToZoom --> mHeaderHeight = $mHeaderHeight")
        if (mScalingRunnable != null && !mScalingRunnable.isFinished) {
            mScalingRunnable.abortAnimation()
        }

        val localLayoutParams = mHeaderContainer!!.layoutParams
        localLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight
        mHeaderContainer!!.layoutParams = localLayoutParams

        if (isCustomHeaderHeight) {
            val zoomLayoutParams = mZoomView!!.layoutParams
            zoomLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight
            mZoomView!!.layoutParams = zoomLayoutParams
        }

        LogUtil.i("打印log日志", "###############################################################")
    }

    /**
     * 更新HeaderView  先移除-->再添加zoomView、HeaderView -->然后添加到listView的head
     */
    private fun updateHeaderView() {
        if (mHeaderContainer != null) {
            mHeaderContainer!!.removeAllViews()

            if (mZoomView != null) {
                mHeaderContainer!!.addView(mZoomView)
            }

            if (mHeaderView != null) {
                mHeaderContainer!!.addView(mHeaderView)
            }
        }
    }

    fun setScrollContentView(contentView: View?) {
        if (contentView != null) {
            if (mContentView != null) {
                mRootContainer!!.removeView(mContentView)
            }
            mContentView = contentView
            mRootContainer!!.addView(mContentView)
        }
    }

    override fun createRootView(context: Context, attrs: AttributeSet?): ScrollView {
        val scrollView = InternalScrollView(context, attrs)
        scrollView.id = R.id.scrollview
        return scrollView
    }

    override fun smoothScrollToTop() {
        Log.d(TAG, "smoothScrollToTop --> ")
        mScalingRunnable!!.startAnimation(200L)
    }

    override fun handleStyledAttributes(a: TypedArray) {
        mRootContainer = LinearLayout(context)
        mRootContainer!!.orientation = LinearLayout.VERTICAL
        mHeaderContainer = FrameLayout(context)

        if (mZoomView != null) {
            mHeaderContainer!!.addView(mZoomView)
        }
        if (mHeaderView != null) {
            mHeaderContainer!!.addView(mHeaderView)
        }
        val contentViewResId = a.getResourceId(R.styleable.PullToZoomView_contentView, 0)
        if (contentViewResId > 0) {
            val mLayoutInflater = LayoutInflater.from(context)
            mContentView = mLayoutInflater.inflate(contentViewResId, null, false)
        }

        mRootContainer!!.addView(mHeaderContainer)
        if (mContentView != null) {
            mRootContainer!!.addView(mContentView)
        }

        mRootContainer!!.clipChildren = false
        mHeaderContainer!!.clipChildren = false

        pullRootView!!.addView(mRootContainer)
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
                localObject = ViewGroup.LayoutParams(width, height)
            }
            (localObject as ViewGroup.LayoutParams).width = width
            localObject.height = height
            mHeaderContainer!!.layoutParams = localObject
            mHeaderHeight = height
            isCustomHeaderHeight = true
        }
    }

    /**
     * 设置HeaderView LayoutParams
     *
     * @param layoutParams LayoutParams
     */
    fun setHeaderLayoutParams(layoutParams: LinearLayout.LayoutParams) {
        if (mHeaderContainer != null) {
            mHeaderContainer!!.layoutParams = layoutParams
            mHeaderHeight = layoutParams.height
            isCustomHeaderHeight = true
        }
    }

    override fun onLayout(paramBoolean: Boolean, paramInt1: Int, paramInt2: Int,
                          paramInt3: Int, paramInt4: Int) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4)
        Log.d(TAG, "onLayout --> ")
        if (mHeaderHeight == 0 && mZoomView != null) {
            mHeaderHeight = mHeaderContainer!!.height
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
                    f2 = mScale - (mScale - 1.0f) * PullToZoomScrollViewEx.sInterpolator.getInterpolation(f1)
                    localLayoutParams = mHeaderContainer!!.layoutParams
                    Log.d(TAG, "ScalingRunnable --> f2 = $f2")
                    if (f2 > 1.2f) {
                        //                        LogUtil.i("打印log日志","##############ddddddddddddddd#################################################");
                        //                        Toast.makeText(mContext,"ddd",Toast.LENGTH_LONG).show();
                    }
                    if (f2 > 1.0f) {
                        localLayoutParams.height = (f2 * mHeaderHeight).toInt()
                        mHeaderContainer!!.layoutParams = localLayoutParams
                        if (isCustomHeaderHeight) {
                            val zoomLayoutParams: ViewGroup.LayoutParams
                            zoomLayoutParams = mZoomView!!.layoutParams
                            zoomLayoutParams.height = (f2 * mHeaderHeight).toInt()
                            mZoomView!!.layoutParams = zoomLayoutParams
                        }
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

    protected inner class InternalScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ScrollView(context, attrs) {
        private var onScrollViewChangedListener: OnScrollViewChangedListener? = null

        fun setOnScrollViewChangedListener(onScrollViewChangedListener: OnScrollViewChangedListener) {
            this.onScrollViewChangedListener = onScrollViewChangedListener
        }

        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
            super.onScrollChanged(l, t, oldl, oldt)
            if (onScrollViewChangedListener != null) {
                onScrollViewChangedListener!!.onInternalScrollChanged(l, t, oldl, oldt)
            }
        }
    }

    protected interface OnScrollViewChangedListener {
        fun onInternalScrollChanged(left: Int, top: Int, oldLeft: Int, oldTop: Int)
    }

    companion object {
        private val TAG = PullToZoomScrollViewEx::class.java.simpleName

        private val sInterpolator = Interpolator { paramAnonymousFloat ->
            val f = paramAnonymousFloat - 1.0f
            1.0f + f * (f * (f * (f * f)))
        }
    }

}
