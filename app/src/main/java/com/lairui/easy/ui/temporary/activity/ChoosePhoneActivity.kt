package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.ParseTextUtil
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity

/**
 * 选择手机号信息   界面
 */
class ChoosePhoneActivity : BasicActivity(), RequestView {


    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.my_image)
    lateinit var mMyImage: ImageView
    @BindView(R.id.choose_phone_tips)
    lateinit var mChoosePhoneTips: TextView
    @BindView(R.id.but_new_phone)
    lateinit var mButNewPhone: Button
    @BindView(R.id.user_old_phone_tv)
    lateinit var mUserOldPhoneTv: TextView
    @BindView(R.id.personal_scrollView)
    lateinit var mPersonalScrollView: ScrollView

    private var mRequestTag = ""

    private lateinit var mPhoneMap: MutableMap<String, Any>

    private lateinit var mParseTextUtil: ParseTextUtil


    override val contentView: Int
        get() = R.layout.activity_choose_phone

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mParseTextUtil = ParseTextUtil(this)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
        }

        val s = "如果启用您的新注册账号：系统将自动注销您原登录账号："
        val spannableString = mParseTextUtil!!.parseValueColorNum(s)
        mChoosePhoneTips!!.text = spannableString
        mTitleText!!.text = resources.getString(R.string.activation_phone)
        getPhonesInfo()
    }

    /**
     * 查询新老手机号信息
     */
    private fun getPhonesInfo() {

        mRequestTag = MethodUrl.userTelephones
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.userTelephones, map)
    }

    /**
     * 设置新的手机号  登录
     */
    private fun submitNewPhone() {
        if (mPhoneMap == null) {
            showToastMsg("获取用户手机号信息失败，请退出重试")
            return
        }

        mRequestTag = MethodUrl.setNewTel
        val map = HashMap<String, Any>()
        map["new_tel"] = mPhoneMap!!["new_tel"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.setNewTel, map)
    }

    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.but_new_phone, R.id.user_old_phone_tv)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.but_new_phone -> submitNewPhone()
            R.id.user_old_phone_tv -> {
                showToastMsg("请使用老账号重新登录")
                closeAllActivity()
                intent = Intent(this@ChoosePhoneActivity, LoginActivity::class.java)
                startActivity(intent)
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
        val intent: Intent
        when (mType) {
            MethodUrl.userTelephones -> {
                mPhoneMap = tData
                val s = "如果启用您的新注册账号：" + mPhoneMap!!["new_tel"] + "，系统将自动注销您原登录账号：" + mPhoneMap!!["old_tel"]
                val spannableString = mParseTextUtil!!.parseValueColorNum(s)
                mChoosePhoneTips!!.text = spannableString
            }
            MethodUrl.setNewTel -> {
                showToastMsg("激活成功，请重新登录")
                closeAllActivity()
                intent = Intent(this@ChoosePhoneActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.userTelephones -> getPhonesInfo()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
