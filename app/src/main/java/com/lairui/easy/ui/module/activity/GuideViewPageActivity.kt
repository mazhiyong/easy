package com.lairui.easy.ui.module.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView


import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.ui.temporary.adapter.GuidePageAdapter
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools

import java.util.ArrayList

import androidx.viewpager.widget.ViewPager

/**
 *
 */
class GuideViewPageActivity : BasicActivity(), RequestView {

    private var mViewPager: ViewPager? = null
    private var mViewList: MutableList<View>? = null
    private var mImageView: ImageView? = null
    private var mImageViews: Array<ImageView?>? = null
    private var mBottonLayout: LinearLayout? = null
    private var mGuidePageAdapter: GuidePageAdapter? = null

    private var mShared: SharedPreferences? = null//存放配置信息的文件
    private var mStartBut: TextView? = null
    override val contentView: Int
        get() = R.layout.guide_view

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    override fun init() {
        val inflater = layoutInflater
        mViewPager = findViewById<View>(R.id.guidePages) as ViewPager
        mBottonLayout = findViewById<View>(R.id.viewGroup) as LinearLayout

        mViewList = ArrayList()
        mViewList!!.add(inflater.inflate(R.layout.startup1, null))
        mViewList!!.add(inflater.inflate(R.layout.startup2, null))
        mImageViews = arrayOfNulls<ImageView>(mViewList!!.size)
        for (i in mViewList!!.indices) {
            mImageView = ImageView(this)
            val params = LayoutParams(UtilTools.dip2px(this, 8), UtilTools.dip2px(this, 8))
            mImageView!!.layoutParams = params
            mImageViews!![i] = mImageView

            if (i != 0) {
                params.leftMargin = 15
            }
            if (i == 0) {
                // 默认选中第一张图片
                mImageViews!![i]?.setBackgroundResource(R.drawable.circle_selector)
                mImageViews!![i]?.isSelected = true
            } else {
                mImageViews!![i]?.setBackgroundResource(R.drawable.circle_selector)
                mImageViews!![i]?.isSelected = false

            }
            mBottonLayout!!.addView(mImageViews!![i])
        }

        mGuidePageAdapter = GuidePageAdapter(this, mViewList as ArrayList<View>)
        mViewPager!!.adapter = mGuidePageAdapter

        mViewPager!!.addOnPageChangeListener(GuidePageChangeListener())
        mShared = getSharedPreferences(MbsConstans.SharedInfoConstans.LOGIN_INFO, Context.MODE_PRIVATE)

        mStartBut = mViewList!![mViewList!!.size - 1].findViewById<View>(R.id.startBtn) as TextView
        mStartBut!!.visibility = View.VISIBLE
        mStartBut!!.setOnClickListener {
            SPUtils.put(this@GuideViewPageActivity, MbsConstans.SharedInfoConstans.IS_FIRST_START, MbsConstans.UpdateAppConstans.VERSION_APP_CODE.toString() + "")
            val bb = SPUtils[this@GuideViewPageActivity, MbsConstans.SharedInfoConstans.LOGIN_OUT, true] as Boolean
            val intent: Intent
            if (bb) {
                intent = Intent(this@GuideViewPageActivity, LoginActivity::class.java)
                this@GuideViewPageActivity.startActivity(intent)
                this@GuideViewPageActivity.finish()
            } else {
                MbsConstans.ACCESS_TOKEN = SPUtils[this@GuideViewPageActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""]!!.toString()
                MbsConstans.REFRESH_TOKEN = SPUtils[this@GuideViewPageActivity, MbsConstans.SharedInfoConstans.REFRESH_TOKEN, ""]!!.toString()
                val s = SPUtils[this@GuideViewPageActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""]!!.toString()
                MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
                if (MbsConstans.USER_MAP == null || MbsConstans.USER_MAP!!.isEmpty()) {
                    intent = Intent(this@GuideViewPageActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
                    intent = Intent(this@GuideViewPageActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        }
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }


    // 指引页面更改事件监听器
    internal inner class GuidePageChangeListener : ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(arg0: Int) {
            // TODO Auto-generated method stub

        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }

        override fun onPageSelected(arg0: Int) {
            for (i in mImageViews!!.indices) {
                if (i == arg0) {
                    mImageViews!![i]?.setBackgroundResource(R.drawable.circle_selector)
                    mImageViews!![i]?.isSelected = true
                } else {
                    mImageViews!![i]?.setBackgroundResource(R.drawable.circle_selector)
                    mImageViews!![i]?.isSelected = false

                }
            }
        }
    }

}
