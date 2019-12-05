package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
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
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 企业打款填写金额认证  界面
 */
class QiyeDakuanCheckActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.money_edit)
    lateinit var mMoneyEdit: EditText
    @BindView(R.id.qiye_modify_tv)
    lateinit var mQiyeModifyTv: TextView
    @BindView(R.id.qiye_dakuan_top_tv)
    lateinit var mQiyeTopTipTv: TextView
    @BindView(R.id.qiye_time_tv)
    lateinit var mQiyeTimeTv: TextView

    private val mPhone = ""

    private var mRequestTag = ""

    private var mLiushui: String? = ""

    override val contentView: Int
        get() = R.layout.activity_qiye_dakuan_check


    override fun onNewIntent(intent: Intent?) {
        if (intent != null) {
            val bundle = intent.extras
            if (bundle != null) {
                mLiushui = bundle.getString("remitid")

                LogUtil.i("-----------------打款认证流水号", mLiushui!!)
            }
        }
        super.onNewIntent(intent)
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.qiye_dakuan_check)
        UtilTools.setMoneyEdit(mMoneyEdit!!, 0.0)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mLiushui = bundle.getString("remitid")
        }


        val s = getString(R.string.qiye_modify_zhanghu)
        if (s.contains("?")) {
            var i = s.lastIndexOf("?")
            i = i + 1
            val ss = SpannableString(s)
            ss.setSpan(TextSpanClick(false), i, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.grey)), 0, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue1)), i, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            mQiyeModifyTv!!.text = ss
            //添加点击事件时，必须设置
            mQiyeModifyTv!!.movementMethod = LinkMovementMethod.getInstance()
        } else {
            mQiyeModifyTv!!.text = s
        }
        val topXml = resources.getString(R.string.qiye_dakuan_tip)
        val topStr = String.format(topXml, "认证的企业")
        mQiyeTopTipTv!!.text = topStr

        val qiyeTimeXml = resources.getString(R.string.qiye_money_time)
        val timeStr = String.format(qiyeTimeXml, "2019-01-01 18:00:00")
        mQiyeTimeTv!!.text = timeStr
    }

    private inner class TextSpanClick(private val status: Boolean) : ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false//取消下划线false
        }

        override fun onClick(v: View) {
            val intent = Intent(this@QiyeDakuanCheckActivity, QiyeCardInfoActivity::class.java)
            startActivity(intent)

        }
    }


    private fun moneyCheck() {//{"remitid":"1910700000291882"}
        val money = mMoneyEdit!!.text.toString() + ""
        if (UtilTools.isEmpty(mMoneyEdit!!, "收款金额")) {
            showToastMsg("收款金额不能为空")
            return
        }
        mRequestTag = MethodUrl.companyPayVerify
        val map = HashMap<String, String>()
        map["remitid"] = mLiushui!!//打款申请流水
        map["amount"] = money//打款金额
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.companyPayVerify, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                mButNext!!.isEnabled = false
                moneyCheck()
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
            MethodUrl.companyPayVerify -> {
                mButNext!!.isEnabled = true
                intent = Intent(this@QiyeDakuanCheckActivity, IdCardSuccessActivity::class.java)
                intent.putExtra(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_AUTH)
                startActivity(intent)
                finish()
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.companyPayVerify -> moneyCheck()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.companyPayVerify -> mButNext!!.isEnabled = true
        }
        dealFailInfo(map, mType)
    }
}
