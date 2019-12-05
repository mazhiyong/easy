package com.lairui.easy.ui.temporary.activity

import android.content.Intent

import androidx.core.content.ContextCompat

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mywidget.dialog.TipMsgDialog
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 企业证书转账界面
 */
class QiyeCaZhuanZhangActivity : BasicActivity(), RequestView {


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
    @BindView(R.id.qiye_receive_num_tv)
    lateinit var mQiyeReceiveNumTv: TextView
    @BindView(R.id.qiye_receive_name_tv)
    lateinit var mQiyeReceiveNameTv: TextView
    @BindView(R.id.qiye_receive_bank_tv)
    lateinit var mQiyeReceiveBankTv: TextView
    @BindView(R.id.qiye_memo_tv)
    lateinit var mQiyeMemoTv: TextView
    @BindView(R.id.qiye_pay_name_tv)
    lateinit var mQiyePayNameTv: TextView
    @BindView(R.id.qiye_pay_num_tv)
    lateinit var mQiyePayNumTv: TextView
    @BindView(R.id.btn_submit)
    lateinit var mBtnSubmit: Button
    private var mRequestTag = ""

    private lateinit var mPayInfo: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_qiye_ca_zhuanzhang
    private lateinit var mZDialog: TipMsgDialog

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.qiye_ca_money)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mPayInfo = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }


        initValueTv()
    }

    /**
     * 检测是否已支付money  匹配来账信息
     */
    private fun checkCa() {
        mRequestTag = MethodUrl.checkCa
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.checkCa, map)
    }

    /**
     * 获取证书信息
     */
    private fun caInfo() {
        mRequestTag = MethodUrl.caConfig
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.caConfig, map)
    }

    override fun showProgress() {

    }

    override fun disimissProgress() {

    }


    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.checkCa -> {
                val s = tData["matchingsta"]!!.toString() + ""
                if (s == "1") {//0未匹配，1已匹配

                    val intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.CAPAY_SUC
                    sendBroadcast(intent)

                    val tip = "已支付费用，前往申请证书"
                    val dian: Int
                    val end = tip.length
                    if (tip.contains("，")) {
                        dian = tip.indexOf("，")
                    } else {
                        dian = tip.length
                    }
                    val ss = SpannableString(tip)
                    ss.setSpan(TextSpanClick(false), dian, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.data_col)), 0, dian, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue1)), dian, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    //
                    showMsgDialog(ss, true)
                } else {
                    showMsgDialog("未查询到支付信息，请稍后重试", false)
                }
                mBtnSubmit!!.isEnabled = true
            }
            MethodUrl.balanceAccount -> {
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.checkCa -> checkCa()
                    MethodUrl.balanceAccount -> {
                    }
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mBtnSubmit!!.isEnabled = true
        dealFailInfo(map, mType)
    }

    private fun initValueTv() {
        var receiveNum = mPayInfo!!["account"]!!.toString() + ""
        receiveNum = UtilTools.getShowBankIdCard(receiveNum)

        val receiveName = mPayInfo!!["accountName"]!!.toString() + ""

        val bankName = mPayInfo!!["branchName"]!!.toString() + ""

        var payNum = mPayInfo!!["baseaccid"]!!.toString() + ""
        payNum = UtilTools.getShowBankIdCard(payNum)
        val payName = mPayInfo!!["firmname"]!!.toString() + ""


        mQiyeReceiveNumTv!!.text = receiveNum
        mQiyeReceiveNameTv!!.text = receiveName
        mQiyeReceiveBankTv!!.text = bankName
        mQiyeMemoTv!!.text = "证书支付费用"
        mQiyePayNameTv!!.text = payName
        mQiyePayNumTv!!.text = payNum
    }


    @OnClick(R.id.left_back_lay, R.id.btn_submit)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.btn_submit -> {
                mBtnSubmit!!.isEnabled = false
                checkCa()
            }
        }/*int min=20;
                int max=22;
                Random random = new Random();
                int num = random.nextInt(max)%(max-min+1) + min;

                intent = new Intent(QiyeCaZhuanZhangActivity.this,QiyeCaResultActivity.class);
                intent.putExtra(MbsConstans.QiYeResultType.RESULT_KEY,num);
                startActivity(intent);*/
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private inner class TextSpanClick(private val status: Boolean) : ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false//取消下划线false
        }

        override fun onClick(v: View) {
            backTo(QiyeCaActivity::class.java, true)
        }
    }

    private fun showMsgDialog(msg: Any, isClose: Boolean) {
        mZDialog = TipMsgDialog(this, true)
        mZDialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                dialog.dismiss()
                if (isClose) {
                    finish()
                }
                true
            } else {
                false
            }
        }
        val onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.cancel -> {
                    mZDialog!!.dismiss()
                    if (isClose) {
                        finish()
                    }
                }
                R.id.confirm -> mZDialog!!.dismiss()
                R.id.tv_right -> {
                    mZDialog!!.dismiss()
                    if (isClose) {
                        finish()
                    }
                }
            }
        }
        mZDialog.setCanceledOnTouchOutside(false)
        mZDialog.setCancelable(true)
        mZDialog.onClickListener = onClickListener
        mZDialog.initValue("温馨提示", msg)
        mZDialog.show()
        mZDialog.tv_cancel!!.text = "确定"
    }

}
