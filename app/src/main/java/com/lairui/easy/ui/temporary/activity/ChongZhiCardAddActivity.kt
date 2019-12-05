package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.AddressSelectDialog
import com.lairui.easy.mywidget.view.BankCardTextWatcher
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.RegexUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity

/**
 * 添加新的充值卡信息
 */
class ChongZhiCardAddActivity : BasicActivity(), RequestView, SelectBackListener {
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

    @BindView(R.id.kaihuhang_lay)
    lateinit var mKaiHuHangLay: LinearLayout
    @BindView(R.id.bank_dian_value_tv)
    lateinit var mBankDianValueTv: TextView
    @BindView(R.id.bank_dian_lay)
    lateinit var mBankDianLay: CardView
    @BindView(R.id.card_user_tv)
    lateinit var mCardUserTv: TextView
    @BindView(R.id.card_num_tv)
    lateinit var mCardNumTv: TextView
    @BindView(R.id.tip_name_tv)
    lateinit var mTipNameTv: TextView
    @BindView(R.id.tip_num_tv)
    lateinit var mTipNumTv: TextView
    @BindView(R.id.tv_bank_type)
    lateinit var mBankTypTv: TextView
    @BindView(R.id.ll_view)
    lateinit var mView: View
    @BindView(R.id.bank_card_account_edit)
    lateinit var mAccountEdit: EditText


    private var mIsShow = false

    private var mRequestTag = ""

    private lateinit var mBankMap: MutableMap<String, Any>
    private lateinit var mBankWDMap: MutableMap<String, Any>
    private lateinit var mAddressMap: MutableMap<String, Any>


    private lateinit var mAddressSelectDialog: AddressSelectDialog

    private var mBackType: String? = ""

    override val contentView: Int
        get() = R.layout.activity_chongzhi_add


    private var cardNum = ""
    private var bankName = ""
    private var phoneNum = ""

    override fun init() {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.bank_card_bind_title)

        val type = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
        if (type == "1") {
            mAccountEdit!!.visibility = View.GONE
            mCardUserTv!!.visibility = View.VISIBLE
            mBankCardEdit!!.hint = "请输入您的银行卡号"
            mBankTypTv!!.text = "银行卡号"
        } else {
            mAccountEdit!!.visibility = View.GONE
            mCardUserTv!!.visibility = View.VISIBLE
            mBankCardEdit!!.hint = "请输入您的银行卡号"
            mBankTypTv!!.text = "银行卡号"
        }

        val intentData = intent
        val bundle = intentData.extras
        if (bundle != null) {
            mBackType = bundle.getString("backtype")
        }

        if (MbsConstans.USER_MAP == null || MbsConstans.USER_MAP!!.isEmpty()) {
            showToastMsg("用户信息获取失败，请重新登录")
            closeActivity()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        } else {
            val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
            if (kind == "1") {
                //mTipNameTv.setText("企业名称");
                mTipNumTv!!.text = "营业执照号"

                LogUtil.i("--------------------", MbsConstans.USER_MAP!!)
                mCardUserTv!!.text = MbsConstans.USER_MAP!!["name"]!!.toString() + ""
                mCardNumTv!!.text = MbsConstans.USER_MAP!!["clno"]!!.toString() + ""
            } else {
                //mTipNameTv.setText("姓名");
                mTipNumTv!!.text = "身份证号"
                mCardUserTv!!.text = MbsConstans.USER_MAP!!["name"]!!.toString() + ""
                mCardNumTv!!.text = ""
            }
        }

        //键盘显示监听//当键盘弹出隐藏的时候会 调用此方法。
        mBankCardEdit!!.viewTreeObserver.addOnGlobalLayoutListener{
            val rect = Rect()
            this@ChongZhiCardAddActivity.window.decorView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = this@ChongZhiCardAddActivity.window.decorView.rootView.height
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
                mBankDianValueTv!!.text = ""
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })


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
        map["scene"] = "ot"//使用场景（wd：提现卡查询使用 ot：其他场景使用）
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.checkBankCard, map)
    }

    private fun cardSubmit() {

        cardNum = mBankCardEdit!!.text.toString() + ""
        bankName = mBankNameTv!!.text.toString() + ""
        phoneNum = mBankCardPhoneEdit!!.text.toString() + ""
        cardNum = cardNum.replace(" ".toRegex(), "")

        if (UtilTools.isEmpty(mBankCardEdit!!, "银行卡号")) {
            showToastMsg("银行卡号不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (!RegexUtil.isBankCard(cardNum)) {
            showToastMsg("银行卡号格式不正确")
            mButNext!!.isEnabled = true
            return
        }

        if (UtilTools.isEmpty(mBankNameTv!!, "开户银行")) {
            showToastMsg("开户银行不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (UtilTools.isEmpty(mBankDianValueTv!!, "开户网点")) {
            showToastMsg("开户网点不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (mBankWDMap == null || mBankWDMap!!.isEmpty()) {
            showToastMsg("开户网点不能为空")
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

        getMsgCodeAction()
    }

    private fun getMsgCodeAction() {
        mRequestTag = MethodUrl.bankCardSms
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        map["accid"] = cardNum//卡号
        map["mobno"] = phoneNum//银行预留手机号
        map["opnbnkid"] = mBankMap!!["opnbnkid"]!!.toString() + ""//开户银行编号
        map["opnbnknm"] = bankName//开户银行名称

        map["opnbnkwdcd"] = mBankWDMap!!["opnbnkwdcd"]!!.toString() + ""//开户网点编号
        map["opnbnkwdnm"] = mBankWDMap!!["opnbnkwdnm"]!!.toString() + ""//开户网点名称

        map["wdprovcd"] = mAddressMap!!["procode"]!!.toString() + ""//开户网点地址-省份-编号
        map["wdprovnm"] = mAddressMap!!["proname"]!!.toString() + ""//开户网点地址-省份-名称
        map["wdcitycd"] = mAddressMap!!["citycode"]!!.toString() + ""//开户网点地址-城市-编号
        map["wdcitynm"] = mAddressMap!!["cityname"]!!.toString() + ""//开户网点地址-城市-名称
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.bankCardSms, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.kaihuhang_lay, R.id.left_back_lay, R.id.bank_dian_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                mButNext!!.isEnabled = false
                cardSubmit()
            }
            R.id.kaihuhang_lay ->

                checkBankCard()
            R.id.bank_dian_lay -> if (mBankMap == null) {
                showToastMsg("银行卡信息不正确，请核实")
            } else {
                intent = Intent(this@ChongZhiCardAddActivity, ChoseBankAddActivity::class.java)
                intent.putExtra("bankid", mBankMap!!["opnbnkid"]!!.toString() + "")
                startActivityForResult(intent, 100)
                /* mAddressSelectDialog = new AddressSelectDialog(this, true, "选择地址", 11);
                    mAddressSelectDialog.setSelectBackListener(this);
                    mAddressSelectDialog.showAtLocation(Gravity.BOTTOM, 0, 0);*/
            }
        }/*intent = new Intent(ChongZhiCardAddActivity.this, BankNameListActivity.class);
                startActivityForResult(intent, 100);*/
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mButNext!!.isEnabled = true
        when (mType) {
            MethodUrl.bankCardSms -> {
                showToastMsg(resources.getString(R.string.get_msg_code_tip))
                val intent = Intent(this@ChongZhiCardAddActivity, CodeMsgActivity::class.java)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_CARD_CHONGZHI)
                intent.putExtra("accid", cardNum)//卡号
                intent.putExtra("mobno", phoneNum)//银行预留手机号
                intent.putExtra("opnbnkid", mBankMap!!["opnbnkid"]!!.toString() + "")//开户银行编号
                intent.putExtra("opnbnknm", bankName)//开户银行名称

                intent.putExtra("opnbnkwdcd", mBankWDMap!!["opnbnkwdcd"]!!.toString() + "")//开户网点编号
                intent.putExtra("opnbnkwdnm", mBankWDMap!!["opnbnkwdnm"]!!.toString() + "")//开户网点名称
                //{name=北京市北京市, proname=北京市, citycode=110100, procode=110000, cityname=北京市}
                // {opnbnkwdcd=310100000149, opnbnkwdnm=上海浦东发展银行北京西直门支行}
                intent.putExtra("wdprovcd", mAddressMap!!["procode"]!!.toString() + "")//开户网点地址-省份-编号
                intent.putExtra("wdprovnm", mAddressMap!!["proname"]!!.toString() + "")//开户网点地址-省份-名称
                intent.putExtra("wdcitycd", mAddressMap!!["citycode"]!!.toString() + "")//开户网点地址-城市-编号
                intent.putExtra("wdcitynm", mAddressMap!!["cityname"]!!.toString() + "")//开户网点地址-城市-名称

                intent.putExtra("backtype", mBackType)

                intent.putExtra("DATA", tData as Serializable)
                startActivity(intent)
            }
            MethodUrl.checkBankCard//
            -> {
                mBankMap = tData
                mBankNameTv!!.text = mBankMap!!["opnbnknm"]!!.toString() + ""
                mKaiHuHangLay!!.visibility = View.VISIBLE
                mView!!.visibility = View.VISIBLE
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
        mButNext!!.isEnabled = true
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
                        mBankWDMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mAddressMap = bundle.getSerializable("DATA2") as MutableMap<String, Any>
                        mBankDianValueTv!!.text = mBankWDMap!!["opnbnkwdnm"]!!.toString() + ""
                        mBankDianValueTv!!.setError(null, null)
                    }

                    LogUtil.i("-------------------------------", mAddressMap.toString() + "           " + mBankWDMap)
                }
                300 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mBankMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        //mBankMap.put("bankid", mBankMap.get("opnbnkid") + "");
                        //mBankMap.put("bankname", mBankMap.get("opnbnknm") + "");
                        //mBankMap.put("logopath", mBankMap.get("logopath") + "");
                        mBankNameTv!!.text = mBankMap!!["opnbnknm"]!!.toString() + ""
                        mBankNameTv!!.setError(null, null)
                    }
                }
            }
        }
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        val intent: Intent
        when (type) {
            11 -> {
                intent = Intent(this@ChongZhiCardAddActivity, BankWdActivity::class.java)
                intent.putExtra("bankid", mBankMap!!["opnbnkid"]!!.toString() + "")
                intent.putExtra("citycode", map["citycode"]!!.toString() + "")
                startActivityForResult(intent, 100)
            }
        }
    }

}
