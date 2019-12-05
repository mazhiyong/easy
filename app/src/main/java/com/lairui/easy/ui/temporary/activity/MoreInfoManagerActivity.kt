package com.lairui.easy.ui.temporary.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.FragmentAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.ui.temporary.fragment.BaseInfoFragment
import com.lairui.easy.ui.temporary.fragment.WorkInfoFragment
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

class MoreInfoManagerActivity : BasicActivity(), RequestView, ReLoadingData {
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
    @BindView(R.id.radio_group)
    lateinit var mRadioGroup: RadioGroup
    @BindView(R.id.info_manager_page)
    lateinit var mFoodManagerPage: ViewPager
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.bianji_but)
    lateinit var mBianjiBut: Button
    @BindView(R.id.bottom_lay)
    lateinit var mBottomLay: LinearLayout
    @BindView(R.id.img1)
    lateinit var mImageView: ImageView
    @BindView(R.id.img2)
    lateinit var mImageView2: ImageView
    @BindView(R.id.lay1)
    lateinit var mLay1: RelativeLayout
    @BindView(R.id.lay2)
    lateinit var mLay2: RelativeLayout
    private lateinit var mData: MutableMap<String, Any>

    //页面适配器
    private lateinit var fragmentAdapter: FragmentAdapter
    //页面管理者
    private lateinit var fm: FragmentManager
    //页面集合
    private val listFragment = ArrayList<Fragment>()

    private var mRequestTag = ""

    private lateinit var baseInfoFragment: BaseInfoFragment
    private lateinit var workInfoFragment: WorkInfoFragment


    override val contentView: Int
        get() = R.layout.activity_more_info_manager

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE) {
                getMoreInfo()
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText.text = resources.getString(R.string.base_msg)
        mRightImg.setImageResource(R.drawable.modify_info)
        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)

        mContent.let { mPageView.setContentView(it) }
        mPageView.subscribRefreshEvent(this)
        mPageView.reLoadingData = this
        mPageView.showLoading()
        mRightImg.visibility = View.GONE
        getMoreInfo()
    }


    private fun initFragment() {
        fm = supportFragmentManager
        mFoodManagerPage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mSwipeBackHelper!!.setSwipeBackEnable(position == 0)
                when (position) {
                    0 -> mRadioGroup.check(R.id.rb_but1)
                    1 -> mRadioGroup.check(R.id.rb_but2)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        baseInfoFragment = BaseInfoFragment()
        workInfoFragment = WorkInfoFragment()

        val bundle = Bundle()
        bundle.putSerializable("DATA", mData as Serializable?)//这里的values就是我们要传的值

        baseInfoFragment.arguments = bundle
        workInfoFragment.arguments = bundle

        listFragment.add(0, baseInfoFragment)
        listFragment.add(1, workInfoFragment)

        fragmentAdapter = FragmentAdapter(supportFragmentManager, listFragment)
        mFoodManagerPage.adapter = fragmentAdapter
        mFoodManagerPage.offscreenPageLimit = 0
        mImageView.visibility = View.VISIBLE
        mImageView2.visibility = View.INVISIBLE

        mRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_but1 -> {
                    mFoodManagerPage.currentItem = 0
                    mImageView.visibility = View.VISIBLE
                    mImageView2.visibility = View.INVISIBLE
                }
                R.id.rb_but2 -> {
                    mFoodManagerPage.currentItem = 1
                    mImageView.visibility = View.INVISIBLE
                    mImageView2.visibility = View.VISIBLE
                }
            }
        }


    }

    /**
     * 获取用户更多资料信息
     */
    private fun getMoreInfo() {

        mRequestTag = MethodUrl.userMoreInfo
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToMap(mHeaderMap, MethodUrl.userMoreInfo, map)
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }


    /**
     * @param tData 数据类型
     * @param mType
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        //

        val intent: Intent
        when (mType) {
            MethodUrl.userMoreInfo -> {
                mData = tData
                if (mData != null) {
                    val mo = mData!!["canMod"]!!.toString() + ""//是否可以修改（0：不可修改 1：可以修改）
                    if (mo == "1") {
                        mRightImg!!.visibility = View.VISIBLE
                    } else {
                        mRightImg!!.visibility = View.GONE
                    }
                } else {
                    mRightImg!!.visibility = View.GONE
                }

                if (fragmentAdapter == null) {
                    initFragment()
                    mPageView!!.showContent()
                } else {
                    baseInfoFragment!!.updateValue(mData!!)
                    workInfoFragment!!.updateValue(mData!!)
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.userMoreInfo -> getMoreInfo()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.bianji_but, R.id.right_img, R.id.lay1, R.id.lay2, R.id.rb_but2, R.id.rb_but1)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.rb_but1, R.id.lay1 -> mRadioGroup!!.check(R.id.rb_but1)
            R.id.rb_but2, R.id.lay2 -> mRadioGroup!!.check(R.id.rb_but2)
            R.id.right_img -> {
                intent = Intent(this@MoreInfoManagerActivity, PerfectInfoActivity::class.java)
                intent.putExtra("type", "2")//代表预览资料中的   编辑资料跳转的
                startActivity(intent)
            }
            R.id.bianji_but -> {
                intent = Intent(this@MoreInfoManagerActivity, PerfectInfoActivity::class.java)
                intent.putExtra("type", "2")//代表预览资料中的   编辑资料跳转的
                startActivity(intent)
            }
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun reLoadingData() {
        getMoreInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }
}
