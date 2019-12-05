package com.lairui.easy.mywidget.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.github.jdsjlzx.interfaces.IRefreshHeader
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.github.jdsjlzx.view.SimpleViewSwitcher
import com.lairui.easy.R

import java.util.Date


class MyRefreshHeader : LinearLayout, IRefreshHeader {

    private var mContainer: LinearLayout? = null
    private var mArrowImageView: ImageView? = null
    private var mProgressBar: SimpleViewSwitcher? = null
    private var mStatusTextView: TextView? = null
    // 显示进度
    // 显示箭头图片
    //mStatusTextView.setTextColor(ContextCompat.getColor(getContext(), hintColor));
    //mArrowImageView.startAnimation(mRotateDownAnim);
    // mArrowImageView.startAnimation(mRotateUpAnim);
    var state = IRefreshHeader.STATE_NORMAL
        set(state) {
            if (state == this.state) return

            if (state == IRefreshHeader.STATE_REFRESHING) {
                mArrowImageView!!.clearAnimation()
                mArrowImageView!!.visibility = View.INVISIBLE
                mProgressBar!!.visibility = View.VISIBLE
                smoothScrollTo(mMeasuredHeight)
            } else if (state == IRefreshHeader.STATE_DONE) {
                mArrowImageView!!.visibility = View.INVISIBLE
                mProgressBar!!.visibility = View.INVISIBLE
            } else {
                mArrowImageView!!.visibility = View.VISIBLE
                mProgressBar!!.visibility = View.INVISIBLE
            }

            when (state) {
                IRefreshHeader.STATE_NORMAL -> {
                    if (this.state == IRefreshHeader.STATE_RELEASE_TO_REFRESH) {
                    }
                    if (this.state == IRefreshHeader.STATE_REFRESHING) {
                        mArrowImageView!!.clearAnimation()
                    }
                    mStatusTextView!!.setText(R.string.listview_header_hint_normal)
                }
                IRefreshHeader.STATE_RELEASE_TO_REFRESH -> if (this.state != IRefreshHeader.STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView!!.clearAnimation()
                    mStatusTextView!!.setText(R.string.listview_header_hint_release)
                }
                IRefreshHeader.STATE_REFRESHING -> mStatusTextView!!.setText(R.string.refreshing)
                IRefreshHeader.STATE_DONE -> mStatusTextView!!.setText(R.string.refresh_done)
            }

            field = state
        }

    private var mHeaderTimeView: TextView? = null

    private var mRotateUpAnim: Animation? = null
    private var mRotateDownAnim: Animation? = null

    var mMeasuredHeight: Int = 0

    private var hintColor: Int = 0

    constructor(context: Context) : super(context) {
        initView()
    }

    /**
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        // 初始情况，设置下拉刷新view高度为0
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 0, 0, 0)
        this.layoutParams = lp
        this.setPadding(0, 0, 0, 0)

        mContainer = LayoutInflater.from(context).inflate(R.layout.my_listview_header, null) as LinearLayout
        addView(mContainer, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0))
        gravity = Gravity.BOTTOM

        mArrowImageView = findViewById<View>(R.id.listview_header_arrow) as ImageView
        mStatusTextView = findViewById<View>(R.id.refresh_status_textview) as TextView

        //init the progress view
        mProgressBar = findViewById<View>(R.id.listview_header_progressbar) as SimpleViewSwitcher
        mProgressBar!!.setView(initIndicatorView(ProgressStyle.BallSpinFadeLoader))

        mRotateUpAnim = RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mRotateUpAnim!!.duration = ROTATE_ANIM_DURATION.toLong()
        mRotateUpAnim!!.fillAfter = true
        mRotateDownAnim = RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mRotateDownAnim!!.duration = ROTATE_ANIM_DURATION.toLong()
        mRotateDownAnim!!.fillAfter = true

        mHeaderTimeView = findViewById<View>(R.id.last_refresh_time) as TextView
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mMeasuredHeight = measuredHeight
        hintColor = android.R.color.darker_gray
    }

    fun setProgressStyle(style: Int) {
        if (style == ProgressStyle.SysProgress) {
            val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyle)
            mProgressBar!!.setView(progressBar)
        } else {
            mProgressBar!!.setView(initIndicatorView(style))
        }
    }

    private fun initIndicatorView(style: Int): View {
        /*AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) LayoutInflater.from(getContext()).inflate(R.layout.layout_indicator_view, null);
        progressView.setIndicatorId(style);
        progressView.setIndicatorColor(Color.GRAY);*/


        val progressView = LayoutInflater.from(context).inflate(R.layout.refresh_anim_view, null) as ImageView
        val aniDrawable: AnimationDrawable
        aniDrawable = progressView.drawable as AnimationDrawable
        aniDrawable.start()
        return progressView
    }

    /*   public void setIndicatorColor(int color) {
        if(mProgressBar.getChildAt(0) instanceof AVLoadingIndicatorView){
            AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) mProgressBar.getChildAt(0);
            progressView.setIndicatorColor(color);
        }
    }*/

    fun setHintTextColor(color: Int) {
        this.hintColor = color
    }

    fun setViewBackgroundColor(color: Int) {
        this.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    fun setArrowImageView(resid: Int) {
        mArrowImageView!!.setImageResource(resid)
    }

    override fun refreshComplete() {
        mHeaderTimeView!!.text = friendlyTime(Date())
        state = IRefreshHeader.STATE_DONE
        Handler().postDelayed({ reset() }, 200)
    }

    override fun getHeaderView(): View {
        return this
    }

    fun setVisibleHeight(height: Int) {
        var height = height
        if (height < 0) height = 0
        val lp = mContainer!!.layoutParams as LinearLayout.LayoutParams
        lp.height = height
        mContainer!!.layoutParams = lp
    }

    override fun getVisibleHeight(): Int {
        val lp = mContainer!!.layoutParams as LinearLayout.LayoutParams
        return lp.height
    }

    override fun getVisibleWidth(): Int {
        return 0
    }

    override fun getType(): Int {
        return IRefreshHeader.STATE_NORMAL
    }

    override fun onReset() {
        state = IRefreshHeader.STATE_NORMAL
    }

    override fun onPrepare() {
        state = IRefreshHeader.STATE_RELEASE_TO_REFRESH
    }

    override fun onRefreshing() {
        state = IRefreshHeader.STATE_REFRESHING
    }

    override fun onMove(offSet: Float, sumOffSet: Float) {

        if (visibleHeight > 0 || offSet > 0) {
            visibleHeight = offSet.toInt() + visibleHeight
            if (state <= IRefreshHeader.STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (visibleHeight > mMeasuredHeight) {
                    onPrepare()
                } else {
                    onReset()
                }
            }
        }
    }

    override fun onRelease(): Boolean {
        var isOnRefresh = false
        val height = visibleHeight
        if (height == 0) {// not visible.
            isOnRefresh = false
        }

        if (visibleHeight > mMeasuredHeight && state < IRefreshHeader.STATE_REFRESHING) {
            state = IRefreshHeader.STATE_REFRESHING
            isOnRefresh = true
        }
        // refreshing and header isn't shown fully. do nothing.
        if (state == IRefreshHeader.STATE_REFRESHING && height > mMeasuredHeight) {
            smoothScrollTo(mMeasuredHeight)
        }
        if (state != IRefreshHeader.STATE_REFRESHING) {
            smoothScrollTo(0)
        }

        if (state == IRefreshHeader.STATE_REFRESHING) {
            val destHeight = mMeasuredHeight
            smoothScrollTo(destHeight)
        }

        return isOnRefresh
    }

    fun reset() {
        smoothScrollTo(0)
        Handler().postDelayed({ state = IRefreshHeader.STATE_NORMAL }, 500)
    }

    private fun smoothScrollTo(destHeight: Int) {
        val animator = ValueAnimator.ofInt(visibleHeight, destHeight)
        animator.setDuration(300).start()
        animator.addUpdateListener { animation -> visibleHeight = animation.animatedValue as Int }
        animator.start()
    }

    fun friendlyTime(time: Date): String {
        val ct = ((System.currentTimeMillis() - time.time) / 1000).toInt()

        if (ct == 0) {
            return context.resources.getString(R.string.text_just)
        }

        if (ct > 0 && ct < 60) {
            return ct.toString() + context.resources.getString(R.string.text_seconds_ago)
        }

        if (ct >= 60 && ct < 3600) {
            return Math.max(ct / 60, 1).toString() + context.resources.getString(R.string.text_minute_ago)
        }
        if (ct >= 3600 && ct < 86400)
            return (ct / 3600).toString() + context.resources.getString(R.string.text_hour_ago)
        if (ct >= 86400 && ct < 2592000) { //86400 * 30
            val day = ct / 86400
            return day.toString() + context.resources.getString(R.string.text_day_ago)
        }
        return if (ct >= 2592000 && ct < 31104000) { //86400 * 30
            (ct / 2592000).toString() + context.resources.getString(R.string.text_month_ago)
        } else (ct / 31104000).toString() + context.resources.getString(R.string.text_year_ago)
    }

    companion object {

        private val ROTATE_ANIM_DURATION = 180
    }

}