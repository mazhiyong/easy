package com.lairui.easy.ui.module5.activity

import android.content.Intent
import android.content.IntentFilter

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
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_account.*

/**
 *账户设置
 */
class AccountActivity : BasicActivity(), RequestView {

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
        get() = R.layout.activity_account

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "账户设置"

    }

    override fun onResume() {
        super.onResume()
        getMsgCodeAction()
    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.SETTING_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.SETTING_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@AccountActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.SETTING_INFO, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.phoneLay,R.id.passwordLay,R.id.paycodeLay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.phoneLay -> {
                val intent = Intent(this@AccountActivity,EditPhobeActivity::class.java)
                startActivity(intent)
            }
            R.id.passwordLay -> {
                val intent = Intent(this@AccountActivity,EditPassWordActivity::class.java)
                startActivity(intent)
            }
            R.id.paycodeLay -> {
                val intent = Intent(this@AccountActivity,EditPayCodeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun showProgress() {
        //showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.SETTING_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (!UtilTools.empty(tData["data"])){
                        val mapData = tData["data"] as MutableMap<String,Any>
                        phoneTv.text = mapData["phone"].toString()
                        if (mapData["is_safety"].toString() == "0"){ //未设置
                            paySetTv.text = "未设置"
                            paySetTv.setTextColor(ContextCompat.getColor(this@AccountActivity,R.color.font_c))
                        }else{//已设置
                            paySetTv.text = "已设置"
                            paySetTv.setTextColor(ContextCompat.getColor(this@AccountActivity,R.color.black99))
                        }
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@AccountActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
