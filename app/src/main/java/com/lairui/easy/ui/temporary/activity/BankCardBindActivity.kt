package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.graphics.Rect
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 绑定银行卡  界面
 */
class BankCardBindActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.name_tv)
    lateinit var mNameTv: TextView
    @BindView(R.id.card_num_tv)
    lateinit var mCardNumTv: TextView
    @BindView(R.id.open_bank_tv)
    lateinit var mOpenBankTv: TextView
    @BindView(R.id.bank_card_value_tv)
    lateinit var mBankCardValueTv: EditText
    @BindView(R.id.edit_parent_lay)
    lateinit var mEditLay: LinearLayout
    @BindView(R.id.root_lay)
    lateinit var root_lay: RelativeLayout

    private var mIsShow = false

    private var mRequestTag = ""

    private var mPatncode: String? = ""

    private lateinit var mBankMap: MutableMap<String, Any>
    private lateinit var mOldBankMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_bank_bind

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey("patncode")) {
                mPatncode = bundle.getString("patncode")
            } else if (bundle.containsKey("DATA")) {
                mOldBankMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            }
        }


        mTitleText!!.text = resources.getString(R.string.bank_card_open_title)
        mNameTv!!.text = MbsConstans.USER_MAP!!["name"]!!.toString() + ""
        mCardNumTv!!.text = MbsConstans.USER_MAP!!["idno"]!!.toString() + ""

        if (mOldBankMap != null) {
            mBankCardValueTv!!.setText("****************" + mOldBankMap!!["accid"]!!)
            mOpenBankTv!!.text = mOldBankMap!!["bankname"]!!.toString() + ""
            mPatncode = mOldBankMap!!["patncode"]!!.toString() + ""
        }

        //键盘显示监听 //当键盘弹出隐藏的时候会 调用此方法。
        mEditLay!!.viewTreeObserver.addOnGlobalLayoutListener{
            val rect = Rect()
            this@BankCardBindActivity.window.decorView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = this@BankCardBindActivity.window.decorView.rootView.height
            Log.e("TAG", rect.bottom.toString() + "#" + screenHeight)
            val heightDifference = screenHeight - rect.bottom
            val visible = heightDifference > screenHeight / 3
            if (visible) {
                showToastMsg("软键盘弹出")
                mIsShow = true
            } else {
                if (mIsShow) {
                    checkBankCard()
                }
                showToastMsg("软键盘隐藏")
                mIsShow = false
            }
        }
    }

    private fun checkBankCard() {

        val num = mBankCardValueTv!!.text.toString() + ""
        mRequestTag = MethodUrl.checkBankCard
        val map = HashMap<String, String>()
        map["accid"] = "6226203000645932"
        map["ptncode"] = mPatncode!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.checkBankCard, map)
    }

    private fun setBankCard() {
        val num = mBankCardValueTv!!.text.toString() + ""
        mRequestTag = MethodUrl.bankCard
        val map = HashMap<String, Any>()
        map["accid"] = "6226203000645932"
        map["ptncode"] = mPatncode!!
        map["bankid"] = mBankMap!!["bankid"]!!.toString() + ""
        map["bankname"] = mBankMap!!["bankname"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.bankCard, map)
    }

    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                intent = Intent(this@BankCardBindActivity, BankCardActivity::class.java)
                startActivity(intent)
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
            MethodUrl.checkBankCard//{"bank_same":"0","bankid":"305","accid":"6226203000645932","bankname":"民生银行","card_type":"1"}
            -> {
                mBankMap = tData
                val sameType = mBankMap!!["bank_same"]!!.toString() + ""
                if (sameType == "0") {

                } else {

                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.checkBankCard -> checkBankCard()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
