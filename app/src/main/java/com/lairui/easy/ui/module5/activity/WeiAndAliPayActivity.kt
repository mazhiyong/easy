package com.lairui.easy.ui.module5.activity

import android.content.Intent

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
import com.lairui.easy.utils.tool.TextViewUtils

/**
 * 微信支付宝  充值界面
 */
class WeiAndAliPayActivity : BasicActivity(), RequestView {

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
        get() = R.layout.activity_weiandali_money

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "微信"
        mTipTv.text = "温馨提示:\n"+"1.转账金额最好有些零头（1000.18），方便系统快速确认是您汇款。\n"+"2.请确认转账成功后，再点击提交等待审核到账；（工作时间及时到账）"

        val textViewUtils = TextViewUtils()
        val s = mTipTv.text.toString()
        textViewUtils.init(s, mTipTv)
        textViewUtils.setTextColor(0, s.indexOf(":"), ContextCompat.getColor(this, R.color.font_c))

        textViewUtils.build()

    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.resetPassCode
        val map = HashMap<String, Any>()
        map["tel"] = mPhone
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.resetPassCode, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {

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
            MethodUrl.resetPassCode -> {
               /* showToastMsg("获取验证码成功")
                intent = Intent(this@AddMoneyActivity, CodeMsgActivity::class.java)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_RESET_LOGIN_PASS)
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra("phonenum", mPhone + "")
                intent.putExtra("showPhone", UtilTools.getPhoneXing(mPhone))
                startActivity(intent)*/
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
        dealFailInfo(map, mType)
    }
}
