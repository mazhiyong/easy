package com.lairui.easy.ui.module5.activity

import android.content.Intent
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity

import androidx.core.content.ContextCompat
import android.view.View
import android.widget.*

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mywidget.dialog.AppDialog
import com.lairui.easy.mywidget.dialog.BankCardSelectDialog
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.mywidget.dialog.SimpleTipMsgDialog
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.tool.*
import kotlinx.android.synthetic.main.activity_editphone.getCodeTv
import kotlinx.android.synthetic.main.activity_tixian_money.*
import java.util.ArrayList

/**
 * 提现界面
 */
class TixianActivity : BasicActivity(), RequestView , SelectBackListener {

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

    private lateinit var mTimeCount: TimeCount

    private lateinit var mDialog: BankCardSelectDialog

    private lateinit var  bankMap :MutableMap<String,Any>


    override val contentView: Int
        get() = R.layout.activity_tixian_money

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "提现"
        mTimeCount = TimeCount(1*60*1000,1000)

        codeEt.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               mButNext.isEnabled = s.toString().isNotEmpty()
            }

        })
    }


    override fun onResume() {
        super.onResume()
        getMsgCodeAction()
    }

    private fun getMsgCodeAction() {
        mRequestTag = MethodUrl.TIXIN_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.TIXIN_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@TixianActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TIXIN_INFO, map)
    }

    private fun getCodeAction() {

        mRequestTag = MethodUrl.TIXIN_CODE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.TIXIN_CODE
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@TixianActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TIXIN_CODE, map)
    }

    private fun sumbitAction() {
        if (TextUtils.isEmpty(moneyEt.text)){
            showToastMsg("请输入提现金额")
            mButNext.isEnabled = true
            return
        }

        if (TextUtils.isEmpty(payCodeEt.text)){
            showToastMsg("请输入提现密码")
            mButNext.isEnabled = true
            return
        }


        mRequestTag = MethodUrl.TIXIN_ACTION
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.TIXIN_ACTION
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@TixianActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["number"] = moneyEt.text.toString()
        map["password"] = payCodeEt.text.toString()
        map["code"] = codeEt.text.toString()
        map["bank_id"] = bankMap["mark"].toString()

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TIXIN_ACTION, map)
    }

    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay,R.id.getCodeTv,R.id.bankLay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.getCodeTv -> {
                mTimeCount.start()
                getCodeAction()
            }
            R.id.bankLay ->{
                mDialog.showAtLocation(Gravity.BOTTOM, 0, 0)
            }
            R.id.but_next -> {
                mButNext.isEnabled = false
                sumbitAction()
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
        var intent: Intent
        when (mType) {
            MethodUrl.TIXIN_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (!UtilTools.empty(tData["data"])){
                        val mapData = tData["data"] as MutableMap<String,Any>
                        if (mapData["certified"].toString() == "0"){
                            val dialog = SimpleTipMsgDialog(this@TixianActivity,true)
                            dialog.initValue("您尚未通过实名认证","立即认证")
                            dialog.setClickListener(View.OnClickListener { v ->
                                when (v.id) {
                                    R.id.cancelIv -> dialog.dismiss()
                                    R.id.dealTv -> {
                                        dialog.dismiss()
                                        intent = Intent(this@TixianActivity,RenZhengActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            })
                            dialog.show()
                        }else if (UtilTools.empty(mapData["bank"].toString())){
                            val dialog = SimpleTipMsgDialog(this@TixianActivity,true)
                            dialog.initValue("您尚未绑定银行卡","立即绑定")
                            dialog.setClickListener(View.OnClickListener { v ->
                                when (v.id) {
                                    R.id.cancelIv -> dialog.dismiss()
                                    R.id.dealTv -> {
                                        intent = Intent(this@TixianActivity,BankCardAddActivity::class.java)
                                        intent.putExtra("mark", "0")
                                        startActivity(intent)
                                        dialog.dismiss()
                                    }
                                }
                            })
                            dialog.show()
                        }else{
                            val bankList = mapData["bank"]  as MutableList<MutableMap<String, Any>>
                            if (bankList.size>0 ){
                                for (item in bankList){
                                    item["name"] = item["card"].toString()
                                }
                                bankMap = bankList[0]
                                bankNumberTv.text = bankList[0]["name"] as String
                                mDialog = BankCardSelectDialog(this@TixianActivity, true, bankList, 20)
                                mDialog.selectBackListener = this@TixianActivity
                            }else{
                                val dialog = SimpleTipMsgDialog(this@TixianActivity,true)
                                dialog.initValue("您尚未绑定银行卡","立即绑定")
                                dialog.setClickListener(View.OnClickListener { v ->
                                    when (v.id) {
                                        R.id.cancelIv -> dialog.dismiss()
                                        R.id.dealTv -> {
                                            intent = Intent(this@TixianActivity,BankCardAddActivity::class.java)
                                            intent.putExtra("mark", "0")
                                            startActivity(intent)
                                            dialog.dismiss()
                                        }
                                    }
                                })
                                dialog.show()
                            }


                        }

                        if (mapData["password"].toString() == "0"){
                            val dialog = SimpleTipMsgDialog(this@TixianActivity,true)
                            dialog.initValue("您尚未设置提现密码","立即设置")
                            dialog.setClickListener(View.OnClickListener { v ->
                                when (v.id) {
                                    R.id.cancelIv -> dialog.dismiss()
                                    R.id.dealTv -> {
                                        intent = Intent(this@TixianActivity,EditPayCodeActivity::class.java)
                                        startActivity(intent)
                                        dialog.dismiss()
                                    }
                                }
                            })
                            dialog.show()
                        }

                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@TixianActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.TIXIN_CODE-> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@TixianActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.TIXIN_ACTION-> when (tData["code"].toString() + "") {
                "1" -> {
                    mButNext.isEnabled = true
                    showToastMsg(tData["msg"].toString() + "")
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                    sendBroadcast(intent)

                    finish()
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@TixianActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when(mType){
            MethodUrl.TIXIN_ACTION -> mButNext.isEnabled = true
        }
    }


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            20 -> {
                bankMap = map
                LogUtil.i("show","mark:"+bankMap["mark"].toString())
                bankNumberTv.text = map["name"] as String
                //but_next.isEnabled = true
            }

        }
    }


    // 倒计时内部类
    inner class TimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            getCodeTv.text = resources.getString(R.string.msg_code_again)
            getCodeTv.setTextColor(ContextCompat.getColor(this@TixianActivity,R.color.font_c))
            getCodeTv.isClickable = true
            MbsConstans.CURRENT_TIME = 0
        }

        override fun onTick(millisUntilFinished: Long) {
            //计时过程显示
            getCodeTv.isClickable = false
            getCodeTv.setTextColor(ContextCompat.getColor(this@TixianActivity,R.color.black99))
            getCodeTv.text = (millisUntilFinished / 1000 ).toString()+"秒后重发"
            MbsConstans.CURRENT_TIME = (millisUntilFinished / 1000).toInt()

        }

    }
}
