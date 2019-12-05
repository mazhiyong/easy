package com.lairui.easy.ui.temporary.activity

import androidx.core.content.ContextCompat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mywidget.dialog.BankCardSelectDialog
import com.lairui.easy.mywidget.dialog.ZZTipMsgDialog
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 充值 校验(短信验证码)
 */
class ChongzhiTestActivity : BasicActivity(), SelectBackListener {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.tv_card)
    lateinit var mCardText: TextView
    @BindView(R.id.tv_tixin_maxmoney)
    lateinit var mMaxMoneyText: TextView
    @BindView(R.id.tv_money)
    lateinit var mMoneyText: EditText
    @BindView(R.id.bt_chongzhi)
    lateinit var mChongzhiButton: Button
    @BindView(R.id.rl_layout)
    lateinit var mLayout: RelativeLayout

    private var mRequestTag = ""
    private var mDataList: List<MutableMap<String, Any>>? = ArrayList()
    private lateinit var mSelectBank: MutableMap<String, Any>


    private lateinit var mCardConfig: MutableMap<String, Any>


    override val contentView: Int
        get() = R.layout.activity_chongzhi_test


    lateinit var mDialog: BankCardSelectDialog
    lateinit var mZDialog: ZZTipMsgDialog


    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.BANKUPDATE_UPDATE) {
                bankCardInfoAction()
            } else if (action == MbsConstans.BroadcastReceiverAction.MONEY_UPDATE) {
                finish()
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.chongzhi)

        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.BANKUPDATE_UPDATE)
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.MONEY_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)

        UtilTools.setMoneyEdit(mMoneyText!!, 0.0)

        bankCardInfoAction()
        cardConfig()

    }


    /**
     * 查询资金托管配置
     */
    private fun cardConfig() {
        mRequestTag = MethodUrl.supervisionConfig
        val map = HashMap<String, String>()
        map["accsn"] = "A"
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeadermap, MethodUrl.supervisionConfig, map)
    }

    /**
     * 获取用户银行卡列表
     */
    private fun bankCardInfoAction() {
        mRequestTag = MethodUrl.bankCardList
        val map = HashMap<String, String>()
        map["accsn"] = "A"
        map["isdefault"] = ""
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeadermap, MethodUrl.bankCardList, map)
    }


    /**
     * 提交
     */
    private fun submitData() {
        val money = mMoneyText!!.text.toString() + ""

        if (!UtilTools.CheckMoneyValid(money)) {
            showToastMsg("请输入金额")
            mChongzhiButton!!.isEnabled = true
            return
        }
        mRequestTag = MethodUrl.chongzhiSubmit
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        map["amount"] = money
        map["accid"] = mSelectBank!!["accid"]!!.toString() + ""
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.chongzhiSubmit, map)
    }

    @OnClick(R.id.back_img, R.id.bt_chongzhi, R.id.left_back_lay, R.id.rl_layout)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.rl_layout -> showDialog()
            R.id.bt_chongzhi -> {
                mChongzhiButton!!.isEnabled = false
                butPress()
            }
        }
    }


    private fun butPress() {
        if (mSelectBank == null) {
            mChongzhiButton!!.isEnabled = true
            showToastMsg("暂无可选择的充值方式")
            return
        }

        val accsn = mSelectBank!!["accsn"]!!.toString() + ""
        //accsn   业务类型(1:提现账户;A充值卡<快捷支付>; D电子账户)
        when (accsn) {
            "1" -> submitData()
            "A" -> submitData()
            "D"//电子账户直接转账
            -> {
                //showBaseMsgDialog("请向"+mSelectBank.get("accid")+"转账",false);
                showTipMsgDialog(mSelectBank!!, false)
                mChongzhiButton!!.isEnabled = true
            }
        }
    }

    private fun showDialog() {
        var mType = 200

        //是否支持银商入金（0：不支持 1：支持）-暂不支持 不需要调试            bankSup
        //是否支持快捷入金（0：不支持 1：支持                   fastSup
        //是否支持跨行转账（0：不支持 1：支持）                 obankSup

        if (mCardConfig == null || mCardConfig!!.isEmpty()) {
            mType = 30
            if (mDataList == null || mDataList!!.size == 0) {
                showToastMsg("暂无数据可选择，请联系客服")
                return
            }
        } else {
            val fastSup = mCardConfig!!["fastSup"]!!.toString() + ""
            val obankSup = mCardConfig!!["obankSup"]!!.toString() + ""
            LogUtil.i("---------------", mCardConfig.toString() + "    " + fastSup + "    " + obankSup)
            if (fastSup == "0") {
                mType = 30
                if (obankSup == "0") {
                    showToastMsg("不支持绑卡业务，请联系客服")
                    return
                } else if (obankSup == "1" && (mDataList == null || mDataList!!.size == 0)) {
                    showToastMsg("数据信息异常，请联系客服")
                    return
                }
            }
        }



        mDialog = mDataList?.let { BankCardSelectDialog(this, true, it, mType) }!!
        mDialog!!.selectBackListener = this
        mDialog!!.onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.add_card_lay -> {
                    val intent = Intent(this@ChongzhiTestActivity, ChongZhiCardAddActivity::class.java)
                    intent.putExtra("backtype", "30")
                    startActivity(intent)
                    mDialog!!.dismiss()
                }
                R.id.tv_cancel -> mDialog!!.dismiss()
            }
        }
        mDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun addOtherPay() {

        /*if (mDataList != null && mDataList.size()>0){
            for (Map<String,Object> map : mDataList){
                map.put("type","300");
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("type","11");
        map.put("opnbnknm","微信");
        map.put("accid","");
        mDataList.add(map);

        map = new HashMap<>();
        map.put("type","12");
        map.put("opnbnknm","支付宝");
        map.put("accid","");
        mDataList.add(map);*/
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

        val intent: Intent
        when (mType) {
            //是否支持银商入金（0：不支持 1：支持）-暂不支持 不需要调试            bankSup
            //是否支持快捷入金（0：不支持 1：支持                   fastSup
            //是否支持跨行转账（0：不支持 1：支持）                 obankSup
            MethodUrl.supervisionConfig//{"obankSup":"0","bankSup":"1","fastSup":"0"}
            -> mCardConfig = tData
            MethodUrl.bankCardList//
            -> {
                mCardText!!.text = "--"
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    //mCardText.setText("暂无可用的银行卡");
                } else {
                    mDataList = JSONUtil.instance.jsonToList(result)
                    if (mDataList != null && mDataList!!.size > 0) {
                        mSelectBank = mDataList!![0]
                        /* for (Map<String, Object> map : mDataList) {
                            String s = map.get("accsn") + "";
                            if (s.equals("2") || s.equals("3")) {
                                mSelectBank = map;
                                break;
                            }
                        }*/
                        if (mSelectBank != null && !mSelectBank!!.isEmpty()) {
                            val accsn = mSelectBank!!["accsn"]!!.toString() + ""
                            var bankName = mSelectBank!!["opnbnknm"]!!.toString() + ""
                            if (UtilTools.empty(bankName)) {
                                if (accsn == "D") {
                                    bankName = "交易账户"
                                } else {
                                    bankName = ""
                                }
                            }
                            mCardText!!.text = bankName + "(" + UtilTools.getIDCardXing(mSelectBank!!["accid"]!!.toString() + "") + ")"
                        } else {
                            mCardText!!.text = "暂无可用的银行卡"
                        }
                    } else {
                        mCardText!!.text = "暂无可用的银行卡"
                    }
                }

                addOtherPay()
            }

            MethodUrl.chongzhiSubmit -> {
                mChongzhiButton!!.isEnabled = true
                val tt = tData["type"]!!.toString() + ""//1：银商入金 2：快捷入金
                val serialno = tData["serialno"]!!.toString() + ""//快捷支付流水号
                LogUtil.i("-----------------------------------------------------------", "$tData     $tt")
                when (tt) {
                    "1" -> {
                        intent = Intent(this@ChongzhiTestActivity, FuKuanFinishActivity::class.java)
                        intent.putExtra("type", "1")
                        intent.putExtra("money", mMoneyText!!.text.toString() + "")
                        intent.putExtra("accid", mSelectBank!!["accid"]!!.toString() + "")
                        intent.putExtra("bankName", mSelectBank!!["opnbnknm"]!!.toString() + "")
                        startActivity(intent)
                        sendBrodcast()
                        finish()
                    }
                    "2" -> {
                        showToastMsg("获取短信验证码成功")
                        intent = Intent(this, CodeMsgActivity::class.java)
                        intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_CHONGZHI_MONEY)
                        intent.putExtra("amount", mMoneyText!!.text.toString() + "")
                        intent.putExtra("accid", mSelectBank!!["accid"]!!.toString() + "")
                        intent.putExtra("bankName", mSelectBank!!["opnbnknm"]!!.toString() + "")
                        intent.putExtra("phone", mSelectBank!!["mobno"]!!.toString() + "")
                        intent.putExtra("DATA", tData as Serializable)
                        startActivity(intent)
                    }
                    else -> {
                    }
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                for (stag in mRequestTagList) {
                    when (stag) {
                        MethodUrl.bankCardList -> bankCardInfoAction()
                        MethodUrl.supervisionConfig -> cardConfig()
                        MethodUrl.chongzhiSubmit -> submitData()
                    }
                }
                mRequestTagList = ArrayList()
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
        mChongzhiButton!!.isEnabled = true
        dealFailInfo(map, mType)
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            30//不显示添加银行卡时候 点击返回的数据
            -> mSelectBank = map
            200//显示 添加银行卡   点击返回的列表数据
            -> mSelectBank = map
        }

        if (mSelectBank != null && !mSelectBank!!.isEmpty()) {
            val accsn = mSelectBank!!["accsn"]!!.toString() + ""
            var bankName = mSelectBank!!["opnbnknm"]!!.toString() + ""
            if (UtilTools.empty(bankName)) {
                if (accsn == "D") {
                    bankName = "交易账户"
                } else {
                    bankName = ""
                }
            }
            mCardText!!.text = bankName + "(" + UtilTools.getIDCardXing(mSelectBank!!["accid"]!!.toString() + "") + ")"
            //accsn   业务类型(1:提现账户;A充值卡<快捷支付>; D电子账户)
            when (accsn) {
                "1" -> {
                }
                "A" -> {
                }
                "D"//电子账户直接转账
                -> {
                    //showBaseMsgDialog("请向"+mSelectBank.get("accid")+"转账",false);
                    showTipMsgDialog(mSelectBank!!, false)
                    mChongzhiButton!!.isEnabled = true
                }
            }
        }
    }

    private fun showTipMsgDialog(map: MutableMap<String, Any>, isClose: Boolean) {
        if (mZDialog == null) {
            mZDialog = ZZTipMsgDialog(this, true)
            mZDialog!!.setOnKeyListener { dialog, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                    dialog.dismiss()
                    if (isClose) {
                        finish()
                    }
                    true
                } else {
                    false
                }
            }
            val onClickListener = View.OnClickListener { v ->
                when (v.id) {
                    R.id.cancel -> {
                        mZDialog!!.dismiss()
                        if (isClose) {
                            finish()
                        }
                    }
                    R.id.sure -> mZDialog!!.dismiss()
                    R.id.tv_right -> {
                        mZDialog!!.dismiss()
                        if (isClose) {
                            finish()
                        }
                    }
                }
            }
            mZDialog!!.setCanceledOnTouchOutside(false)
            mZDialog!!.setCancelable(true)
            mZDialog!!.onClickListener = onClickListener
        }
        mZDialog!!.initValue(map)
        mZDialog!!.show()

        mZDialog!!.mNameTv!!.text = MbsConstans.USER_MAP!!["comname"]!!.toString() + ""

    }


    private fun sendBrodcast() {
        val intent = Intent()
        intent.action = MbsConstans.BroadcastReceiverAction.MONEY_UPDATE
        sendBroadcast(intent)
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

}
