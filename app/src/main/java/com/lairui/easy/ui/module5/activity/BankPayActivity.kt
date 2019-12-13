package com.lairui.easy.ui.module5.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
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
import com.lairui.easy.utils.tool.RegexUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.TextViewUtils
import kotlinx.android.synthetic.main.activity_bank_money.*
import kotlinx.android.synthetic.main.activity_yaoqing_money.*

/**
 * 银行卡 充值界面
 */
class BankPayActivity : BasicActivity(), RequestView {

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

    private var mClipboardManager: ClipboardManager? = null
    private var clipData: ClipData? = null

    override val contentView: Int
        get() = R.layout.activity_bank_money

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "银行卡"
        mTipTv.text = "温馨提示:\n"+"1.通过望山银行向平台对公账号转账，请务必选择实时到账选项；（不收任何手续费）\n"+"2.请确认转账成功后，再点击提交等待审核到账；（工作时间及时到账）"

        val textViewUtils = TextViewUtils()
        val s = mTipTv.text.toString()
        textViewUtils.init(s, mTipTv)
        textViewUtils.setTextColor(0, s.indexOf(":"), ContextCompat.getColor(this, R.color.font_c))

        textViewUtils.build()

        mClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager



        moneyEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mButNext.isEnabled = s.toString().isNotEmpty()
            }

        })

        getMsgCodeAction()

    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.CHONGZHI_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CHONGZHI_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BankPayActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHONGZHI_INFO, map)
    }

    private fun chongZhiAction() {
        if (TextUtils.isEmpty(nameEt.text)){
            showToastMsg("请输入付款人姓名")
            mButNext.isEnabled = true
            return
        }

        if (TextUtils.isEmpty(numbeEt.text)){
            showToastMsg("请输入流水号")
            mButNext.isEnabled = true
            return
        }

        mRequestTag = MethodUrl.CHONGZHI_ACTION
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CHONGZHI_ACTION
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BankPayActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["type"] = "3"
        map["number"] = moneyEt.text.toString()
        map["name"] = nameEt.text.toString()
        map["serial"] = numbeEt.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHONGZHI_ACTION, map)

    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.copyBankAddrress,R.id.copyBankName,R.id.copyBankNumber,R.id.copyBankPeople,R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.copyBankAddrress  -> {
                clipData = ClipData.newPlainText("", bankAddressEt.text.toString())
                mClipboardManager!!.primaryClip = clipData
                showToastMsg("复制成功")
            }
            R.id.copyBankName   -> {
                clipData = ClipData.newPlainText("", bankNameEt.text.toString())
                mClipboardManager!!.primaryClip = clipData
                showToastMsg("复制成功")
            }
            R.id.copyBankNumber   -> {
                clipData = ClipData.newPlainText("", bankNumberEt.text.toString())
                mClipboardManager!!.primaryClip = clipData
                showToastMsg("复制成功")
            }
            R.id.copyBankPeople  -> {
                clipData = ClipData.newPlainText("", bankPeopleEt.text.toString())
                mClipboardManager!!.primaryClip = clipData
                showToastMsg("复制成功")
            }
            R.id.but_next -> {
                mButNext.isEnabled = false
                chongZhiAction()
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
            MethodUrl.CHONGZHI_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (!UtilTools.empty(tData["data"])){
                        val mapData = tData["data"] as MutableMap<String,Any>
                        val mapBank = mapData["bank"] as MutableMap<String,Any>
                        bankPeopleEt.setText(mapBank["name"].toString())
                        bankNameEt.setText(mapBank["bank"].toString())
                        bankNumberEt.setText(mapBank["card"].toString())
                        bankAddressEt.setText(mapBank["address"].toString())
                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BankPayActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.CHONGZHI_ACTION -> when (tData["code"].toString() + "") {
                MethodUrl.CHONGZHI_ACTION -> mButNext.isEnabled = true
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BankPayActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when(mType){
            MethodUrl.CHONGZHI_ACTION -> mButNext.isEnabled = true
        }
    }
}
