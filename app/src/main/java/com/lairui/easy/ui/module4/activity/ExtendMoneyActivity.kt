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
 * 追加保证金  界面
 */
class ExtendMoneyActivity : BasicActivity(), RequestView {

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
    private var ratio = 0.00

    override val contentView: Int
        get() = R.layout.activity_extend_money

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val bundel = intent.extras
        if (bundel == null){
            finish()
        }else{
            mark = bundel.getString("mark")
        }

        mTitleText.text = "扩大配资"

        moneyEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mButNext.isEnabled = s.toString().isNotEmpty()
                if (s.toString().isNotEmpty()){
                    lixiTv.text = UtilTools.numFormat(ratio*s.toString().toDouble(),2)
                }else{
                    lixiTv.text = "0.00"
                }

            }

        })


        getMsgCodeAction()
    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.CAPITAL_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CAPITAL_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@ExtendMoneyActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CAPITAL_INFO, map)
    }


    private fun sumbitAction() {
        mRequestTag = MethodUrl.CAPITAL_ACTION
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CAPITAL_ACTION
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@ExtendMoneyActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        map["number"] = moneyEt.text.toString()

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CAPITAL_ACTION, map)
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
            MethodUrl.CAPITAL_INFO -> when (tData["code"].toString()) {
                "1" -> {
                    if (tData["data"].toString().isNotEmpty()){
                        val mapData = tData["data"] as MutableMap<String,Any>
                        ratio = mapData["ratio"].toString().toDouble()
                        val textViewUtils = TextViewUtils()
                        val listTip =  JSONUtil.instance.jsonToListStr(mapData["tips"].toString())
                        var s = "温馨提示:"
                        if (listTip != null) {
                            for (item in listTip){
                                s =s+item+"\n"
                            }
                        }
                        mTipTv.text = s
                        textViewUtils.init(s, mTipTv)
                        textViewUtils.setTextColor(0, s.indexOf(":"), ContextCompat.getColor(this, R.color.font_c))

                        textViewUtils.build()


                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@ExtendMoneyActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.CAPITAL_ACTION-> when (tData["code"].toString()) {
                "1" -> {
                    mButNext.isEnabled = true
                    showToastMsg(tData["msg"].toString() + "")
                }
                "0" -> {
                    mButNext.isEnabled = true
                    showToastMsg(tData["msg"].toString() + "")
                }
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@ExtendMoneyActivity, LoginActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when(mType){
            MethodUrl.CAPITAL_ACTION-> mButNext.isEnabled = true
        }
    }
}
