package com.lairui.easy.mywidget.viewpager

import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import java.util.ArrayList

/**
 * Created by yukuo on 2016/5/10.
 * 这是一个滚动视图的适配器
 */
class ViewPagerAdapter(list: List<ImageView>, private val onItemClickListener: OnItemClickListener?) : PagerAdapter() {
    private var list = ArrayList<ImageView>()

    init {
        this.list = list as ArrayList<ImageView>
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

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = list[position]
        if (onItemClickListener != null) {
            v.setOnClickListener { onItemClickListener.onItemClick(position) }
        }
        container.addView(v)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}
