package com.lairui.easy.ui.module.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.CountDownTimer
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.ui.temporary.activity.CodeMsgActivity
import com.lairui.easy.utils.tool.*
import kotlinx.android.synthetic.main.activity_regist.*
import java.io.Serializable
import java.util.*

class ResetPassWordActivity : BasicActivity(), RequestView, SelectBackListener, CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.tv_zhuti)
    lateinit var mTvZhuti: TextView
    @BindView(R.id.zhuti_lay)
    lateinit var mZhutiLay: LinearLayout
    @BindView(R.id.et_code)
    lateinit var mEtCode: EditText
    @BindView(R.id.iv_code)
    lateinit var mIvCode: ImageView
    @BindView(R.id.bt_next)
    lateinit var mBtNext: Button
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.arrow_view)
    lateinit var mArrowView: ImageView


    private lateinit var mDialog: KindSelectDialog
    private var mRequestTag = ""
    private var authcode = ""
    private var type = ""
    private var invcode = ""


    private lateinit var mKindMap: MutableMap<String, Any>

    private lateinit var mTimeCount:TimeCount


    override val contentView: Int
        get() = R.layout.activity_reset_password


    override fun init() {

        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTimeCount = TimeCount(1*60*1000,1000)
        mTitleText.text = ""
        mDivideLine.visibility = View.GONE

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            invcode = bundle.getString("result")!! + ""
        }

        togglePwd.setOnCheckedChangeListener(this)

    }


    /**
     * 获取全局字典配置信息
     */
    fun getNameCodeInfo() {
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.nameCode, map)
    }

    /**
     * 是否显示密码
     */
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            //显示密码
            etPassWord.transformationMethod = HideReturnsTransformationMethod.getInstance()
            etPassWordAgain.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            //隐藏密码
            etPassWord.transformationMethod = PasswordTransformationMethod.getInstance()
            etPassWordAgain.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }


    @OnClick( R.id.getCodeTv, R.id.bt_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.getCodeTv -> {
                if (UtilTools.empty(etPhone.text)){
                    showToastMsg("请输入手机号")
                    return
                }
                mTimeCount.start()
                getCodeAction()
            }
            R.id.bt_next -> {
                mBtNext.isEnabled = false
                mBtNext.setTextColor(ContextCompat.getColor(this@ResetPassWordActivity,R.color.black99))
                registAction()
            }
        }
    }

    private fun getCodeAction() {
        mRequestTag = MethodUrl.FORGOT_CODE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.FORGOT_CODE
        map["phone"] = etPhone.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.FORGOT_CODE, map)

    }


    private fun getTempTokenAction() {
        mRequestTag = MethodUrl.tempToken
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToMap(mHeaderMap, MethodUrl.tempToken, map)
    }


    private fun getImageCodeAction() {
        type = "0"
        mRequestTag = MethodUrl.imageCode
        val map = HashMap<String, String>()
        map["token"] = MbsConstans.TEMP_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MethodUrl.imageCode, map)
    }


    private fun registAction() {

        if (TextUtils.isEmpty(etPhone.text)){
            showToastMsg("请输入手机号")
            mBtNext.isEnabled = true
            mBtNext.setTextColor(ContextCompat.getColor(this@ResetPassWordActivity,R.color.white))
            return
        }

        if (TextUtils.isEmpty(etCode.text)) {
            showToastMsg("请输入短信验证码")
            mBtNext.isEnabled = true
            mBtNext.setTextColor(ContextCompat.getColor(this@ResetPassWordActivity,R.color.white))
            return
        }

        if (TextUtils.isEmpty(etPassWord.text) || TextUtils.isEmpty(etPassWordAgain.text)) {
            showToastMsg("请设置密码")
            mBtNext.isEnabled = true
            mBtNext.setTextColor(ContextCompat.getColor(this@ResetPassWordActivity,R.color.white))
            return
        }



        mRequestTag = MethodUrl.FORGOT_ACTION
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.FORGOT_ACTION
        map["phone"] = etPhone.text.toString()
        map["code"] = etCode.text.toString()
        map["password"] = etPassWord.text.toString()
        map["confirm"] = etPassWordAgain.text.toString()

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.FORGOT_ACTION, map)


    }

    private fun cheackImageCodeAction() {
        type = "1"
        mRequestTag = MethodUrl.imageCode
        val map = HashMap<String, Any>()
        map["imgcode"] = mEtCode!!.text.toString()
        map["temptoken"] = MbsConstans.TEMP_TOKEN
        map["tel"] = etPhone!!.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.imageCode, map)

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
        mBtNext.isEnabled = true
        mBtNext.setTextColor(ContextCompat.getColor(this@ResetPassWordActivity,R.color.white))
        val intent: Intent
        when (mType) {
            MethodUrl.FORGOT_CODE -> {
                // 返回值  code  1正常  0异常    -1异常登录
                LogUtil.i("show","code:"+tData["code"])
                when (tData["code"].toString()){
                    "1" -> showToastMsg(tData["msg"] as  String)
                    "0" -> showToastMsg(tData["msg"] as  String)

                }

            }
            MethodUrl.FORGOT_ACTION -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    finish()
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
            }

            MethodUrl.tempToken -> {
                MbsConstans.TEMP_TOKEN = tData["temp_token"]!!.toString() + ""
                getImageCodeAction()
            }
            MethodUrl.imageCode ->
                // 获取图片验证码
                if (tData.containsKey("img")) {//获取图片验证码
                    val bitmap = tData["img"] as Bitmap?
                    mIvCode!!.setImageBitmap(bitmap)
                } else {
                    authcode = tData["authcode"]!!.toString() + "" //验证图片验证码
                    getSmgCodeAction()
                }
            MethodUrl.regSms -> {
                showToastMsg(resources.getString(R.string.code_phone_tip))
                intent = Intent(this, CodeMsgActivity::class.java)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_PHONE_REGIST)
                intent.putExtra("phone", etPhone!!.text.toString())
                intent.putExtra("authcode", authcode)
                intent.putExtra("invcode", invcode)
                intent.putExtra("showPhone", etPhone!!.text.toString())
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra("kind", mKindMap!!["code"]!!.toString() + "")
                startActivity(intent)
            }
        }

    }

    private fun getSmgCodeAction() {
        mRequestTag = MethodUrl.regSms
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        map["tel"] = etPhone!!.text.toString()
        map["authcode"] = authcode
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.regSms, map)
    }


    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mBtNext.isEnabled = true
        mBtNext.setTextColor(ContextCompat.getColor(this@ResetPassWordActivity,R.color.white))
        when (mType) {
            MethodUrl.imageCode -> if (type == "0") { //请求验证码失败
                mIvCode!!.setImageResource(R.drawable.default_pic)
            } else { //验证验证码失败
                getImageCodeAction()
            }
        }

        dealFailInfo(map, mType)
    }


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {

    }


    // 倒计时内部类
   inner class TimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            getCodeTv.text = resources.getString(R.string.msg_code_again)
            getCodeTv.setTextColor(ContextCompat.getColor(this@ResetPassWordActivity,R.color.font_c))
            getCodeTv.isClickable = true
            MbsConstans.CURRENT_TIME = 0
        }

        override fun onTick(millisUntilFinished: Long) {
            //计时过程显示
            getCodeTv.isClickable = false
            getCodeTv.setTextColor(ContextCompat.getColor(this@ResetPassWordActivity,R.color.black99))
            getCodeTv.text = (millisUntilFinished / 1000 ).toString()+"秒后重发"
            MbsConstans.CURRENT_TIME = (millisUntilFinished / 1000).toInt()

        }

    }
}
