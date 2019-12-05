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
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.MySelectDialog
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.RegexUtil
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.lairui.easy.mywidget.view.BankCardTextWatcher
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 添加收款人   界面
 */
class AddSKPeopleActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.name_tv)
    lateinit var mNameTv: EditText
    @BindView(R.id.sk_zhanghu_eidt)
    lateinit var mSkZhanghuEidt: EditText
    @BindView(R.id.kaihu_bank_value_tv)
    lateinit var mKaihuBankValueTv: TextView
    @BindView(R.id.name_bank_lay)
    lateinit var mNameBankLay: CardView
    @BindView(R.id.kaihu_bank_lay)
    lateinit var mKaihuBankLay: CardView
    @BindView(R.id.jine_value_edit)
    lateinit var mJineValueEdit: EditText
    @BindView(R.id.zhaiyao_value_edit)
    lateinit var mZhaiyaoValueEdit: EditText
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.people_image_view)
    lateinit var mSelectImageView: ImageView
    @BindView(R.id.kaihu_bank_dian_tv)
    lateinit var mKaihuBankDianTv: TextView
    @BindView(R.id.gong_si_value_tv)
    lateinit var mGongSiValueTv: TextView
    @BindView(R.id.gong_si_lay)
    lateinit var mGongSiLay: CardView
    @BindView(R.id.bank_name_arrow_view)
    lateinit var mBankArrowView: ImageView

    private lateinit var mGSDialog: MySelectDialog

    private lateinit var mGSMap: MutableMap<String, Any>

    private var mRequestTag = ""

    private var mBankNum = ""
    private var mName = ""

    private lateinit var mHezuoMap: MutableMap<String, Any>

    private var mIsShow = false

    private lateinit var mBankMap: MutableMap<String, Any>
    private lateinit var mWangDianMap: MutableMap<String, Any>
    private lateinit var mAddMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_add_skp

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mHezuoMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }

        //"patncode": "SKFH2018",
        //		"vaccid": "1832618000002590",
        //		"secstatus": "",
        //		"accid": "6235559020000001965",
        //		"zifangnme": "上海分行",
        //		"zifangbho": "SHFH2018"
        /* mHezuoMap = new HashMap<>();
        mHezuoMap.put("patncode","SKFH2018");
        mHezuoMap.put("vaccid","1832618000002590");
        mHezuoMap.put("secstatus","");
        mHezuoMap.put("accid","6235559020000001965");
        mHezuoMap.put("zifangnme","上海分行");
        mHezuoMap.put("zifangbho","SHFH2018");*/

        mTitleText!!.text = resources.getString(R.string.add_money_people)

        val list = SelectDataUtil.gongsi()
        mGSDialog = MySelectDialog(this, true, list, "选择类型", 30)
        mGSDialog!!.selectBackListener = this

        mGSMap = list[0]
        mGongSiValueTv!!.text = mGSMap!!["name"]!!.toString() + ""
        mNameTv!!.setHint(R.string.hint_shoukuan_ren2)


        isSelectBankName()
        mSkZhanghuEidt?.let { BankCardTextWatcher.bind(it) }
        UtilTools.setMoneyEdit(mJineValueEdit!!, 0.0)

        mSkZhanghuEidt!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mKaihuBankValueTv!!.text = ""
                mKaihuBankDianTv!!.text = ""
                showBankTip()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })

        /*mSkZhanghuEidt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (!UtilTools.empty( mSkZhanghuEidt.getText().toString())){
                        checkBankCard();
                    }else {
                    }
                }
            }
        });*/

        //键盘显示监听 //当键盘弹出隐藏的时候会 调用此方法。
        mSkZhanghuEidt!!.viewTreeObserver.addOnGlobalLayoutListener{
            val rect = Rect()
            this@AddSKPeopleActivity.window.decorView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = this@AddSKPeopleActivity.window.decorView.rootView.height
            Log.e("TAG", rect.bottom.toString() + "#" + screenHeight)
            val heightDifference = screenHeight - rect.bottom
            val visible = heightDifference > screenHeight / 3
            if (visible) {
                //showToastMsg("软键盘弹出");
                mIsShow = true
            } else {
                if (mIsShow && mSkZhanghuEidt!!.hasFocus()) {
                    if (mGSMap != null) {
                        val code = mGSMap!!["code"]!!.toString() + ""
                        if (code == "1") {// 账户类型(1: 对公; 2: 对私
                        } else if (code == "2") {
                            checkBankCard()
                        }
                    }
                }
                //showToastMsg("软键盘隐藏");
                mIsShow = false
            }
        }
    }

    private fun isSelectBankName() {
        val code = mGSMap!!["code"]!!.toString() + ""
        if (code == "1") {// 账户类型(1: 对公; 2: 对私
            mNameBankLay!!.isEnabled = true
            mBankArrowView!!.visibility = View.VISIBLE
            mKaihuBankValueTv!!.hint = resources.getString(R.string.please_choose)
            mNameTv!!.setHint(R.string.hint_shoukuan_ren2)

        } else if (code == "2") {//2: 对私
            mNameBankLay!!.isEnabled = true
            mBankArrowView!!.visibility = View.GONE
            mKaihuBankValueTv!!.hint = resources.getString(R.string.bank_jiaoyan)
            mNameTv!!.setHint(R.string.hint_shoukuan_ren)

        }
    }

    private fun showBankTip() {
        val code = mGSMap!!["code"]!!.toString() + ""
        if (code == "1") {// 账户类型(1: 对公; 2: 对私
            mKaihuBankValueTv!!.hint = resources.getString(R.string.please_choose)
        } else if (code == "2") {//2: 对私
            mKaihuBankValueTv!!.hint = resources.getString(R.string.bank_jiaoyan)
        }
    }

    private fun checkBankCard() {

        var num = mSkZhanghuEidt!!.text.toString() + ""
        num = num.replace(" ".toRegex(), "")

        mRequestTag = MethodUrl.checkBankCard
        val map = HashMap<String, String>()
        if (UtilTools.empty(num)) {
            showToastMsg("请输入合法的银行卡号")
            return
        }
        //---------------------------------这个地方是要校验银行账户信息的
        //---------------------------------对私14-19位校验  对公4-32位校验，对私使用接口匹配开户行，对公选择开户行
        val code = mGSMap!!["code"]!!.toString() + ""
        if (code == "1") {// 账户类型(1: 对公; 2: 对私
            val b = RegexUtil.isGongCard(num)
            if (!b) {
                showToastMsg("请输入合法的银行卡号")
                return
            }
        } else if (code == "2") {//2: 对私
            val b = RegexUtil.isSiCard(num)
            if (!b) {
                showToastMsg("请输入合法的银行卡号")
                return
            }
        }


        map["accid"] = num
        map["ptncode"] = mHezuoMap!!["patncode"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.checkBankCard, map)
    }


    private fun submitAction() {

        if (mGSMap == null || mGSMap!!.isEmpty()) {
            UtilTools.isEmpty(mGongSiValueTv!!, "账户类型")
            showToastMsg("账户类型不能为空")
            mButNext!!.isEnabled = true
            return
        }

        if (UtilTools.isEmpty(mNameTv!!, "收款人")) {
            showToastMsg("收款人不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (UtilTools.isEmpty(mSkZhanghuEidt!!, "收款账户")) {
            showToastMsg("收款账户不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (UtilTools.isEmpty(mKaihuBankValueTv!!, "开户银行")) {
            showToastMsg("开户银行不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (UtilTools.isEmpty(mKaihuBankDianTv!!, "开户网点")) {
            showToastMsg("开户网点不能为空")
            mButNext!!.isEnabled = true
            return
        }

        mBankNum = mSkZhanghuEidt!!.text.toString() + ""
        mBankNum = mBankNum.replace(" ".toRegex(), "")
        //---------------------------------这个地方是要校验银行账户信息的
        //---------------------------------对私14-19位校验  对公4-32位校验，对私使用接口匹配开户行，对公选择开户行
        val code = mGSMap!!["code"]!!.toString() + ""
        if (code == "1") {// 账户类型(1: 对公; 2: 对私
            val b = RegexUtil.isGongCard(mBankNum)
            if (!b) {
                showToastMsg("请输入合法的银行卡号")
                mButNext!!.isEnabled = true
                return
            }
        } else if (code == "2") {
            val b = RegexUtil.isSiCard(mBankNum)
            if (!b) {
                showToastMsg("请输入合法的银行卡号")
                mButNext!!.isEnabled = true
                return
            }
        }

        if (UtilTools.isEmpty(mJineValueEdit!!, "金额")) {
            showToastMsg("金额不能为空")
            mButNext!!.isEnabled = true
            return
        }

        if (UtilTools.isEmpty(mZhaiyaoValueEdit!!, "摘要")) {
            showToastMsg("摘要不能为空")
            mButNext!!.isEnabled = true
            return
        }

        mName = mNameTv!!.text.toString() + ""

        mButNext!!.isEnabled = true

        mRequestTag = MethodUrl.skPeopleAdd
        mAddMap = HashMap()

        mAddMap!!["acctype"] = mGSMap!!["code"]!!.toString() + ""//账户类型(1: 对公; 2: 对私)
        mAddMap!!["accname"] = mName//户名
        mAddMap!!["accid"] = mBankNum//帐号
        mAddMap!!["bankid"] = mBankMap!!["bankid"]!!.toString() + ""//开户行ID
        mAddMap!!["bankname"] = mBankMap!!["bankname"]!!.toString() + ""//开户行名称
        // mAddMap.put("crossmark",mBankMap.get("crossmark")+"");//跨行标识（1 本行 2 跨行）
        mAddMap!!["wdcode"] = mWangDianMap!!["opnbnkwdcd"]!!.toString() + ""//开户网点编号
        mAddMap!!["wdname"] = mWangDianMap!!["opnbnkwdnm"]!!.toString() + ""//开户网点名称
        mAddMap!!["amt"] = mJineValueEdit!!.text.toString() + ""//金额
        mAddMap!!["memo"] = mZhaiyaoValueEdit!!.text.toString() + ""//摘要

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.skPeopleAdd, mAddMap!!)
    }


    @OnClick(R.id.kaihu_bank_lay, R.id.but_next, R.id.back_img, R.id.people_image_view, R.id.name_bank_lay, R.id.gong_si_lay, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.kaihu_bank_lay -> if (mBankMap == null) {
                showToastMsg("请先填写银行卡信息")
            } else {
                intent = Intent(this@AddSKPeopleActivity, ChoseBankAddActivity::class.java)
                intent.putExtra("bankid", mBankMap!!["bankid"]!!.toString() + "")
                startActivityForResult(intent, 200)
            }
            R.id.but_next -> {
                mButNext!!.isEnabled = false
                submitAction()
            }
            R.id.people_image_view -> {
                intent = Intent(this@AddSKPeopleActivity, SkPeopleListActivity::class.java)
                startActivityForResult(intent, 100)
            }
            R.id.gong_si_lay -> mGSDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.name_bank_lay -> {
                val code = mGSMap!!["code"]!!.toString() + ""
                if (code == "1") {// 账户类型(1: 对公; 2: 对私
                    intent = Intent(this@AddSKPeopleActivity, BankNameListActivity::class.java)
                    startActivityForResult(intent, 300)
                } else if (code == "2") {//2: 对私
                    checkBankCard()
                }
            }
        }
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            30 ->

                if (mGSMap == null) {

                } else {
                    val oldCode = mGSMap!!["code"]!!.toString() + ""
                    val code = map["code"]!!.toString() + ""
                    if (oldCode == code) {
                        mGSMap = map
                        mGongSiValueTv!!.text = mGSMap!!["name"]!!.toString() + ""
                        mGongSiValueTv!!.setError(null, null)
                    } else {
                        mGSMap = map
                        mGongSiValueTv!!.text = mGSMap!!["name"]!!.toString() + ""
                        mGongSiValueTv!!.setError(null, null)

                        mKaihuBankValueTv!!.text = ""
                        mKaihuBankValueTv!!.setError(null, null)

                        mKaihuBankDianTv!!.text = ""
                        mKaihuBankDianTv!!.setError(null, null)

                        mSkZhanghuEidt!!.setText("")
                        mSkZhanghuEidt!!.setError(null, null)

                        mNameTv!!.setText("")
                        mNameTv!!.setError(null, null)
                    }
                    isSelectBankName()// 账户类型(1: 对公; 2: 对私
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
        tData as MutableMap<String,Any>
        when (mType) {
            MethodUrl.skPeopleAdd//{custid=null}
            -> {
                showToastMsg("添加成功")
                val intent = Intent()
                intent.putExtra("DATA", mAddMap as Serializable?)
                setResult(Activity.RESULT_OK, intent)
                mButNext!!.isEnabled = true
                finish()
            }
            MethodUrl.checkBankCard//{"bank_same":"0","bankid":"305","accid":"6226203000645932","bankname":"民生银行","card_type":"1"}
            -> {
                mBankMap = tData
                val tt = mBankMap!!["bank_same"]!!.toString() + ""
                if (tt == "0") {//是否同行（0：不是，1：是）
                    mBankMap!!["crossmark"] = "2"//跨行标识（1 本行 2 跨行
                } else {
                    mBankMap!!["crossmark"] = "1"
                }
                mKaihuBankValueTv!!.text = mBankMap!!["bankname"]!!.toString() + ""
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"].toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.checkBankCard -> checkBankCard()
                    MethodUrl.skPeopleAdd -> submitAction()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.skPeopleAdd//{custid=null}
            -> mButNext!!.isEnabled = true
            MethodUrl.checkBankCard//{"bank_same":"0","bankid":"305","accid":"6226203000645932","bankname":"民生银行","card_type":"1"}
            -> {
            }
        }
        dealFailInfo(map, mType)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        val bundle: Bundle?
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        /**
                         * "bankid": "3002",
                         * "crossmark": "1",
                         * "accid": "6224101646431613",
                         * "wdcode": "503290004522",
                         * "bankname": "南洋商业银行",
                         * "wdname": "南洋商业银行（中国）有限公司上海分行",
                         * "accname": "我的卡1",
                         * "acctype": "1"
                         */
                        mBankMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mSkZhanghuEidt!!.setText(mBankMap!!["accid"]!!.toString() + "")
                        mKaihuBankValueTv!!.text = mBankMap!!["bankname"]!!.toString() + ""
                        mNameTv!!.setText(mBankMap!!["accname"].toString() + "")
                        mNameTv!!.setError(null, null)
                        mSkZhanghuEidt!!.setError(null, null)
                        mKaihuBankValueTv!!.setError(null, null)

                        mWangDianMap = HashMap()
                        mWangDianMap!!["opnbnkwdcd"] = mBankMap!!["wdcode"]!!.toString() + ""//开户网点编号
                        mWangDianMap!!["opnbnkwdnm"] = mBankMap!!["wdname"]!!.toString() + ""//开户网点名称
                        mKaihuBankDianTv!!.text = mBankMap!!["wdname"].toString() + ""


                        val list = SelectDataUtil.gongsi()
                        val acctype = mBankMap!!["acctype"]!!.toString() + ""
                        if (acctype == "1") {//账户类型(1: 对公; 2: 对私)
                            mGSMap = list[0]
                            mGongSiValueTv!!.text = mGSMap!!["name"]!!.toString() + ""
                            mNameTv!!.setHint(R.string.hint_shoukuan_ren2)
                        } else {
                            mGSMap = list[1]
                            mGongSiValueTv!!.text = mGSMap!!["name"]!!.toString() + ""
                            mNameTv!!.setHint(R.string.hint_shoukuan_ren)

                        }
                        mGongSiValueTv!!.setError(null, null)
                    }
                }
                200 -> {
                    bundle = data!!.extras
                    if (bundle != null) { //{"opnbnkwdnm":"南洋商业银行（中国）有限公司北京分行","opnbnkwdcd":"503100000015"}
                        mWangDianMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mKaihuBankDianTv!!.text = mWangDianMap!!["opnbnkwdnm"].toString() + ""
                        mKaihuBankDianTv!!.setError(null, null)
                    }
                }
                300 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mBankMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mBankMap!!["bankid"] = mBankMap!!["opnbnkid"]!!.toString() + ""
                        mBankMap!!["bankname"] = mBankMap!!["opnbnknm"]!!.toString() + ""
                        mBankMap!!["logopath"] = mBankMap!!["logopath"]!!.toString() + ""
                        mKaihuBankValueTv!!.text = mBankMap!!["opnbnknm"].toString() + ""
                        mKaihuBankValueTv!!.setError(null, null)
                    }
                }
            }
        }
    }


}
