package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.AddressSelectDialog2
import com.lairui.easy.mywidget.dialog.MySelectDialog
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

class PerfectInfoActivity : BasicActivity(), SelectBackListener, RequestView {
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
    @BindView(R.id.name_value_tv)
    lateinit var mNameValueTv: TextView
    @BindView(R.id.card_num_value_tv)
    lateinit var mCardNumValueTv: TextView
    @BindView(R.id.country_value_tv)
    lateinit var mCountryValueTv: TextView
    @BindView(R.id.country_lay)
    lateinit var mCountryLay: CardView
    @BindView(R.id.huji_value_tv)
    lateinit var mHujiValueTv: TextView
    @BindView(R.id.huji_lay)
    lateinit var mHujiLay: CardView
    @BindView(R.id.other_lay)
    lateinit var mOtherLay: CardView
    @BindView(R.id.other_value_eddit)
    lateinit var mOtherValueEddit: EditText
    @BindView(R.id.marray_value_tv)
    lateinit var mMarrayValueTv: TextView
    @BindView(R.id.marray_lay)
    lateinit var mMarrayLay: CardView
    @BindView(R.id.juzhu_value_tv)
    lateinit var mJuzhuValueTv: TextView
    @BindView(R.id.juzhu_lay)
    lateinit var mJuzhuLay: CardView
    @BindView(R.id.juzhu_detail_value_edit)
    lateinit var mJuzhuDetailValueEdit: EditText
    @BindView(R.id.zhufang_value_tv)
    lateinit var mZhufangValueTv: TextView
    @BindView(R.id.zhufang_info_lay)
    lateinit var mZhufangInfoLay: CardView
    @BindView(R.id.zujin_value_edit)
    lateinit var mZujinValueEdit: EditText
    @BindView(R.id.other_value_edit2)
    lateinit var mOtherValueEdit2: EditText
    @BindView(R.id.gdphone_value_edit)
    lateinit var mGdphoneValueEdit: EditText
    @BindView(R.id.tx_address_value_tv)
    lateinit var mTxAddressValueTv: TextView
    @BindView(R.id.tx_address_lay)
    lateinit var mTxAddressLay: CardView
    @BindView(R.id.txdetail_value_edit)
    lateinit var mTxdetailValueEdit: EditText
    @BindView(R.id.busines_name_lay)
    lateinit var mBusinesNameLay: LinearLayout
    @BindView(R.id.btn_next)
    lateinit var mBtnNext: Button
    @BindView(R.id.education_value_tv)
    lateinit var mEducationValueTv: TextView
    @BindView(R.id.education_lay)
    lateinit var mEducationLay: CardView
    @BindView(R.id.huji_line)
    lateinit var mHujiLine: View
    @BindView(R.id.other_line)
    lateinit var mOtherLine: View
    @BindView(R.id.juzhu_detail_lay)
    lateinit var mJuzhuDetailLay: CardView
    @BindView(R.id.zujin_lay)
    lateinit var mZujinLay: CardView
    @BindView(R.id.other_lay2)
    lateinit var mOtherLay2: CardView
    @BindView(R.id.juzhu_detail_line)
    lateinit var mJuzhuDetailLine: View
    @BindView(R.id.zujin_line)
    lateinit var mZujinLine: View
    @BindView(R.id.other_line2)
    lateinit var mOtherLine2: View
    @BindView(R.id.tx_detail_line)
    lateinit var mTxDetailLine: View
    @BindView(R.id.tx_detail_lay)
    lateinit var mTxDetailLay: CardView
    @BindView(R.id.minzu_value_tv)
    lateinit var mMinzuValueTv: TextView
    @BindView(R.id.minzu_lay)
    lateinit var mMinzuLay: CardView


    private lateinit var mGuojiDialog: MySelectDialog
    private lateinit var mHunyinDialog: MySelectDialog
    private lateinit var mXueliDialog: MySelectDialog
    private lateinit var mZhufangDialog: MySelectDialog

    private lateinit var mHuJiAddSelectDialog: AddressSelectDialog2
    private lateinit var mJuZhuAddSelectDialog: AddressSelectDialog2
    private lateinit var mTxAddSelectDialog: AddressSelectDialog2

    private var mRequestTag = ""

    private lateinit var mData: MutableMap<String, Any>

    private var mViewType: String? = ""
    private lateinit var mShake: Animation


    override val contentView: Int
        get() = R.layout.activity_perfect_info


    private var mCountryMap: MutableMap<String, Any>? = HashMap()
    private var mHujiAddMap: MutableMap<String, Any>? = HashMap()
    private var mJuzhuAddMap: MutableMap<String, Any>? = HashMap()
    private var mTxAddMap: MutableMap<String, Any>? = HashMap()
    private var mMarryMap: MutableMap<String, Any>? = HashMap()
    private var mEducationMap: MutableMap<String, Any>? = HashMap()
    private var mZhuFangMap: MutableMap<String, Any>? = HashMap()
    private var mMinZuMap: MutableMap<String, Any>? = HashMap()

    override fun init() {
        //        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 60);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.perfect_info)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mViewType = bundle.getString("type")
        }

        mShake = AnimationUtils.loadAnimation(this, R.anim.shake)//加载动画资源文件


        mHujiLay!!.visibility = View.GONE
        mOtherLay!!.visibility = View.GONE
        mHujiLine!!.visibility = View.GONE
        mOtherLine!!.visibility = View.GONE

        mJuzhuDetailLine!!.visibility = View.GONE
        mJuzhuDetailLay!!.visibility = View.GONE

        mZujinLine!!.visibility = View.GONE
        mZujinLay!!.visibility = View.GONE
        mOtherLine2!!.visibility = View.GONE
        mOtherLay2!!.visibility = View.GONE

        mTxDetailLay!!.visibility = View.GONE
        mTxDetailLine!!.visibility = View.GONE

        UtilTools.setMoneyEdit(mZujinValueEdit!!, 0.0)

        initAllDialog()

        getMoreInfo()

    }

    /**
     * 获取用户更多资料信息
     */
    private fun getMoreInfo() {

        mRequestTag = MethodUrl.userMoreInfo
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.userMoreInfo, map)
    }

    private fun initValutView() {


        val list = SelectDataUtil.getNameCodeByType("nation")
        mGuojiDialog = MySelectDialog(this, true, list, "国籍", 1)
        mGuojiDialog!!.selectBackListener = this

        //List<Map<String, Object>> list2 = SelectDataUtil.getMarry();
        val list2 = SelectDataUtil.getNameCodeByType("marital")
        mHunyinDialog = MySelectDialog(this, true, list2, "婚姻", 2)
        mHunyinDialog!!.selectBackListener = this

        //List<Map<String, Object>> list3 = SelectDataUtil.getEducation();
        val list3 = SelectDataUtil.getNameCodeByType("education")
        mXueliDialog = MySelectDialog(this, true, list3, "学历", 3)
        mXueliDialog!!.selectBackListener = this

        //List<Map<String, Object>> list4 = SelectDataUtil.getHouse();
        val list4 = SelectDataUtil.getNameCodeByType("house")
        mZhufangDialog = MySelectDialog(this, true, list4, "住房信息", 4)
        mZhufangDialog!!.selectBackListener = this

        val minzuCode = mData!!["nation"]!!.toString() + ""
        val minzuName = mData!!["nationname"]!!.toString() + ""

        val name = mData!!["finame"]!!.toString() + ""
        val idCardNum = mData!!["farnzjno"]!!.toString() + ""
        val huji = mData!!["register"]!!.toString() + ""
        val guoji = mData!!["nationality"]!!.toString() + ""
        val hunyin = mData!!["marry"]!!.toString() + ""
        val xueli = mData!!["education"]!!.toString() + ""
        val juzhuShengName = mData!!["houprname"]!!.toString() + ""
        val juzhuShengCode = mData!!["houprcode"]!!.toString() + ""
        val juzhuCityName = mData!!["houciname"]!!.toString() + ""
        val juzhuCityCode = mData!!["houcicode"]!!.toString() + ""

        val juzhuAdd = juzhuShengName + "" + juzhuCityName


        if (UtilTools.empty(juzhuShengCode) || UtilTools.empty(juzhuCityCode)) {
            mJuzhuDetailLay!!.visibility = View.GONE
            mJuzhuDetailLine!!.visibility = View.GONE
        } else {
            mJuzhuAddMap!!["proname"] = juzhuShengName//居住地址  省份名称
            mJuzhuAddMap!!["procode"] = juzhuShengCode//居住地址  省份code
            mJuzhuAddMap!!["cityname"] = juzhuCityName//居住地址  城市名称
            mJuzhuAddMap!!["citycode"] = juzhuCityCode//居住地址  城市code
            mJuzhuDetailLay!!.visibility = View.VISIBLE
            mJuzhuDetailLine!!.visibility = View.VISIBLE
            mJuzhuDetailValueEdit!!.setText(mData!!["houaddr"]!!.toString() + "")

        }
        val txShengName = mData!!["cmnprname"]!!.toString() + ""
        val txShengCode = mData!!["cmnprcode"]!!.toString() + ""
        val txCityName = mData!!["cmnciname"]!!.toString() + ""
        val txCityCode = mData!!["cmncicode"]!!.toString() + ""
        val txAdd = txShengName + "" + txCityName

        if (UtilTools.empty(txShengCode) || UtilTools.empty(txCityCode)) {
            mTxDetailLay!!.visibility = View.GONE
            mTxDetailLine!!.visibility = View.GONE

        } else {
            mTxAddMap!!["proname"] = txShengName//通讯地址  省份名称
            mTxAddMap!!["procode"] = txShengCode//通讯地址  省份code
            mTxAddMap!!["cityname"] = txCityName//通讯地址  城市名称
            mTxAddMap!!["citycode"] = txCityCode//通讯地址  城市code
            mTxDetailLay!!.visibility = View.VISIBLE
            mTxDetailLine!!.visibility = View.VISIBLE
            mTxdetailValueEdit!!.setText(mData!!["cmnaddr"]!!.toString() + "")
        }


        val zhufangZk = mData!!["houproperty"]!!.toString() + ""
        val guhua = mData!!["phone"]!!.toString() + ""

        if (UtilTools.empty(name)) {
        } else {
            mNameValueTv!!.text = name
        }
        if (UtilTools.empty(idCardNum)) {
        } else {
            // mCardNumValueTv.setText(idCardNum);
            mCardNumValueTv!!.text = UtilTools.getIDCardXing(idCardNum)
        }
        if (UtilTools.empty(huji)) {
        } else {
            mHujiValueTv!!.text = huji
            mHujiAddMap = HashMap()
            mHujiAddMap!!["name"] = huji
        }

        if (UtilTools.empty(minzuCode) || UtilTools.empty(minzuName)) {
        } else {
            mMinzuValueTv!!.text = minzuName
            mMinZuMap!!["code"] = minzuCode
            mMinZuMap!!["name"] = minzuName
        }
        if (UtilTools.empty(guhua)) {
        } else {
            mGdphoneValueEdit!!.setText(guhua)
        }

        if (UtilTools.empty(juzhuShengCode) || UtilTools.empty(juzhuCityCode)) {
        } else {
            mJuzhuValueTv!!.text = juzhuAdd
        }
        if (UtilTools.empty(txShengCode) || UtilTools.empty(txCityCode)) {
        } else {
            mTxAddressValueTv!!.text = txAdd
        }

        if (UtilTools.empty(guoji)) {
            mCountryValueTv!!.text = ""
        } else {
            if (guoji == "OTHER") {
                mOtherLay!!.visibility = View.VISIBLE
                mHujiLay!!.visibility = View.GONE

                mOtherLine!!.visibility = View.VISIBLE
                mHujiLine!!.visibility = View.GONE
            } else {
                mOtherLay!!.visibility = View.GONE
                mHujiLay!!.visibility = View.VISIBLE

                mOtherLine!!.visibility = View.GONE
                mHujiLine!!.visibility = View.VISIBLE
            }
            mCountryMap = SelectDataUtil.getMap(guoji, SelectDataUtil.getNameCodeByType("nation"))
            if (mCountryMap != null) {
                mCountryValueTv!!.text = mCountryMap!!["name"]!!.toString() + ""
            }
        }

        if (UtilTools.empty(hunyin)) {
            mMarrayValueTv!!.text = ""
        } else {
            mMarryMap = SelectDataUtil.getMap(hunyin, SelectDataUtil.getNameCodeByType("marital"))
            if (mMarryMap != null) {
                mMarrayValueTv!!.text = mMarryMap!!["name"]!!.toString() + ""
            }
        }

        if (UtilTools.empty(xueli)) {
            mEducationValueTv!!.text = ""
        } else {
            mEducationMap = SelectDataUtil.getMap(xueli, SelectDataUtil.getNameCodeByType("education"))
            if (mEducationMap != null) {
                mEducationValueTv!!.text = mEducationMap!!["name"]!!.toString() + ""
            }
        }

        if (UtilTools.empty(zhufangZk)) {
            mZhufangValueTv!!.text = ""
        } else {
            if (zhufangZk == "6") {
                mOtherLay2!!.visibility = View.VISIBLE
                mZujinLay!!.visibility = View.GONE
                mOtherLine2!!.visibility = View.VISIBLE
                mZujinLine!!.visibility = View.GONE
                mOtherValueEdit2!!.setText(mData!!["houmemo"]!!.toString() + "")
            } else if (zhufangZk == "5") {
                mOtherLay2!!.visibility = View.GONE
                mZujinLay!!.visibility = View.VISIBLE
                mOtherLine2!!.visibility = View.GONE
                mZujinLine!!.visibility = View.VISIBLE
                mZujinValueEdit!!.setText(mData!!["houmonthrent"]!!.toString() + "")
            } else {
                mOtherLay2!!.visibility = View.GONE
                mZujinLay!!.visibility = View.GONE
                mOtherLine2!!.visibility = View.GONE
                mZujinLine!!.visibility = View.GONE
            }

            mZhuFangMap = SelectDataUtil.getMap(zhufangZk, SelectDataUtil.getNameCodeByType("house"))
            if (mZhuFangMap != null) {
                mZhufangValueTv!!.text = mZhuFangMap!!["name"]!!.toString() + ""
            }
        }
    }


    private fun getItemValues() {

        val name = mData!!["finame"]!!.toString() + ""
        val idCardNum = mData!!["farnzjno"]!!.toString() + ""

        val valueMap = HashMap<String, String>()

        if (mCountryMap == null || mCountryMap!!.isEmpty()) {
            UtilTools.isEmpty(mCountryValueTv!!, "国籍")
            showToastMsg("请选择国籍")
            return
        }

        if (mCountryMap!!["code"]!!.toString() + "" == "OTHER") {
            if (UtilTools.isEmpty(mOtherValueEddit!!, "其它")) {
                showToastMsg("请输入内容")
                return
            }
        } else {
            if (mHujiAddMap == null || mHujiAddMap!!.isEmpty()) {
                UtilTools.isEmpty(mHujiValueTv!!, "户口所在地")
                showToastMsg("请选择户口所在地")
                return
            } else {

            }
        }

        if (mMinZuMap == null || mMinZuMap!!.isEmpty()) {
            UtilTools.isEmpty(mMinzuValueTv!!, "民族")
            showToastMsg("请选择民族")
            return
        }
        if (mMarryMap == null || mMarryMap!!.isEmpty()) {
            UtilTools.isEmpty(mMarrayValueTv!!, "婚姻")
            showToastMsg("请选择婚姻")
            return
        }

        if (mEducationMap == null || mEducationMap!!.isEmpty()) {
            UtilTools.isEmpty(mEducationValueTv!!, "学历")

            showToastMsg("请选择学历")
            return
        }
        if (mJuzhuAddMap == null || mJuzhuAddMap!!.isEmpty()) {
            UtilTools.isEmpty(mJuzhuValueTv!!, "居住地址")
            showToastMsg("请选择居住地址")
            return
        }

        if (UtilTools.isEmpty(mJuzhuDetailValueEdit!!, "详细地址")) {
            showToastMsg("请输入详细地址")
            return
        }

        /*if(!UtilTools.isContainsChinese(mJuzhuDetailValueEdit.getText()+"")){
            mJuzhuDetailLay.startAnimation(mShake);
            showToastMsg("详细地址不合法，请重新输入");
            return;
        }*/

        if (mZhuFangMap == null || mZhuFangMap!!.isEmpty()) {
            UtilTools.isEmpty(mZhufangValueTv!!, "住房信息")
            showToastMsg("请选择住房信息")
            return
        }

        if (mZhuFangMap!!["code"]!!.toString() + "" == "6") {
            if (UtilTools.isEmpty(mOtherValueEdit2!!, "其它")) {
                showToastMsg("请输入内容")
                return
            }
        } else if (mZhuFangMap!!["code"]!!.toString() + "" == "5") {
            if (UtilTools.isEmpty(mZujinValueEdit!!, "租金")) {
                showToastMsg("请输入内容")
                return
            }
        }

        if (UtilTools.isEmpty(mGdphoneValueEdit!!, "电话")) {
            showToastMsg("请输入电话")
            return
        }


        if (mTxAddMap == null || mTxAddMap!!.isEmpty()) {
            UtilTools.isEmpty(mTxAddressValueTv!!, "通讯地址")
            showToastMsg("请选择通讯地址")
            return
        }

        if (UtilTools.isEmpty(mTxdetailValueEdit!!, "详细地址")) {
            showToastMsg("请输入详细地址")
            return
        }

        /*if(!UtilTools.isContainsChinese(mTxdetailValueEdit.getText()+"")){
            mTxDetailLay.startAnimation(mShake);
            showToastMsg("详细地址不合法，请重新输入");
            return;
        }*/

        valueMap["finame"] = name//姓名
        valueMap["farnzjno"] = idCardNum//身份证号
        valueMap["nationality"] = mCountryMap!!["code"]!!.toString() + ""//国籍
        valueMap["register"] = mHujiAddMap!!["name"]!!.toString() + ""//户口所在地
        valueMap["nation"] = mMinZuMap!!["code"]!!.toString() + ""//民族编码
        valueMap["marry"] = mMarryMap!!["code"]!!.toString() + ""//婚姻
        valueMap["education"] = mEducationMap!!["code"]!!.toString() + ""//学历
        //proname    procode  cityname  citycode  name
        valueMap["houprname"] = mJuzhuAddMap!!["proname"]!!.toString() + ""//居住地址  省份名称
        valueMap["houprcode"] = mJuzhuAddMap!!["procode"]!!.toString() + ""//居住地址  省份code
        valueMap["houciname"] = mJuzhuAddMap!!["cityname"]!!.toString() + ""//居住地址  城市名称
        valueMap["houcicode"] = mJuzhuAddMap!!["citycode"]!!.toString() + ""//居住地址  城市code
        valueMap["houaddr"] = mJuzhuDetailValueEdit!!.text.toString() + ""//居住地址  详细地址
        valueMap["houproperty"] = mZhuFangMap!!["code"]!!.toString() + ""//住房状况  现在居住权

        if (mZhuFangMap!!["code"]!!.toString() + "" == "6") {
            valueMap["houmemo"] = mOtherValueEdit2!!.text.toString() + ""//住房状况  其它
        } else if (mZhuFangMap!!["code"]!!.toString() + "" == "5") {
            valueMap["houmonthrent"] = mZujinValueEdit!!.text.toString() + ""//住房状况  月租金
        }
        valueMap["phone"] = mGdphoneValueEdit!!.text.toString() + ""// 固定电话

        //proname    procode  cityname  citycode  name
        valueMap["cmnprname"] = mTxAddMap!!["proname"]!!.toString() + ""//通讯地址  省份名称
        valueMap["cmnprcode"] = mTxAddMap!!["procode"]!!.toString() + ""//通讯地址  省份code
        valueMap["cmnciname"] = mTxAddMap!!["cityname"]!!.toString() + ""//通讯地址  城市名称
        valueMap["cmncicode"] = mTxAddMap!!["citycode"]!!.toString() + ""//通讯地址  城市code
        valueMap["cmnaddr"] = mTxdetailValueEdit!!.text.toString() + ""//通讯地址  详细地址


        LogUtil.i("打印log日志", "最后的结果是$valueMap")
        val intent = Intent(this, WorkInfoActivity::class.java)
        intent.putExtra("DATA", valueMap as Serializable)
        intent.putExtra("DEFAULT_DATA", mData as Serializable?)
        intent.putExtra("type", mViewType)
        startActivity(intent)

    }


    private fun initAllDialog() {
        //List<Map<String, Object>> list = SelectDataUtil.getCountry();
        val list = SelectDataUtil.getNameCodeByType("nation")
        mGuojiDialog = MySelectDialog(this, true, list, "国籍", 1)
        mGuojiDialog!!.selectBackListener = this

        //List<Map<String, Object>> list2 = SelectDataUtil.getMarry();
        val list2 = SelectDataUtil.getNameCodeByType("marital")
        mHunyinDialog = MySelectDialog(this, true, list2, "婚姻", 2)
        mHunyinDialog!!.selectBackListener = this

        //List<Map<String, Object>> list3 = SelectDataUtil.getEducation();
        val list3 = SelectDataUtil.getNameCodeByType("education")
        mXueliDialog = MySelectDialog(this, true, list3, "学历", 3)
        mXueliDialog!!.selectBackListener = this

        //List<Map<String, Object>> list4 = SelectDataUtil.getHouse();
        val list4 = SelectDataUtil.getNameCodeByType("house")
        mZhufangDialog = MySelectDialog(this, true, list4, "住房信息", 4)
        mZhufangDialog!!.selectBackListener = this

        mHuJiAddSelectDialog = AddressSelectDialog2(this, true, "选择地址", 10)
        mHuJiAddSelectDialog!!.selectBackListener = this

        mJuZhuAddSelectDialog = AddressSelectDialog2(this, true, "选择地址", 11)
        mJuZhuAddSelectDialog!!.selectBackListener = this

        mTxAddSelectDialog = AddressSelectDialog2(this, true, "选择地址", 12)
        mTxAddSelectDialog!!.selectBackListener = this

    }


    private fun showCountryDialog() {
        mGuojiDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showMarryDialog() {
        mHunyinDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showEducationDialog() {
        mXueliDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showZhufangDialog() {
        mZhufangDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }


    @OnClick(R.id.country_lay, R.id.huji_lay, R.id.marray_lay, R.id.juzhu_lay, R.id.zhufang_info_lay, R.id.tx_address_lay, R.id.btn_next, R.id.education_lay, R.id.back_img, R.id.left_back_lay, R.id.minzu_lay)
    fun onViewClicked(view: View) {

        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.country_lay -> showCountryDialog()
            R.id.huji_lay -> mHuJiAddSelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.minzu_lay -> {
                intent = Intent(this@PerfectInfoActivity, NormalNameListActivity::class.java)
                intent.putExtra("type", "1")
                startActivityForResult(intent, 120)
            }
            R.id.marray_lay -> showMarryDialog()
            R.id.education_lay -> showEducationDialog()
            R.id.juzhu_lay -> mJuZhuAddSelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.zhufang_info_lay -> showZhufangDialog()
            R.id.tx_address_lay -> mTxAddSelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.btn_next -> getItemValues()
        }/*intent = new Intent(PerfectInfoActivity.this,WorkInfoActivity.class);
                startActivity(intent);*/
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            1 -> {
                mCountryMap = map
                mCountryValueTv!!.text = mCountryMap!!["name"]!!.toString() + ""
                mCountryValueTv!!.setError(null, null)

                layShow()
            }
            2 -> {
                mMarryMap = map
                mMarrayValueTv!!.text = mMarryMap!!["name"]!!.toString() + ""
                mMarrayValueTv!!.setError(null, null)
            }
            3 -> {
                mEducationMap = map
                mEducationValueTv!!.text = mEducationMap!!["name"]!!.toString() + ""
                mEducationValueTv!!.setError(null, null)
            }
            4 -> {
                mZhuFangMap = map
                mZhufangValueTv!!.text = mZhuFangMap!!["name"]!!.toString() + ""
                mZhufangValueTv!!.setError(null, null)
                layShow2()
            }
            10 -> {
                mHujiAddMap = map
                mHujiValueTv!!.text = mHujiAddMap!!["name"]!!.toString() + ""
                mHujiValueTv!!.setError(null, null)
            }
            11 -> {
                mJuzhuAddMap = map
                mJuzhuValueTv!!.text = mJuzhuAddMap!!["name"]!!.toString() + ""
                mJuzhuValueTv!!.setError(null, null)
                mJuzhuDetailLine!!.visibility = View.VISIBLE
                mJuzhuDetailLay!!.visibility = View.VISIBLE
            }
            12 -> {
                mTxAddMap = map
                mTxAddressValueTv!!.text = mTxAddMap!!["name"]!!.toString() + ""
                mTxAddressValueTv!!.setError(null, null)
                mTxDetailLay!!.visibility = View.VISIBLE
                mTxDetailLine!!.visibility = View.VISIBLE
            }
        }
    }

    private fun layShow() {
        val code = mCountryMap!!["code"]!!.toString() + ""
        if (code == "OTHER") {
            mOtherLay!!.visibility = View.VISIBLE
            mHujiLay!!.visibility = View.GONE

            mOtherLine!!.visibility = View.VISIBLE
            mHujiLine!!.visibility = View.GONE

        } else {
            mOtherLay!!.visibility = View.GONE
            mHujiLay!!.visibility = View.VISIBLE

            mOtherLine!!.visibility = View.GONE
            mHujiLine!!.visibility = View.VISIBLE
        }
    }

    private fun layShow2() {
        val code = mZhuFangMap!!["code"]!!.toString() + ""
        mOtherValueEdit2!!.setError(null, null)
        mZujinValueEdit!!.setError(null, null)
        if (code == "5") {
            mOtherLay2!!.visibility = View.GONE
            mZujinLay!!.visibility = View.VISIBLE
            mOtherLine2!!.visibility = View.GONE
            mZujinLine!!.visibility = View.VISIBLE
        } else if (code == "6") {
            mOtherLay2!!.visibility = View.VISIBLE
            mZujinLay!!.visibility = View.GONE
            mOtherLine2!!.visibility = View.VISIBLE
            mZujinLine!!.visibility = View.GONE

        } else {
            mOtherLay2!!.visibility = View.GONE
            mZujinLay!!.visibility = View.GONE
            mOtherLine2!!.visibility = View.GONE
            mZujinLine!!.visibility = View.GONE
        }
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.userMoreInfo -> {
                mData = tData
                initValutView()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.userMoreInfo -> getMoreInfo()
                }
            }
        }
    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        dealFailInfo(map, mType)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bundle: Bundle?
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                120 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mMinZuMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mMinzuValueTv!!.text = mMinZuMap!!["name"]!!.toString() + ""
                        mMinzuValueTv!!.setError(null, null)

                    }
                }
            }
        }
    }

}
