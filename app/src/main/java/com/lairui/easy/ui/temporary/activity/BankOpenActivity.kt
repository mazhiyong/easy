package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.lairui.easy.mywidget.view.BankCardTextWatcher
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 开户  界面
 */
class BankOpenActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.bank_card_edit)
    lateinit var mBankCardEdit: EditText
    @BindView(R.id.bank_name_tv)
    lateinit var mBankNameTv: TextView
    @BindView(R.id.kaihu_bank_add_edit)
    lateinit var mKaihuBankAddEdit: EditText
    @BindView(R.id.bank_card_phone_edit)
    lateinit var mBankCardPhoneEdit: EditText
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.open_bank_tips)
    lateinit var mOpenBankTipsTv: TextView
    @BindView(R.id.jiaoyan)
    lateinit var mJiaoYanTv: TextView

    @BindView(R.id.kaihuhang_lay)
    lateinit var mKaiHuHangLay: LinearLayout
    @BindView(R.id.kaihuhang_line)
    lateinit var mKaiHuHangLine: View


    private var mIsShow = false

    private var mRequestTag = ""

    private lateinit var mBankMap: MutableMap<String, Any>

    private lateinit var mHeZuoMap: MutableMap<String, Any>


    override val contentView: Int
        get() = R.layout.activity_bank_open

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.OPEN_BANK) {
                finish()
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.bank_card_open_title)

        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.OPEN_BANK)
        registerReceiver(mBroadcastReceiver, intentFilter)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mHeZuoMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }
        /*{
            "patncode": "13065230",
                "vaccid": "1834913000002786",
                "secstatus": "3",
                "accid": "6235559020000001338",
                "zifangnme": "天津分行",
                "zifangbho": "043362"
        }*/
        /*  mHeZuoMap = new HashMap<>();
        mHeZuoMap.put("patncode","13065230");
        mHeZuoMap.put("vaccid","1834913000002786");
        mHeZuoMap.put("secstatus","");
        mHeZuoMap.put("accid","6235559020000001338");
        mHeZuoMap.put("zifangnme","天津分行");
        mHeZuoMap.put("zifangbho","043362");
*/
        if (MbsConstans.USER_MAP == null) {
            showToastMsg(resources.getString(R.string.exception_info))
            finish()
            return
        }

        var ss = MbsConstans.USER_MAP!!["name"]!!.toString() + ""
        ss = ss.substring(1, ss.length)
        mOpenBankTipsTv!!.text = "请填写*" + ss + "的银行卡信息"

        //键盘显示监听 //当键盘弹出隐藏的时候会 调用此方法。
        mBankCardEdit!!.viewTreeObserver.addOnGlobalLayoutListener{
            val rect = Rect()
            this@BankOpenActivity.window.decorView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = this@BankOpenActivity.window.decorView.rootView.height
            Log.e("TAG", rect.bottom.toString() + "#" + screenHeight)
            val heightDifference = screenHeight - rect.bottom
            val visible = heightDifference > screenHeight / 3
            if (visible) {
                mIsShow = true
            } else {
                if (mIsShow && mBankCardEdit!!.hasFocus()) {
                    var cardNum = mBankCardEdit!!.text.toString() + ""
                    cardNum = cardNum.replace(" ".toRegex(), "")
                    if (!UtilTools.empty(cardNum)) {
                        if (RegexUtil.isBankCard(cardNum)) {
                            checkBankCard()
                        }
                    }
                }
                mIsShow = false
            }
        }

        BankCardTextWatcher.bind(mBankCardEdit!!)

        mBankCardEdit!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mBankNameTv!!.text = ""
                mBankNameTv!!.hint = resources.getString(R.string.bank_jiaoyan)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })

        //mKaiHuHangLay.setEnabled(false);
        //mKaiHuHangLay.setVisibility(View.GONE);
        //mKaiHuHangLine.setVisibility(View.GONE);


        mButNext!!.isEnabled = false
    }

    private fun checkBankCard() {

        var num = mBankCardEdit!!.text.toString() + ""
        num = num.replace(" ".toRegex(), "")
        if (UtilTools.empty(num)) {
            showToastMsg("请输入银行卡号")
            return
        }
        mRequestTag = MethodUrl.checkBankCard
        val map = HashMap<String, String>()
        map["accid"] = num + ""
        map["ptncode"] = mHeZuoMap!!["patncode"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.checkBankCard, map)
    }


    private fun cardSubmit() {
        var cardNum = mBankCardEdit!!.text.toString() + ""
        val bankName = mBankNameTv!!.text.toString() + ""
        val phoneNum = mBankCardPhoneEdit!!.text.toString() + ""
        cardNum = cardNum.replace(" ".toRegex(), "")

        if (UtilTools.isEmpty(mBankCardEdit!!, "银行卡号")) {
            showToastMsg("银行卡号不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (!RegexUtil.isBankCard(cardNum)) {
            showToastMsg("银行卡格式不正确")
            mButNext!!.isEnabled = true
            return
        }

        if (UtilTools.empty(bankName)) {
            showToastMsg("开户银行不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (UtilTools.isEmpty(mBankCardPhoneEdit!!, "手机号码")) {
            showToastMsg("手机号码不能为空")
            mButNext!!.isEnabled = true
            return
        }

        if (!RegexUtil.isPhone(phoneNum)) {
            showToastMsg("手机号码格式不正确")
            mButNext!!.isEnabled = true
            return
        }



        mRequestTag = MethodUrl.bankFourCheck
        val map = HashMap<String, String>()
        map["accno"] = cardNum + ""
        map["patncode"] = mHeZuoMap!!["patncode"]!!.toString() + ""
        map["phone"] = phoneNum + ""
        map["bankid"] = mBankMap!!["bankid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.bankFourCheck, map)
    }

    @OnClick(R.id.back_img, R.id.but_next, R.id.kaihuhang_lay, R.id.left_back_lay, R.id.jiaoyan)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.jiaoyan -> checkBankCard()
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                mButNext!!.isEnabled = false
                cardSubmit()
            }
            R.id.kaihuhang_lay ->
                /* intent = new Intent(BankOpenActivity.this,BankNameListActivity.class);
                startActivityForResult(intent,100);*/
                checkBankCard()
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
            MethodUrl.bankFourCheck -> {
                mButNext!!.isEnabled = true
                showToastMsg(tData["result"]!!.toString() + "")
                val intent = Intent(this@BankOpenActivity, BankOpenXieyiActivity::class.java)
                var cardNum = mBankCardEdit!!.text.toString() + ""
                cardNum = cardNum.replace(" ".toRegex(), "")
                intent.putExtra("accno", cardNum + "")//银行卡
                intent.putExtra("patncode", mHeZuoMap!!["patncode"]!!.toString() + "")//合作方
                intent.putExtra("opnbnknm", mBankMap!!["bankname"].toString() + "" + "")//开户行名称
                intent.putExtra("opnbnkid", mBankMap!!["bankid"]!!.toString() + "")//开户行编号
                intent.putExtra("logopath", mBankMap!!["logopath"]!!.toString() + "")//银行头像
                startActivity(intent)
            }
            MethodUrl.checkBankCard//{"bank_same":"0","bankid":"305","accid":"6226203000645932","bankname":"民生银行","card_type":"1"}
            -> {

                mBankMap = tData
                mKaiHuHangLay!!.visibility = View.VISIBLE
                mKaiHuHangLine!!.visibility = View.VISIBLE
                mBankNameTv!!.text = mBankMap!!["bankname"]!!.toString() + ""

                mJiaoYanTv!!.visibility = View.GONE

                val sameType = mBankMap!!["bank_same"]!!.toString() + ""
                val cardType = mBankMap!!["card_type"]!!.toString() + ""
                if (cardType == "1") {
                    if (sameType == "0") {
                        //showToastMsg("此卡不是本行银行卡，请更换银行卡");
                    } else {

                    }
                    mButNext!!.isEnabled = true
                } else {
                    showToastMsg("此卡为信用卡，请填写储蓄卡信息")
                    mButNext!!.isEnabled = false
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.checkBankCard -> checkBankCard()
                    MethodUrl.bankFourCheck -> cardSubmit()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.bankFourCheck -> mButNext!!.isEnabled = true
        }
        dealFailInfo(map, mType)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val bundle: Bundle?
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                100 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mBankMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mBankNameTv!!.text = mBankMap!!["opnbnknm"]!!.toString() + ""
                        mBankNameTv!!.setError(null, null)
                        mButNext!!.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

}
