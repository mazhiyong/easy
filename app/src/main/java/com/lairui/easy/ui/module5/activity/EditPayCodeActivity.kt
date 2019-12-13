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
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_editphone.*
import kotlinx.android.synthetic.main.activity_editphone.codeEt
import kotlinx.android.synthetic.main.activity_editphone.getCodeTv
import kotlinx.android.synthetic.main.activity_password.*
import kotlinx.android.synthetic.main.activity_password.phoneEt
import kotlinx.android.synthetic.main.activity_paycode.*

/**
 *修改支付密码
 */
class EditPayCodeActivity : BasicActivity(), RequestView {

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

    private var mRequestTag = ""

    private var mType = ""

    private lateinit var mTimeCount: TimeCount

    override val contentView: Int
        get() = R.layout.activity_paycode

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "修改提现密码"
        mTimeCount = TimeCount(1*60*1000,1000)
        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            val s = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""].toString()
            MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
        }
        phoneEt.setText(MbsConstans.USER_MAP!!["phone"].toString())

        codeEt.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mButNext.isEnabled = s.toString().isNotEmpty()
            }

        })
    }

    private fun getCodeAction() {

        mRequestTag = MethodUrl.MODIFY_PAYCODE_CODE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.MODIFY_PAYCODE_CODE
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@EditPayCodeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.MODIFY_PAYCODE_CODE, map)
    }


    private fun getMsgCodeAction() {
        if (TextUtils.isEmpty(payCodeEt.text) || TextUtils.isEmpty(payCodeAgainEt.text)){
            showToastMsg("请设置支付密码")
            mButNext.isEnabled = true
            return
        }

        mRequestTag = MethodUrl.MODIFY_PAYCODE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.MODIFY_PAYCODE
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@EditPayCodeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["password"] = payCodeEt.text.toString()
        map["confirm"] = payCodeAgainEt.text.toString()
        map["code"] = codeEt.text.toString()

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.MODIFY_PAYCODE, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.getCodeTv,R.id.but_next)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.getCodeTv ->{
                mTimeCount.start()
                getCodeAction()
            }
            R.id.but_next ->{
                mButNext.isEnabled = false
                getMsgCodeAction()
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
            MethodUrl.MODIFY_PAYCODE -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    finish()
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@EditPayCodeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


            MethodUrl.MODIFY_PAYCODE_CODE -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@EditPayCodeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when(mType){
            MethodUrl.MODIFY_PAYCODE-> mButNext.isEnabled = true
        }
    }
    // 倒计时内部类
    inner class TimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            getCodeTv.text = resources.getString(R.string.msg_code_again)
            getCodeTv.setTextColor(ContextCompat.getColor(this@EditPayCodeActivity,R.color.font_c))
            getCodeTv.isClickable = true
            MbsConstans.CURRENT_TIME = 0
        }

        override fun onTick(millisUntilFinished: Long) {
            //计时过程显示
            getCodeTv.isClickable = false
            getCodeTv.setTextColor(ContextCompat.getColor(this@EditPayCodeActivity,R.color.black99))
            getCodeTv.text = (millisUntilFinished / 1000 ).toString()+"秒后重发"
            MbsConstans.CURRENT_TIME = (millisUntilFinished / 1000).toInt()

        }

    }


}
