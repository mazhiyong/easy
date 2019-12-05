package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.os.Parcelable
import android.view.View

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

// 指引页面数据适配器
class GuidePageAdapter(context: Context, private val pageViews: List<View>) : PagerAdapter() {
    override fun getCount(): Int {
        return pageViews.size
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

    override fun getItemPosition(`object`: Any): Int {
        // TODO Auto-generated method stub
        return super.getItemPosition(`object`)
    }

    override fun destroyItem(arg0: View, arg1: Int, arg2: Any) {
        // TODO Auto-generated method stub
        (arg0 as ViewPager).removeView(pageViews[arg1])
    }

    override fun instantiateItem(arg0: View, arg1: Int): Any {
        // TODO Auto-generated method stub
        (arg0 as ViewPager).addView(pageViews[arg1])
        return pageViews[arg1]
    }

    override fun restoreState(arg0: Parcelable?, arg1: ClassLoader?) {
        // TODO Auto-generated method stub

    }

    override fun saveState(): Parcelable? {
        // TODO Auto-generated method stub
        return null
    }

    override fun startUpdate(arg0: View) {
        // TODO Auto-generated method stub

    }

    override fun finishUpdate(arg0: View) {
        // TODO Auto-generated method stub

    }
}