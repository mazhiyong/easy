package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat

import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
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
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity

/**
 * 录入企业信息  界面
 */
class QiyeInfoActivity : BasicActivity(), RequestView {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.qiye_name_edit)
    lateinit var mQiyeNameEdit: EditText
    @BindView(R.id.qiye_shxy_edit)
    lateinit var mQiyeShxyEdit: EditText
    @BindView(R.id.user_name_edit)
    lateinit var mUserNameEdit: EditText
    @BindView(R.id.idcard_edit)
    lateinit var mIdcardEdit: EditText
    @BindView(R.id.phone_edit)
    lateinit var mPhoneEdit: TextView
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    private var mOpType: String? = ""

    private var mRequestTag = ""

    private val mUrl = ""

    override val contentView: Int
        get() = R.layout.activity_qiye_info

    override fun init() {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mOpType = bundle.getString("type")
        }
        mTitleText!!.text = resources.getString(R.string.qiye_info)
        val mPhone = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, ""]!!.toString() + ""
        mPhoneEdit!!.text = mPhone

    }


    private fun submitAction() {

        val qiyeName = mQiyeNameEdit!!.text.toString() + ""
        val qiyeXyDaima = mQiyeShxyEdit!!.text.toString() + ""
        val name = mUserNameEdit!!.text.toString() + ""
        val num = mIdcardEdit!!.text.toString() + ""

        if (mOpType == "1") {
            if (UtilTools.isEmpty(mQiyeNameEdit!!, "企业名称")) {
                showToastMsg("企业名称不能为空")
                mButNext!!.isEnabled = true
                return
            }
            if (UtilTools.isEmpty(mQiyeShxyEdit!!, "统一社会信用代码")) {
                showToastMsg("统一社会信用代码不能为空")
                mButNext!!.isEnabled = true
                return
            }

        }
        if (UtilTools.isEmpty(mUserNameEdit!!, "姓名")) {
            showToastMsg("姓名不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (UtilTools.isEmpty(mIdcardEdit!!, "身份证号")) {
            showToastMsg("身份证号不能为空")
            mButNext!!.isEnabled = true
            return
        }


        mRequestTag = MethodUrl.companyCheck

        val map = HashMap<String, String>()

        map["firmname"] = qiyeName//企业名称
        map["yingyezz"] = qiyeXyDaima//社会统一信用代码证
        map["farnname"] = name//法人名称
        map["farnzjno"] = num//法人证件号码


        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.companyCheck, map)
    }

    //重写返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //点击完返回键，执行的动作
            finish()
        }
        return true
    }

    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> {
                finish()
                backTo(LoginActivity::class.java, false)
            }
            R.id.left_back_lay -> {
                finish()
                backTo(LoginActivity::class.java, false)
            }
            R.id.but_next -> {
                mButNext!!.isEnabled = false
                submitAction()
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
        mButNext!!.isEnabled = true
        val intent: Intent
        when (mType) {
            MethodUrl.companyCheck -> {
                mButNext!!.isEnabled = true
                intent = Intent(this@QiyeInfoActivity, QiyeInfoShowActivity::class.java)
                startActivity(intent)
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.companyCheck -> submitAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mButNext!!.isEnabled = true
        dealFailInfo(map, mType)
    }

}
