package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.secret.RSAUtils
import com.lairui.easy.utils.tool.RegexUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity

/**
 * 重置登录密码  界面
 */
class ResetLoginPassButActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.but_sure)
    lateinit var mButNext: Button
    @BindView(R.id.new_pass_edit)
    lateinit var mNewPassEdit: EditText
    @BindView(R.id.new_pass_again_edit)
    lateinit var mNewPassAgainEdit: EditText
    @BindView(R.id.togglePwd1)
    lateinit var mTogglePwd1: ToggleButton
    @BindView(R.id.togglePwd2)
    lateinit var mTogglePwd2: ToggleButton


    private var mAuthCode = ""
    private var mPhone = ""
    private var mType = ""
    private var mKind = ""
    private var mInvcode = ""

    private var mRequestTag = ""


    override val contentView: Int
        get() = R.layout.activity_reset_login_pass_but

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mAuthCode = bundle.getString("authcode")!! + ""
            mPhone = bundle.getString("phone")!! + ""
            mType = bundle.getString("type")!! + ""
            mKind = bundle.getString("kind")!! + ""
            mInvcode = bundle.getString("invcode")!! + ""

        }
        //重置密码
        if (mType == "0") {
            mTitleText!!.text = resources.getString(R.string.reset_login_pass)
        } else { //设置密码
            mTitleText!!.text = resources.getString(R.string.set_login_pass)
        }


        mTogglePwd1!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //显示密码
                mNewPassEdit!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                //隐藏密码
                mNewPassEdit!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        mTogglePwd2!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //显示密码
                mNewPassAgainEdit!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                //隐藏密码
                mNewPassAgainEdit!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

    }


    private fun submitAction() {
        if (mType == "0") {
            mRequestTag = MethodUrl.resetPassSubmit
        } else {
            mRequestTag = MethodUrl.registerAction
        }
        val password = mNewPassEdit!!.text.toString() + ""
        val passwordAgain = mNewPassAgainEdit!!.text.toString() + ""


        if (UtilTools.isEmpty(mNewPassEdit!!, getString(R.string.pass_word))) {
            return
        }



        if (UtilTools.isEmpty(mNewPassAgainEdit!!, getString(R.string.pass_word))) {
            return
        }
        if (password != passwordAgain) {
            showToastMsg("两次输入密码不一样，请重新输入")
            return
        }

        val s = RegexUtil.isLetterDigit(password)
        when (s) {
            0 -> {
            }
            1 -> {
                showToastMsg(resources.getString(R.string.must_has_shuzi))
                return
            }
            2 -> {
                showToastMsg(resources.getString(R.string.must_has_zimu))
                return
            }
            3 -> {
                showToastMsg(resources.getString(R.string.set_new_pass_tip))
                return
            }
        }

        // String pass = AESHelper.encrypt(password, AESHelper.password);
        val pass = RSAUtils.encryptContent(password, RSAUtils.publicKey)
        val mHeaderMap = HashMap<String, String>()
        //注册
        if (mType == "1") {
            val map = HashMap<String, Any>()
            map["authcode"] = mAuthCode
            map["password"] = pass
            map["telephone"] = mPhone
            map["firmkind"] = mKind
            map["invcode"] = mInvcode
            mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.registerAction, map)
        } else { //重置密码
            val map = HashMap<String, String>()
            map["authcode"] = mAuthCode
            map["password"] = pass
            map["tel"] = mPhone
            mRequestPresenterImp!!.requestPutToRes(mHeaderMap, MethodUrl.resetPassSubmit, map)
        }

    }

    @OnClick(R.id.back_img, R.id.but_sure, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_sure -> submitAction()
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
            MethodUrl.resetPassSubmit -> {
                showToastMsg("重置密码成功")
                //backTo(LoginActivity.class, false);
                closeAllActivity()

                intent = Intent(this@ResetLoginPassButActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            MethodUrl.registerAction -> {
                showToastMsg("注册成功")
                closeAllActivity()

                intent = Intent(this@ResetLoginPassButActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.resetPassSubmit -> submitAction()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
