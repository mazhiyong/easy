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
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

/**
 * 开户成功  界面
 */
class BankOpenSuccessActivity : BasicActivity(), RequestView {

    private val TAG = "BankOpenSuccessActivity"

    @BindView(R.id.card_num_tv)
    lateinit var mCardNumTv: EditText
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.containerLayout)
    lateinit var mContainerLayout: LinearLayout
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


    private val mRequestTag = ""

    override val contentView: Int
        get() = R.layout.activity_bank_open_success

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        //        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 60);

        mTitleText!!.text = resources.getString(R.string.bank_card_open_title)
        mCardNumTv!!.isFocusable = true
        mCardNumTv!!.requestFocus()
        mCardNumTv!!.isEnabled = false

    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
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
        when (mType) {
            MethodUrl.serverRandom//
            -> {
            }
            MethodUrl.erLeihuPass -> {
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.serverRandom -> {
                    }
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        dealFailInfo(map, mType)
    }

}
