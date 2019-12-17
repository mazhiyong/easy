package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.flyco.dialog.widget.popup.base.BasePopup
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.AddressSelectDialog2
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.mywidget.dialog.MySelectDialog
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.PopuTipView
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil


import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.MainActivity

/**
 * 工作信息  界面
 */
class WorkInfoActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.work_name_edit)
    lateinit var mWorkNameEdit: EditText
    @BindView(R.id.work_phone_value_edit)
    lateinit var mWorkPhoneValueEdit: EditText
    @BindView(R.id.work_address_value_tv)
    lateinit var mWorkAddressValueTv: TextView
    @BindView(R.id.work_detail_value_edit)
    lateinit var mWorkDetailValueEdit: EditText
    @BindView(R.id.work_detail_line)
    lateinit var mWorkDetailLine: View
    @BindView(R.id.zhiwei_value_edit)
    lateinit var mZhiweiValueEdit: TextView
    @BindView(R.id.zhiwei_lay)
    lateinit var mZhiweiLay: CardView
    @BindView(R.id.work_xingzhi_value_tv)
    lateinit var mWorkXingzhiValueTv: TextView
    @BindView(R.id.work_xingzhi_lay)
    lateinit var mWorkXingzhiLay: CardView
    @BindView(R.id.renzhi_value_tv)
    lateinit var mRenzhiValueTv: TextView
    @BindView(R.id.renzhi_time_lay)
    lateinit var mRenzhiTimeLay: CardView
    @BindView(R.id.congye_value_tv)
    lateinit var mCongyeValueTv: TextView
    @BindView(R.id.congye_time_lay)
    lateinit var mCongyeTimeLay: CardView
    @BindView(R.id.suoshuhy_edit)
    lateinit var mSuoshuhyEdit: TextView
    @BindView(R.id.work_address_lay)
    lateinit var mWorkAddressLay: CardView
    @BindView(R.id.work_address_detail_lay)
    lateinit var mWorkAddressDetailLay: CardView
    @BindView(R.id.btn_submit)
    lateinit var mSubmitButton: Button
    @BindView(R.id.tips_icon1)
    lateinit var mTipsIcon1: ImageView
    @BindView(R.id.tips_icon2)
    lateinit var mTipsIcon2: ImageView
    @BindView(R.id.yue_shouru_value_tv)
    lateinit var mYueShouruValueTv: EditText
    @BindView(R.id.yue_shouru_lay)
    lateinit var mYueShouruLay: CardView
    @BindView(R.id.hangye_lay)
    lateinit var mHangyeLay: CardView


    private lateinit var mXingzhiMap: MutableMap<String, Any>
    private  var mZhiyeMap: MutableMap<String, Any>? = null

    private var mRequestTag = ""

    private lateinit var mValueMap: MutableMap<String, Any>

    private lateinit var mDefaultMap: MutableMap<String, Any>


    private var mGongzuoTime = ""
    private var mCongyeTime = ""

    private var mWorkAddressMap: MutableMap<String, Any>? = HashMap()
    private lateinit var mQuickCustomPopup: SimpleCustomPop

    private lateinit var mHangyeMap: MutableMap<String, Any>
    private var mViewType: String? = ""

    private var mZhiyeList: List<MutableMap<String, Any>>? = ArrayList()


    override val contentView: Int
        get() = R.layout.activity_work_info

    lateinit var mWorkDiaog: MySelectDialog
    lateinit var mGongZuoDialog: DateSelectDialog
    lateinit var mCongyeDialog: DateSelectDialog
    lateinit var mWorkAddressDialog: AddressSelectDialog2


    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mValueMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            mDefaultMap = bundle.getSerializable("DEFAULT_DATA") as MutableMap<String, Any>
            mViewType = bundle.getString("type")
        }

        mTitleText!!.text = resources.getString(R.string.perfect_info)

        UtilTools.setMoneyEdit(mYueShouruValueTv!!, 0.0)
        initDiaog()
        initValutView()
    }


    private fun initValutView() {
        mQuickCustomPopup = SimpleCustomPop(this)
        val hangye = mDefaultMap!!["cmptrades"]!!.toString() + ""
        if (!UtilTools.empty(hangye)) {
            val ss = SelectDataUtil.getJson(this, "hangye_work.json")
            val mHangyeList = JSONUtil.instance.jsonToList(ss)
            for (mm in mHangyeList!!) {
                val code = mm["code"]!!.toString() + ""
                if (code == hangye) {
                    mHangyeMap = mm
                    mZhiyeList = mHangyeMap!!["typeList"] as List<MutableMap<String, Any>>?
                    break
                }
            }
        }
        if (mHangyeMap != null) {
            mSuoshuhyEdit!!.text = mHangyeMap!!["name"]!!.toString() + ""

            val zhiweiCode = mDefaultMap!!["posicode"]!!.toString() + ""
            val zhiwei = mDefaultMap!!["posiname"]!!.toString() + ""
            if (!UtilTools.empty(zhiweiCode)) {
                mZhiyeMap = HashMap()
                mZhiyeMap!!["code"] = zhiweiCode
                mZhiyeMap!!["name"] = zhiwei
                mZhiweiValueEdit!!.text = mDefaultMap!!["posiname"]!!.toString() + ""
            }

        } else {
            mZhiyeMap = null
            mZhiweiValueEdit!!.text = ""
            mZhiweiValueEdit!!.setError(null, null)
        }


        if (UtilTools.empty(mDefaultMap!!["cmpname"]!!.toString() + "")) {
            mWorkNameEdit!!.setText("")
        } else {
            mWorkNameEdit!!.setText(mDefaultMap!!["cmpname"]!!.toString() + "")
        }

        if (UtilTools.empty(mDefaultMap!!["cmptel"]!!.toString() + "")) {

        } else {
            mWorkPhoneValueEdit!!.setText(mDefaultMap!!["cmptel"]!!.toString() + "")
        }


        val workShengName = mDefaultMap!!["cmpprname"]!!.toString() + ""

        val workShengCode = mDefaultMap!!["cmpprcode"]!!.toString() + ""
        val workCityName = mDefaultMap!!["cmpciname"]!!.toString() + ""
        val workCityCode = mDefaultMap!!["cmpcicode"]!!.toString() + ""
        if (UtilTools.empty(workShengCode) || UtilTools.empty(workCityCode)) {
            mWorkAddressDetailLay!!.visibility = View.GONE
            mWorkDetailLine!!.visibility = View.GONE

        } else {
            mWorkAddressMap!!["proname"] = workShengName//通讯地址  省份名称
            mWorkAddressMap!!["procode"] = workShengCode//通讯地址  省份code
            mWorkAddressMap!!["cityname"] = workCityName//通讯地址  城市名称
            mWorkAddressMap!!["citycode"] = workCityCode//通讯地址  城市code
            mWorkAddressDetailLay!!.visibility = View.VISIBLE
            mWorkDetailLine!!.visibility = View.VISIBLE
            mWorkAddressValueTv!!.text = workShengName + "" + workCityName
            mWorkDetailValueEdit!!.setText(mDefaultMap!!["cmpaddr"]!!.toString() + "")
        }

        // mZhiweiValueEdit.setText(mDefaultMap.get("position") + "");
        mGongzuoTime = mDefaultMap!!["jobstartdate"]!!.toString() + ""
        mCongyeTime = mDefaultMap!!["tradesstartdate"]!!.toString() + ""

        mRenzhiValueTv!!.text = UtilTools.getStringFromSting2(mGongzuoTime, "yyyyMMdd", "yyyy-MM-dd")
        mCongyeValueTv!!.text = UtilTools.getStringFromSting2(mCongyeTime, "yyyyMMdd", "yyyy-MM-dd")


        val yueMoney = mDefaultMap!!["income"]!!.toString() + ""
        if (UtilTools.empty(yueMoney)) {
            mYueShouruValueTv!!.setText("")
        } else {
            mYueShouruValueTv!!.setText(yueMoney)
        }


        val xingzhi = mDefaultMap!!["jobnature"]!!.toString() + ""
        mXingzhiMap = SelectDataUtil.getMap(xingzhi, SelectDataUtil.getNameCodeByType("job"))
        if (mXingzhiMap != null) {
            mWorkXingzhiValueTv!!.text = mXingzhiMap!!["name"]!!.toString() + ""
        }
    }

    /**
     *
     */
    private fun submitAction() {

        if (UtilTools.isEmpty(mWorkNameEdit!!, "单位名称")) {
            showToastMsg("请输入单位名称")
            return
        }
        if (mHangyeMap == null || mHangyeMap!!.isEmpty()) {
            UtilTools.isEmpty(mSuoshuhyEdit!!, "所属行业")
            showToastMsg("请选择所属行业")
            return
        }
        if (UtilTools.isEmpty(mWorkPhoneValueEdit!!, "单位电话")) {
            showToastMsg("请输入单位电话")

            return
        }
        if (mWorkAddressMap == null || mWorkAddressMap!!.isEmpty()) {
            UtilTools.isEmpty(mWorkAddressValueTv!!, "单位地址")
            showToastMsg("请选择单位地址")
            return
        }
        if (UtilTools.isEmpty(mWorkDetailValueEdit!!, "详细地址")) {
            showToastMsg("请输入详细地址")
            return
        }
        if (UtilTools.isEmpty(mZhiweiValueEdit!!, "职位")) {
            showToastMsg("请选择职位")
            return
        }

        if (mXingzhiMap == null || mXingzhiMap!!.isEmpty()) {
            UtilTools.isEmpty(mWorkXingzhiValueTv!!, "工作性质")
            showToastMsg("请选择工作性质")
            return
        }
        if (UtilTools.isEmpty(mYueShouruValueTv!!, "月收入")) {
            showToastMsg("请输入月收入")
            return
        }
        if (UtilTools.empty(mGongzuoTime)) {
            UtilTools.isEmpty(mRenzhiValueTv!!, "任职时间")
            showToastMsg("请选择时间")
            return
        }
        if (UtilTools.empty(mCongyeTime)) {
            UtilTools.isEmpty(mCongyeValueTv!!, "从业时间")
            showToastMsg("请选择时间")
            return
        }

        mValueMap!!["cmpname"] = mWorkNameEdit!!.text.toString() + ""//公司名称
        mValueMap!!["cmptrades"] = mHangyeMap!!["code"]!!.toString() + ""//公司行业
        mValueMap!!["cmptel"] = mWorkPhoneValueEdit!!.text.toString() + ""//公司电话
        //proname    procode  cityname  citycode  name
        mValueMap!!["cmpprname"] = mWorkAddressMap!!["proname"]!!.toString() + ""//公司地址 省份名称
        mValueMap!!["cmpprcode"] = mWorkAddressMap!!["procode"]!!.toString() + ""//公司地址 省份code
        mValueMap!!["cmpciname"] = mWorkAddressMap!!["cityname"]!!.toString() + ""//公司地址 城市名称
        mValueMap!!["cmpcicode"] = mWorkAddressMap!!["citycode"]!!.toString() + ""//公司地址 城市code
        mValueMap!!["cmpaddr"] = mWorkDetailValueEdit!!.text.toString() + ""//公司详细地址
        //mValueMap.put("position", mZhiweiValueEdit.getText() + "");//职位
        mValueMap!!["posicode"] = mZhiyeMap!!["code"]!!.toString() + ""//职位
        mValueMap!!["jobnature"] = mXingzhiMap!!["code"]!!.toString() + ""//工作性质
        mValueMap!!["income"] = mYueShouruValueTv!!.text.toString() + ""//月收入
        mValueMap!!["jobstartdate"] = mGongzuoTime//现职开始时间
        mValueMap!!["tradesstartdate"] = mCongyeTime//所在行业开始时间(格式：yyyymmdd)


        LogUtil.i("show", mValueMap!!)
        mRequestTag = MethodUrl.submitUserInfo

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.submitUserInfo, mValueMap!!)
    }

    private fun initDiaog() {
        //List<Map<String, Object>> list = SelectDataUtil.getJobType();
        val list = SelectDataUtil.getNameCodeByType("job")
        mWorkDiaog = MySelectDialog(this, true, list, "工作性质", 11)
        mWorkDiaog!!.selectBackListener = this

        mGongZuoDialog = DateSelectDialog(this, true, "选择日期", 2000)
        mCongyeDialog = DateSelectDialog(this, true, "选择日期", 2001)
        mGongZuoDialog!!.selectBackListener = this
        mCongyeDialog!!.selectBackListener = this


        mWorkAddressDialog = AddressSelectDialog2(this, true, "选择地址", 31)
        mWorkAddressDialog!!.selectBackListener = this
    }


    override fun showProgress() {

        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        //{smstoken=sms_token@3948602038bb8ecf912e0ede4a577ebd, send_tel=151****3298}

        when (mType) {
            MethodUrl.submitUserInfo -> {
                val intent = Intent()
                intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                sendBroadcast(intent)
                showToastMsg("提交成功")
                if (mViewType == "2") {
                    backTo(MoreInfoManagerActivity::class.java, false)
                } else if (mViewType == "3") {
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
                    MethodUrl.submitUserInfo -> submitAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    @OnClick(R.id.back_img, R.id.work_xingzhi_lay, R.id.renzhi_time_lay, R.id.congye_time_lay, R.id.work_address_lay, R.id.btn_submit, R.id.tips_icon1, R.id.tips_icon2, R.id.left_back_lay, R.id.zhiwei_lay, R.id.hangye_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.hangye_lay -> {
                intent = Intent(this@WorkInfoActivity, HangYeActivity::class.java)
                intent.putExtra("TYPE", "1")
                startActivityForResult(intent, 60)
            }
            R.id.zhiwei_lay ->

                if (mHangyeMap == null || mHangyeMap!!.isEmpty()) {
                    showToastMsg("请先选择行业信息")
                } else {
                    if (mZhiyeList == null || mZhiyeList!!.size == 0) {
                        showToastMsg("职业信息异常，请联系管理员")
                    } else {
                        intent = Intent(this@WorkInfoActivity, NormalNameListActivity::class.java)
                        intent.putExtra("type", "2")
                        intent.putExtra("DATA", mZhiyeList as Serializable?)
                        startActivityForResult(intent, 130)
                    }
                }
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.work_xingzhi_lay -> mWorkDiaog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.renzhi_time_lay -> mGongZuoDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.congye_time_lay -> mCongyeDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.work_address_lay -> mWorkAddressDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.btn_submit -> submitAction()
            R.id.tips_icon1 -> {
                /* mQuickCustomPopup.initTvValue("在本单位任职的开始时间");
                mQuickCustomPopup
                        .anchorView(mTipsIcon1)
                        .offset(10, 0)
                        .gravity(Gravity.TOP)
                        .showAnim(null)
                        .dismissAnim(null)
                        .dimEnabled(false)
                        .show();*/
                val s1 = "在本单位任职的开始时间"

                val mp1 = PopuTipView(this@WorkInfoActivity, s1, R.layout.popu_lay_top)
                mTipsIcon1?.let { mp1.show(it, 1) }
            }
            R.id.tips_icon2 -> {
                /*mQuickCustomPopup.initTvValue("从事本行业的开始时间");

                mQuickCustomPopup
                        .anchorView(mTipsIcon2)
                        .offset(10, 0)
                        .gravity(Gravity.TOP)
                        .showAnim(null)
                        .dismissAnim(null)
                        .dimEnabled(false)
                        .show();*/

                val s2 = "从事本行业的开始时间"

                val mp2 = PopuTipView(this@WorkInfoActivity, s2, R.layout.popu_lay_top)
                mTipsIcon2?.let { mp2.show(it, 1) }
            }
        }/*  View inflate = View.inflate(WorkInfoActivity.this, R.layout.popup_bubble_text, null);
                TextView mTextView = inflate.findViewById(R.id.tv_bubble);
                mTextView.setText("在本单位任职的开始时间" );


              new BubblePopup(WorkInfoActivity.this, inflate)
                        .anchorView(mTipsIcon1)
                        .bubbleColor(Color.parseColor("#A6000000"))
                        .gravity(Gravity.TOP)
                        .margin(UtilTools.px2dip(WorkInfoActivity.this,mTipsIcon1.getLeft()),8)
                        .triangleWidth(5)
                        .triangleHeight(5)
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();*//*View inflate2 = View.inflate(WorkInfoActivity.this, R.layout.popup_bubble_text, null);
                TextView mTextView2 = inflate2.findViewById(R.id.tv_bubble);
                mTextView2.setText("" );


             new BubblePopup(WorkInfoActivity.this, inflate2)
                        .anchorView(mTipsIcon2)
                        .bubbleColor(Color.parseColor("#A6000000"))
                        .gravity(Gravity.TOP)
                        .margin(UtilTools.px2dip(WorkInfoActivity.this,mTipsIcon2.getLeft()),8)
                        .triangleWidth(5)
                        .triangleHeight(5)
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();*/
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            11 -> {
                mXingzhiMap = map
                mWorkXingzhiValueTv!!.text = mXingzhiMap!!["name"]!!.toString() + ""
                mWorkXingzhiValueTv!!.setError(null, null)
            }
            2000 -> {
                mGongzuoTime = map["date"]!!.toString() + ""
                mRenzhiValueTv!!.text = map["year"].toString() + "-" + map["month"] + "-" + map["day"] + ""
                mRenzhiValueTv!!.setError(null, null)
            }
            2001 -> {
                mCongyeTime = map["date"]!!.toString() + ""
                mCongyeValueTv!!.text = map["year"].toString() + "-" + map["month"] + "-" + map["day"] + ""
                mCongyeValueTv!!.setError(null, null)
            }
            31 -> {
                mWorkAddressMap = map
                mWorkAddressValueTv!!.text = mWorkAddressMap!!["name"]!!.toString() + ""
                mWorkAddressDetailLay!!.visibility = View.VISIBLE
                mWorkAddressValueTv!!.setError(null, null)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bundle: Bundle?
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                60 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mHangyeMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mZhiyeList = mHangyeMap!!["typeList"] as List<MutableMap<String, Any>>?
                        mSuoshuhyEdit!!.text = mHangyeMap!!["name"]!!.toString() + ""
                        mSuoshuhyEdit!!.setError(null, null)
                        mZhiyeMap = null
                        mZhiweiValueEdit!!.text = ""
                        mZhiweiValueEdit!!.setError(null, null)

                    }
                }
                130 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mZhiyeMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mZhiweiValueEdit!!.text = mZhiyeMap!!["name"]!!.toString() + ""
                        mZhiweiValueEdit!!.setError(null, null)
                    }
                }
            }
        }
    }


    private inner class SimpleCustomPop(context: Context)//            setCanceledOnTouchOutside(false);
        : BasePopup<SimpleCustomPop>(context) {


        private lateinit var mTextView: TextView

        override fun onCreatePopupView(): View {
            val inflate = View.inflate(mContext, R.layout.popup_bubble_text, null)
            mTextView = inflate.findViewById(R.id.tv_bubble)
            return inflate
        }

        override fun setUiBeforShow() {}

        fun initTvValue(str: String) {
            mTextView!!.text = str
        }
    }
}
