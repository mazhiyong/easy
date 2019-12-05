package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 提交结果   借款申请
 */
class ResultMoneyActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.my_image)
    lateinit var mMyImage: ImageView
    @BindView(R.id.submit_result_tv)
    lateinit var mSubmitResultTv: TextView
    @BindView(R.id.submit_tip_tv)
    lateinit var mSubmitTipTv: TextView
    @BindView(R.id.but_back)
    lateinit var mButBack: Button
    @BindView(R.id.title_tv1)
    lateinit var mTitleTv1: TextView
    @BindView(R.id.value_tv1)
    lateinit var mValueTv1: TextView
    @BindView(R.id.title_lay1)
    lateinit var mTitleLay1: LinearLayout
    @BindView(R.id.title_tv2)
    lateinit var mTitleTv2: TextView
    @BindView(R.id.value_tv2)
    lateinit var mValueTv2: TextView
    @BindView(R.id.title_lay2)
    lateinit var mTitleLay2: LinearLayout
    @BindView(R.id.line2)
    lateinit var mLine2: View
    @BindView(R.id.title_tv3)
    lateinit var mTitleTv3: TextView
    @BindView(R.id.value_tv3)
    lateinit var mValueTv3: TextView
    @BindView(R.id.title_lay3)
    lateinit var mTitleLay3: LinearLayout
    @BindView(R.id.line3)
    lateinit var mLine3: View


    private var mOpType = 0

    private lateinit var mDataMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_money_result

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        var intentBroadcast = Intent()
        intentBroadcast.action = MbsConstans.BroadcastReceiverAction.JIE_HUAN_UPDATE
        sendBroadcast(intentBroadcast)

        intentBroadcast = Intent()
        intentBroadcast.action = MbsConstans.BroadcastReceiverAction.DAIBAN_INFO_UPDATE
        sendBroadcast(intentBroadcast)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mOpType = bundle.getInt(MbsConstans.ResultType.RESULT_KEY)
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }

        when (mOpType) {
            MbsConstans.ResultType.RESULT_JIEKUAN -> {
                mTitleText!!.text = resources.getString(R.string.borrow_money_title)
                mMyImage!!.setImageResource(R.drawable.wait)
                mButBack!!.text = resources.getString(R.string.but_back)
                mSubmitResultTv!!.text = resources.getString(R.string.submit_success)
                initJikuan()
            }
            MbsConstans.ResultType.RESULT_HUANKUAN -> {
                mTitleText!!.text = resources.getString(R.string.repay_title)
                mMyImage!!.setImageResource(R.drawable.wait)
                mButBack!!.text = resources.getString(R.string.but_back)
                mSubmitResultTv!!.text = resources.getString(R.string.repay_success)
                initHuanKuan()
            }
        }
    }

    private fun initJikuan() {
        mTitleTv1!!.text = "借款金额"
        mTitleTv2!!.text = "借款期限"
        mValueTv1!!.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney(mDataMap!!["reqmoney"]!!.toString() + "")

        val danwei = mDataMap!!["limitunit"]!!.toString() + ""//借款期限单位
        //Map<String,Object> mm = SelectDataUtil.getMap(danwei,SelectDataUtil.getQixianDw());
        val mm = SelectDataUtil.getMap(danwei, SelectDataUtil.getNameCodeByType("limitUnit"))

        mValueTv2!!.text = mDataMap!!["loanlimit"].toString() + "" + mm["name"]

        mTitleLay3!!.visibility = View.GONE
        mLine3!!.visibility = View.GONE
    }

    private fun initHuanKuan() {
        mTitleTv1!!.text = "还款本金"
        mTitleTv2!!.text = "还款利息"
        mTitleTv3!!.text = "支付方式"

        mValueTv1!!.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney(mDataMap!!["backbejn"]!!.toString() + "")
        mValueTv2!!.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney(mDataMap!!["backlixi"]!!.toString() + "")

        val payType = mDataMap!!["backtype"]!!.toString() + ""
        //还款账户类型(1：结算账户还款;2：资金账户还款)
        if (payType == "2") {
            mValueTv3!!.text = "资金账户还款"
        } else {
            mValueTv3!!.text = "结算账户还款"
        }

    }

    /**
     * 网络连接请求
     */
    private fun submitInstall() {

        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.installCerSubmit, map)
    }

    @OnClick(R.id.back_img, R.id.but_back, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay, R.id.back_img -> finish()
            R.id.but_back -> finish()
        }
    }


    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}
