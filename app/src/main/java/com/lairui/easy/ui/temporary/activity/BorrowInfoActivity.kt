package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 借款信息   界面
 */
class BorrowInfoActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.money_tv)
    lateinit var mMoneyTv: TextView
    @BindView(R.id.borrow_bianhao_value)
    lateinit var mBorrowBianhaoValue: TextView
    @BindView(R.id.borrow_payman_value)
    lateinit var mBorrowPaymanValue: TextView
    @BindView(R.id.borrow_getman_value)
    lateinit var mBorrowGetmanValue: TextView
    @BindView(R.id.borrow_givetime_value)
    lateinit var mBorrowGivetimeValue: TextView
    @BindView(R.id.borrow_daoqi_value)
    lateinit var mBorrowDaoqiValue: TextView
    @BindView(R.id.borrow_limit_value)
    lateinit var mBorrowLimitValue: TextView
    @BindView(R.id.borrow_lilv_value)
    lateinit var mBorrowLilvValue: TextView
    @BindView(R.id.borrow_givetype_value)
    lateinit var mBorrowGivetypeValue: TextView
    @BindView(R.id.borrow_use_value)
    lateinit var mBorrowUseValue: TextView
    @BindView(R.id.borrow_benjin_value)
    lateinit var mBorrowBenjinValue: TextView
    @BindView(R.id.borrow_lixi_value)
    lateinit var mBorrowLixiValue: TextView
    @BindView(R.id.borrow_yue_value)
    lateinit var mBorrowYueValue: TextView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    @BindView(R.id.jiekuan_fafang_lay)
    lateinit var mJieKuanFaFangLay: LinearLayout
    @BindView(R.id.jiekuan_daoqi_lay)
    lateinit var mJieKuanDaoqiLay: LinearLayout

    private var mRequestTag = ""


    private lateinit var  mDataMap: MutableMap<String, Any>
    private lateinit var mResultMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_borrow_info

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            // mDataMap = (Map<String, Object>) bundle.getSerializable("DATA");
            mResultMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }
        mTitleText!!.text = resources.getString(R.string.detail_title)
        initView()
        setValueTv()
        //borrowDetail();
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()
    }


    private fun setValueTv() {

        mPageView!!.showContent()
        //期限单位(1:年,2:月,3:日)
        val type = mResultMap!!["limitunit"]!!.toString() + ""
        val chuzhang = mResultMap!!["vrtacct"]!!.toString() + ""//出账品种(0:流动资金贷款;1:银行承兑汇票)
        val use = mResultMap!!["loanuse"]!!.toString() + ""//出账品种(0:流动资金贷款;1:银行承兑汇票)

        //期限单位
        //Map<String,Object> danweiMap = SelectDataUtil.getMap(type,SelectDataUtil.getQixianDw());
        val danweiMap = SelectDataUtil.getMap(type, SelectDataUtil.getNameCodeByType("limitUnit"))
        val danwei = danweiMap["name"]!!.toString() + ""

        //出账类型
        var chuzhangStr = ""
        val chuzhangMap = SelectDataUtil.getMap(chuzhang, SelectDataUtil.chuzhangType)
        chuzhangStr = chuzhangMap["name"]!!.toString() + ""

        //借款用途  贷款用途


        val mm = UtilTools.getRMBMoney(mResultMap!!["reqmoney"]!!.toString() + "")
        val benjin = UtilTools.getRMBMoney(mResultMap!!["backmoney"]!!.toString() + "")
        val lixi = UtilTools.getRMBMoney(mResultMap!!["backlixi"]!!.toString() + "")

        val lilv = UtilTools.getlilv(mResultMap!!["loanlilv"]!!.toString() + "")

        var loandate = mResultMap!!["loandate"]!!.toString() + ""
        loandate = UtilTools.getStringFromSting(loandate, "yyyyMMdd")
        var stopdate = mResultMap!!["stopdate"]!!.toString() + ""
        stopdate = UtilTools.getStringFromSting(stopdate, "yyyyMMdd")

        mMoneyTv!!.text = mm
        mBorrowBenjinValue!!.text = benjin
        mBorrowLixiValue!!.text = lixi
        mBorrowLilvValue!!.text = lilv
        mBorrowGivetimeValue!!.text = loandate
        mBorrowDaoqiValue!!.text = stopdate

        mBorrowBianhaoValue!!.text = mResultMap!!["loansqid"]!!.toString() + ""
        mBorrowPaymanValue!!.text = mResultMap!!["zifangnme"]!!.toString() + ""
        mBorrowGetmanValue!!.text = mResultMap!!["firmname"]!!.toString() + ""

        mBorrowLimitValue!!.text = mResultMap!!["loanlimit"].toString() + "" + danwei

        mBorrowGivetypeValue!!.text = chuzhangStr
        mBorrowUseValue!!.text = use + ""


        val allm = java.lang.Double.valueOf(mResultMap!!["reqmoney"]!!.toString() + "")
        val backm = java.lang.Double.valueOf(mResultMap!!["backmoney"]!!.toString() + "")
        val leftm = UtilTools.sub(allm, backm)
        val ss = UtilTools.getRMBMoney(leftm.toString() + "")
        mBorrowYueValue!!.text = ss


        //借款状态（1：放款中 2：还款中 3：已结清 4：已驳回）  loanstate
        val status = mResultMap!!["loanstate"]!!.toString() + ""
        Log.i("show", "status:$status")
        when (status) {
            "1" -> {
                mJieKuanFaFangLay!!.visibility = View.GONE
                mJieKuanDaoqiLay!!.visibility = View.GONE
            }
            "2" -> {
            }
            "3" -> {
            }
            "4" -> {
                mJieKuanFaFangLay!!.visibility = View.GONE
                mJieKuanDaoqiLay!!.visibility = View.GONE
            }
            else -> {
                mJieKuanFaFangLay!!.visibility = View.GONE
                mJieKuanDaoqiLay!!.visibility = View.GONE
            }
        }

    }


    private fun borrowDetail() {

        mRequestTag = MethodUrl.borrowDetail
        val map = HashMap<String, String>()
        map["loansqid"] = mDataMap!!["loansqid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.borrowDetail, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.borrowDetail//
            -> {
                mResultMap = tData
                setValueTv()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.borrowDetail -> borrowDetail()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mPageView!!.showNetworkError()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        borrowDetail()
    }
}
