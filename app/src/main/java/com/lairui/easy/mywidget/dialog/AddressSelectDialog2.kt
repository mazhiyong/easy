package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.AddressWheelAdapter
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SelectDataUtil
import com.wx.wheelview.widget.WheelView

import java.util.ArrayList
import java.util.HashMap

class AddressSelectDialog2 : BaseDialog {


    private var mProvince: WheelView<MutableMap<String,Any>>? = null
    private var mCity: WheelView<MutableMap<String,Any>>? = null
    private var mArea: WheelView<MutableMap<String,Any>>? = null


    private var mCancleTv: TextView? = null
    private var mTitleTv: TextView? = null
    private var mSureTv: TextView? = null

    override var mContext: Context

    private var mTitleStr = ""
    private val mSelectTime = ""

    private val mYearStr = ""
    private val mMonthStr = ""
    private val mDayStr = ""

    private var mType = 0
    var selectBackListener: SelectBackListener? = null

    private var mList = ArrayList<MutableMap<String, Any>>()

    private var resultMap: MutableMap<String, Any> = HashMap()


    private val selectAddress: Boolean
        get() {
            val mProMap = mProvince!!.selectionItem as MutableMap<String, Any>
            val mCityMap = mCity!!.selectionItem as MutableMap<String, Any>

            val mCityList = mProMap["children"] as List<MutableMap<String, Any>>?
            if (mCityList!!.contains(mCityMap)) {
                resultMap = HashMap()
                resultMap["proname"] = mProMap["label"]!!.toString() + ""
                resultMap["procode"] = mProMap["value"]!!.toString() + ""
                resultMap["cityname"] = mCityMap["label"]!!.toString() + ""
                resultMap["citycode"] = mCityMap["value"]!!.toString() + ""
                resultMap["name"] = mProMap["label"].toString() + "" + mCityMap["label"] + ""
                return true
            } else {
                return false
            }
        }


    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, isStyle: Boolean, title: String, type: Int) : super(context, isStyle) {
        mContext = context
        mTitleStr = title
        mType = type

        val ss = SelectDataUtil.getJson(context, "china_city_data2.json")
        mList = JSONUtil.instance.jsonToList(ss) as ArrayList<MutableMap<String, Any>>

    }

    override fun onCreateView(): View {

        val view = View.inflate(context, R.layout.dialog_address_select, null)
        mProvince = view.findViewById(R.id.one_wheelview)
        mCity = view.findViewById(R.id.two_wheelview)
        mArea = view.findViewById(R.id.three_wheelview)

        mSureTv = view.findViewById(R.id.sure_tv)
        mTitleTv = view.findViewById(R.id.title_tv)
        mCancleTv = view.findViewById(R.id.cancel_tv)

        mProvince!!.setWheelAdapter(AddressWheelAdapter(mContext))
        mProvince!!.skin = WheelView.Skin.Holo
        mProvince!!.setWheelData(mList)
        val style = WheelView.WheelViewStyle()
        style.selectedTextColor = Color.parseColor("#0288ce")
        //style.holoBorderColor = Color.RED;
        style.textColor = Color.GRAY
        mProvince!!.style = style
        mProvince!!.setWheelSize(5)

        mCity!!.setWheelAdapter(AddressWheelAdapter(mContext))
        mCity!!.skin = WheelView.Skin.Holo
        mCity!!.setWheelData(getCityData(0))
        mCity!!.style = style
        mCity!!.setWheelSize(5)

        mArea!!.visibility = View.GONE

        /*  mArea.setWheelAdapter(new MyWheelAdapter(context));
        mArea.setSkin(WheelView.Skin.Holo);
        mArea.setWheelData(getAreaData(0,0));
        mArea.setStyle(style);
        mArea.setWheelSize(5);*/

        return view
    }

    override fun setUiBeforShow() {
        mTitleTv!!.text = mTitleStr
        mSureTv!!.setOnClickListener {
            if (selectBackListener != null) {

                if (selectAddress) {
                    selectBackListener!!.onSelectBackListener(resultMap, mType)
                    dismiss()

                } else {
                    TipsToast.showToastMsg("我还在转，不要慌")
                }
            }
        }

        mCancleTv!!.setOnClickListener { dismiss() }

        mProvince!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<MutableMap<String,Any>> {
            override fun onItemSelected(position: Int, o: MutableMap<String,Any>) {
                mCity!!.setWheelData(getCityData(position))

                // int mCityPosition = mCity.getCurrentPosition();
                // mArea.setWheelData(getAreaData(position,mCityPosition));
            }
        })
        mCity!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<MutableMap<String,Any>> {
            override fun onItemSelected(position: Int, o: MutableMap<String,Any>) {
                val mCityPosition = mProvince!!.currentPosition
                //mArea.setWheelData(getAreaData(mCityPosition,position));
            }
        })
        mArea!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<MutableMap<String,Any>> {
            override fun onItemSelected(position: Int, o: MutableMap<String,Any>) {}
        })


        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = SlideBottomEnter()
        bas_in.duration(200)
        bas_out = SlideBottomExit()
        bas_out.duration(300)

        widthScale(1f)
        dimEnabled(true)
        // baseDialog.heightScale(1f);
        showAnim(bas_in)
        dismissAnim(bas_out)//
    }


    private fun getCityData(position: Int): List<MutableMap<String, Any>>? {

        return mList[position]["children"] as List<MutableMap<String, Any>>?
    }
    /* private List<Map<String,Object>> getAreaData(int i,int position){
        Map<String,Object> mCityMap = (Map<String,Object>)mList.get(i);
        List<Map<String,Object>> mCityList = (List<Map<String,Object>>) mCityMap.get("cityList");

        Map<String,Object> mAreaMap = (Map<String,Object>)mCityList.get(position);
        List<Map<String,Object>> mAreaList = (List<Map<String,Object>>) mAreaMap.get("cityList");

        return mAreaList;
    }*/

}
