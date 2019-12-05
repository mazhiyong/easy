package com.lairui.easy.ui.temporary.activity

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.CommonSelectDialog
import com.lairui.easy.ui.temporary.fragment.LineDataFragment2
import com.lairui.easy.ui.temporary.fragment.ListDataFragment
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.OnClick


/**
 * 我的应收账款
 */
class MyShouMoneyActivity : BasicActivity(), RequestView, ReLoadingData, SelectBackListener {


    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.line_lay)
    lateinit var mLineLay: RelativeLayout
    @BindView(R.id.list_lay)
    lateinit var mListLay: RelativeLayout
    @BindView(R.id.main_bottom)
    lateinit var mMainBottom: LinearLayout
    @BindView(R.id.fragment_container)
    lateinit var mFragmentContainer: RelativeLayout


    @BindView(R.id.name_tv)
    lateinit var mNameTv: TextView
    @BindView(R.id.ll_lay)
    lateinit var mLlLay: LinearLayout

    private var index: Int = 0
    private var currentTabIndex: Int = 0
    private lateinit var fragments: Array<Fragment>
    private lateinit var mLineDataFragment: LineDataFragment2
    private lateinit var mListDataFragment: ListDataFragment

    private var mRequestTag = ""

    private lateinit var mDialog: CommonSelectDialog

    lateinit var selectKehuMap: MutableMap<String, Any>


    private var mKehuList: List<MutableMap<String, Any>>? = ArrayList()


    override val contentView: Int
        get() = R.layout.activity_my_shou_money

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = "我的应收账款"

        getfukuanfangInfo()

        mLineDataFragment = LineDataFragment2()
        mListDataFragment = ListDataFragment()
        fragments = arrayOf<Fragment>(mLineDataFragment!!, mListDataFragment!!)
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, mLineDataFragment!!)
                .show(mLineDataFragment!!)
                .commitAllowingStateLoss()


        mSwipeBackHelper!!.setIsOnlyTrackingLeftEdge(true)
    }


    @OnClick(R.id.left_back_lay, R.id.ll_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.ll_lay //查询付款方
            -> if (mKehuList != null && mKehuList!!.size > 0) {
                mDialog = CommonSelectDialog(this, true, mKehuList!!, 10)
                mDialog!!.selectBackListener = this
                mDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            } else {
                getfukuanfangInfo()
            }
        }
    }

    //查询付款方
    private fun getfukuanfangInfo() {
        mRequestTag = MethodUrl.payCompanyList
        val map = HashMap<String, String>()
        /* map.put("flowdate",mSxMap.get("flowdate")+"");
        map.put("flowid",mSxMap.get("flowid")+"");
        map.put("autoid",mSxMap.get("autoid")+"");*/
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.payCompanyList, map)
    }


    override fun reLoadingData() {
        getfukuanfangInfo()
    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {
        showProgressDialog()
    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {
        dismissProgressDialog()
    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.payCompanyList -> {
                mKehuList = tData["payFirmInfoList"] as List<MutableMap<String, Any>>?
                if (mKehuList != null && mKehuList!!.size > 0) {
                    //默认第一个
                    val map = mKehuList!![0]
                    selectKehuMap = map
                    mNameTv!!.text = map["payfirmname"]!!.toString() + ""

                    (fragments!![index] as LineDataFragment2).getShouMoneyInfoLine()
                } else {
                    showToastMsg("未查询到付款方数据")
                    mNameTv!!.text = "暂无付款方"
                }
            }

            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.payCompanyList -> getfukuanfangInfo()
                }
            }
        }
    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }


    fun onTabClicked(view: View) {
        when (view.id) {
            R.id.line_lay //折线
            -> {
                index = 0
                mLineLay!!.setBackgroundColor(ContextCompat.getColor(this, R.color.line_background))
                mListLay!!.setBackgroundColor(ContextCompat.getColor(this, R.color.list_background))
            }
            R.id.list_lay //列表
            -> {
                index = 1
                mListLay!!.setBackgroundColor(ContextCompat.getColor(this, R.color.line_background))
                mLineLay!!.setBackgroundColor(ContextCompat.getColor(this, R.color.list_background))
            }
        }//mSwipeBackHelper.setSwipeBackEnable(false);
        //mSwipeBackHelper.setSwipeBackEnable(true);
        if (currentTabIndex != index) {
            val trx = supportFragmentManager.beginTransaction()
            trx.hide(fragments!![currentTabIndex])
            if (!fragments!![index].isAdded) {
                trx.add(R.id.fragment_container, fragments!![index])
            } else {
                when (index) {
                    0 -> (fragments!![index] as LineDataFragment2).getShouMoneyInfoLine()
                    1 -> (fragments!![index] as ListDataFragment).getShouMoneyInfoList()
                }
            }
            trx.show(fragments!![index]).commitAllowingStateLoss()
        }

        currentTabIndex = index
    }


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            10 -> {
                selectKehuMap = map
                mNameTv!!.text = map["payfirmname"]!!.toString() + ""
            }
        }
    }
}
