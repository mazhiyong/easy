package com.lairui.easy.ui.temporary.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools

import java.util.HashMap

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * 完善资料   基本信息
 */
class BaseInfoFragment : Fragment() {

    @BindView(R.id.idcard_value_tv)
    lateinit var mIdcardValueTv: TextView
    @BindView(R.id.country_value_tv)
    lateinit var mCountryValueTv: TextView
    @BindView(R.id.huji_value_tv)
    lateinit var mHujiValueTv: TextView
    @BindView(R.id.islove_value_tv)
    lateinit var mIsloveValueTv: TextView
    @BindView(R.id.study_value_tv)
    lateinit var mStudyValueTv: TextView
    @BindView(R.id.house_address_value_tv)
    lateinit var mHouseAddressValueTv: TextView
    @BindView(R.id.house_detail_value_tv)
    lateinit var mHouseDetailValueTv: TextView
    @BindView(R.id.house_info_value_tv)
    lateinit var mHouseInfoValueTv: TextView
    @BindView(R.id.phone_value_tv)
    lateinit var mPhoneValueTv: TextView
    @BindView(R.id.phone_address_value_tv)
    lateinit var mPhoneAddressValueTv: TextView
    @BindView(R.id.phone_address_detail_value_tv)
    lateinit var mPhoneAddressDetailValueTv: TextView
    lateinit var unbinder: Unbinder
    @BindView(R.id.name_value_tv)
    lateinit var mNameValueTv: TextView
    @BindView(R.id.minzu_value_tv)
    lateinit var mMinzuValueTv: TextView
    @BindView(R.id.zujin_line)
    lateinit var mZujinLine: View
    @BindView(R.id.zujin_value_edit)
    lateinit var mZujinValueEdit: TextView
    @BindView(R.id.zujin_lay)
    lateinit var mZujinLay: CardView
    private lateinit var mRootView: View

    private var mDataMap: MutableMap<String, Any> = HashMap()

    private val mPage = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        if(savedInstanceState == null){
            super.onCreate(Bundle())
        }else{
            super.onCreate(savedInstanceState)
        }

        val inflater = activity!!.layoutInflater
        mRootView = inflater.inflate(R.layout.fragment_base_info, activity!!.findViewById<View>(R.id.info_manager_page) as ViewGroup, false)
        unbinder = ButterKnife.bind(this, mRootView!!)
        val bundle = arguments
        mDataMap = bundle!!.getSerializable("DATA") as MutableMap<String, Any>
        initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val p = mRootView!!.parent as ViewGroup
        p?.removeAllViewsInLayout()
        unbinder = ButterKnife.bind(this, mRootView!!)
        return mRootView
    }

    fun updateValue(map: MutableMap<String, Any>) {
        mDataMap = map
        initView()
    }


    fun initView() {
        val name = mDataMap["finame"]!!.toString() + ""
        if (UtilTools.empty(name)) {
            mNameValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mNameValueTv!!.text = name
        }
        val idCard = mDataMap["farnzjno"]!!.toString() + ""
        if (UtilTools.empty(idCard)) {
            mIdcardValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mIdcardValueTv!!.text = UtilTools.getIDCardXing(idCard)
        }
        val nationality = mDataMap["nationality"]!!.toString() + ""
        if (UtilTools.empty(nationality)) {
            mCountryValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            //Map<String, Object> codeMap = SelectDataUtil.getMap(nationality, SelectDataUtil.getCountry());
            val codeMap = SelectDataUtil.getMap(nationality, SelectDataUtil.getNameCodeByType("nation"))

            mCountryValueTv!!.text = codeMap["name"]!!.toString() + ""
        }
        val huji = mDataMap["register"]!!.toString() + ""
        if (UtilTools.empty(huji)) {
            mHujiValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mHujiValueTv!!.text = huji
        }

        val minzu = mDataMap["nationname"]!!.toString() + ""
        if (UtilTools.empty(minzu)) {
            mMinzuValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mMinzuValueTv!!.text = minzu
        }

        val marry = mDataMap["marry"]!!.toString() + ""
        if (UtilTools.empty(marry)) {
            mIsloveValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            //Map<String, Object> codeMap = SelectDataUtil.getMap(marry, SelectDataUtil.getMarry());
            val codeMap = SelectDataUtil.getMap(marry, SelectDataUtil.getNameCodeByType("marital"))
            mIsloveValueTv!!.text = codeMap["name"]!!.toString() + ""
        }
        val education = mDataMap["education"]!!.toString() + ""
        if (UtilTools.empty(education)) {
            mStudyValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            //Map<String, Object> codeMap = SelectDataUtil.getMap(education, SelectDataUtil.getEducation());
            val codeMap = SelectDataUtil.getMap(education, SelectDataUtil.getNameCodeByType("education"))

            mStudyValueTv!!.text = codeMap["name"]!!.toString() + ""
        }
        val houprname = mDataMap["houprname"].toString() + "" + mDataMap["houciname"]
        if (UtilTools.empty(houprname)) {
            mHouseAddressValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mHouseAddressValueTv!!.text = houprname
        }
        val houdetail = mDataMap["houaddr"]!!.toString() + ""
        if (UtilTools.empty(houdetail)) {
            mHouseDetailValueTv!!.text = resources.getString(R.string.tv_defalut_value)
            mHouseDetailValueTv!!.visibility = View.GONE
        } else {
            mHouseDetailValueTv!!.visibility = View.VISIBLE
            mHouseDetailValueTv!!.text = houdetail
        }
        val zhufangZk = mDataMap["houproperty"]!!.toString() + ""
        if (UtilTools.empty(zhufangZk)) {
            mHouseInfoValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            //Map<String, Object> codeMap = SelectDataUtil.getMap(zhufangZk, SelectDataUtil.getHouse());
            val codeMap = SelectDataUtil.getMap(zhufangZk, SelectDataUtil.getNameCodeByType("house"))
            mHouseInfoValueTv!!.text = codeMap["name"]!!.toString() + ""
            if (zhufangZk == "6") {
                mZujinLay!!.visibility = View.GONE
                mZujinLine!!.visibility = View.GONE
                mHouseInfoValueTv!!.text = codeMap["name"].toString() + "\n" + mDataMap["houmemo"]
            } else if (zhufangZk == "5") {
                mZujinLay!!.visibility = View.VISIBLE
                mZujinLine!!.visibility = View.VISIBLE
                mZujinValueEdit!!.text = mDataMap["houmonthrent"]!!.toString() + ""
            } else {
                mZujinLay!!.visibility = View.GONE
                mZujinLine!!.visibility = View.GONE
            }
        }
        val phone = mDataMap["phone"]!!.toString() + ""
        if (UtilTools.empty(phone)) {
            mPhoneValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mPhoneValueTv!!.text = phone
        }
        val cmnaddr = mDataMap["cmnprname"].toString() + "" + mDataMap["cmnciname"]
        if (UtilTools.empty(cmnaddr)) {
            mPhoneAddressValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mPhoneAddressValueTv!!.text = cmnaddr
        }
        val cmnaddrDetail = mDataMap["cmnaddr"]!!.toString() + ""
        if (UtilTools.empty(cmnaddrDetail)) {
            mPhoneAddressDetailValueTv!!.text = resources.getString(R.string.tv_defalut_value)
            mPhoneAddressDetailValueTv!!.visibility = View.GONE
        } else {
            mPhoneAddressDetailValueTv!!.visibility = View.VISIBLE
            mPhoneAddressDetailValueTv!!.text = cmnaddrDetail
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }
}
