package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.BankCardSelectDialog
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.lairui.easy.mywidget.view.PullScrollView
import com.jaeger.library.StatusBarUtil





import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Pattern

import butterknife.BindView
import butterknife.OnClick

/**
 * 还款界面  界面
 */
class HuankuanActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.tv_repay_capital)
    lateinit var mTvRepayCapital: EditText
    @BindView(R.id.tv_principal)
    lateinit var mTvPrincipal: TextView
    @BindView(R.id.tv_loan_balance)
    lateinit var mTvLoanBalance: TextView
    @BindView(R.id.zhifu_name_tv)
    lateinit var mZhifuNameTv: TextView
    @BindView(R.id.image_view)
    lateinit var mBankImageView: ImageView
    @BindView(R.id.xieyi_tv)
    lateinit var mXieyiTv: TextView
    @BindView(R.id.iv_select_pay)
    lateinit var mIvSelectPay: LinearLayout
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.xiyi_lay)
    lateinit var mXieyiLay: LinearLayout
    @BindView(R.id.check_box_xieyi)
    lateinit var mXieyiCheckBox: CheckBox
    @BindView(R.id.zhifu_arrow_view)
    lateinit var mArrowView: ImageView
    @BindView(R.id.yue_tv)
    lateinit var mYueTv: TextView

    @BindView(R.id.toggle_money)
    lateinit var mToggleMoney: ImageView
    @BindView(R.id.zhanghu_yue_lay)
    lateinit var mZhanghuYueLay: LinearLayout
    @BindView(R.id.zhanghu_yue_line)
    lateinit var mZhanghuYueLine: View

    @BindView(R.id.refresh_layout)
    lateinit var refreshLayout: PullScrollView

    @BindView(R.id.yy_edit)
    lateinit var mYuanyinET: EditText
    @BindView(R.id.yuanyin_lay)
    lateinit var mYuanLay: LinearLayout

    private var mRequestTag = ""


    private var mDataMap: MutableMap<String, Any> = HashMap()
    private var mPayConfig: MutableMap<String, Any> = HashMap()
    private var mIsShow = false

    private var mType: String? = ""
    private var mYuanyin = ""

    private var mPayCreateMap: MutableMap<String, Any> = HashMap()
    private var mLixi: String? = ""
    private var mBenjin: String? = ""

    private lateinit var mDialog: BankCardSelectDialog
    private lateinit var mDataList: List<MutableMap<String, Any>>
    private lateinit var mBankList: List<MutableMap<String, Any>>

    private lateinit var mBankMap: MutableMap<String, Any>

    private val mIsShowDialog = false


    private var mMoney = 0.0
    private var mYuEMoney = 0.0

    private lateinit var mHuankuanMap: MutableMap<String, Any>

    private var mRequestLixi: Double = 0.toDouble()


    private lateinit var mKindSelectDialog: KindSelectDialog

    private var mPayType: String? = "2"

    override val contentView: Int
        get() = R.layout.pay_money_layout

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.JIE_HUAN_UPDATE) {
                finish()
            }

        }
    }


    private var isCheck = false


    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mButNext!!.isEnabled = true
                }
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        val bundle = intent.extras

        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.JIE_HUAN_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)

        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            if (bundle.containsKey("TYPE")) {
                mPayCreateMap = bundle.getSerializable("DATA2") as MutableMap<String, Any>
                mType = bundle.getString("TYPE")
                mLixi = bundle.getString("LIXI")
                mBenjin = bundle.getString("BENJIN")
                mYuEMoney = bundle.getDouble("MAXMONEY")
                mPayType = bundle.getString("PAYTYPE")
            }
        }
        mTitleText!!.text = resources.getString(R.string.repay_title)

        UtilTools.setMoneyEdit(mTvRepayCapital!!, 0.0)

        //键盘显示监听//当键盘弹出隐藏的时候会 调用此方法。
        mTvRepayCapital!!.viewTreeObserver.addOnGlobalLayoutListener{
            val rect = Rect()
            this@HuankuanActivity.window.decorView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = this@HuankuanActivity.window.decorView.rootView.height
            Log.e("TAG", rect.bottom.toString() + "#" + screenHeight)
            val heightDifference = screenHeight - rect.bottom
            val visible = heightDifference > screenHeight / 3
            if (visible) {
                mIsShow = true
            } else {
                if (mIsShow) {
                    getLixiAction()
                    //showToastMsg("软键盘隐藏");
                }
                mIsShow = false
            }
        }

        if (mType == "1") {
            mYuanLay.visibility = View.GONE

            mTvRepayCapital.isEnabled = false
            mButNext.text = resources.getString(R.string.but_submit)
            mXieyiLay.visibility = View.VISIBLE
            mTvRepayCapital.setText(mBenjin)
            val l = UtilTools.divide(java.lang.Double.valueOf(mLixi!!), 100.0)

            mTvPrincipal.text = UtilTools.fromDouble(l) + ""
            mIvSelectPay.isEnabled = false
            mTvLoanBalance.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney(mYuEMoney.toString() + "")
            if (mPayType == "2") {
                mZhifuNameTv.text = "资金账户还款"
            } else {
                mZhifuNameTv.text = "结算账户还款"
            }
            mBankImageView.visibility = View.GONE
            setXieyi()
            refreshLayout.setPullRefreshEnabled(false)

            mZhanghuYueLay.visibility = View.GONE
            mZhanghuYueLine.visibility = View.GONE
        } else {
            mYuanLay.visibility = View.VISIBLE

            showProgressDialog()
            //bankCardInfoAction();
            getConfigAction()
            mXieyiLay.visibility = View.GONE

            refreshLayout.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            refreshLayout.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)
            refreshLayout.setPullRefreshEnabled(true)

            refreshLayout.setRefreshListener(object:PullScrollView.RefreshListener{
                override fun onRefresh() {
                    getConfigAction()

                }

            })
        }
    }


    /**
     * 服务协议的显示信息
     */
    private fun setXieyi() {

        val tip = "已阅读并同意《还款申请书》"
        var dian = tip.length
        if (tip.contains("《")) {
            dian = tip.indexOf("《")
        } else {
            dian = tip.length
        }

        /* 用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)
        Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)*/
        val ss = SpannableString(tip)
        ss.setSpan(TextSpanClick(false), dian, tip.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.data_col)), 0, dian, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.xiey_color)), dian, tip.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mXieyiTv!!.text = ss
        //添加点击事件时，必须设置
        mXieyiTv!!.movementMethod = LinkMovementMethod.getInstance()

        mXieyiCheckBox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mButNext!!.isEnabled = true
            } else {
                mButNext!!.isEnabled = false
            }
        }
        mArrowView!!.visibility = View.INVISIBLE

    }


    /**
     * 获取还款申请配置信息
     */
    private fun getConfigAction() {
        mButNext!!.isEnabled = false

        mRequestTag = MethodUrl.repayConfig
        val map = HashMap<String, String>()
        map["loansqid"] = mDataMap["loansqid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.repayConfig, map)
    }

    /**
     * 获取利息信息
     */
    private fun getLixiAction() {

        val money = mTvRepayCapital!!.text.toString() + ""
        if (UtilTools.empty(money)) {
            return
        }
        /* double shuri = Double.valueOf(money);
        if (shuri > mMoney){
            return;
        }*/


        mRequestTag = MethodUrl.repayLixi
        val map = HashMap<String, String>()
        map["loansqid"] = mDataMap["loansqid"]!!.toString() + ""
        map["backbejn"] = mTvRepayCapital!!.text.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.repayLixi, map)
        showProgressDialog()
    }

    /**
     * 生成还款申请书
     */
    private fun createRepay() {

        if (UtilTools.isEmpty(mTvRepayCapital!!, "金额")) {
            showToastMsg("金额不能为空")
            mButNext!!.isEnabled = true
            return
        }
        val money = mTvRepayCapital!!.text.toString() + ""

        val shuru = java.lang.Double.valueOf(money)
        if (shuru > mMoney) {
            showToastMsg("输入金额不能大于" + UtilTools.fromDouble(mMoney) + "元")
            mButNext!!.isEnabled = true
            return
        }
        /*double lx = UtilTools.divide(mRequestLixi,100);

        double allMoney = UtilTools.add(shuru,lx);
        if(allMoney > mYuEMoney){
            showToastMsg("账户余额不足");
            mButNext.setEnabled(true);
            return;
        }*/
        if (shuru == 0.0) {
            showToastMsg("输入余额不能为0")
            mButNext!!.isEnabled = true
            return
        }

        if (UtilTools.empty(mZhifuNameTv!!.text.toString().trim { it <= ' ' })) {
            showToastMsg("请选择支付方式")
            mButNext!!.isEnabled = true
            return
        }


        mYuanyin = mYuanyinET!!.text.toString().trim { it <= ' ' }

        mRequestTag = MethodUrl.repayCreate
        val map = HashMap<String, Any>()
        map["loansqid"] = mDataMap["loansqid"]!!.toString() + ""//借款编号
        map["backbejn"] = mTvRepayCapital!!.text.toString() + ""//归还本金
        map["memo"] = mYuanyin  //还款原因
        val l = UtilTools.divide(java.lang.Double.valueOf(mRequestLixi), 100.0)
        map["backlixi"] = l.toString() + ""//归还利息
        map["backtype"] = mPayType!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.repayCreate, map)
        showProgressDialog()
    }

    /**
     * 获取用户银行卡列表
     */
    private fun bankCardInfoAction() {
        mRequestTag = MethodUrl.bankCard
        val map = HashMap<String, String>()
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeadermap, MethodUrl.bankCard, map)
        showProgressDialog()
    }

    /**
     * 还款提交申请
     */
    private fun submitData() {

        if (!mXieyiCheckBox!!.isChecked) {
            mButNext!!.isEnabled = true
            showToastMsg(resources.getString(R.string.xieyi_tips))
            return
        }

        mRequestTag = MethodUrl.repayApply
        val map = HashMap<String, Any>()
        map["rtnbillid"] = mPayCreateMap["rtnbillid"]!!.toString() + ""//还款申请编号
        map["loansqid"] = mDataMap["loansqid"]!!.toString() + ""//借款编号
        map["backbejn"] = mBenjin!! + ""//归还本金
        map["memo"] = mYuanyin  //还款原因
        val l = UtilTools.divide(java.lang.Double.valueOf(mLixi!!), 100.0)
        map["backlixi"] = l.toString() + ""//归还利息
        map["backtype"] = mPayType!!//还款账户类型(1：结算账户还款;2：资金账户还款
        mHuankuanMap = map
        val mHeaderMap = HashMap<String, String>()
        LogUtil.i("还款参数打印", map)
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.repayApply, map)
        showProgressDialog()
    }

    /**
     * 余额查询
     */
    private fun YueMoney() {
        mRequestTag = MethodUrl.allZichan
        val mHeaderMap = HashMap<String, String>()
        val map = HashMap<String, String>()
        //还款账户类型(1：结算账户还款;2：资金账户还款
        if (mZhifuNameTv!!.text.toString().trim { it <= ' ' } == "结算账户还款") {
            // map.put("qry_type","accid");
            // map.put("accid",mBankMap.get("accid")+"");
        } else {
            map["qry_type"] = "acct"
        }
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.allZichan, map)
        showProgressDialog()
    }


    //二类户余额查询
    private fun erLeiHuMoney() {
        if (mBankMap == null || mBankMap!!.isEmpty()) {
            showToastMsg("请选择支付方式")
            return
        }

        mRequestTag = MethodUrl.erleiMoney
        val map = HashMap<String, String>()
        map["patncode"] = mBankMap!!["patncode"]!!.toString() + ""
        map["crdno"] = mBankMap!!["accid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.erleiMoney, map)
        showProgressDialog()
    }

    override fun viewEnable() {
        if (mRequestTag == MethodUrl.repayConfig) {

        } else {

        }
        refreshLayout!!.setRefreshCompleted()
    }


    fun getBeginAndEnd(pattern: Pattern,
                       content: String): List<MutableMap<String, Any>> {
        val values = ArrayList<MutableMap<String, Any>>()
        val matcher = pattern.matcher(content)
        while (matcher.find()) {
            val value = HashMap<String, Any>()
            value["VALUE"] = matcher.group()
            value["BEGIN"] = Integer.valueOf(matcher.start())
            value["END"] = Integer.valueOf(matcher.end())
            values.add(value)
        }
        return values
    }

    /**
     * 显示银行卡信息
     */
    private fun showCard(str: String) {
        /* Spannable mSpannableString = new SpannableString(str);

        //匹配小括号里面内容
        List<Map<String, Object>> values = this.getBeginAndEnd(
                Pattern.compile("(?<=\\()[^\\)]+"), str);

        if (values == null || values.size() <= 0) {
            values = this.getBeginAndEnd(Pattern.compile("(?<=\\()[^\\)]+"), str);
        }


        for (Map<String, Object> value : values) {
            int begin = Integer.parseInt(value.get("BEGIN").toString());
            int end = Integer.parseInt(value.get("END").toString());
             mSpannableString.setSpan(new AbsoluteSizeSpan(UtilTools.sp2px(this,12)), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // mSpannableString.setSpan(new
            // StyleSpan(android.graphics.Typeface.BOLD), begin, end,
            // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
            mSpannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.black)), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色

        }
        mZhifuNameTv.setText(mSpannableString);*/


        val mSpan = SpannableString(str)
        val values2 = this.getBeginAndEnd(Pattern.compile("\\d+"), str)
        for (value in values2) {
            val begin = Integer.parseInt(value["BEGIN"]!!.toString())
            val end = Integer.parseInt(value["END"]!!.toString())
            mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(this, 16)), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.gray_text2)), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //设置前景色为洋红色
        }
        mZhifuNameTv!!.text = mSpan

    }

    @OnClick(R.id.back_img, R.id.but_next, R.id.iv_select_pay, R.id.left_back_lay, R.id.toggle_money)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.toggle_money -> {
                val b = mToggleMoney!!.isSelected
                if (!b) {
                    //erLeiHuMoney();
                    YueMoney()
                    mYueTv!!.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney("0")
                    mToggleMoney!!.isSelected = true

                } else {
                    mToggleMoney!!.isSelected = false
                    mYueTv!!.text = "****"
                }
            }
            R.id.iv_select_pay -> {
                /* if (mDialog != null){
                    mDialog.showAtLocation(Gravity.BOTTOM,0,0);
                }else {
                    bankCardInfoAction();
                    mIsShow = true;
                }*/
                //选择支付方式   还款账户类型(1：结算账户还款;2：资金账户还款)
                val allList = SelectDataUtil.getListByKeyList(SelectDataUtil.getNameCodeByType("repayAcct"))
                var mTypeList: MutableList<MutableMap<String, Any>> = ArrayList()

                val s = mPayConfig["zftype"]!!.toString() + ""

                if (s == "0") {
                    mTypeList.add(SelectDataUtil.getMap("1", allList))
                } else if (s == "1") {
                    mTypeList.add(SelectDataUtil.getMap("2", allList))
                } else {
                    mTypeList = allList as MutableList<MutableMap<String, Any>>
                }

                LogUtil.i("还款方式-----------------------", mTypeList)


                mKindSelectDialog = KindSelectDialog(this, true, mTypeList, 10)
                mKindSelectDialog!!.selectBackListener = this
                mKindSelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            }
            R.id.but_next -> {
                mButNext!!.isEnabled = false
                if (mType == "1") {
                    //submitData();
                    if (!mXieyiCheckBox!!.isChecked) {
                        mButNext!!.isEnabled = true
                        showToastMsg(resources.getString(R.string.xieyi_tips))
                        return
                    }
                    if (isCheck) {
                        submitData()
                    } else {

                        PermissionsUtils.requsetRunPermission(this@HuankuanActivity, object : RePermissionResultBack {
                            override fun requestSuccess() {
                                netWorkWarranty()
                            }

                            override fun requestFailer() {
                                toast(R.string.failure)
                                mButNext!!.isEnabled = true
                            }
                        }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)


                    }
                    //enterNextPage();
                    // submitData();
                } else {
                    createRepay()
                }
            }
            R.id.back_img -> {
                finish()
                finish()
            }
            R.id.left_back_lay -> finish()
        }
    }

    override fun showProgress() {
        //showProgressDialog();
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        //{smstoken=sms_token@3948602038bb8ecf912e0ede4a577ebd, send_tel=151****3298}
        val intent: Intent
        when (mType) {
            MethodUrl.erleiMoney -> {
                val yue = java.lang.Double.valueOf(tData["acctbal"]!!.toString() + "")
                mYuEMoney = UtilTools.divide(yue, 100.0)
                mYueTv!!.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney(tData["acctbal"]!!.toString() + "")
                mToggleMoney!!.isSelected = true
            }
            MethodUrl.bankCard -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {

                } else {
                    mDataList = JSONUtil.instance.jsonToList(result)!!
                }
                if (mDataList != null && mDataList!!.size > 0) {

                    mBankList = ArrayList(mDataList!!)
                    /*for (Map<String,Object> mm : mDataList){
                        String accid = mm.get("accid")+"";
                        String isDefault = mm.get("isdefault")+"";
                        if (!UtilTools.empty(accid) && isDefault.equals("1")){

                            mBankList.add(mm);
                        }
                    }*/
                    mBankMap = mBankList!![0]
                    mDialog = BankCardSelectDialog(this, true, mBankList as ArrayList<MutableMap<String, Any>>, 30)
                    mDialog!!.selectBackListener = this

                    val accid = mBankMap!!["accid"]!!.toString() + ""
                    val weihao = accid.substring(accid.length - 4, accid.length)
                    val s = mBankMap!!["bankname"].toString() + "(" + weihao + ")"
                    showCard(s)

                    erLeiHuMoney()

                    GlideUtils.loadImage(this@HuankuanActivity, mBankMap!!["logopath"]!!.toString() + "", mBankImageView!!)
                    if (mIsShow) {
                        mDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
                    }
                } else {
                    showToastMsg("暂无银行卡信息")
                }
                mIsShow = false
            }
            MethodUrl.repayApply//还款提交数据
            -> {
                isCheck = false
                showToastMsg("提交成功")
                intent = Intent(this, ResultMoneyActivity::class.java)
                intent.putExtra(MbsConstans.ResultType.RESULT_KEY, MbsConstans.ResultType.RESULT_HUANKUAN)
                intent.putExtra("DATA", mHuankuanMap as Serializable?)
                startActivity(intent)
                finish()
            }
            MethodUrl.repayCreate//生成还款申请书
            -> {
                intent = Intent(this@HuankuanActivity, HuankuanActivity::class.java)
                intent.putExtra("DATA", mDataMap as Serializable)
                intent.putExtra("DATA2", tData as Serializable)
                intent.putExtra("TYPE", "1")
                intent.putExtra("LIXI", mRequestLixi.toString() + "")
                intent.putExtra("BENJIN", mTvRepayCapital!!.text.toString() + "")
                intent.putExtra("MAXMONEY", mMoney)
                intent.putExtra("PAYTYPE", mPayType!! + "")
                startActivity(intent)
                mButNext!!.isEnabled = true
            }
            MethodUrl.repayLixi//利息
            -> {

                val lixi2 = java.lang.Double.valueOf(tData["repaylx"]!!.toString() + "")
                mRequestLixi = lixi2
                val d2 = UtilTools.divide(lixi2, 100.0)
                mTvPrincipal!!.text = UtilTools.fromDouble(d2) + ""
            }
            MethodUrl.repayConfig//配置信息
            -> {
                //bankCardInfoAction();
                mButNext!!.isEnabled = true
                refreshLayout!!.setRefreshCompleted()
                mPayConfig = tData
                val dd = java.lang.Double.valueOf(mPayConfig["backbejn"]!!.toString() + "")
                mMoney = UtilTools.divide(dd, 100.0)
                mTvRepayCapital!!.setText(UtilTools.fromDouble(mMoney) + "")

                UtilTools.setMoneyEdit(mTvRepayCapital!!, mMoney)

                val lixi = java.lang.Double.valueOf(mPayConfig["repaylx"]!!.toString() + "")
                mRequestLixi = lixi
                val d = UtilTools.divide(lixi, 100.0)
                mTvPrincipal!!.text = UtilTools.fromDouble(d) + ""
                mTvLoanBalance!!.text = UtilTools.getRMBMoney(mPayConfig["backbejn"]!!.toString() + "")


                val s = mPayConfig["zftype"]!!.toString() + ""

                // 还款账户类型(1：结算账户还款;2：资金账户还款)
                if (s == "0") {
                    mPayType = "1"
                } else if (s == "1") {
                    mPayType = "2"
                } else {
                    mPayType = "2"
                }

                if (mPayType == "1") {
                    mZhanghuYueLay!!.visibility = View.GONE
                }

                val mTypeMap = SelectDataUtil.getMapByKey(mPayType!!, SelectDataUtil.getNameCodeByType("repayAcct"))
                mZhifuNameTv!!.text = mTypeMap[mPayType!!]!!.toString() + ""
            }
            MethodUrl.allZichan //查询余额
            -> {
                mYueTv!!.text = UtilTools.getRMBMoney(tData["use_amt"]!!.toString() + "")
                mToggleMoney!!.isSelected = true
            }

            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                for (stag in mRequestTagList) {
                    when (stag) {
                        MethodUrl.bankCard -> {
                        }
                        MethodUrl.repayConfig -> getConfigAction()
                        MethodUrl.repayApply//还款提交数据
                        -> submitData()
                        MethodUrl.repayCreate//生成还款申请书
                        -> createRepay()
                        MethodUrl.repayLixi//利息
                        -> getLixiAction()
                        MethodUrl.erleiMoney -> erLeiHuMoney()
                        MethodUrl.allZichan -> YueMoney()
                    }//bankCardInfoAction();
                }
                mRequestTagList = ArrayList()
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.allZichan -> {
                mToggleMoney!!.isSelected = false
                mYueTv!!.text = "****"
            }
            MethodUrl.erleiMoney -> {
                mToggleMoney!!.isSelected = false
                mYueTv!!.text = "****"
            }
            MethodUrl.repayApply//还款提交数据
            -> {
                isCheck = false
                mButNext!!.isEnabled = true
                finish()
            }
            MethodUrl.repayCreate//生成还款申请书
            -> mButNext!!.isEnabled = true
            MethodUrl.repayLixi//利息
            -> {
            }
            MethodUrl.repayConfig//配置信息
            -> {
                mButNext!!.isEnabled = false
                refreshLayout!!.setRefreshCompleted()
                val msg = map["errmsg"]!!.toString() + ""
            }
        }
        dealFailInfo(map, mType)
        //mButNext.setEnabled(true);
    }


    /* private TipMsgDialog mTipMsgDialog;
    private void showZhangDialog(String msg){
        if (mTipMsgDialog == null){
            mTipMsgDialog = new TipMsgDialog(this,true);
            mTipMsgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0) {
                        dialog.dismiss();
                        finish();
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.cancel:
                            mTipMsgDialog.dismiss();
                            finish();
                            break;
                        case R.id.confirm:
                            mTipMsgDialog.dismiss();
                            break;
                        case R.id.tv_right:
                            mTipMsgDialog.dismiss();
                            finish();
                            break;
                    }
                }
            };
            mTipMsgDialog.setCanceledOnTouchOutside(false);
            mTipMsgDialog.setCancelable(true);
            mTipMsgDialog.setOnClickListener(onClickListener);
        }
        mTipMsgDialog.initValue("温馨提示",msg);
        mTipMsgDialog.show();
    }*/


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            30 -> {
                mBankMap = map
                erLeiHuMoney()
                val accid = mBankMap!!["accid"]!!.toString() + ""
                val weihao = accid.substring(accid.length - 4, accid.length)
                val s = mBankMap!!["bankname"].toString() + "(" + weihao + ")"
                showCard(s)
                GlideUtils.loadImage(this@HuankuanActivity, map["logopath"]!!.toString() + "", mBankImageView!!)
            }
            10 -> {
                mPayType = map["code"]!!.toString() + ""
                mZhifuNameTv!!.text = map["name"]!!.toString() + ""

                if (mPayType == "1") {
                    mZhanghuYueLay!!.visibility = View.GONE
                } else {
                    mZhanghuYueLay!!.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun enterNextPage() {
        //startActivityForResult(Intent(this, LivenessActivity::class.java), PAGE_INTO_LIVENESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)

        var intent: Intent
        val bundle: Bundle?
        if (requestCode == 1) {
            when (resultCode) {
                //人脸识别还款
                MbsConstans.FaceType.FACE_CHECK_HUANKUAN -> {
                    bundle = data!!.extras
                    if (bundle == null) {
                        isCheck = false
                        mButNext!!.isEnabled = true
                    } else {
                        showProgress()
                        isCheck = true
                        mButNext!!.isEnabled = false
                        submitData()
                    }
                }
                else -> mButNext!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_CHECK_HUANKUAN)
                intent = Intent(this@HuankuanActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mButNext!!.isEnabled = true
            }
        }
    }

    /**
     * 联网授权
     */
    private fun netWorkWarranty() {

      /*  val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@HuankuanActivity)
            val licenseManager = LivenessLicenseManager(this@HuankuanActivity)
            manager.registerLicenseManager(licenseManager)
            manager.takeLicenseFromNetwork(uuid)
            if (licenseManager.checkCachedLicense() > 0) {
                //授权成功
                mHandler.sendEmptyMessage(1)
            } else {
                //授权失败
                mHandler.sendEmptyMessage(2)
            }
        }).start()*/
    }


    private inner class TextSpanClick(private val status: Boolean) : ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false//取消下划线false
        }

        override fun onClick(v: View) {
            val intent = Intent(this@HuankuanActivity, PDFLookActivity::class.java)
            intent.putExtra("id", mPayCreateMap["pdfurl"]!!.toString() + "")
            startActivity(intent)
        }
    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

    companion object {
        private val PAGE_INTO_LIVENESS = 101
    }

}
