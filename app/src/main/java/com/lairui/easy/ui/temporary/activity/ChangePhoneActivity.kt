package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

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

/**
 * 更换手机号  界面
 */
class ChangePhoneActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.new_phone_edit)
    lateinit var mNewPhoneEdit: EditText


    private var mAuthCode: String? = ""

    private var mRequestTag = ""
    private var mPhone = ""

    override val contentView: Int
        get() = R.layout.activity_change_phone

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mAuthCode = bundle.getString("authcode")
        }
        mTitleText!!.text = resources.getString(R.string.login_phone_num)
    }

    /**
     * 获取短信验证码
     */
    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.changePhoneMsgCode
        val map = HashMap<String, Any>()
        map["tel"] = mPhone
        map["authcode"] = mAuthCode!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.changePhoneMsgCode, map)
    }

    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.but_next -> {

                mPhone = mNewPhoneEdit!!.text.toString() + ""

                if (UtilTools.isEmpty(mNewPhoneEdit!!, "手机号码")) {
                    showToastMsg("手机号码不能为空")
                    return
                }
                if (!RegexUtil.isPhone(mPhone)) {
                    showToastMsg("手机号码格式不正确")
                    return
                }
                getMsgCodeAction()
            }
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        //{smstoken=sms_token@3948602038bb8ecf912e0ede4a577ebd, send_tel=151****3298}
        val intent: Intent
        when (mType) {
            MethodUrl.changePhoneMsgCode -> {
                showToastMsg("获取验证码成功")
                intent = Intent(this@ChangePhoneActivity, CodeMsgActivity::class.java)
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_NEW_PHONE_CHANGE)
                intent.putExtra("authcode", mAuthCode)
                intent.putExtra("phone", mPhone)
                intent.putExtra("showPhone", mPhone)
                startActivity(intent)
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.changePhoneMsgCode -> getMsgCodeAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
