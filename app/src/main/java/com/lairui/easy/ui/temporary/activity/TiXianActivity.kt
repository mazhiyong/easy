package com.lairui.easy.ui.temporary.activity


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

import android.content.IntentFilter
import android.util.Log
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
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.BankCardSelectDialog
import com.lairui.easy.mywidget.dialog.TipMsgDialog
import com.lairui.easy.mywidget.dialog.TradePassDialog
import com.lairui.easy.utils.secret.RSAUtils
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 提现
 */
class TiXianActivity : BasicActivity(), RequestView, SelectBackListener, TradePassDialog.PassFullListener {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.tv_card)
    lateinit var mCardText: TextView
    @BindView(R.id.tv_tixin_maxmoney)
    lateinit var mMaxMoneyText: TextView
    @BindView(R.id.iv_morecard)
    lateinit var mCardArrowIamge: ImageView
    @BindView(R.id.but_next)
    lateinit var mNextButton: Button
    @BindView(R.id.money_edit)
    lateinit var mMoneyEdit: EditText
    @BindView(R.id.tixian_tip_tv)
    lateinit var mTixianTipTv: TextView
    @BindView(R.id.tv_tips)
    lateinit var mTipText: TextView
    @BindView(R.id.tv_toaccout_time)
    lateinit var mTimeText: TextView
    @BindView(R.id.tv_yue_money)
    lateinit var mYueText: TextView
    @BindView(R.id.ll_layout)
    lateinit var mLayout2: LinearLayout
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.rl_layout)
    lateinit var mRlLayout: RelativeLayout
    private var mDataList: List<MutableMap<String, Any>>? = ArrayList()
    private var mDataList2: List<MutableMap<String, Any>>? = ArrayList()
    private var mRequestTag = ""
    private lateinit var mDialog: BankCardSelectDialog
    var type: Int = 0
    //合作方编号
    lateinit var patncode: String

    private lateinit var mTradePassDialog: TradePassDialog
    private lateinit var mSetTiXianBank: MutableMap<String, Any>

    private var mIsShow = false
    private lateinit var mTradeStateMap: MutableMap<String, Any>

    private var mTradePass = ""

    private lateinit var mCardConfig: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_ti_xian

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.BANKUPDATE_UPDATE) {

                val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
                if (kind == "1") {
                    bankCardInfoAction()
                } else {
                    bankCardInfoAction()
                    bankCardInfoAction2()
                }
            } else if (action == MbsConstans.BroadcastReceiverAction.MONEY_UPDATE) {
                finish()
            } else if (action == MbsConstans.BroadcastReceiverAction.TRADE_PASS_UPDATE) {
                tradePassState()
            }
        }
    }


    lateinit var mZDialog: TipMsgDialog

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.tixian)

        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.BANKUPDATE_UPDATE)
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.MONEY_UPDATE)
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.TRADE_PASS_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)

        UtilTools.setMoneyEdit(mMoneyEdit!!, 0.0)
        type = intent.getIntExtra("TYPE", 0)

        mNextButton!!.isEnabled = false
        //获取用户所有的银行卡
        bankCardInfoAction()
        getMoneyAction()
        tradePassState()

        val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
        if (kind == "0") {//个人的情况下  添加提现卡  转成 添加快捷卡  然后选择快捷卡把这个快捷卡变成提现卡
            cardConfig()
        }
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
     * 请求资产信息（账号资金，可用资金，冻结资金）
     */
    private fun getMoneyAction() {
        showProgressDialog()
        //??????
        mRequestTag = MethodUrl.allZichan
        val map = HashMap<String, String>()
        map["qry_type"] = "acct"
        map["accid"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.allZichan, map)
    }


    /**
     * 获取 提现 银行卡列表
     */
    private fun bankCardInfoAction() {
        showProgressDialog()
        mRequestTag = MethodUrl.bankCardList
        val map = HashMap<String, String>()
        val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
        if (kind == "1") {
            map["accsn"] = "1"
        } else {

        }

        map["accsn"] = "1"
        map["isdefault"] = ""
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeadermap, MethodUrl.bankCardList, map)
    }

    /**
     * 是否设置交易密码
     */
    private fun tradePassState() {
        mRequestTag = MethodUrl.isTradePass
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.isTradePass, map)
    }

    /**
     * 获取 个 人 银行卡列表
     */
    private fun bankCardInfoAction2() {
        showProgressDialog()
        mRequestTag = MethodUrl.bankCardList2
        val map = HashMap<String, String>()
        val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeadermap, MethodUrl.bankCardList2, map)
    }

    private fun setTiXianAction() {
        showProgressDialog()
        mRequestTag = MethodUrl.bindCard
        val map = HashMap<String, Any>()


        map["accid"] = mSetTiXianBank!!["accid"]!!.toString() + ""//银行卡号
        map["opnbnkid"] = mSetTiXianBank!!["opnbnkid"]!!.toString() + ""//开户银行编号
        map["opnbnknm"] = mSetTiXianBank!!["opnbnknm"]!!.toString() + ""//开户银行名称
        map["opnbnkwdcd"] = mSetTiXianBank!!["opnbnkwdcd"]!!.toString() + ""//开户网点编号
        map["opnbnkwdnm"] = mSetTiXianBank!!["opnbnkwdnm"]!!.toString() + ""//开户网点名称
        //        map.put("wdprovcd", mSetTiXianBank.get("wdprovcd")+"");//开户网点地址-省份-编号
        //        map.put("wdprovnm", mSetTiXianBank.get("wdprovnm")+"");//开户网点地址-省份-名称
        //        map.put("wdcitycd",mSetTiXianBank.get("wdcitycd")+"");//开户网点地址-城市-编号
        //        map.put("wdcitynm", mSetTiXianBank.get("wdcitynm")+"");//开户网点地址-城市-名称

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.bindCard, map)
    }


    private fun buttonPress() {

        val tradeState = mTradeStateMap!!["trd_pwd_state"]!!.toString() + ""
        if (tradeState == "0") {//交易密码状态（0：未设置，1：已设置）
            showMsgDialog("暂未设置交易密码，前往设置交易密码", false)
            mNextButton!!.isEnabled = true
            return
        }

        if (mSetTiXianBank == null || mSetTiXianBank!!.isEmpty()) {
            mNextButton!!.isEnabled = true
            showToastMsg("暂无可选的银行卡信息")
            return
        }


        val money = mMoneyEdit!!.text.toString() + ""
        if (UtilTools.isEmpty(mMoneyEdit!!, "金额")) {
            showToastMsg("金额不能为空")
            mNextButton!!.isEnabled = true
            return
        }

        if (!UtilTools.CheckMoneyValid(money)) {
            showToastMsg("请输入有效金额")
            mNextButton!!.isEnabled = true
            return
        }
        showPassDialog()
        mNextButton!!.isEnabled = true
    }


    private fun submitData() {

        if (mSetTiXianBank == null || mSetTiXianBank!!.isEmpty()) {
            showToastMsg("暂无可选的银行卡信息")
            mNextButton!!.isEnabled = true
            return
        }


        val money = mMoneyEdit!!.text.toString() + ""
        if (UtilTools.isEmpty(mMoneyEdit!!, "金额")) {
            showToastMsg("金额不能为空")
            mNextButton!!.isEnabled = true
            return
        }

        if (!UtilTools.CheckMoneyValid(money)) {
            showToastMsg("请输入有效金额")
            mNextButton!!.isEnabled = true
            return
        }
        mRequestTag = MethodUrl.tixianSubmit
        val map = HashMap<String, Any>()
        map["amount"] = money//银行卡号
        map["accid"] = mSetTiXianBank!!["accid"]!!.toString() + ""//银行卡号
        map["trd_pwd"] = RSAUtils.encryptContent(mTradePass, RSAUtils.publicKey)//交易密码
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.tixianSubmit, map)
    }

    private fun showPassDialog() {

        if (mTradePassDialog == null) {
            mTradePassDialog = TradePassDialog(this, true)
            mTradePassDialog.passFullListener = this
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0)
            mTradePassDialog.mPasswordEditText!!.text = null

            mTradePassDialog.mForgetPassTv!!.setOnClickListener { getMsgCodeAction() }

        } else {
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0)
            mTradePassDialog.mPasswordEditText!!.text = null

        }
    }

    private fun getMsgCodeAction() {
        mRequestTag = MethodUrl.forgetTradePass
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        val tel = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, ""]!!.toString() + ""
        map["tel"] = tel//手机账号
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.forgetTradePass, map)
    }


    override fun onResume() {
        super.onResume()

        if (mIsRefresh) {
            //bankCardInfoAction();
            tradePassState()
        }
        mIsRefresh = false
    }

    @OnClick(R.id.left_back_lay, R.id.back_img, R.id.title_text, R.id.but_next, R.id.rl_layout)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                mNextButton!!.isEnabled = false
                buttonPress()
            }
            R.id.rl_layout -> {
                val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
                if (kind == "1") {
                    //企业没有提现卡的时候  去绑定提现卡界面
                    if (mSetTiXianBank == null || mSetTiXianBank!!.isEmpty()) {
                        intent = Intent(this, BankTiXianModifyActivity::class.java)
                        intent.putExtra("backtype", "20")
                        startActivity(intent)
                    } else {
                        //如果企业有提现卡的时候  弹出来选择
                        if (mDataList == null) {
                            mDataList = ArrayList()
                        }
                        val mAddDialog = BankCardSelectDialog(this, true, mDataList!!, 30)
                        mAddDialog.selectBackListener = this
                        mAddDialog.onClickListener = View.OnClickListener { v ->
                            when (v.id) {
                                R.id.add_card_lay -> {
                                    val intent = Intent(this@TiXianActivity, BankTiXianModifyActivity::class.java)
                                    intent.putExtra("backtype", "20")
                                    startActivity(intent)
                                    mAddDialog.dismiss()
                                }
                                R.id.tv_cancel -> mAddDialog.dismiss()
                            }
                        }
                        mAddDialog.showAtLocation(Gravity.BOTTOM, 0, 0)
                    }
                } else {
                    if (mDataList2 == null || mDataList2!!.size == 0) {
                        mIsShow = true
                        //获取个人 银行卡列表
                        bankCardInfoAction2()
                    } else {
                        //dialog展示个人银行卡列表
                        showBankSelectPer()
                    }
                }
            }
        }//mBackText.setEnabled(false);
        // submitData();
    }


    private fun showBankSelectPer() {

        var isShow = 200

        if (mCardConfig == null || mCardConfig.isEmpty()) {
            cardConfig()
            return
        } else {
            val fastSup = mCardConfig!!["fastSup"]!!.toString() + ""
            //是否支持银商入金（0：不支持 1：支持）-暂不支持 不需要调试            bankSup
            //是否支持快捷入金（0：不支持 1：支持                   fastSup
            //是否支持跨行转账（0：不支持 1：支持）                 obankSup
            if (fastSup == "1") {
                isShow = 200
            } else {
                isShow = 30
            }
        }


        mDialog = mDataList2?.let { BankCardSelectDialog(this, true, it, isShow) }!!
        mDialog!!.selectBackListener = this
        mDialog!!.onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.add_card_lay -> {
                    /*Intent intent = new Intent(TiXianActivity.this, BankTiXianModifyActivity.class);
                        intent.putExtra("backtype","20");
                        startActivity(intent);*/
                    val intent = Intent(this@TiXianActivity, ChongZhiCardAddActivity::class.java)
                    intent.putExtra("backtype", "110")
                    startActivity(intent)
                    mDialog!!.dismiss()
                }
                R.id.tv_cancel -> mDialog!!.dismiss()
            }
        }
        mDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mNextButton!!.isEnabled = true
        val intent: Intent
        when (mType) {
            MethodUrl.supervisionConfig//{"obankSup":"0","bankSup":"1","fastSup":"0"}
            -> {
                mCardConfig = tData
                //是否支持银商入金（0：不支持 1：支持）-暂不支持 不需要调试            bankSup
                //是否支持快捷入金（0：不支持 1：支持                   fastSup
                //是否支持跨行转账（0：不支持 1：支持）                 obankSup
                if (mIsShow) {
                    showBankSelectPer()
                    mIsShow = false
                }
            }
            MethodUrl.forgetTradePass -> {
                intent = Intent(this@TiXianActivity, CodeMsgActivity::class.java)
                val tel = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, ""]!!.toString() + ""
                intent.putExtra("DATA", tData as Serializable?)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_TRADE_PASS)
                intent.putExtra("showPhone", tel + "")
                startActivity(intent)
                mTradePassDialog!!.dismiss()
            }
            //交易密码状态
            MethodUrl.isTradePass -> {
                mNextButton!!.isEnabled = true
                mTradeStateMap = tData
            }
            MethodUrl.allZichan -> if (tData != null && !tData.isEmpty()) {
                val money = UtilTools.getRMBMoney(tData["bal_amt"]!!.toString() + "")
                val yue = UtilTools.getRMBMoney(tData["use_amt"]!!.toString() + "")
                val dongjie = UtilTools.getRMBMoney(tData["frz_amt"]!!.toString() + "")
                mTixianTipTv!!.text = "可提现余额$yue"
            }
            MethodUrl.tixianSubmit -> {
                mTradePassDialog!!.dismiss()
                dismissProgressDialog()
                sendBrodcast()
                intent = Intent(this@TiXianActivity, FuKuanFinishActivity::class.java)
                intent.putExtra("type", "2")
                intent.putExtra("money", mMoneyEdit!!.text.toString() + "")
                intent.putExtra("accid", mSetTiXianBank!!["accid"]!!.toString() + "")
                intent.putExtra("bankName", mSetTiXianBank!!["opnbnknm"]!!.toString() + "")
                startActivity(intent)
            }
            MethodUrl.bindCard -> {
                dismissProgressDialog()
                mCardText!!.text = mSetTiXianBank!!["opnbnknm"].toString() + "(" + UtilTools.getIDCardXing(mSetTiXianBank!!["accid"]!!.toString() + "") + ")"
            }
            MethodUrl.bankCardList -> {
                dismissProgressDialog()
                val result = tData!!["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    mCardText!!.text = "当前无可用的银行卡"
                } else {
                    Log.i("show", "银行卡列表：$result")
                    mDataList = JSONUtil.instance.jsonToList(result)
                    if (mDataList != null && mDataList!!.size > 0) {

                        for (map in mDataList!!) {
                            val s = map["accsn"]!!.toString() + ""
                            if (s == "1" || s == "3") {
                                mSetTiXianBank = map
                            }
                        }
                        if (mSetTiXianBank != null && !mSetTiXianBank!!.isEmpty()) {
                            mCardText!!.text = mSetTiXianBank!!["opnbnknm"].toString() + "(" + UtilTools.getIDCardXing(mSetTiXianBank!!["accid"]!!.toString() + "") + ")"
                        } else {
                            val kind = MbsConstans.USER_MAP!!["kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
                            if (kind == "1") {
                                mCardText!!.text = "当前无可用的银行卡"
                            } else {
                                mCardText!!.text = "请选择"
                            }
                        }
                    } else {
                        mCardText!!.text = "当前无可用的银行卡"
                    }
                }
            }
            MethodUrl.bankCardList2 -> {
                dismissProgressDialog()
                mDataList2 = JSONUtil.instance.jsonToList(tData!!["result"]!!.toString() + "")

                if (mIsShow) {
                    showBankSelectPer()
                    mIsShow = false
                }
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData!!["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                showProgressDialog()
                for (stag in mRequestTagList) {
                    when (stag) {
                        MethodUrl.isTradePass -> tradePassState()
                        MethodUrl.forgetTradePass -> getMsgCodeAction()
                        MethodUrl.bankCardList2 -> bankCardInfoAction2()
                        MethodUrl.bankCardList -> bankCardInfoAction()
                        MethodUrl.tixianSubmit -> submitData()
                        MethodUrl.bindCard -> setTiXianAction()
                        MethodUrl.allZichan -> getMoneyAction()
                        MethodUrl.supervisionConfig -> cardConfig()
                    }
                }
                mRequestTagList = ArrayList()
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mNextButton!!.isEnabled = true
        dismissProgressDialog()
        when (mType) {
            MethodUrl.tixianSubmit -> {
            }
        }//mTradePassDialog.dismiss();
        dealFailInfo(map, mType)
    }

    //更新选择后的结果
    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        mSetTiXianBank = map
        when (type) {
            30//不显示添加银行卡时候 点击返回的数据
            -> {
                mSetTiXianBank = map
                mCardText!!.text = mSetTiXianBank!!["opnbnknm"].toString() + "(" + UtilTools.getIDCardXing(mSetTiXianBank!!["accid"]!!.toString() + "") + ")"
            }
            200//显示 添加银行卡   点击返回的列表数据
            -> {
                mSetTiXianBank = map
                setTiXianAction()
            }
        }

    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

    private fun sendBrodcast() {
        val intent = Intent()
        intent.action = MbsConstans.BroadcastReceiverAction.MONEY_UPDATE
        sendBroadcast(intent)
    }

    override fun onPassFullListener(pass: String) {

        mTradePassDialog.mPasswordEditText!!.text = null
        mTradePass = pass
        mNextButton!!.isEnabled = false
        submitData()
    }

    private fun showMsgDialog(msg: Any, isClose: Boolean) {
        mZDialog = TipMsgDialog(this@TiXianActivity, true)
        mZDialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                dialog.dismiss()
                if (isClose) {
                    //finish();
                }
                true
            } else {
                false
            }
        }
        val onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.cancel -> {
                    val intent = Intent(this@TiXianActivity, ModifyOrderPassActivity::class.java)
                    intent.putExtra("BACK_TYPE", "2")
                    intent.putExtra("TYPE", "1")
                    startActivity(intent)
                    mZDialog!!.dismiss()
                    if (isClose) {
                        //finish();
                    }
                }
                R.id.confirm -> mZDialog!!.dismiss()
                R.id.tv_right -> {
                    mZDialog!!.dismiss()
                    if (isClose) {
                        //finish();
                    }
                }
            }
        }
        mZDialog.setCanceledOnTouchOutside(false)
        mZDialog.setCancelable(true)
        mZDialog.onClickListener = onClickListener
        mZDialog.initValue("温馨提示", msg)
        mZDialog.show()
        mZDialog.tv_right!!.visibility = View.VISIBLE
        mZDialog.tv_cancel!!.text = "设置交易密码"
        mZDialog.tv_cancel!!.setTextColor(ContextCompat.getColor(this@TiXianActivity, R.color.black))
    }
}
