package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.utils.tool.UtilTools
import com.wx.wheelview.adapter.ArrayWheelAdapter
import com.wx.wheelview.adapter.BaseWheelAdapter
import com.wx.wheelview.widget.WheelView

import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap

class DateSelectDialog : BaseDialog {


    private var mCurrentYear: Int = 0

    private var mYear: WheelView<String>? = null
    private var mMonth: WheelView<String>? = null
    private var mDay: WheelView<String>? = null

    private var mCancleTv: TextView? = null
    private var mTitleTv: TextView? = null
    private var mSureTv: TextView? = null

    override var mContext: Context
    private var mTitleStr = ""
    private var mSelectTime = ""

    private var mYearStr = ""
    private var mMonthStr = ""
    private var mDayStr = ""

    private var mType = 0
    var selectBackListener: SelectBackListener? = null


    constructor(context: Context) : super(context) {
        mContext = context
    }

    //type  2000  2001（从业工作时间） 已经被占用，其他界面请选择其他数字
    constructor(context: Context, isStyle: Boolean, title: String, type: Int) : super(context, isStyle) {
        mContext = context
        mTitleStr = title
        mType = type
    }


    override fun onCreateView(): View {

        val calendar = Calendar.getInstance()
        //年
        val year = calendar.get(Calendar.YEAR)
        //月
        val month = calendar.get(Calendar.MONTH)//如果需要用的话需要  calendar.get(Calendar.MONTH)+1
        //日
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        //获取系统时间
        //小时
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        //分钟
        val minute = calendar.get(Calendar.MINUTE)
        //秒
        val second = calendar.get(Calendar.SECOND)

        val view = View.inflate(context, R.layout.dialog_time_select, null)
        mYear = view.findViewById(R.id.one_wheelview)
        mMonth = view.findViewById(R.id.two_wheelview)
        mDay = view.findViewById(R.id.three_wheelview)

        mSureTv = view.findViewById(R.id.sure_tv)
        mTitleTv = view.findViewById(R.id.title_tv)
        mCancleTv = view.findViewById(R.id.cancel_tv)


        val mYearsList = createYear()

        mYear!!.setWheelAdapter(ArrayWheelAdapter(mContext) as BaseWheelAdapter<String>)
        mYear!!.skin = WheelView.Skin.Holo
        mYear!!.setWheelData(mYearsList)
        val style = WheelView.WheelViewStyle()
        style.selectedTextColor = Color.parseColor("#0288ce")
        style.textColor = Color.GRAY
        style.selectedTextSize = 20
        mYear!!.style = style

        var index = mYear!!.wheelCount - 1
        for (i in mYearsList.indices) {
            val ss = mYearsList[i]
            if (ss == mCurrentYear.toString() + "") {
                index = i
                break
            }
        }
        mYear!!.setWheelSize(5)
        mYear!!.selection = index
        mYear!!.setExtraText("年", Color.parseColor("#0288ce"), UtilTools.sp2px(mContext, 15), UtilTools.dip2px(mContext, 35))

        mMonth!!.setWheelAdapter(ArrayWheelAdapter(mContext) as BaseWheelAdapter<String>)
        mMonth!!.skin = WheelView.Skin.Holo
        mMonth!!.setWheelData(createMonth())
        mMonth!!.style = style
        mMonth!!.setWheelSize(5)
        mMonth!!.selection = month
        mMonth!!.setExtraText("月", Color.parseColor("#0288ce"), UtilTools.sp2px(mContext, 15), UtilTools.dip2px(mContext, 20))

        mDay!!.setWheelAdapter(ArrayWheelAdapter(mContext) as  BaseWheelAdapter<String>)
        mDay!!.skin = WheelView.Skin.Holo
        mDay!!.setWheelData(createDay(2016, 5))
        mDay!!.style = style
        mDay!!.setWheelSize(5)
        mDay!!.selection = day - 1
        mDay!!.setExtraText("日", Color.parseColor("#0288ce"), UtilTools.sp2px(mContext, 15), UtilTools.dip2px(mContext, 20))

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

        mYear!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<String> {
            override fun onItemSelected(position: Int, o: String) {
                val ss = mMonth!!.selectionItem.toString() + ""
                mDay!!.setWheelData(createDay(Integer.valueOf(o.toString() + ""), Integer.valueOf(ss)))
                getSelectTime()
            }
        })
        mMonth!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<String> {
            override fun onItemSelected(position: Int, o: String) {
                val ss = mYear!!.selectionItem.toString() + ""
                mDay!!.setWheelData(createDay(Integer.valueOf(ss), Integer.valueOf(o.toString() + "")))
                getSelectTime()
            }
        })
        mDay!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<String> {
            override fun onItemSelected(position: Int, o: String) {
                getSelectTime()
            }
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
        val ss1 = mYear!!.selectionItem.toString() + ""
        val ss2 = mMonth!!.selectionItem.toString() + ""
        val ss3 = mDay!!.selectionItem.toString() + ""
        mYearStr = ss1
        mMonthStr = ss2
        mDayStr = ss3
        mSelectTime = ss1 + ss2 + ss3
    }


    private fun createYear(): ArrayList<String> {
        val list = ArrayList<String>()

        val c = Calendar.getInstance()
        val nowYear = c.get(Calendar.YEAR)
        mCurrentYear = nowYear
        var startYear = nowYear - 2
        if (mType == 2000) {
            startYear = nowYear - 40
        } else if (mType == 2001) {
            startYear = nowYear - 20
        }

        val nextYear = nowYear + 2
        for (i in startYear..nextYear) {
            list.add("" + i)
        }
        return list
    }

    private fun createMonth(): MutableList<String>? {
        val list = ArrayList<String>()
        for (i in 1..12) {
            if (i < 10) {
                list.add("0$i")
            } else {
                list.add("" + i)
            }
        }
        return list
    }

    private fun createDay(mYear: Int, mMonth: Int): ArrayList<String> {

        val day = getDay(mYear, mMonth)
        val list = ArrayList<String>()
        for (i in 1..day) {
            if (i < 10) {
                list.add("0$i")
            } else {
                list.add("" + i)
            }
        }
        return list
    }


    /**
     * @param year
     * @param month
     * @return
     */
    private fun getDay(year: Int, month: Int): Int {
        var day = 30
        var flag = false
        when (year % 4) {
            0 -> flag = true
            else -> flag = false
        }
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> day = 31
            2 -> day = if (flag) 29 else 28
            else -> day = 30
        }
        return day
    }

}
