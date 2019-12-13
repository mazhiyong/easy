package com.lairui.easy.ui.module5.activity

import android.content.Intent
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher

import androidx.core.content.ContextCompat
import android.view.View
import android.widget.*

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.*
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_editphone.*
import kotlinx.android.synthetic.main.activity_editphone.getCodeTv

/**
 *绑定手机号
 */
class EditPhobeActivity : BasicActivity(), RequestView {

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

    @BindView(R.id.tipTv)
    lateinit var mTipTv: TextView

    private var mPhone = ""
    private var code1 =""

    private var mRequestTag = ""

    private lateinit var mTimeCount: TimeCount


    override val contentView: Int
        get() = R.layout.activity_editphone

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "修改手机号"
        mTimeCount = TimeCount(1*60*1000,1000)
        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            val s = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""].toString()
            MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
        }
        phoneEt.setText(MbsConstans.USER_MAP!!["phone"] as String)

        codeEt.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    mButNext.isEnabled = true
                }
            }

        })

    }


    private fun getCodeAction() {

        mRequestTag = MethodUrl.MODIFY_PHONE_OLD_CODE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.MODIFY_PHONE_OLD_CODE
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@EditPhobeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.MODIFY_PHONE_OLD_CODE, map)
    }

    private fun getCodeAction2() {

        mRequestTag = MethodUrl.MODIFY_PHONE_NEW_CODE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.MODIFY_PHONE_NEW_CODE
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@EditPhobeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["phone"] = phoneEt.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.MODIFY_PHONE_NEW_CODE, map)
    }

    private fun subMitAction() {
        mRequestTag = MethodUrl.MODIFY_PHONE_NEXT_CODE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.MODIFY_PHONE_NEXT_CODE
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@EditPhobeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        code1 = codeEt.text.toString()
        map["code1"] = code1
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.MODIFY_PHONE_NEXT_CODE, map)
    }

    private fun subMitAction2() {
        mRequestTag = MethodUrl.MODIFY_PHONE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.MODIFY_PHONE
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@EditPhobeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["code1"] = code1
        map["code"] = codeEt.text.toString()
        map["phone"] = phoneEt.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.MODIFY_PHONE, map)
    }


    private fun getUserInfoAction() {
        mRequestTag = MethodUrl.ACCOUNT_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.ACCOUNT_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@EditPhobeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.ACCOUNT_INFO, map)
    }



    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.getCodeTv,R.id.but_next)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.getCodeTv ->{
                if (mRequestTag != "" && mRequestTag == MethodUrl.MODIFY_PHONE_NEXT_CODE){
                    if (TextUtils.isEmpty(phoneEt.text)){
                        showToastMsg("请输入手机号")
                        mButNext.isEnabled = true
                        return
                    }
                    mTimeCount.start()
                    getCodeAction2()
                }else{
                    mTimeCount.start()
                    getCodeAction()
                }


            }
            R.id.but_next ->{
                when(mRequestTag){
                    //获取旧手机号验证码点击下一步,校验
                    MethodUrl.MODIFY_PHONE_OLD_CODE ->{
                        mButNext.isEnabled = false
                        subMitAction()
                    }
                    //获取新的手机号验证码点击下一步,提交
                    MethodUrl.MODIFY_PHONE_NEW_CODE ->{
                        mButNext.isEnabled = false
                        subMitAction2()
                    }


                }

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
        val intent: Intent
        when (mType) {
            MethodUrl.MODIFY_PHONE_NEXT_CODE -> when (tData["code"].toString() + "") {
                "1" -> {
                    getCodeTv.isClickable = true
                    getCodeTv.setTextColor(ContextCompat.getColor(this@EditPhobeActivity,R.color.font_c))
                    mButNext.text = "确定"
                    phoneTv2.text = "新手机号码"
                    phoneEt.isEnabled = true
                    codeEt.setText("")
                    phoneEt.setText("")
                    phoneEt.setHint("请输入新手机号")
                    phoneEt.requestFocus()
                    if (mTimeCount != null){
                        mTimeCount.cancel()
                    }
                    getCodeTv.text = "获取验证码"

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@EditPhobeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.MODIFY_PHONE -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                   // getUserInfoAction()
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                    sendBroadcast(intent)
                    finish()
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@EditPhobeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.MODIFY_PHONE_OLD_CODE -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@EditPhobeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.MODIFY_PHONE_NEW_CODE -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@EditPhobeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.ACCOUNT_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    MbsConstans.USER_MAP = tData["data"] as MutableMap<String, Any>?
                    SPUtils.put(this@EditPhobeActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP!!))
                    finish()
                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    contentView
                    val intent = Intent(this@EditPhobeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }




        }
    }



    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when(mType){
            MethodUrl.MODIFY_PHONE_NEXT_CODE ->  mButNext.isEnabled = true
        }
    }


    // 倒计时内部类
    inner class TimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            getCodeTv.text = resources.getString(R.string.msg_code_again)
            getCodeTv.setTextColor(ContextCompat.getColor(this@EditPhobeActivity,R.color.font_c))
            getCodeTv.isClickable = true
            MbsConstans.CURRENT_TIME = 0
        }

        override fun onTick(millisUntilFinished: Long) {
            //计时过程显示
            getCodeTv.isClickable = false
            getCodeTv.setTextColor(ContextCompat.getColor(this@EditPhobeActivity,R.color.black99))
            getCodeTv.text = (millisUntilFinished / 1000 ).toString()+"秒后重发"
            MbsConstans.CURRENT_TIME = (millisUntilFinished / 1000).toInt()

        }

    }
}
