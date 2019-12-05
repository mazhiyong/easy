package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView

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
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.AddressSelectDialog
import com.lairui.easy.mywidget.view.BankCardTextWatcher
import com.lairui.easy.utils.tool.RegexUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module.activity.MainActivity

/**
 * 修改，变更提现卡   界面
 */
class BankTiXianModifyActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.kaihu_arrow_view)
    lateinit var mKaihuArrwoView: ImageView
    @BindView(R.id.bank_tip_tv)
    lateinit var mBankTipTv: TextView

    private lateinit var mAddressMap: MutableMap<String, Any>

    private var mIsShow = false

    private var mRequestTag = ""

    private lateinit var mBankMap: MutableMap<String, Any>
    private lateinit var mBankWDMap: MutableMap<String, Any>
    private lateinit var mAddressSelectDialog: AddressSelectDialog

    private lateinit var mHezuoMap: MutableMap<String, Any>

    private var mBackType = ""

    override val contentView: Int
        get() = R.layout.activity_bank_tixian_modify

    override fun init() {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.bind_tixian_title)

        val intent1 = intent
        val bundle = intent1.extras
        if (bundle != null) {
            mHezuoMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            mBackType = bundle.getString("backtype")!! + ""
        }
        if (MbsConstans.USER_MAP == null || MbsConstans.USER_MAP!!.isEmpty()) {
            showToastMsg("用户信息获取失败，请重新登录")
            closeActivity()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            return
        } else { ///{auth=1, firm_kind=1, head_pic=default, name=阿里巴巴, tel=158****9191, idno=410725****3616, cmpl_info=1}
            val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
            if (kind == "1") {
                mCardUserTv!!.text = MbsConstans.USER_MAP!!["comname"]!!.toString() + ""
                mKaihuArrwoView!!.visibility = View.VISIBLE
                mKaiHuHangLay!!.isEnabled = true
                mBankNameTv!!.hint = "请选择"
                mBankTipTv!!.text = resources.getString(R.string.bank_card_title)
                mBankCardEdit!!.setHint(R.string.hint_bank_zhanghu)
            } else {
                mCardUserTv!!.text = MbsConstans.USER_MAP!!["name"]!!.toString() + ""
                mKaihuArrwoView!!.visibility = View.GONE
                mKaiHuHangLay!!.isEnabled = true
                mBankNameTv!!.hint = "点击校验"
                mBankTipTv!!.text = resources.getString(R.string.bank_card_title2)
                mBankCardEdit!!.setHint(R.string.bank_card_edit_tip)

                //键盘显示监听 //当键盘弹出隐藏的时候会 调用此方法。
                mBankCardEdit!!.viewTreeObserver.addOnGlobalLayoutListener{
                    val rect = Rect()
                    this@BankTiXianModifyActivity.window.decorView.getWindowVisibleDisplayFrame(rect)
                    val screenHeight = this@BankTiXianModifyActivity.window.decorView.rootView.height
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
                                } else {
                                    showToastMsg("请输入合法的银行卡号")
                                }
                            } else {
                                showToastMsg("银行账户不能为空")
                            }
                        }
                        mIsShow = false
                    }
                }

            }
        }

        //mBankCardEdit.setText("6210812430032354652");
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
        mRequestPresenterImp = RequestPresenterImp(this, this)
        var num = mBankCardEdit!!.text.toString() + ""
        num = num.replace(" ".toRegex(), "")
        if (UtilTools.empty(num)) {
            showToastMsg("请输入银行卡号")
            return
        }
        mRequestTag = MethodUrl.checkBankCard
        val map = HashMap<String, String>()
        map["accid"] = num
        map["scene"] = "wd"
        //map.put("patncode", mHezuoMap.get("patncode")+"");
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.checkBankCard, map)
    }


    private fun cardSubmit() {

        var cardNum = mBankCardEdit!!.text.toString() + ""
        cardNum = cardNum.replace(" ".toRegex(), "")


        var cardTip = "银行卡号"
        val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
        if (kind == "1") {
            cardTip = resources.getString(R.string.bank_card_title)
        } else {
            cardTip = resources.getString(R.string.bank_card_title2)
        }

        if (UtilTools.isEmpty(mBankCardEdit!!, cardTip)) {
            showToastMsg(cardTip + "不能为空")
            mButNext!!.isEnabled = true
            return
        }

        if (kind == "1") {
            if (!RegexUtil.isGongCard(cardNum)) {
                showToastMsg(cardTip + "格式不正确")
                mButNext!!.isEnabled = true
                return
            }
        } else {
            if (!RegexUtil.isBankCard(cardNum)) {
                showToastMsg(cardTip + "格式不正确")
                mButNext!!.isEnabled = true
                return
            }
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


        mRequestPresenterImp = RequestPresenterImp(this, this)
        mRequestTag = MethodUrl.bindCard
        val map = HashMap<String, Any>()
        map["accid"] = cardNum//银行卡号
        map["opnbnkid"] = mBankMap!!["opnbnkid"]!!.toString() + ""//开户银行编号
        map["opnbnknm"] = mBankMap!!["opnbnknm"]!!.toString() + ""//开户银行名称
        map["opnbnkwdcd"] = mBankWDMap!!["opnbnkwdcd"]!!.toString() + ""//开户网点编号
        map["opnbnkwdnm"] = mBankWDMap!!["opnbnkwdnm"]!!.toString() + ""//开户网点名称
        //        map.put("wdprovcd", mAddressMap.get("procode")+"");//开户网点地址-省份-编号
        //        map.put("wdprovnm", mAddressMap.get("proname")+"");//开户网点地址-省份-名称
        //        map.put("wdcitycd",mAddressMap.get("citycode")+"");//开户网点地址-城市-编号
        //        map.put("wdcitynm", mAddressMap.get("cityname")+"");//开户网点地址-城市-名称

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.bindCard, map)
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
            R.id.kaihuhang_lay -> {
                val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
                if (kind == "1") {
                    intent = Intent(this@BankTiXianModifyActivity, BankNameListActivity::class.java)
                    startActivityForResult(intent, 30)
                } else {
                    checkBankCard()
                }
            }
            R.id.bank_dian_lay -> if (mBankMap == null) {
                showToastMsg("银行卡信息不正确，请核实")
            } else {
                intent = Intent(this@BankTiXianModifyActivity, ChoseBankAddActivity::class.java)
                intent.putExtra("bankid", mBankMap!!["opnbnkid"]!!.toString() + "")
                startActivityForResult(intent, 100)
                /* mAddressSelectDialog = new AddressSelectDialog(this, true, "选择地址", 11);
                    mAddressSelectDialog.setSelectBackListener(this);
                    mAddressSelectDialog.showAtLocation(Gravity.BOTTOM, 0, 0);*/
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
        when (mType) {
            MethodUrl.checkBankCard//
            -> {
                mBankMap = tData
                mBankNameTv!!.text = mBankMap!!["opnbnknm"]!!.toString() + ""
            }
            MethodUrl.bindCard -> {
                showToastMsg(tData["result"]!!.toString() + "")
                sendBrodcast2()
                if (mBackType == "20") {
                    finish()
                } else {
                    backTo(MainActivity::class.java, true)
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
        mButNext!!.isEnabled = true
        dealFailInfo(map, mType)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val bundle: Bundle?
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                30 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mBankMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mBankNameTv!!.text = mBankMap!!["opnbnknm"]!!.toString() + ""
                        mBankNameTv!!.setError(null, null)
                    }
                }
                100 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mBankWDMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mAddressMap = bundle.getSerializable("DATA2") as MutableMap<String, Any>
                        mBankDianValueTv!!.text = mBankWDMap!!["opnbnkwdnm"]!!.toString() + ""
                        mBankDianValueTv!!.setError(null, null)
                    }
                }
            }
        }
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        val intent: Intent
        when (type) {
            11 -> {
                mAddressMap = map
                intent = Intent(this@BankTiXianModifyActivity, BankWdActivity::class.java)
                intent.putExtra("bankid", mBankMap!!["opnbnkid"]!!.toString() + "")
                intent.putExtra("citycode", map["citycode"]!!.toString() + "")
                startActivityForResult(intent, 100)
            }
        }
    }

    private fun sendBrodcast2() {
        val intent = Intent()
        intent.action = MbsConstans.BroadcastReceiverAction.BANKUPDATE_UPDATE
        sendBroadcast(intent)
    }
}
