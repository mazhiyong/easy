package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.MyWheelAdapter
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SelectDataUtil
import com.wx.wheelview.widget.WheelView
import java.util.ArrayList
import java.util.HashMap
class AddressSelectDialog : BaseDialog {


    private var mProvince: WheelView<MutableMap<String,Any>>? = null
    private var mCity: WheelView<MutableMap<String,Any>>? = null
    private var mArea: WheelView<MutableMap<String,Any>>? = null


    private var mCancleTv: TextView? = null
    private var mTitleTv: TextView? = null
    private var mSureTv: TextView? = null

    internal var context: Context? = null

    private var mTitleStr = ""
    private var mSelectTime = ""

    private var mYearStr = ""
    private var mMonthStr = ""
    private var mDayStr = ""

    private var mType = 0
    var selectBackListener: SelectBackListener? = null

    private var mList = ArrayList<MutableMap<String, Any>>()


    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, isStyle: Boolean, title: String, type: Int) : super(context, isStyle) {
        this.context = context
        mTitleStr = title
        mType = type

        val ss = SelectDataUtil.getJson(context, "china_city_data.json")
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

        mProvince!!.setWheelAdapter(MyWheelAdapter(context!!))
        mProvince!!.skin = WheelView.Skin.Holo
        mProvince!!.setWheelData(mList)
        val style = WheelView.WheelViewStyle()
        style.selectedTextColor = Color.parseColor("#0288ce")
        //style.holoBorderColor = Color.RED;
        style.textColor = Color.GRAY
        mProvince!!.style = style
        mProvince!!.setWheelSize(5)

        mCity!!.setWheelAdapter(MyWheelAdapter(context!!))
        mCity!!.skin = WheelView.Skin.Holo
        mCity!!.setWheelData(getCityData(0))
        mCity!!.style = style
        mCity!!.setWheelSize(5)

        mArea!!.setWheelAdapter(MyWheelAdapter(context!!))
        mArea!!.skin = WheelView.Skin.Holo
        mArea!!.setWheelData(getAreaData(0, 0))
        mArea!!.style = style
        mArea!!.setWheelSize(5)

        return view
    }

    override fun setUiBeforShow() {
        mTitleTv!!.text = mTitleStr
        mSureTv!!.setOnClickListener {
            if (selectBackListener != null) {

                val map = HashMap<String, Any>()
                map["year"] = mYearStr
                map["month"] = mMonthStr
                map["day"] = mDayStr
                map["date"] = mSelectTime
                selectBackListener!!.onSelectBackListener(map, mType)
            }
            dismiss()
        }

        mCancleTv!!.setOnClickListener { dismiss() }

        mProvince!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<MutableMap<String, Any>> {
            override fun onItemSelected(position: Int, t: MutableMap<String, Any>?) {
                mCity!!.setWheelData(getCityData(position))
                val mCityPosition = mCity!!.currentPosition
                mArea!!.setWheelData(getAreaData(position, mCityPosition))
            }
        })
        mCity!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<MutableMap<String, Any>> {
            override fun onItemSelected(position: Int, t: MutableMap<String, Any>?) {
                val mCityPosition = mProvince!!.currentPosition
                mArea!!.setWheelData(getAreaData(mCityPosition, position))
            }
        })
        mArea!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<MutableMap<String,Any>> {
            override fun onItemSelected(position: Int, t: MutableMap<String, Any>?) {}
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
        dismissAnim(bas_out)
    }


    private fun getSelectTime() {
        val ss1 = mProvince!!.selectionItem.toString() + ""
        val ss2 = mCity!!.selectionItem.toString() + ""
        val ss3 = mArea!!.selectionItem.toString() + ""
        mYearStr = ss1
        mMonthStr = ss2
        mDayStr = ss3
        mSelectTime = ss1 + ss2 + ss3
    }


    private fun getCityData(position: Int): List<MutableMap<String, Any>>? {

        return mList[position]["cityList"] as List<MutableMap<String, Any>>?
    }

    private fun getAreaData(i: Int, position: Int): List<MutableMap<String, Any>>? {
        val mCityList = mList[i]["cityList"] as List<MutableMap<String, Any>>?

        return mCityList!![position]["cityList"] as List<MutableMap<String, Any>>?
    }

}
