package com.lairui.easy.mywidget.viewpager


import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout


import com.lairui.easy.R

import java.util.ArrayList


/**
 * Created by yukuo on 2016/5/10.
 * 这是一个横向滚动的viewpager
 */
class BannerViewPager : LinearLayout, ViewPager.OnPageChangeListener {
    private var onItemClickListener: OnItemClickListener? = null
    private var fl_banner_viewpager: FrameLayout? = null
    private var ll_scroll_banner_indicator: LinearLayout? = null
    private var vp_banner: SetingScrollBanner? = null
    private var list: List<String> = ArrayList()//数据源集合
    private val imageViews = ArrayList<ImageView>()//图片集合
    private var scrolltime = 3000 // 默认滚动时间
    private var releaseTime: Long = 0 // 手指松开、页面不滚动时间，防止手机松开后短时间进行切换
    private val LOOP = 200 // 转动
    private val LOOP_WAIT = 201 // 等待
    //是否循环 默认循环
    /**
     * 是否处于循环状态
     *
     * @return
     */
    /**
     * 是否循环，默认不开启，开启前，请将views的最前面与最后面各加入一个视图，用于循环
     * 如果需要设置请在添加数据之前的时候进行使用
     *
     * @param isLoop 是否循环
     */
    var isLoop = true
    var isWhee = true // 是否轮播
    private var isScrolling = false//是否在滚动
    private var indicators: Array<ImageView?>? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var currentPosition: Int = 0
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == LOOP && imageViews.size != 0) {
                if (!isScrolling) {
                    val max = imageViews.size + 1
                    val position = (currentPosition + 1) % imageViews.size
                    vp_banner!!.setCurrentItem(position, true)
                    if (position == max) { // 最后一页时回到第一页
                        vp_banner!!.setCurrentItem(1, false)
                    }
                }

                releaseTime = System.currentTimeMillis()
                this.removeCallbacks(runnable)
                this.postDelayed(runnable, scrolltime.toLong())
                return
            }
            if (msg.what == LOOP_WAIT && imageViews.size != 0) {
                this.removeCallbacks(runnable)
                this.postDelayed(runnable, scrolltime.toLong())
            }

        }
    }

    internal val runnable: Runnable = Runnable {
        if (context != null && isWhee) {
            val now = System.currentTimeMillis()
            // 检测上一次滑动时间与本次之间是否有触击(手滑动)操作，有的话等待下次轮播
            if (now - releaseTime > scrolltime - 500) {
                handler.sendEmptyMessage(LOOP)
            } else {
                handler.sendEmptyMessage(LOOP_WAIT)
            }
        }
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        //设置宽高
        layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val view = View.inflate(context, R.layout.view_scrollbanner, null)
        fl_banner_viewpager = view.findViewById<View>(R.id.fl_banner_viewpager) as FrameLayout
        vp_banner = view.findViewById<View>(R.id.vp_banner) as SetingScrollBanner
        ll_scroll_banner_indicator = view.findViewById<View>(R.id.ll_scroll_banner_indicator) as LinearLayout
        addView(view)
    }

    /**
     * 设置是否轮播，默认不轮播,轮播一定是循环的
     *
     * @param isWheel
     */
    fun setWheel(isWhel: Boolean?) {
        this.isWhee = isWhel!!
        isLoop = true
        if (isWhel) {
            handler.postDelayed(runnable, scrolltime.toLong())
        }
    }

    /**
     * 是否处于轮播状态
     *
     * @return
     */
    fun isWheel(): Boolean {
        return isWhee
    }


    /**
     * 设置数据源
     *
     * @param views        view集合
     * @param list         数据源集合
     * @param listener     回调监听
     * @param showPosition 默认选中的位置
     */
    @JvmOverloads
    fun setData(views: List<ImageView>, list: List<String>, listener: OnItemClickListener, showPosition: Int = 0) {
        var showPosition = showPosition
        this.onItemClickListener = listener
        this.list = list
        imageViews.clear()
        /**
         * 如果没有数据的话就隐藏展示
         */
        if (list.size == 0) {
            fl_banner_viewpager!!.visibility = View.GONE
            return
        }
        /**
         * 如果只有一个图片就不显示下边的指针了
         */
        if (list.size == 1) {
            setScrollable(false)
            isWhee = false
            ll_scroll_banner_indicator!!.visibility = View.GONE
            this.imageViews.add(views[0])
        } else {
            //添加数据
            for (item in views) {
                this.imageViews.add(item)
            }
        }

        //设置指示器
        val ivSize = views.size
        indicators = arrayOfNulls<ImageView>(ivSize)
        //如果是需呀循环的话就重新设置指示器
        if (isLoop)
            indicators = arrayOfNulls(ivSize - 2)
        ll_scroll_banner_indicator!!.removeAllViews()
        for (i in indicators!!.indices) {
            val view = View.inflate(context, R.layout.view_banner_viewpager_indicator, null)
            indicators!![i] = view.findViewById<View>(R.id.iv_baner_indicator) as ImageView
            ll_scroll_banner_indicator!!.addView(view)
        }
        viewPagerAdapter = ViewPagerAdapter()
        // 默认指向第一项，下方viewPager.setCurrentItem将触发重新计算指示器指向
        setIndicator(0)
        vp_banner!!.offscreenPageLimit = 3//默认缓存所有的界面
        vp_banner!!.setOnPageChangeListener(this)
        vp_banner!!.adapter = viewPagerAdapter
        if (showPosition < 0 || showPosition > views.size) {
            showPosition = 0
        }
        if (isLoop) {
            showPosition = showPosition + 1
        }
        vp_banner!!.currentItem = showPosition
    }

    /**
     * 设置指示器
     *
     * @param selectedPosition 默认指示器位置
     */
    private fun setIndicator(selectedPosition: Int) {
        for (i in indicators!!.indices) {
            indicators!![i]?.setBackgroundResource(R.drawable.icon_point)
        }
        if (indicators!!.size > selectedPosition)
            indicators!![selectedPosition]?.setBackgroundResource(R.drawable.icon_point_pre)
    }


    /**
     * 设置viewpager是否可以滚动
     *
     * @param enable 是否
     */
    fun setScrollable(enable: Boolean) {
        vp_banner!!.setScrollable(enable)
    }

    /**
     * 设置轮播暂停时间，即没多少秒切换到下一张视图.默认3000ms
     *
     * @param time 毫秒为单位
     */
    fun setScrollTime(time: Int) {
        this.scrolltime = time
    }

    /**
     * 设置指示器居中，默认指示器在右方
     */
    fun setIndicatorCenter() {
        val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        ll_scroll_banner_indicator!!.layoutParams = params
    }

    /**
     * 刷新数据，当外部视图更新后，通知刷新数据
     */
    fun refreshData() {
        if (viewPagerAdapter != null)
            viewPagerAdapter!!.notifyDataSetChanged()
    }

    /**
     * banner点击的监听器
     */
    interface OnItemClickListener {
        /**
         * 条目点击回调的方法
         *
         * @param postion 位置
         */
        fun onItemClick(postion: Int)
    }

    /**
     * 页面适配器 返回对应的view
     *
     * @author Yuedong Li
     */
    private inner class ViewPagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return imageViews.size
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val v = imageViews[position]
            if (onItemClickListener != null) {
                v.setOnClickListener {
                    if (list.size == 1) {
                        onItemClickListener!!.onItemClick(0)
                    } else {
                        onItemClickListener!!.onItemClick(position - 1)
                    }
                }
            }
            container.addView(v)
            return v
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    /**
     * 隐藏Banner
     */
    fun hideBanner() {
        fl_banner_viewpager!!.visibility = View.GONE
    }

    /**
     * 隐藏指示器
     */
    fun hideIndicator() {
        ll_scroll_banner_indicator!!.visibility = View.GONE
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        val max = imageViews.size - 1
        var positi = position
        currentPosition = position
        if (isLoop) {
            if (position == 0) {
                currentPosition = max - 1
            } else if (position == max) {
                currentPosition = 1
            }
            positi = currentPosition - 1
        }
        setIndicator(positi)
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == 1) { // viewPager在滚动
            isScrolling = true
            return
        } else if (state == 0) { // viewPager滚动结束
            if (vp_banner != null)
                vp_banner!!.setScrollable(true)

            releaseTime = System.currentTimeMillis()

            vp_banner!!.setCurrentItem(currentPosition, false)

        }
        isScrolling = false
    }
}
/**
 * 设置数据源
 *
 * @param views    viee集合
 * @param list     数据源集合
 * @param listener 回调监听
 */
