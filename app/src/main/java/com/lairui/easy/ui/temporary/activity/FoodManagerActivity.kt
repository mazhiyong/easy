package com.lairui.easy.ui.temporary.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.FragmentAdapter
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.ui.temporary.fragment.FoodFragment
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList

import butterknife.BindView

class FoodManagerActivity : BasicActivity() {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.rb_but1)
    lateinit var mRbBut1: RadioButton
    @BindView(R.id.rb_but2)
    lateinit var mRbBut2: RadioButton
    @BindView(R.id.rb_but3)
    lateinit var mRbBut3: RadioButton
    @BindView(R.id.rb_but4)
    lateinit var mRbBut4: RadioButton
    @BindView(R.id.rb_but5)
    lateinit var mRbBut5: RadioButton
    @BindView(R.id.radio_group)
    lateinit var mRadioGroup: RadioGroup
    @BindView(R.id.food_manager_page)
    lateinit var mFoodManagerPage: ViewPager

    //页面适配器
    private lateinit var fragmentAdapter: FragmentAdapter
    //页面管理者
    private lateinit var fm: FragmentManager
    //页面集合
    private val listFragment = ArrayList<Fragment>()


    override val contentView: Int
        get() = R.layout.activity_food_manager

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.yellow), 0)
        fm = supportFragmentManager
        mFoodManagerPage!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mSwipeBackHelper!!.setSwipeBackEnable(position == 0)
                when (position) {
                    0 -> mRadioGroup!!.check(R.id.rb_but1)
                    1 -> mRadioGroup!!.check(R.id.rb_but2)
                    2 -> mRadioGroup!!.check(R.id.rb_but3)
                    3 -> mRadioGroup!!.check(R.id.rb_but4)
                    4 -> mRadioGroup!!.check(R.id.rb_but5)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        listFragment.add(0, FoodFragment())
        listFragment.add(1, FoodFragment())
        listFragment.add(2, FoodFragment())
        listFragment.add(3, FoodFragment())
        listFragment.add(4, FoodFragment())

        fragmentAdapter = FragmentAdapter(supportFragmentManager, listFragment)
        mFoodManagerPage!!.adapter = fragmentAdapter
        mFoodManagerPage!!.offscreenPageLimit = 0



        mRadioGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_but1 -> mFoodManagerPage!!.currentItem = 0
                R.id.rb_but2 -> mFoodManagerPage!!.currentItem = 1
                R.id.rb_but3 -> mFoodManagerPage!!.currentItem = 2
                R.id.rb_but4 -> mFoodManagerPage!!.currentItem = 3
                R.id.rb_but5 -> mFoodManagerPage!!.currentItem = 4
            }
        }
    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {

    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {

    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }
}
