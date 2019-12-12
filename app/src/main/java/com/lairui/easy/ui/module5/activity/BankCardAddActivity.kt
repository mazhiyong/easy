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
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module.activity.RegistActivity
import com.lairui.easy.ui.module5.adapter.BankCardListAdapter
import com.lairui.easy.utils.tool.*
import kotlinx.android.synthetic.main.activity_bankcard_add.*
import kotlinx.android.synthetic.main.activity_bankcard_add.but_next
import kotlinx.android.synthetic.main.activity_bankcard_add.getCodeTv
import kotlinx.android.synthetic.main.activity_regist.*
import kotlinx.android.synthetic.main.activity_renzheng.*

/**
 *添加 银行卡
 */
class BankCardAddActivity : BasicActivity(), RequestView ,SelectBackListener{

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
    private lateinit var mDialog: KindSelectDialog
    private lateinit var mark :String

    private var bankMap :MutableMap<String,Any>? = null



    override val contentView: Int
        get() = R.layout.activity_bankcard_add

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "添加银行卡"
        mTipTv.text = "温馨提示:\n"+"1.通过望山银行向平台对公账号转账，请务必选择实时到账选项；（不收任何手续费）\n"+"2.请确认转账成功后，再点击提交等待审核到账；（工作时间及时到账）"

        val  bundle = intent.extras
        if (bundle != null){
           mark = bundle.get("mark").toString()
           if(UtilTools.empty(mark)){
               mark = "0"
           }
        }else{
            finish()
        }


        mTimeCount = TimeCount(1*60*1000,1000)

        val textViewUtils = TextViewUtils()
        val s = mTipTv.text.toString()
        textViewUtils.init(s, mTipTv)
        textViewUtils.setTextColor(0, s.indexOf(":"), ContextCompat.getColor(this, R.color.font_c))

        textViewUtils.build()

        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            val s = SPUtils[this@BankCardAddActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""].toString()
            MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
        }
        phoneTv.text = MbsConstans.USER_MAP!!["phone"] as String


        codeEt.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()){
                    but_next.isEnabled = true
                }
            }

        })

        getMsgCodeAction()

    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.BANK_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.BANK_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BankCardAddActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.BANK_INFO, map)


    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay,R.id.selectBankLay,R.id.getCodeTv)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.selectBankLay -> mDialog.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.getCodeTv -> {
                mTimeCount.start()
                getCodeAction()
            }
            R.id.but_next -> {
                but_next.isEnabled = false
                addBankAction()
            }
        }
    }

    private fun getCodeAction() {
        mRequestTag = MethodUrl.BANK_CODE
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.BANK_CODE
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BankCardAddActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.BANK_CODE, map)

    }


    private fun addBankAction() {
        if (TextUtils.isEmpty(cardNameEt.text)){
            showToastMsg("请输入持卡人姓名信息")
            but_next.isEnabled = true
            return
        }
        if (TextUtils.isEmpty(cardChildBankEt.text)){
            showToastMsg("请输入开户支行信息")
            but_next.isEnabled = true
            return
        }
        if (TextUtils.isEmpty(cardNumberEt.text)){
            showToastMsg("请输入银行卡号信息")
            but_next.isEnabled = true
            return
        }
        if (TextUtils.isEmpty(codeEt.text)){
            showToastMsg("请输入手机验证码")
            but_next.isEnabled = true
            return
        }
        if (UtilTools.empty(bankMap)){
            showToastMsg("请选择开户行")
            but_next.isEnabled = true
            return
        }

        mRequestTag = MethodUrl.BINDBANK_ACTION
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.BINDBANK_ACTION
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BankCardAddActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] =  mark
        map["name"] = cardNameEt.text.toString()
        map["card"] = cardNumberEt.text.toString()
        map["bank_id"] = bankMap!!["id"].toString()
        map["address"] =cardChildBankEt.text.toString()
        map["code"] = codeEt.text.toString()

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.BINDBANK_ACTION, map)
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
            MethodUrl.BANK_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (!UtilTools.empty(tData["data"])) {
                        val mapData = tData["data"] as MutableMap<String, Any>

                        if (!UtilTools.empty(mapData) && !UtilTools.empty(mapData["bank"])) {

                            val bankList = mapData["bank"] as MutableList<MutableMap<String, Any>>
                            if (!UtilTools.empty(bankList) && bankList.size > 0) {
                                for (item in bankList) {
                                    item["name"] = item["bank"] as String
                                }
                                mDialog = KindSelectDialog(this@BankCardAddActivity, true, bankList, 20)
                                mDialog.setClickListener(this@BankCardAddActivity)
                            }
                            if (!UtilTools.empty(mapData["account"])){
                                cardNameEt.setText(mapData["account"].toString())
                            }
                            if (!UtilTools.empty(mapData["name"])){
                                selectBankTv.setText(mapData["name"].toString())
                            }
                            if (!UtilTools.empty(mapData["card"])){
                                cardNumberEt.setText(mapData["card"].toString())
                            }
                            if (!UtilTools.empty(mapData["address"])){
                                cardChildBankEt.setText(mapData["address"].toString())
                            }


                        }
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BankCardAddActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.BANK_CODE -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BankCardAddActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.BINDBANK_ACTION -> {
                but_next.isEnabled = true
                when (tData["code"].toString() + "") {
                    "1" -> {
                        showToastMsg(tData["msg"].toString() + "")
                        //backTo(BankCardListActivity::class.java,true)
                        finish()
                    }
                    "0" -> showToastMsg(tData["msg"].toString() + "")
                    "-1" -> {
                        closeAllActivity()
                        val intent = Intent(this@BankCardAddActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

        override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
            dealFailInfo(map, mType)
            when (mType) {
                MethodUrl.BINDBANK_ACTION -> {
                    but_next.isEnabled = true
                }
            }
        }


        override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
            when (type) {
                20 -> {
                    bankMap = map
                    selectBankTv.text = map["name"] as String
                    //but_next.isEnabled = true
                }

            }
        }

        // 倒计时内部类
        inner class TimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

            override fun onFinish() {
                getCodeTv.text = resources.getString(R.string.msg_code_again)
                getCodeTv.setTextColor(ContextCompat.getColor(this@BankCardAddActivity, R.color.font_c))
                getCodeTv.isClickable = true
                MbsConstans.CURRENT_TIME = 0
            }

            override fun onTick(millisUntilFinished: Long) {
                //计时过程显示
                getCodeTv.isClickable = false
                getCodeTv.setTextColor(ContextCompat.getColor(this@BankCardAddActivity, R.color.black99))
                getCodeTv.text = (millisUntilFinished / 1000).toString() + "秒后重发"
                MbsConstans.CURRENT_TIME = (millisUntilFinished / 1000).toInt()

            }

        }
    }

