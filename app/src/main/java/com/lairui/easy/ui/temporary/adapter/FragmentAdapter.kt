package com.lairui.easy.ui.temporary.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.View

class FragmentAdapter : FragmentPagerAdapter {

    //列表数据
    private var list: List<Fragment>? = null

    constructor(fm: FragmentManager) : super(fm) {}

    constructor(fm: FragmentManager, list: List<Fragment>) : super(fm) {
        this.list = list
    }

    override fun getItem(arg0: Int): Fragment {
        return list!![arg0]
    }

    override fun getCount(): Int {
        return list!!.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }
}
