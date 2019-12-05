package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
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
 * 还款详情  界面
 */
class RepaymentInfoActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.borrow_bianhao_value)
    lateinit var mBorrowBianhaoValue: TextView
    @BindView(R.id.repay_bianhao_value)
    lateinit var mRepayBianhaoValue: TextView
    @BindView(R.id.borrow_man_value)
    lateinit var mBorrowManValue: TextView
    @BindView(R.id.repay_time_value)
    lateinit var mRepayTimeValue: TextView
    @BindView(R.id.repay_benjin_value)
    lateinit var mRepayBenjinValue: TextView
    @BindView(R.id.repay_lixi_value)
    lateinit var mRepayLixiValue: TextView
    @BindView(R.id.repay_type_value)
    lateinit var mRepayTypeValue: TextView
    @BindView(R.id.status_value)
    lateinit var mStatusValue: TextView
    @BindView(R.id.des_value)
    lateinit var mDesValue: TextView
    @BindView(R.id.borrow_hetong_value)
    lateinit var mBorrowHetongValue: TextView
    @BindView(R.id.repay_hetong_value)
    lateinit var mRepayHetongValue: TextView

    private var mRequestTag = ""


    private lateinit var mDataMap: MutableMap<String, Any>
    private lateinit var mResultMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_repayment_info

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }
        mTitleText!!.text = resources.getString(R.string.repay_detail_title)
        initView()
        repayDetail()
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()
    }


    private fun setValueTv() {

        mPageView!!.showContent()


        val status = mResultMap!!["checkstate"]!!.toString() + ""
        var statusStr = ""
        when (status) {
            "1" -> statusStr = "已申请"
            "2" -> statusStr = "还款成功"
            "3" -> statusStr = "还款失败"
        }

        val list = SelectDataUtil.getNameCodeByType("repayState")
        val mm = SelectDataUtil.getMapByKey(status + "", list)
        statusStr = mm[status + ""]!!.toString() + ""

        val backType = mResultMap!!["backtype"]!!.toString() + ""
        var backTypeStr = ""
        //还款账户类型(1：客户结算账户还款;2：客户资金账户还款;3：核心企业资金账户代还;4：借款客户网银还款;5：核心企业网银代
        when (backType) {
            "1" -> backTypeStr = "客户结算账户还款"
            "2" -> backTypeStr = "客户资金账户还款"
            "3" -> backTypeStr = "核心企业资金账户代还"
            "4" -> backTypeStr = "借款客户网银还款"
            "5" -> backTypeStr = "核心企业网银代"
        }

        val list2 = SelectDataUtil.getNameCodeByType("repayAcct")
        val mm2 = SelectDataUtil.getMapByKey(backType + "", list2)
        backTypeStr = mm2[backType + ""]!!.toString() + ""

        val benjin = UtilTools.getRMBMoney(mResultMap!!["backbejn"]!!.toString() + "")
        val lixi = UtilTools.getRMBMoney(mResultMap!!["backlixi"]!!.toString() + "")

        val time = UtilTools.getStringFromSting2(mResultMap!!["creatime"]!!.toString() + "", "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss")

        mBorrowBianhaoValue!!.text = mResultMap!!["loansqid"]!!.toString() + ""
        mRepayBianhaoValue!!.text = mResultMap!!["rtnbillid"]!!.toString() + ""
        mBorrowManValue!!.text = mResultMap!!["zifangnme"]!!.toString() + ""
        mRepayTimeValue!!.text = time + ""
        mRepayBenjinValue!!.text = benjin
        mRepayLixiValue!!.text = lixi
        mRepayTypeValue!!.text = backTypeStr
        mStatusValue!!.text = statusStr
        //mDesValue.setText(mResultMap.get("loansqid")+"");
        //mBorrowHetongValue.setText(mResultMap.get("loanpath")+"");
        //mRepayHetongValue.setText(mResultMap.get("repaypath")+"");

    }


    private fun repayDetail() {

        mRequestTag = MethodUrl.repaymentDetail
        val map = HashMap<String, String>()
        map["rtnbillid"] = mDataMap!!["rtnbillid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.repaymentDetail, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.borrow_hetong_value, R.id.repay_hetong_value)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.borrow_hetong_value -> {
                //  mBorrowHetongValue.setText(mResultMap.get("loanpath")+"");
                //        mRepayHetongValue.setText(mResultMap.get("repaypath")+"");
                intent = Intent(this@RepaymentInfoActivity, PDFLookActivity::class.java)
                intent.putExtra("id", mResultMap!!["loanpath"]!!.toString() + "")
                startActivity(intent)
            }
            R.id.repay_hetong_value -> {
                intent = Intent(this@RepaymentInfoActivity, PDFLookActivity::class.java)
                intent.putExtra("id", mResultMap!!["repaypath"]!!.toString() + "")
                startActivity(intent)
            }
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
            MethodUrl.repaymentDetail//
            -> {
                mResultMap = tData
                setValueTv()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.repaymentDetail -> repayDetail()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mPageView!!.showNetworkError()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        repayDetail()
    }

}
