package com.lairui.easy.ui.temporary.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * 完善资料  工作信息
 */
class WorkInfoFragment : Fragment() {
    @BindView(R.id.work_company_name_tv)
    lateinit var mWorkCompanyNameTv: TextView
    @BindView(R.id.work_kind_value_tv)
    lateinit var mWorkKindValueTv: TextView
    @BindView(R.id.work_phone_value_tv)
    lateinit var mWorkPhoneValueTv: TextView
    @BindView(R.id.work_address_value_tv)
    lateinit var mWorkAddressValueTv: TextView
    @BindView(R.id.work_address_detail_tv)
    lateinit var mWorkAddressDetailValueTv: TextView
    @BindView(R.id.work_name_value_tv)
    lateinit var mWorkNameValueTv: TextView
    @BindView(R.id.work_xingzhi_value_tv)
    lateinit var mWorkXingzhiValueTv: TextView
    @BindView(R.id.work_time_value_tv)
    lateinit var mWorkTimeValueTv: TextView
    @BindView(R.id.work_start_value_tv)
    lateinit var mWorkStartValueTv: TextView
    @BindView(R.id.yue_money_value_tv)
    lateinit var mWorkMoneyValueTv: TextView
    lateinit var unbinder: Unbinder
    private lateinit var mRootView: View

    private lateinit var mDataMap: MutableMap<String, Any>


    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        if(savedInstanceState == null){
            super.onCreate(Bundle())
        }else{
            super.onCreate(savedInstanceState)
        }

        val inflater = activity!!.layoutInflater
        mRootView = inflater.inflate(R.layout.fragment_work_info, activity!!.findViewById<View>(R.id.info_manager_page) as ViewGroup, false)
        unbinder = ButterKnife.bind(this, mRootView!!)

        val bundle = arguments
        mDataMap = bundle!!.getSerializable("DATA") as MutableMap<String, Any>
        initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val p = mRootView!!.parent as ViewGroup
        p?.removeAllViewsInLayout()
        return mRootView
    }

    fun updateValue(map: MutableMap<String, Any>) {
        mDataMap = map
        initView()
    }

    fun initView() {
        if (UtilTools.empty(mDataMap!!["cmpname"]!!.toString() + "")) {
            mWorkCompanyNameTv!!.text = ""
        } else {
            mWorkCompanyNameTv!!.text = mDataMap!!["cmpname"]!!.toString() + ""
        }

        lateinit var mHangyeMap: MutableMap<String, Any>
        val workKind = mDataMap!!["cmptrades"]!!.toString() + ""

        if (!UtilTools.empty(workKind)) {
            val ss = SelectDataUtil.getJson(activity!!, "hangye.json")
            val mHangyeList = JSONUtil.instance.jsonToList(ss)
            for (mm in mHangyeList!!) {
                val code = mm["code"]!!.toString() + ""
                if (code == workKind) {
                    mHangyeMap = mm
                    break
                }
            }
        }
        if (mHangyeMap != null) {
            mWorkKindValueTv!!.text = mHangyeMap["name"]!!.toString() + ""
        } else {
            mWorkKindValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        }


        /*  if (UtilTools.empty(workKind)) {
            mWorkKindValueTv.setText(getResources().getString(R.string.tv_defalut_value));
        } else {
            mWorkKindValueTv.setText(workKind);
        }*/
        val cmptel = mDataMap!!["cmptel"]!!.toString() + ""
        if (UtilTools.empty(cmptel)) {
            mWorkPhoneValueTv!!.text = ""
        } else {
            mWorkPhoneValueTv!!.text = cmptel
        }

        val workAddress = mDataMap!!["cmpprname"].toString() + "" + mDataMap!!["cmpciname"]

        val cmpaddr = mDataMap!!["cmpaddr"]!!.toString() + ""
        if (UtilTools.empty(cmpaddr)) {
            mWorkAddressValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mWorkAddressValueTv!!.text = workAddress
            mWorkAddressDetailValueTv!!.text = cmpaddr

        }
        val position = mDataMap!!["posiname"]!!.toString() + ""//职业
        if (UtilTools.empty(position)) {
            mWorkNameValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            mWorkNameValueTv!!.text = position
        }
        val jobnature = mDataMap!!["jobnature"]!!.toString() + ""//工作性质
        if (UtilTools.empty(jobnature)) {
            mWorkXingzhiValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            //Map<String,Object> map = SelectDataUtil.getMap(jobnature,SelectDataUtil.getJobType());
            val map = SelectDataUtil.getMap(jobnature, SelectDataUtil.getNameCodeByType("job"))
            mWorkXingzhiValueTv!!.text = map["name"]!!.toString() + ""
        }

        val income = mDataMap!!["income"]!!.toString() + ""//月收入
        if (UtilTools.empty(income)) {
            mWorkMoneyValueTv!!.text = ""
        } else {
            mWorkMoneyValueTv!!.text = income
        }


        val jobstartdate = mDataMap!!["jobstartdate"]!!.toString() + ""
        if (UtilTools.empty(jobstartdate)) {
            mWorkTimeValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            val ss = UtilTools.getStringFromSting2(jobstartdate, "yyyyMMdd", "yyyy年MM月dd日")
            mWorkTimeValueTv!!.text = ss
        }
        val tradesstartdate = mDataMap!!["tradesstartdate"]!!.toString() + ""
        if (UtilTools.empty(tradesstartdate)) {
            mWorkStartValueTv!!.text = resources.getString(R.string.tv_defalut_value)
        } else {
            val ss = UtilTools.getStringFromSting2(tradesstartdate, "yyyyMMdd", "yyyy年MM月dd日")
            mWorkStartValueTv!!.text = ss
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }
}// Required empty public constructor
