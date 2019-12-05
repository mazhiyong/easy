package com.lairui.easy.ui.module5.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

class MyViewPagerAdapter(fm: FragmentManager?, private val mFragments: List<Fragment>?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment { /* Fragment page = null;
        if (mFragments.size()> position){
            page = mFragments.get(position);
            if (page != null){
                return page;
            }
        }
        while (position > mFragments.size()){
            mFragments.add(null);
        }*/
        return mFragments!![position]
    }

    override fun getCount(): Int {
        return mFragments?.size ?: 0
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

}