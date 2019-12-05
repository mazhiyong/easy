package com.lairui.easy.ui.temporary.fragment

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.MyLineChartView
import com.lairui.easy.ui.temporary.activity.MyShouMoneyActivity
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Collections
import java.util.Date
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 折线统计 应收账款
 */
class LineDataFragment : BasicFragment(), RequestView, ReLoadingData, SelectBackListener {

    @BindView(R.id.linechaer_view)
    lateinit var mLinechaerView: MyLineChartView
    @BindView(R.id.start_time_value_tv)
    lateinit var mStartTimeValueTv: TextView
    @BindView(R.id.end_time_value_tv)
    lateinit var mEndTimeValueTv: TextView
    @BindView(R.id.query_bt)
    lateinit var mQueryBt: Button
    @BindView(R.id.date_tv)
    lateinit var mDateTv: TextView
    private lateinit var mLoadingWindow: LoadingWindow
    private var mRequestTag = ""
    private lateinit var mySelectDialog: DateSelectDialog
    private lateinit var mySelectDialog2: DateSelectDialog

    private var mStartTime = ""
    private var mEndTime = ""

    private var mSelectStartTime = ""
    private var mSelectEndTime = ""
    private val mSelectType = ""


    private lateinit var xValues: MutableList<String>   //x轴数据集合
    private lateinit var yValues: MutableList<Float>  //y轴数据集合

    override val layoutId: Int
        get() = R.layout.fragment_linedata


    override fun init() {
        mLoadingWindow = activity?.let { LoadingWindow(it, R.style.Dialog) }!!
        setBarTextColor()
        initData()
    }

    //查询应收账款池列表信息
    fun getShouMoneyInfoLine() {
        val mapKehu = (activity as MyShouMoneyActivity).selectKehuMap
        if (mapKehu == null) {
            (activity as MyShouMoneyActivity).showToastMsg("请选择付款方")
            mLoadingWindow!!.cancleView()
            return
        }
        mRequestTag = MethodUrl.shoumoneyLine
        val map = HashMap<String, String>()
        map["payfirmname"] = mapKehu["payfirmname"]!!.toString() + "" //购买方名称
        map["begindate"] = mStartTime //开始时间
        map["autoid"] = mEndTime  //结束时间
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.shoumoneyLine, map)
    }


    private fun initData() {

        mySelectDialog = activity?.let { DateSelectDialog(it, true, "选择日期", 21) }!!
        mySelectDialog!!.selectBackListener = this
        mySelectDialog2 = activity?.let { DateSelectDialog(it, true, "选择日期", 22) }!!
        mySelectDialog2!!.selectBackListener = this


        //默认查询最近一周
        val sTime = UtilTools.getWeekAgo(Date(), -6)
        val eTime = UtilTools.getStringFromDate(Date(), "yyyyMMdd")

        mSelectStartTime = sTime
        mSelectEndTime = eTime
        mStartTime = mSelectStartTime
        mEndTime = mSelectEndTime

        mStartTimeValueTv!!.text = UtilTools.getStringFromSting2(mStartTime, "yyyyMMdd", "yyyy-MM-dd")
        mEndTimeValueTv!!.text = UtilTools.getStringFromSting2(mEndTime, "yyyyMMdd", "yyyy-MM-dd")

        xValues = ArrayList()
        yValues = ArrayList()
        for (i in 0..6) {
            xValues!!.add(UtilTools.dateTypeTo(UtilTools.getWeekAgo(Date(), -1 * i)))
            yValues!!.add(0f)
        }
        Collections.reverse(xValues)

        mDateTv!!.text = xValues!![0] + "至" + xValues!![xValues!!.size - 1] + "应收账款走势图"
        mLinechaerView!!.setXValues(xValues as ArrayList<String>)
        mLinechaerView!!.setYValues(yValues as ArrayList<Float>)

    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow!!.cancleView()
        val intent: Intent
        when (mType) {
            MethodUrl.shoumoneyLine -> {
                val list = tData["pondinfoList"] as List<MutableMap<String, Any>>?
                if (list != null && list.size > 0) {
                    responseData(list)
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.shoumoneyLine -> getShouMoneyInfoLine()
                }
            }
        }
    }

    private fun responseData(list: List<MutableMap<String, Any>>) {
        xValues = ArrayList()
        yValues = ArrayList()

        for (map in list) {
            xValues!!.add(UtilTools.dateTypeTo(map["acctime"]!!.toString() + ""))
            yValues!!.add(UtilTools.fenToWanYuan(map["yszkcsw"]!!.toString() + ""))
        }

        mDateTv!!.text = xValues!![0] + "至" + xValues!![xValues!!.size - 1] + "应收账款走势图"

        // xy轴集合自己添加数据
        mLinechaerView!!.setXValues(xValues as ArrayList<String>)
        mLinechaerView!!.setYValues(yValues as ArrayList<Float>)

        mLinechaerView!!.updateUI()

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        mLoadingWindow!!.cancleView()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        mLoadingWindow!!.showView()
        getShouMoneyInfoLine()
    }

    @OnClick(R.id.start_time_value_tv, R.id.end_time_value_tv, R.id.query_bt)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.start_time_value_tv -> mySelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.end_time_value_tv -> mySelectDialog2!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.query_bt -> {

                val start = mStartTimeValueTv!!.text.toString().trim { it <= ' ' }
                val end = mEndTimeValueTv!!.text.toString().trim { it <= ' ' }

                mStartTime = UtilTools.getStringFromSting2(start, "yyyy-MM-dd", "yyyyMMdd")
                mEndTime = UtilTools.getStringFromSting2(end, "yyyy-MM-dd", "yyyyMMdd")

                val day = UtilTools.dateDiff(start, end, "yyyy-MM-dd")

                val sd = SimpleDateFormat("yyyyMMdd")
                var time: Long = 0
                var endTime: Long = 0

                val date = Date()
                try {
                    time = sd.parse(mStartTime).time
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                if (day < 6) {
                    (activity as MyShouMoneyActivity).showToastMsg("查询时间周期不能少于7日")
                    endTime = time + 6 * 24 * 60 * 60 * 1000
                    date.time = endTime
                    mEndTime = sd.format(date)
                }

                if (day > 14) {
                    (activity as MyShouMoneyActivity).showToastMsg("查询时间周期不能多于15日")
                    endTime = time + 14 * 24 * 60 * 60 * 1000
                    date.time = endTime
                    mEndTime = sd.format(date)

                }


                mStartTimeValueTv!!.text = UtilTools.getStringFromSting2(mStartTime, "yyyyMMdd", "yyyy-MM-dd")
                mEndTimeValueTv!!.text = UtilTools.getStringFromSting2(mEndTime, "yyyyMMdd", "yyyy-MM-dd")

                mLoadingWindow!!.showView()
                getShouMoneyInfoLine()
            }
        }
    }


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            21 -> {
                mSelectStartTime = map["date"]!!.toString() + ""
                mStartTimeValueTv!!.text = map["year"].toString() + "-" + map["month"] + "-" + map["day"]
                mStartTime = mSelectStartTime
            }
            22 -> {
                mSelectEndTime = map["date"]!!.toString() + ""
                mEndTimeValueTv!!.text = map["year"].toString() + "-" + map["month"] + "-" + map["day"]
                mEndTime = mSelectEndTime
            }
        }
    }
}
