package com.lairui.easy.ui.module.activity

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
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 修改登录密码  界面
 */
class ModifyLoginPassActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.old_pass_edit)
    lateinit var mOldPassEdit: EditText
    @BindView(R.id.new_pass_edit)
    lateinit var mNewPassEdit: EditText
    @BindView(R.id.sure_again_pass_edit)
    lateinit var mSureAgainPassEdit: EditText
    @BindView(R.id.togglePwd1)
    lateinit var mTogglePwd1: ToggleButton
    @BindView(R.id.togglePwd2)
    lateinit var mTogglePwd2: ToggleButton
    @BindView(R.id.togglePwd3)
    lateinit var mTogglePwd3: ToggleButton
    @BindView(R.id.but_next)
    lateinit var mButNext: Button

    private var mPhone = ""

    private var mRequestTag = ""

    override val contentView: Int
        get() = R.layout.activity_modify_login_pass

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.modify_login_pass)

        mPhone = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, ""]!!.toString() + ""


        mTogglePwd1!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //显示密码
                mOldPassEdit!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                //隐藏密码
                mOldPassEdit!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        mTogglePwd2!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //显示密码
                mNewPassEdit!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                //隐藏密码
                mNewPassEdit!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        mTogglePwd3!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //显示密码
                mSureAgainPassEdit!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                //隐藏密码
                mSureAgainPassEdit!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }


    private fun submitData() {

        val oldPass = mOldPassEdit!!.text.toString() + ""
        val password = mNewPassEdit!!.text.toString() + ""
        val passwordAgain = mSureAgainPassEdit!!.text.toString() + ""

        if (UtilTools.isEmpty(mOldPassEdit!!, getString(R.string.old_pass))) {
            return
        }

        val old = RegexUtil.isLetterDigit(oldPass)
        when (old) {
            0 -> {
            }
            1 -> {
                showToastMsg("原密码" + resources.getString(R.string.must_has_shuzi))
                return
            }
            2 -> {
                showToastMsg("原密码" + resources.getString(R.string.must_has_zimu))
                return
            }
            3 -> {
                showToastMsg(resources.getString(R.string.set_new_pass_tip))
                return
            }
        }


        if (UtilTools.isEmpty(mNewPassEdit!!, getString(R.string.pass_word))) {
            return
        }
        if (UtilTools.isEmpty(mSureAgainPassEdit!!, getString(R.string.pass_word))) {
            return
        }
        if (password != passwordAgain) {
            showToastMsg("两次输入新密码不一致，请重新输入")
            return
        }

        if (oldPass == password) {
            showToastMsg("新密码不能与原密码相同，请重新输入")
            return
        }

        val s = RegexUtil.isLetterDigit(password)
        when (s) {
            0 -> {
            }
            1 -> {
                showToastMsg("新密码" + resources.getString(R.string.must_has_shuzi))
                return
            }
            2 -> {
                showToastMsg("新密码" + resources.getString(R.string.must_has_zimu))
                return
            }
            3 -> {
                showToastMsg(resources.getString(R.string.set_new_pass_tip))
                return
            }
        }
        val jiamiOld = RSAUtils.encryptContent(oldPass, RSAUtils.publicKey)
        val jiamiNew = RSAUtils.encryptContent(password, RSAUtils.publicKey)


        mRequestTag = MethodUrl.modifyLoginPass
        val map = HashMap<String, String>()
        map["oldpass"] = jiamiOld
        map["newpass"] = jiamiNew
        map["tel"] = mPhone
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPutToRes(mHeaderMap, MethodUrl.modifyLoginPass, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> submitData()
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
            MethodUrl.modifyLoginPass -> {
                showToastMsg("修改密码成功")
                closeAllActivity()

                intent = Intent(this@ModifyLoginPassActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.modifyLoginPass -> submitData()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
