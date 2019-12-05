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
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 交易明细详情   界面
 */
class TradeDetailActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.trade_title_value_tv)
    lateinit var mTradeTitleValueTv: TextView
    @BindView(R.id.trade_money_value_tv)
    lateinit var mTradeMoneyValueTv: TextView
    @BindView(R.id.trade_status_value_tv)
    lateinit var mTradeStatusValueTv: TextView
    @BindView(R.id.trade_time_value_tv)
    lateinit var mTradeTimeValueTv: TextView
    @BindView(R.id.trade_num_value_tv)
    lateinit var mTradeNumValueTv: TextView
    @BindView(R.id.trade_des_value_tv)
    lateinit var mTradeDesValueTv: TextView
    @BindView(R.id.trade_zhuangtai_value_tv)
    lateinit var mTradeZhuangtaiValueTv: TextView
    @BindView(R.id.trade_fenqi_value_tv)
    lateinit var mTradeFenqiValueTv: TextView

    private var mRequestTag = ""


    private lateinit var mDataMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_trade_detail

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }

        mTitleText!!.text = resources.getString(R.string.detail_title)
        initView()

    }


    private fun initView() {
        if (mDataMap == null) {
            return
        }

        var title = mDataMap!!["target"]!!.toString() + ""
        var money = mDataMap!!["amt"]!!.toString() + ""
        val time = mDataMap!!["dealtime"]!!.toString() + ""
        val tradeNum = mDataMap!!["billno"]!!.toString() + ""
        val des = mDataMap!!["abstract"]!!.toString() + ""
        val zhaungtai = mDataMap!!["state"]!!.toString() + ""
        val fenqi = mDataMap!![""]!!.toString() + ""


        val type = mDataMap!!["billtype"]!!.toString() + ""
        var typeShow = ""
        when (type) {
            "top_up" -> typeShow = "充值"
            "withdraw" -> typeShow = "提现"
            "borrow" -> typeShow = "借款"
            "repayment" -> typeShow = "还款"
            "other" -> typeShow = "其他"
        }

        money = UtilTools.getRMBMoney(money)

        if (UtilTools.empty(title)) {
            title = ""
            mTradeTitleValueTv!!.visibility = View.GONE
        } else {
            mTradeTitleValueTv!!.visibility = View.VISIBLE
        }
        mTradeTitleValueTv!!.text = title
        mTradeMoneyValueTv!!.text = money
        mTradeStatusValueTv!!.text = typeShow + "" + zhaungtai
        mTradeTimeValueTv!!.text = time
        mTradeNumValueTv!!.text = tradeNum
        mTradeDesValueTv!!.text = des
        mTradeZhuangtaiValueTv!!.text = zhaungtai
        mTradeFenqiValueTv!!.text = typeShow
    }


    private fun traderListAction() {


        mRequestTag = MethodUrl.bankCardList
        val map = HashMap<String, String>()
        map["ptncode"] = ""
        map["busi_type"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.tradeList, map)
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

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

}
