package com.lairui.easy.ui.temporary.activity

import androidx.core.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

class FuKuanFinishActivity : BasicActivity(), RequestView {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.tv_money)
    lateinit var mTvMoney: TextView
    @BindView(R.id.bt_finish)
    lateinit var mBtFinish: Button
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.tv_message)
    lateinit var mTvMessage: TextView
    @BindView(R.id.card_type)
    lateinit var mCardType: TextView
    @BindView(R.id.accid_value_tv)
    lateinit var mAccidValueTv: TextView
    @BindView(R.id.card_lay)
    lateinit var mCardLay: LinearLayout
    @BindView(R.id.line1)
    lateinit var mLine1: View
    @BindView(R.id.iv_icon)
    lateinit var mIvIcon: ImageView

    private var mAccid: String? = ""
    private var mBankName: String? = ""

    override val contentView: Int
        get() = R.layout.activity_fu_kuan_finish

    override fun init() {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mTvMoney!!.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney(bundle.getString("money")!!)
            val type = bundle.getString("type")
            when (type) {
                "0" -> {
                    mTitleText!!.text = "付款"
                    mTvMessage!!.text = "付款成功"
                    mCardLay!!.visibility = View.GONE
                    mLine1!!.visibility = View.GONE
                }
                "1" -> {
                    mTitleText!!.text = "充值"
                    mTvMessage!!.text = "充值成功"
                    mAccid = bundle.getString("accid")

                    mCardLay!!.visibility = View.VISIBLE
                    mLine1!!.visibility = View.VISIBLE
                    mBankName = bundle.getString("bankName")
                    mAccidValueTv!!.text = mBankName + "(" + UtilTools.getCardNoFour(mAccid!!) + ")"
                }
                "2" -> {
                    mTitleText!!.text = "提现"
                    mTvMessage!!.text = "提现成功"
                    mAccid = bundle.getString("accid")
                    mCardLay!!.visibility = View.VISIBLE
                    mLine1!!.visibility = View.VISIBLE
                    mBankName = bundle.getString("bankName")
                    mAccidValueTv!!.text = mBankName + "(" + UtilTools.getCardNoFour(mAccid!!) + ")"
                }
                "3" -> {
                    mTitleText!!.text = "提现"
                    mTvMessage!!.text = "提现失败"
                    mIvIcon!!.setImageResource(R.drawable.notice)
                    mAccid = bundle.getString("accid")
                    mCardLay!!.visibility = View.VISIBLE
                    mLine1!!.visibility = View.VISIBLE
                    mBankName = bundle.getString("bankName")
                    mAccidValueTv!!.text = mBankName + "(" + UtilTools.getCardNoFour(mAccid!!) + ")"
                }

                "4" -> {
                    mTitleText!!.text = "充值"
                    mTvMessage!!.text = "充值失败"
                    mIvIcon!!.setImageResource(R.drawable.notice)
                    mAccid = bundle.getString("accid")
                    mCardLay!!.visibility = View.VISIBLE
                    mLine1!!.visibility = View.VISIBLE
                    mBankName = bundle.getString("bankName")
                    mAccidValueTv!!.text = mBankName + "(" + UtilTools.getCardNoFour(mAccid!!) + ")"
                }

                "5" -> {
                    mTitleText!!.text = "付款"
                    mTvMessage!!.text = "付款失败"
                    mIvIcon!!.setImageResource(R.drawable.notice)
                    mCardLay!!.visibility = View.GONE
                    mLine1!!.visibility = View.GONE
                }
            }
        }
    }


    @OnClick(R.id.back_img, R.id.bt_finish, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.bt_finish -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backTo(AllZiChanActivity::class.java, false)
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }
}
