package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mywidget.dialog.ImageCodeDialog
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.MainActivity

/**
 */
class CodeMsgActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.msg_code_edit)
    lateinit var mMsgCodeEdit: EditText
    @BindView(R.id.msg_code_tv)
    lateinit var mGetCodeTv: TextView
    @BindView(R.id.msg_code_tip)
    lateinit var mGetCodeTip: TextView

    private var mData: MutableMap<String, Any> = HashMap()
    private var mOpType = 0

    private var mPhone: String? = ""
    private lateinit var mTimeCount: TimeCount

    private var mCodeUrl = ""
    private var mCodeCheck = ""

    private var mNewPhone: String? = ""
    private var mNewAuthCode: String? = ""

    private var mShowPhone: String? = ""

    private var mRequestTag = ""

    private var mAuthCode = ""

    private var mIdNum: String? = ""

    private var mKind = ""
    private var mInvcode = ""


    //绑定银行卡信息
    private var mAccid: String? = ""//卡号
    private var mMobno: String? = ""//银行预留手机号
    private var mOpnbnkid: String? = ""//开户银行编号
    private var mOpnbnknm: String? = ""//开户银行名称
    private var mOpnbnkwdcd: String? = ""//开户网点编号
    private var mOpnbnkwdnm: String? = ""//开户网点名称
    private var mWdprovcd: String? = ""//开户网点地址-省份-编号
    private var mWdprovnm: String? = ""//开户网点地址-省份-名称
    private var mWdcitycd: String? = ""//开户网点地址-城市-编号
    private var mWdcitynm: String? = ""//开户网点地址-城市-名称
    private var mBackType: String? = ""//返回到哪个activity


    //充值金钱信息
    private var mCAccid: String? = ""
    private var mCMoney: String? = ""

    override val contentView: Int
        get() = R.layout.activity_code_msg


    private var mImageCode = ""
    private lateinit var mImageCodeDialog: ImageCodeDialog

    private lateinit var mImageView: ImageView
    private lateinit var mBitmap: Bitmap

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        /*if (MbsConstans.USER_MAP != null){
            mPhone = MbsConstans.USER_MAP.get("tel")+"";
        }else {*/
        mPhone = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, ""]!!.toString() + ""
        //        }
        mTimeCount = TimeCount((1 * 60 * 1000).toLong(), 1000)//构造CountDownTimer对象

        mTitleText!!.text = resources.getString(R.string.msg_code_title)

        mMsgCodeEdit!!.requestFocus()

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mData = bundle.getSerializable("DATA") as MutableMap<String, Any>
            mOpType = bundle.getInt(MbsConstans.CodeType.CODE_KEY)
            if (bundle.containsKey("phonenum")) {
                mPhone = bundle.getString("phonenum")!! + ""
            }
            mShowPhone = bundle.getString("showPhone")!! + ""
            mKind = bundle.getString("kind")!! + ""
            mInvcode = bundle.getString("invcode")!! + ""
        }

        when (mOpType) {
            MbsConstans.CodeType.CODE_PHONE_CHANGE//更换手机号 老的手机号
            -> {
                mCodeUrl = MethodUrl.changePhoneMsgCode
                mCodeCheck = MethodUrl.checkCode
            }
            MbsConstans.CodeType.CODE_NEW_PHONE_CHANGE//更换手机号 新的手机号
            -> {
                mCodeUrl = MethodUrl.changePhoneMsgCode
                mCodeCheck = MethodUrl.checkCode
                if (bundle != null) {
                    mNewPhone = bundle.getString("phone")
                    mNewAuthCode = bundle.getString("authcode")
                }
                mPhone = mNewPhone
            }
            MbsConstans.CodeType.CODE_RESET_LOGIN_PASS//重置登录密码
            -> {
                mCodeUrl = MethodUrl.resetPassCode
                mCodeCheck = MethodUrl.checkCode
            }
            MbsConstans.CodeType.CODE_INSTALL//通过短信验证码  安装安全证书
            -> {
                if (bundle != null) {
                    mIdNum = bundle.getString("idno")
                }
                mCodeUrl = MethodUrl.installCode
                mCodeCheck = MethodUrl.checkCode
                mPhone = mIdNum
            }
            MbsConstans.CodeType.CODE_WANGYIN//开通网银
            -> {
                mCodeUrl = MethodUrl.normalSms
                mCodeCheck = MethodUrl.checkCode
            }
            MbsConstans.CodeType.CODE_MODIFY_ORDER_PASS//修改交易密码
            -> {
            }
            MbsConstans.CodeType.CODE_PHONE_REGIST //注册
            -> {
                mCodeUrl = MethodUrl.regSms
                mCodeCheck = MethodUrl.checkCode
                mPhone = mShowPhone
            }
            MbsConstans.CodeType.CODE_CARD_CHONGZHI//绑定充值卡
            -> {
                mCodeUrl = MethodUrl.bankCardSms
                mCodeCheck = MethodUrl.checkBankSms
                if (bundle != null) {
                    mAccid = bundle.getString("accid")//卡号
                    mMobno = bundle.getString("mobno")//银行预留手机号
                    mOpnbnkid = bundle.getString("opnbnkid")//开户银行编号
                    mOpnbnknm = bundle.getString("opnbnknm")//开户银行名称

                    mOpnbnkwdcd = bundle.getString("opnbnkwdcd")//开户网点编号
                    mOpnbnkwdnm = bundle.getString("opnbnkwdnm")//开户网点名称
                    mWdprovcd = bundle.getString("wdprovcd")//开户网点地址-省份-编号
                    mWdprovnm = bundle.getString("wdprovnm")//开户网点地址-省份-名称
                    mWdcitycd = bundle.getString("wdcitycd")//开户网点地址-城市-编号
                    mWdcitynm = bundle.getString("wdcitynm")//开户网点地址-城市-名称
                    mBackType = bundle.getString("backtype")//返回到哪个activity
                    mShowPhone = mMobno
                }
            }
            MbsConstans.CodeType.CODE_CHONGZHI_MONEY  //充值金额  获取短信验证码
            -> {
                mCodeUrl = MethodUrl.chongzhiSubmit
                mCodeCheck = MethodUrl.chongzhiMoneyCodeCheck//
                if (bundle != null) {
                    mCAccid = bundle.getString("accid")//卡号
                    mCMoney = bundle.getString("amount")//金额
                    mOpnbnknm = bundle.getString("bankName")//银行名称
                    mPhone = bundle.getString("phone")//银行预留手机号码
                    mShowPhone = mPhone
                }
            }
            MbsConstans.CodeType.CODE_TRADE_PASS  //忘记交易密码获取短信验证码
            -> {
                mCodeUrl = MethodUrl.forgetTradePass
                mCodeCheck = MethodUrl.checkCode
            }
        }

        mGetCodeTip!!.text = mGetCodeTip!!.text.toString() + " " + UtilTools.getPhoneXing(mPhone!!)

        mTimeCount!!.start()
        // getMsgCodeAction();
    }


    private fun getMsgCodeAction() {
        mRequestTag = mCodeUrl
        val map = HashMap<String, Any>()
        when (mOpType) {
            MbsConstans.CodeType.CODE_PHONE_CHANGE//更换手机号 老的手机号
            -> {
            }
            MbsConstans.CodeType.CODE_NEW_PHONE_CHANGE//更换手机号 新的手机号
            -> {
                map["tel"] = mNewPhone!!
                map["authcode"] = mNewAuthCode!!
            }
            MbsConstans.CodeType.CODE_RESET_LOGIN_PASS//重置登录密码
            -> map["tel"] = mPhone!!
            MbsConstans.CodeType.CODE_INSTALL//安装证书  需要的短信验证码
            -> map["idno"] = mIdNum!!
            MbsConstans.CodeType.CODE_WANGYIN//开通网银
            -> map["busi"] = "COMMON"
            MbsConstans.CodeType.CODE_MODIFY_ORDER_PASS//修改交易密码
            -> {
            }
            MbsConstans.CodeType.CODE_PHONE_REGIST //注册
            -> {
                map["tel"] = mPhone!!
                map["authcode"] = mAuthCode
            }
            MbsConstans.CodeType.CODE_CARD_CHONGZHI//绑定充值卡获取短信验证码
            -> {
                map["accid"] = mAccid!!
                map["mobno"] = mMobno!!
                map["opnbnkid"] = mOpnbnkid!!
                map["opnbnknm"] = mOpnbnknm!!

                map["opnbnkwdcd"] = mOpnbnkwdcd!!//开户网点编号
                map["opnbnkwdnm"] = mOpnbnkwdnm!!//开户网点名称
                map["wdprovcd"] = mWdprovcd!!//开户网点地址-省份-编号
                map["wdprovnm"] = mWdprovnm!!//开户网点地址-省份-名称
                map["wdcitycd"] = mWdcitycd!!//开户网点地址-城市-编号
                map["wdcitynm"] = mWdcitynm!!//开户网点地址-城市-名称
            }
            MbsConstans.CodeType.CODE_CHONGZHI_MONEY//充值钱操作  获取短信验证码
            -> {
                map["amount"] = mCMoney!!
                map["accid"] = mCAccid!!
            }
            MbsConstans.CodeType.CODE_TRADE_PASS//忘记交易密码   获取短信验证码
            -> map["tel"] = mPhone!!
        }

        val mHeaderMap = HashMap<String, String>()
        //mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.resetPassCode, map);
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, mCodeUrl, map)
    }

    private fun checkMsgCode() {

        mRequestTag = mCodeCheck
        val msgCode = mMsgCodeEdit!!.text.toString() + ""

        if (UtilTools.empty(msgCode)) {
            mButNext!!.isEnabled = true
            showToastMsg("请输入验证码")
            return
        }

        val map = HashMap<String, Any>()
        map["smstoken"] = mData["smstoken"]!!.toString() + ""
        map["smscode"] = msgCode
        map["busi"] = "COMMON"
        when (mOpType) {
            MbsConstans.CodeType.CODE_PHONE_CHANGE//更换手机号 老的手机号
            -> {
                map["tel"] = mPhone!!
                map["busi"] = "MODTEL_OLD"
            }
            MbsConstans.CodeType.CODE_NEW_PHONE_CHANGE//更换手机号 新的手机号
            -> {
                map["tel"] = mNewPhone!!
                map["busi"] = "MODTEL_NEW"
            }
            MbsConstans.CodeType.CODE_RESET_LOGIN_PASS//重置登录密码
            -> {
                map["tel"] = mPhone!!
                map["busi"] = "MODPWD"
            }
            MbsConstans.CodeType.CODE_INSTALL// 安装安全证书
            -> {
                map["tel"] = mPhone!!
                map["busi"] = "CA_APPLY"
            }
            MbsConstans.CodeType.CODE_WANGYIN//网银短信验证
            -> map["tel"] = mPhone!!
            MbsConstans.CodeType.CODE_MODIFY_ORDER_PASS//修改交易密码
            -> {
            }
            MbsConstans.CodeType.CODE_PHONE_REGIST//注册
            -> {
                map["busi"] = "REGISTER"
                map["tel"] = mPhone!!
            }
            MbsConstans.CodeType.CODE_CARD_CHONGZHI//绑定充值卡
            -> {
                map.clear()
                map["bindNo"] = mData["bindNo"]!!.toString() + ""
                map["smsCode"] = msgCode
                map["accid"] = mAccid!!
            }

            MbsConstans.CodeType.CODE_CHONGZHI_MONEY//充值钱操作
            -> {
                map.clear()
                map["serialno"] = mData["serialno"]!!.toString() + ""
                map["smscode"] = msgCode
            }
            MbsConstans.CodeType.CODE_TRADE_PASS//忘记交易密码 验证短信验证码
            -> {
                map["tel"] = mPhone!!
                map["busi"] = "FORGET_TRDPWD"
            }
        }
        val mHeaderMap = HashMap<String, String>()
        //mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.checkCode, map);
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, mCodeCheck, map)
    }


    private fun submitAction() {

        mRequestTag = MethodUrl.changePhoneSubmit
        val map = HashMap<String, String>()
        map["authcode"] = mAuthCode
        map["tel_new"] = mNewPhone!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPutToRes(mHeaderMap, MethodUrl.changePhoneSubmit, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.msg_code_tv, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.msg_code_tv -> if (mOpType == MbsConstans.CodeType.CODE_PHONE_REGIST) {
                getImageCode()
            } else {
                mTimeCount!!.start()
                getMsgCodeAction()
            }
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                mButNext!!.isEnabled = false
                checkMsgCode()
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
        mButNext!!.isEnabled = true
        val intent: Intent
        when (mType) {
            MethodUrl.forgetTradePass -> {
                mData = tData
                mTimeCount!!.start()
                showToastMsg("获取验证码成功")
            }
            MethodUrl.imageCode ->

                // 获取图片验证码
                if (tData.containsKey("img")) {//获取图片验证码
                    mBitmap = (tData["img"] as Bitmap?)!!

                    if (mImageCodeDialog == null) {
                        showZhangDialog()
                    } else {
                        mImageCodeDialog.show()
                        mImageCodeDialog.mContentTv!!.setImageBitmap(mBitmap)
                    }

                } else {
                    mAuthCode = tData["authcode"]!!.toString() + "" //验证图片验证码
                    getMsgCodeAction()
                }
            MethodUrl.normalSms -> {
                mData = tData
                mTimeCount!!.start()
                showToastMsg("获取验证码成功")
            }
            //重置密码获取短信验证码
            MethodUrl.resetPassCode//{smstoken=sms_token@d72f4338e1d05c746b36316e6a67f552, send_tel=151****3298}
            -> {
                mData = tData
                mTimeCount!!.start()
                showToastMsg("获取验证码成功")
            }
            //更改手机号获取短信验证码
            MethodUrl.changePhoneMsgCode//{smstoken=sms_token@d72f4338e1d05c746b36316e6a67f552, send_tel=151****3298}
            -> {
                mData = tData
                mTimeCount!!.start()
                showToastMsg("获取验证码成功")
            }
            //注册
            MethodUrl.regSms -> {
                mData = tData
                mTimeCount!!.start()
                showToastMsg("获取验证码成功")
            }
            //绑卡短信验证码
            MethodUrl.bankCardSms -> {
                mData = tData
                showToastMsg("获取短信验证码成功")
            }
            MethodUrl.checkCode//{"authcode":"auth_code@cbfb271f02bc96baf91ae104a741320a"}
            -> {

                UtilTools.hideSoftInput(this@CodeMsgActivity, mMsgCodeEdit!!)
                when (mOpType) {
                    MbsConstans.CodeType.CODE_PHONE_CHANGE//更换手机号 老的手机号
                    -> {
                        intent = Intent(this@CodeMsgActivity, ChangePhoneActivity::class.java)
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        startActivity(intent)
                        finish()
                    }
                    MbsConstans.CodeType.CODE_NEW_PHONE_CHANGE//更换手机号 新的手机号
                    -> {
                        mAuthCode = tData["authcode"]!!.toString() + ""
                        // submitAction(tData.get("authcode")+"");
                        submitAction()
                    }
                    MbsConstans.CodeType.CODE_RESET_LOGIN_PASS//重置登录密码
                    -> {
                        intent = Intent(this@CodeMsgActivity, ResetLoginPassButActivity::class.java)
                        intent.putExtra("phone", mPhone)
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        intent.putExtra("type", "0")
                        startActivity(intent)
                        finish()
                    }
                    MbsConstans.CodeType.CODE_INSTALL// 安装安全证书
                    -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.CodeType.CODE_INSTALL, intent)
                        finish()
                    }
                    MbsConstans.CodeType.CODE_WANGYIN//开通网银
                    -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.CodeType.CODE_WANGYIN, intent)
                        finish()
                    }
                    MbsConstans.CodeType.CODE_MODIFY_ORDER_PASS//修改交易密码
                    -> {
                    }
                    MbsConstans.CodeType.CODE_PHONE_REGIST//注册
                    -> {
                        mTimeCount!!.start()
                        intent = Intent(this@CodeMsgActivity, ResetLoginPassButActivity::class.java)
                        intent.putExtra("phone", mPhone)
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        intent.putExtra("type", "1")
                        intent.putExtra("kind", mKind)
                        intent.putExtra("invcode", mInvcode)
                        startActivity(intent)
                        finish()
                    }
                    MbsConstans.CodeType.CODE_TRADE_PASS -> {
                        mTimeCount!!.start()
                        intent = Intent(this@CodeMsgActivity, ModifyOrderPassActivity::class.java)
                        intent.putExtra("TYPE", "3")
                        intent.putExtra("BACK_TYPE", "3")
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        startActivity(intent)
                        finish()
                    }
                }
            }
            MethodUrl.changePhoneSubmit -> {
                showToastMsg("修改手机号成功")
                intent = Intent(this@CodeMsgActivity, SubmitResultActivity::class.java)
                intent.putExtra(MbsConstans.ResultType.RESULT_KEY, MbsConstans.ResultType.RESULT_PHONE_CHANGE)
                startActivity(intent)
                finish()
            }
            MethodUrl.checkBankSms//绑充值卡卡验证短信验证码
            -> {
                showToastMsg("绑卡成功")
                sendBrodcast2()
                if (mBackType == "10") {
                    backTo(BankCardActivity::class.java, false)
                } else if (mBackType == "20") {
                    backTo(TiXianActivity::class.java, true)
                } else if (mBackType == "30") {
                    backTo(ChongzhiTestActivity::class.java, true)
                } else if (mBackType == "40") {
                    //backTo(PayMoneyChoseWayActivity.class,true);
                    finish()
                } else if (mBackType == "110") {//个人账户 提现界面添加银行卡  跳转
                    backTo(TiXianActivity::class.java, true)
                    finish()
                } else if (mBackType == "100") {//个人账户 首页进来申请额度  检测没有提现卡的时候
                    backTo(MainActivity::class.java, true)
                } else {
                    finish()
                }
            }
            MethodUrl.chongzhiSubmit -> {
                mData = tData
                showToastMsg("获取短信验证码成功")
            }
            MethodUrl.chongzhiMoneyCodeCheck//充值钱验证短信验证码
            -> {
                intent = Intent(this@CodeMsgActivity, FuKuanFinishActivity::class.java)
                intent.putExtra("type", "1")
                intent.putExtra("money", mCMoney)
                intent.putExtra("accid", mCAccid)
                intent.putExtra("bankName", mOpnbnknm)
                startActivity(intent)
                sendBrodcast()
                finish()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.forgetTradePass -> getMsgCodeAction()
                    MethodUrl.checkBankSms -> checkMsgCode()
                    MethodUrl.resetPassCode -> getMsgCodeAction()
                    MethodUrl.changePhoneMsgCode -> getMsgCodeAction()
                    MethodUrl.checkCode -> checkMsgCode()
                    MethodUrl.changePhoneSubmit -> submitAction()
                    MethodUrl.chongzhiMoneyCodeCheck -> checkMsgCode()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mButNext!!.isEnabled = true
        val msg = map["errmsg"]!!.toString() + ""
        val errcodeStr = map["errcode"]!!.toString() + ""
        var errorCode = -1
        try {
            errorCode = java.lang.Double.valueOf(errcodeStr).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.i("show", "这里出现异常了" + e.message)
        }

        when (mType) {

            //重置密码获取短信验证码
            MethodUrl.resetPassCode//{smstoken=sms_token@d72f4338e1d05c746b36316e6a67f552, send_tel=151****3298}
            -> {
            }
            //更改手机号获取短信验证码
            MethodUrl.changePhoneMsgCode//{smstoken=sms_token@d72f4338e1d05c746b36316e6a67f552, send_tel=151****3298}
            -> {
            }
            MethodUrl.checkCode//{"authcode":"auth_code@cbfb271f02bc96baf91ae104a741320a"}
            -> {
            }
            MethodUrl.changePhoneSubmit -> {
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
            }
            MethodUrl.checkBankSms//绑定充值卡  验证短信验证码操作
            -> {
            }
        }// finish();
        //finish();
        /*if (errorCode == ErrorHandler.REFRESH_TOKEN_DATE_CODE){
                }else if (errorCode == ErrorHandler.ACCESS_TOKEN_DATE_CODE){
                }else if(errorCode == ErrorHandler.PHONE_NO_ACTIVE){
                } else {

                    showToastMsg(msg);
                    finish();
                    return;
                }*/

        //1006 access_token过期需要重新登录      1007 refresh_token  过期需要重新获取refresh_token
        dealFailInfo(map, mType)
    }


    private fun getImageCode() {

        mRequestTag = MethodUrl.imageCode
        val map = HashMap<String, String>()
        map["token"] = MbsConstans.TEMP_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.imageCode, map)
    }

    private fun checkImageCode() {

        mRequestTag = MethodUrl.imageCode
        val map = HashMap<String, Any>()
        map["temptoken"] = MbsConstans.TEMP_TOKEN
        map["imgcode"] = mImageCode
        map["tel"] = mPhone!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.imageCode, map)
    }

    private fun showZhangDialog() {
        mImageCodeDialog = ImageCodeDialog(this, true)
        val onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.cancel -> mImageCodeDialog.dismiss()
                R.id.confirm -> {

                    mImageCode = mImageCodeDialog.mEditText!!.text.toString() + ""

                    if (UtilTools.empty(mImageCode)) {

                        showToastMsg("请输入验证码")
                        return@OnClickListener
                    }


                    checkImageCode()
                    mImageCodeDialog!!.dismiss()
                }
                R.id.tv_right -> mImageCodeDialog!!.dismiss()
            }
        }
        mImageCodeDialog.setCanceledOnTouchOutside(false)
        mImageCodeDialog.setCancelable(true)
        mImageCodeDialog.onClickListener = onClickListener
        mImageCodeDialog.initValue("图形验证码", "")
        mImageCodeDialog.show()

        mImageCodeDialog.mContentTv!!.setImageBitmap(mBitmap)

        mImageCodeDialog.mContentTv!!.setOnClickListener { getImageCode() }


        mImageCodeDialog.tv_cancel!!.isEnabled = false
        mImageCodeDialog.tv_cancel!!.visibility = View.GONE
    }


    /* 定义一个倒计时的内部类 */
    private inner class TimeCount(millisInFuture: Long, countDownInterval: Long)//参数依次为总时长,和计时的时间间隔
        : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {//计时完毕时触发
            mGetCodeTv.text = resources.getString(R.string.msg_code_again)
            mGetCodeTv.isClickable = true
            MbsConstans.CURRENT_TIME = 0
        }

        override fun onTick(millisUntilFinished: Long) {//计时过程显示
            mGetCodeTv.isClickable = false
            mGetCodeTv.text = (millisUntilFinished / 1000).toString() + "秒"
            MbsConstans.CURRENT_TIME = (millisUntilFinished / 1000).toInt()
        }
    }

    private fun sendBrodcast() {
        val intent = Intent()
        intent.action = MbsConstans.BroadcastReceiverAction.MONEY_UPDATE
        sendBroadcast(intent)
    }

    private fun sendBrodcast2() {
        val intent = Intent()
        intent.action = MbsConstans.BroadcastReceiverAction.BANKUPDATE_UPDATE
        sendBroadcast(intent)
    }

}
