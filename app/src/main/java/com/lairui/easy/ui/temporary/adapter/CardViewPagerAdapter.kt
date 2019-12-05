package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.ViewGroup


class CardViewPagerAdapter(private val mContext: Context, private val mViews: List<View>) : PagerAdapter() {

    override fun getCount(): Int {
        return mViews.size
        //        return Integer.MAX_VALUE;
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        // TODO Auto-generated method stub
        return arg0 === arg1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //        int pos = position % mViews.size();
        val mRootView = mViews[position]
        (container as ViewPager).removeView(mRootView)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //        int pos = position % mViews.size();
        val mRootView = mViews[position]
        //((ViewPager) container).addView(mRootView);


        /*  ViewParent viewParent = mRootView.getParent();
        if (viewParent != null) {
            container.removeView(mRootView);
        }*/
        (container as ViewPager).addView(mRootView)
        return mRootView
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

}
