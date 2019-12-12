package com.lairui.easy.ui.module5.activity

import android.content.Intent
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod

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
import com.lairui.easy.mywidget.dialog.AppDialog
import com.lairui.easy.mywidget.dialog.SimpleTipMsgDialog
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.TextViewUtils
import kotlinx.android.synthetic.main.activity_renzheng.*

/**
 * 认证界面
 */
class RenZhengActivity : BasicActivity(), RequestView {

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

    override val contentView: Int
        get() = R.layout.activity_renzheng

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "实名认证"
        getMsgCodeAction()

        nameEt.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty() && !TextUtils.isEmpty(idCardEt.text)){
                    but_next.isEnabled = true
                }
            }

        })

        idCardEt.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty() && !TextUtils.isEmpty(nameEt.text)){
                    but_next.isEnabled = true
                }
            }

        })

    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.CERTIFIED_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CERTIFIED_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@RenZhengActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CERTIFIED_INFO, map)
    }

    private fun renzhengAction() {

        mRequestTag = MethodUrl.CERTIFIED_ACTION
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CERTIFIED_ACTION
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@RenZhengActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["name"] = nameEt.text.toString()
        map["card"] = idCardEt.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CERTIFIED_ACTION, map)
    }




    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
               /* val dialog = SimpleTipMsgDialog(this@RenZhengActivity,true)
                dialog.initValue("您未通过实名认证","立即认证")
                dialog.setClickListener(View.OnClickListener { v ->
                    when (v.id) {
                        R.id.cancelIv -> dialog.dismiss()
                        R.id.dealTv -> {
                        }
                    }
                })

                dialog.show()*/
                but_next.isEnabled = false
                renzhengAction()
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
            MethodUrl.CERTIFIED_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    val mapData = tData["data"] as MutableMap<String,Any>
                    if (!UtilTools.empty(mapData)) {
                        when(mapData["certified"].toString()+""){
                            "0"->{ //未提交
                                nameEt.isEnabled = true
                                idCardEt.isEnabled = true
                                but_next.text = "提交"
                                but_next.isEnabled = false
                            }
                            "1"->{ //待审核
                                nameEt.isEnabled = false
                                idCardEt.isEnabled = false
                                nameEt.setText(mapData["name"] as String)
                                idCardEt.setText(mapData["card"] as String)
                                but_next.text = "待审核"
                                but_next.isEnabled = false
                            }
                            "2"->{ //已认证
                                nameEt.isEnabled = false
                                idCardEt.isEnabled = false
                                nameEt.setText(mapData["name"] as String)
                                idCardEt.setText(mapData["card"] as String)
                                but_next.text = "已认证"
                                but_next.isEnabled = false
                            }


                        }

                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@RenZhengActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.CERTIFIED_ACTION -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    getMsgCodeAction()
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@RenZhengActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when(mType){
            MethodUrl.CERTIFIED_ACTION -> but_next.isEnabled = true
        }

    }
}
