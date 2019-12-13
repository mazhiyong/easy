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
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_password.*

/**
 *修改登录密码
 */
class EditPassWordActivity : BasicActivity(), RequestView {

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

    override val contentView: Int
        get() = R.layout.activity_password

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "修改登录密码"

        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            val s = SPUtils[this@EditPassWordActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""].toString()
            MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
        }
        phoneEt.setText(MbsConstans.USER_MAP!!["phone"].toString())

        oldPassWordEt.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()){
                    mButNext.isEnabled = true
                }
            }

        })
    }


    private fun getMsgCodeAction() {
        if (TextUtils.isEmpty(oldPassWordEt.text)){
            showToastMsg("请输入旧密码")
            mButNext.isEnabled = true
            return
        }

        if (TextUtils.isEmpty(newPassWordEt.text) || TextUtils.isEmpty(newPassWordEtAgain.text)){
            showToastMsg("请设置新密码")
            mButNext.isEnabled = true
            return
        }


        mRequestTag = MethodUrl.MODIFY_PASSWORD
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.MODIFY_PASSWORD
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@EditPassWordActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["original"] = oldPassWordEt.text.toString()
        map["password"] = newPassWordEt.text.toString()
        map["confirm"] = newPassWordEtAgain.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.MODIFY_PASSWORD, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.but_next)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
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
            MethodUrl.MODIFY_PASSWORD -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    closeAllActivity()
                    val intent = Intent(this@EditPassWordActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@EditPassWordActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.resetPassCode -> getMsgCodeAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mButNext.isEnabled = true
        dealFailInfo(map, mType)
    }
}
