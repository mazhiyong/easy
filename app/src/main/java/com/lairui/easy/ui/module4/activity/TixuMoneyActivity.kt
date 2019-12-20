package com.lairui.easy.ui.module4.activity

import android.content.Intent
import android.text.Editable
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

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.*
import kotlinx.android.synthetic.main.activity_add_money.*
import kotlinx.android.synthetic.main.activity_add_money.moneyEt
import kotlinx.android.synthetic.main.activity_extend_money.*

/**
 * 提取收益  界面
 */
class TixuMoneyActivity : BasicActivity(), RequestView {

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


    private var mark = ""


    override val contentView: Int
        get() = R.layout.activity_tiqu_money

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val bundel = intent.extras
        if (bundel == null){
            finish()
        }else{
            mark = bundel.getString("mark")
        }

        mTitleText.text = "提取收益"
        mTipTv.text = "温馨提示:可提金额=策略方案对应交易账号的可取资金-配资方案的风险保证金"

        val textViewUtils = TextViewUtils()
        val s = mTipTv.text.toString()
        textViewUtils.init(s, mTipTv)
        textViewUtils.setTextColor(0, s.indexOf(":"), ContextCompat.getColor(this, R.color.font_c))

        textViewUtils.build()

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
        mRequestTag = MethodUrl.PROFIT_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PROFIT_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@TixuMoneyActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PROFIT_INFO, map)
    }

    private fun sumbitAction() {
        mRequestTag = MethodUrl.PROFIT_ACTION
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PROFIT_ACTION
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@TixuMoneyActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        map["number"] = moneyEt.text.toString()

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PROFIT_ACTION, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
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
        val intent: Intent
        when (mType) {
            MethodUrl.PROFIT_INFO -> when (tData["code"].toString()) {
                "1" -> {
                    if (tData["data"].toString().isNotEmpty()){
                        val mapData = tData["data"] as MutableMap<String,Any>
                        val textViewUtils = TextViewUtils()
                        var s = "温馨提示:"+mapData["tips"]
                        mTipTv.text = s
                        textViewUtils.init(s, mTipTv)
                        textViewUtils.setTextColor(0, s.indexOf(":"), ContextCompat.getColor(this, R.color.font_c))

                        textViewUtils.build()

                        lixiTv.text = UtilTools.getNormalMoney(mapData["profit"].toString())


                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@TixuMoneyActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.PROFIT_ACTION-> when (tData["code"].toString()) {
                "1" -> {
                    mButNext.isEnabled = true
                    showToastMsg(tData["msg"].toString() + "")
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                    sendBroadcast(intent)
                }
                "0" -> {
                    mButNext.isEnabled = true
                    showToastMsg(tData["msg"].toString() + "")
                }
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@TixuMoneyActivity, LoginActivity::class.java)
                    startActivity(intent)
                }

            }
        }
        }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when(mType){
            MethodUrl.PROFIT_ACTION-> mButNext.isEnabled = true
        }
    }
}



