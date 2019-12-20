package com.lairui.easy.ui.module5.activity

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
import kotlinx.android.synthetic.main.activity_weiandali_money.*

/**
 * 微信支付宝  充值界面
 */
class WeiAndAliPayActivity : BasicActivity(), RequestView {

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
    private var mType = ""

    private var mRequestTag = ""

    override val contentView: Int
        get() = R.layout.activity_weiandali_money

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val bundle = intent.extras
        if (bundle != null){
            mType = bundle["TYPE"].toString()
            if (mType == "1"){
                mTitleText.text = "微信"
                typeTv.text = "微信扫码充值"
            }else{
                mTitleText.text = "支付宝"
                typeTv.text = "支付宝扫码充值"
            }
        }else{
            finish()
        }

        mTipTv.text = "温馨提示:\n"+"1.转账金额最好有些零头（1000.18），方便系统快速确认是您汇款。\n"+"2.请确认转账成功后，再点击提交等待审核到账；（工作时间及时到账）"

        val textViewUtils = TextViewUtils()
        val s = mTipTv.text.toString()
        textViewUtils.init(s, mTipTv)
        textViewUtils.setTextColor(0, s.indexOf(":"), ContextCompat.getColor(this, R.color.font_c))

        textViewUtils.build()
        moneyEt.addTextChangedListener(object :TextWatcher{
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
            MbsConstans.ACCESS_TOKEN = SPUtils[this@WeiAndAliPayActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
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
            MbsConstans.ACCESS_TOKEN = SPUtils[this@WeiAndAliPayActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["type"] = mType
        map["number"] = moneyEt.text.toString()
        map["name"] = nameEt.text.toString()
        map["serial"] = numbeEt.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHONGZHI_ACTION, map)

    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
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
                      if(mType == "1"){
                       GlideUtils.loadImage(this@WeiAndAliPayActivity,mapData["we_chat_pay"].toString(),erWeiCodeIv)
                      }else{
                          GlideUtils.loadImage(this@WeiAndAliPayActivity,mapData["ali_pay"].toString(),erWeiCodeIv)
                      }
                  }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@WeiAndAliPayActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.CHONGZHI_ACTION -> when (tData["code"].toString() + "") {
                MethodUrl.CHONGZHI_ACTION -> mButNext.isEnabled = true
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                    sendBroadcast(intent)
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@WeiAndAliPayActivity, LoginActivity::class.java)
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
