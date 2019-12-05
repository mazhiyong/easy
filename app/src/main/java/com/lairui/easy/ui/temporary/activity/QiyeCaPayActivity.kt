package com.lairui.easy.ui.temporary.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.QiyeCaPayAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil


import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

class QiyeCaPayActivity : BasicActivity(), RequestView, ReLoadingData {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.title_bar_view)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.tv_money)
    lateinit var mMoneyText: TextView
    @BindView(R.id.tv_money2)
    lateinit var mMoneyText2: TextView
    @BindView(R.id.tv_gongyingshang)
    lateinit var mGongyingshangText: TextView
    @BindView(R.id.bt_pay)
    lateinit var mButton: Button
    @BindView(R.id._rcv_pay_way)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.zhekou_tv)
    lateinit var mZhekouTv: TextView
    @BindView(R.id.bottom_money_tv)
    lateinit var mBottomMoneyTv: TextView


    lateinit var mPayWayAdapter: QiyeCaPayAdapter
    private val mDataList = ArrayList<MutableMap<String, Any>>()//支付方式总列表

    private lateinit var mBankList: List<MutableMap<String, Any>>//银行列表
    private lateinit var mPayTypeMap: MutableMap<String, Any>//选中的支付方式

    private var mRequestTag = ""

    private var mMoney: String? = "0"
    private var mMemo: String? = ""
    private var mRclno: String? = ""//收款人客户号
    private var name: String? = ""

    private lateinit var mPayInfo: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_qiye_ca_pay

    private lateinit var mSetTiXianBank: MutableMap<String, Any>

    //广播监听
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val b = intent.extras
            if (MbsConstans.BroadcastReceiverAction.CAPAY_SUC == action) {
                finish()
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);

        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null)

        val layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(this))
        mTitleBarView!!.layoutParams = layoutParams
        mTitleBarView!!.setPadding(0, UtilTools.getStatusHeight2(this), 0, 0)

        mTitleText!!.text = resources.getString(R.string.qiye_ca_money)
        mTitleText!!.setTextColor(ContextCompat.getColor(this, R.color.white))

        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()

        val filter = IntentFilter()
        filter.addAction(MbsConstans.BroadcastReceiverAction.CAPAY_SUC)
        registerReceiver(receiver, filter)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mMoney = bundle.getString("money")
            mMemo = bundle.getString("memo")
            mRclno = bundle.getString("rclno")
            name = bundle.getString("name")
        }

        val ss = UtilTools.getRMBMoney(mMoney!!)
        /* ParseTextUtil parseTextUtil = new ParseTextUtil(this);
        Spannable spannable =  parseTextUtil.getDianType(ss);*/
        mMoneyText!!.text = ss
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        getDefaultPay()
        initRecycleView()
        payInfo()

    }

    override fun setBarTextColor() {
        StatusBarUtil.setDarkMode(this)
    }

    private fun getDefaultPay() {
        var map: MutableMap<String, Any> = HashMap()
        map = HashMap()
        map["opnbnknm"] = "微信"
        map["type"] = "12"
        map["icon"] = R.drawable.pay_weixin
        mDataList.add(map)

        map = HashMap()
        map["opnbnknm"] = "支付宝"
        map["type"] = "13"
        map["icon"] = R.drawable.pay_zhifubao
        mDataList.add(map)

        map = HashMap()
        map["opnbnknm"] = "网银转账"
        map["type"] = "15"
        map["icon"] = R.drawable.pay_zhuanzhang
        mDataList.add(map)


    }


    /**
     * 证书费用信息   和转账信息
     */
    private fun payInfo() {
        mRequestTag = MethodUrl.zsMoneyInfo
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.zsMoneyInfo, map)
    }


    /**
     * 获取用户银行卡列表
     */
    private fun bankCardInfoAction() {
        mRequestTag = MethodUrl.bankCardList

        val map = HashMap<String, String>()
        map["accsn"] = "2"
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeadermap, MethodUrl.bankCardList, map)
    }


    private fun initRecycleView() {
        if (mPayWayAdapter == null) {
            mPayWayAdapter = QiyeCaPayAdapter(this, mDataList)
            mRecyclerView!!.adapter = mPayWayAdapter
            mPayWayAdapter!!.itemClickListener = object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {
                    mPayTypeMap = mDataList[position]

                }
            }
        } else {
            mPayWayAdapter!!.notifyDataSetChanged()
        }
        if (mDataList.size < 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }

    private fun butAction() {
        if (mPayTypeMap == null || mPayTypeMap!!.isEmpty()) {
            showToastMsg("请选择支付方式")
            mButton!!.isEnabled = true
            return
        }
        val intent: Intent
        val payType = mPayTypeMap!!["type"]!!.toString() + ""
        when (payType) {
            "12"//微信
            -> {
                showToastMsg("暂未开通")
                mButton!!.isEnabled = true
            }
            "13"//支付宝
            -> {
                showToastMsg("暂未开通")
                mButton!!.isEnabled = true
            }
            "15"//转账
            -> {
                mButton!!.isEnabled = false
                intent = Intent(this@QiyeCaPayActivity, QiyeCaZhuanZhangActivity::class.java)
                intent.putExtra("DATA", mPayInfo as Serializable?)
                startActivity(intent)
                mButton!!.isEnabled = true
            }
        }
    }


    override fun onResume() {
        super.onResume()
    }

    @OnClick(R.id.back_img, R.id.bt_pay, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.bt_pay//付款
            -> {
                mButton!!.isEnabled = false
                butAction()
            }
        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        dismissProgressDialog()
        val intent: Intent
        mButton!!.isEnabled = true
        when (mType) {
            MethodUrl.zsMoneyInfo -> {
                mPayInfo = tData

                val money = mPayInfo!!["charge"]!!.toString() + ""
                //折扣比例、
                val bili = mPayInfo!!["rebate"]!!.toString() + ""
                mMoneyText2!!.text = UtilTools.getRMBMoney(money)
                mMoneyText2!!.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG

                val moneyReally = UtilTools.mul(java.lang.Double.valueOf(money), java.lang.Double.valueOf(bili))
                val moneyStr = UtilTools.getDianType2(this@QiyeCaPayActivity, UtilTools.getRMBMoney(moneyReally.toString() + ""))
                mMoneyText!!.text = moneyStr

                mBottomMoneyTv!!.text = UtilTools.getRMBMoney(moneyReally.toString() + "")

                val biliNum = UtilTools.mul(100.0, java.lang.Double.valueOf(bili))

                val biliStr = UtilTools.getlilv(biliNum.toString() + "")

                mZhekouTv!!.text = "折扣:$biliStr"
            }
            MethodUrl.bankCardList -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                } else {
                    mBankList = JSONUtil.instance.jsonToList(result)!!
                    if (mBankList != null && mBankList!!.size > 0) {
                        for (map in mDataList) {
                            val s = map["accsn"]!!.toString() + ""
                            if (s == "1" || s == "3") {
                                mSetTiXianBank = map
                            }
                        }
                    } else {

                    }
                }
                mDataList.clear()
                getDefaultPay()
                initRecycleView()
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.bankCardList -> bankCardInfoAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dismissProgressDialog()
        mButton!!.isEnabled = true
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        bankCardInfoAction()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}
