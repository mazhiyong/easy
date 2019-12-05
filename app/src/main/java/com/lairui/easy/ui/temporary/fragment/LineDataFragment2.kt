package com.lairui.easy.ui.temporary.fragment

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView

import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.mywidget.line.DetailsMarkerView
import com.lairui.easy.mywidget.line.MyLineChart
import com.lairui.easy.mywidget.line.PositionMarker
import com.lairui.easy.mywidget.line.RoundMarker
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.MyLineChartView
import com.lairui.easy.ui.temporary.activity.MyShouMoneyActivity
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.Collections
import java.util.Date
import java.util.HashMap

import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick

/**
 * 折线统计 应收账款
 */
class LineDataFragment2 : BasicFragment(), RequestView, ReLoadingData, SelectBackListener {

    @BindView(R.id.linechaer_view)
    lateinit var mLinechaerView: MyLineChartView
    @BindView(R.id.start_time_value_tv)
    lateinit var mStartTimeValueTv: TextView
    @BindView(R.id.end_time_value_tv)
    lateinit var mEndTimeValueTv: TextView
    @BindView(R.id.query_bt)
    lateinit var mQueryBt: TextView
    @BindView(R.id.chart)
    lateinit var mLineChart: MyLineChart
    @BindView(R.id.chart2)
    lateinit var mLineChart2: MyLineChart
    @BindView(R.id.scroll_view)
    lateinit var mScrollView: ScrollView
    @BindView(R.id.rb_left)
    lateinit var mRbLeft: RadioButton
    @BindView(R.id.rb_right)
    lateinit var mRbRight: RadioButton
    @BindView(R.id.radio_group)
    lateinit var mRadioGroup: RadioGroup
    @BindView(R.id.date_lay)
    lateinit var mDateLay: LinearLayout
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
        get() = R.layout.fragment_linedata2


    private var mShowType = "1"


    override fun init() {
        mLoadingWindow = activity?.let { LoadingWindow(it, R.style.Dialog) }!!
        setBarTextColor()
        mLineChart!!.visibility = View.VISIBLE
        mLineChart2!!.visibility = View.GONE
        mLineChart!!.setNoDataText("暂无数据")
        mLineChart2!!.setNoDataText("暂无数据")
        val paint = mLineChart!!.getPaint(Chart.PAINT_INFO)
        paint.textSize = 60f
        paint.color = ContextCompat.getColor(activity!!, R.color.black_middle)
        val paint2 = mLineChart2!!.getPaint(Chart.PAINT_INFO)
        paint2.textSize = 60f
        paint2.color = ContextCompat.getColor(activity!!, R.color.black_middle)

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
        map["enddate"] = mEndTime  //结束时间
        map["qrytype"] = mShowType  //结束时间
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


        // xy轴集合自己添加数据
        mLinechaerView!!.setXValues(xValues as ArrayList<String>)
        mLinechaerView!!.setYValues(yValues as ArrayList<Float>)

        mLinechaerView!!.updateUI()


        if (mShowType == "1") {
            initLine(list)
        } else {
            initLine2(list)
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        mLoadingWindow!!.cancleView()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        mLoadingWindow!!.showView()
        getShouMoneyInfoLine()
    }

    @OnClick(R.id.start_time_value_tv, R.id.end_time_value_tv, R.id.query_bt, R.id.rb_left, R.id.rb_right)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.rb_left -> {
                mShowType = "1"
                mDateLay!!.visibility = View.VISIBLE
                mLineChart!!.visibility = View.VISIBLE
                mLineChart2!!.visibility = View.GONE
                getShouMoneyInfoLine()
            }
            R.id.rb_right -> {
                mShowType = "2"
                mDateLay!!.visibility = View.GONE
                mLineChart!!.visibility = View.GONE
                mLineChart2!!.visibility = View.VISIBLE
                getShouMoneyInfoLine()
            }
            R.id.start_time_value_tv -> mySelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.end_time_value_tv -> mySelectDialog2!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.query_bt -> {

                val start = mStartTimeValueTv!!.text.toString().trim { it <= ' ' }
                val end = mEndTimeValueTv!!.text.toString().trim { it <= ' ' }

                mStartTime = UtilTools.getStringFromSting2(start, "yyyy-MM-dd", "yyyyMMdd")
                mEndTime = UtilTools.getStringFromSting2(end, "yyyy-MM-dd", "yyyyMMdd")

                /* long day = UtilTools.dateDiff(start, end, "yyyy-MM-dd");

                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                long time = 0;
                long endTime = 0;

                Date date = new Date();
                try {
                    time = sd.parse(mStartTime).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (day < 6) {
                    ((MyShouMoneyActivity) getActivity()).showToastMsg("查询时间周期不能少于7日");
                    endTime = time + 6 * 24 * 60 * 60 * 1000;
                    date.setTime(endTime);
                    mEndTime = sd.format(date);
                }

                if (day > 14) {
                    ((MyShouMoneyActivity) getActivity()).showToastMsg("查询时间周期不能多于15日");
                    endTime = time + 14 * 24 * 60 * 60 * 1000;
                    date.setTime(endTime);
                    mEndTime = sd.format(date);

                }*/

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

    private fun initData1() {

    }

    private fun initData2() {


    }


    private fun initLine(mDataList: List<MutableMap<String, Any>>) {
        mLineChart!!.animateXY(1500, 0)
        //1.设置x轴和y轴的点
        val entries = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()
        for (i in mDataList.indices) {
            val map = mDataList[i]
            entries.add(Entry(i.toFloat(), UtilTools.fenToWanYuan(map["yszkcsw"]!!.toString() + "")))
            entries2.add(Entry(i.toFloat(), UtilTools.fenToWanYuan(map["yjcsw"]!!.toString() + "")))
        }

        //2.把数据赋值到你的线条
        val dataSet = LineDataSet(entries, "1") // add entries to dataset
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER//设置折线图显示模式

        dataSet.setDrawCircles(true)//设置是否画圆
        dataSet.setDrawCircleHole(false)//设置是否空心圆
        dataSet.color = Color.parseColor("#7d7d7d")//线条颜色
        dataSet.color = ContextCompat.getColor(activity!!, R.color.line_color)//线条颜色
        dataSet.setCircleColor(ContextCompat.getColor(activity!!, R.color.line_color))//圆点颜色
        dataSet.lineWidth = 1f//线条宽度

        val dataSet2 = LineDataSet(entries2, "2") // add entries to dataset
        dataSet2.mode = LineDataSet.Mode.HORIZONTAL_BEZIER//设置折线图显示模式
        dataSet2.setDrawCircles(true)//设置是否画圆
        dataSet2.setDrawCircleHole(false)//设置是否空心圆
        dataSet2.color = ContextCompat.getColor(activity!!, R.color.line_color2)//线条颜色
        dataSet2.setCircleColor(ContextCompat.getColor(activity!!, R.color.line_color2))//圆点颜色
        dataSet2.lineWidth = 1f//线条宽度


        mLineChart!!.setScaleEnabled(true)//设置是否可以缩放
        mLineChart!!.isScaleXEnabled = true//设置x轴可以缩放
        mLineChart!!.isScaleYEnabled = false//设置y轴不可以缩放
        if (mDataList.size > 60) {
            //            mLineChart2.zoomToCenter(3f, 1f);
            mLineChart!!.zoom(3f, 1f, 0f, 0f)
            //mLineChart2.setScaleMinima(3f, 1f);
        } else if (mDataList.size > 30) {
            //            mLineChart2.zoomToCenter(1.6f, 1f);
            mLineChart!!.zoom(1.6f, 1f, 0f, 0f)

            //mLineChart2.setScaleMinima(1.6f, 1f);
        } else if (mDataList.size > 15) {
            //            mLineChart2.zoomToCenter(1.3f, 1f);
            mLineChart!!.zoom(1.3f, 1f, 0f, 0f)
            //mLineChart2.setScaleMinima(1.3f, 1f);
        }

        //         mLineChart2.getViewPortHandler().setMaximumScaleX(5);
        mLineChart!!.viewPortHandler.setMaximumScaleY(1f)

        //mLineChart2.setGridBackgroundColor(R.color.colorAccent);//设置网格背景应与绘制的颜色。
        //mLineChart2.setDrawBorders(true);//启用/禁用绘制图表边框（chart周围的线）。
        //mLineChart2.setBorderColor(R.color.colorAccent);//设置chart边框线的颜色。


        //mLineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        //mLineChart2.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

        //mLineChart2.getLineData().getDataSets().get(0).setVisible(true);
        //设置样式
        val rightAxis = mLineChart!!.axisRight
        //设置图表右边的y轴禁用
        rightAxis.isEnabled = false//设置y右边禁用
        rightAxis.axisMaximum = dataSet.yMax * 2

        val leftAxis = mLineChart!!.axisLeft
        leftAxis.axisMinimum = 0f
        //是否绘制0所在的网格线
        leftAxis.setDrawZeroLine(false)
        //        leftAxis.setSpaceBottom(10f);
        //设置图表左边的y轴禁用
        leftAxis.isEnabled = true
        leftAxis.axisMaximum = dataSet.yMax * 1.4f
        leftAxis.axisMinimum = 0f
        if (dataSet.yMin < 0) {
            leftAxis.axisMinimum = dataSet.yMin * 1.4f
        }

        //设置x轴
        val xAxis = mLineChart!!.xAxis
        xAxis.textColor = Color.parseColor("#333333")
        xAxis.textSize = 11f
        xAxis.gridColor = R.color.colorPrimary
        xAxis.granularity = 0.1f//设置坐标轴间隔大小
        xAxis.axisMinimum = 0f
        xAxis.setDrawAxisLine(true)//是否绘制轴线
        xAxis.setDrawGridLines(true)//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true)//绘制标签  指x轴上的对应数值
        xAxis.position = XAxis.XAxisPosition.BOTTOM//设置x轴的显示位置
        xAxis.granularity = 1f//禁止放大x轴标签重绘
        xAxis.setAvoidFirstLastClipping(true)
        // 标签倾斜
        //xAxis.setLabelRotationAngle(45);
        val list = ArrayList<String>()


        for (map in mDataList) {
            val s = UtilTools.getStringFromSting2(map["acctime"]!!.toString() + "", "yyyyMMdd", "MM/dd")
            list.add(s)
        }

        xAxis.valueFormatter = IndexAxisValueFormatter(list)

        //透明化图例
        val legend = mLineChart!!.legend
        legend.form = Legend.LegendForm.NONE
        legend.textColor = Color.WHITE
        //legend.setYOffset(-2);

        //点击图表坐标监听
        mLineChart!!.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                //查看覆盖物是否被回收
                if (mLineChart!!.isMarkerAllNull) {
                    //重新绑定覆盖物
                    createMakerView(list)
                    //并且手动高亮覆盖物
                    //mLineChart.highlightValue(h);
                }
            }

            override fun onNothingSelected() {

            }
        })

        //隐藏x轴描述
        val description = Description()
        description.isEnabled = false
        mLineChart!!.description = description

        //创建覆盖物
        createMakerView(list)

        //3.chart设置数据
        val lineData = LineData(dataSet, dataSet2)
        //是否绘制线条上的文字
        lineData.setDrawValues(false)
        mLineChart!!.data = lineData
        mLineChart!!.invalidate()

    }

    private fun initLine2(mDataList: List<MutableMap<String, Any>>) {

        mLineChart2!!.animateXY(1500, 0)
        //1.设置x轴和y轴的点
        val entries = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()
        for (i in mDataList.indices) {
            val map = mDataList[i]
            entries.add(Entry(i.toFloat(), UtilTools.fenToWanYuan(map["yszkcsw"]!!.toString() + "")))
            entries2.add(Entry(i.toFloat(), UtilTools.fenToWanYuan(map["yjcsw"]!!.toString() + "")))
        }


        //2.把数据赋值到你的线条
        val dataSet = LineDataSet(entries, "2222") // add entries to dataset
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER//设置折线图显示模式

        dataSet.setDrawCircles(true)//设置是否画圆
        dataSet.setDrawCircleHole(false)//设置是否空心圆
        dataSet.color = Color.parseColor("#7d7d7d")//线条颜色
        dataSet.color = ContextCompat.getColor(activity!!, R.color.line_color)//线条颜色
        dataSet.setCircleColor(ContextCompat.getColor(activity!!, R.color.line_color))//圆点颜色
        dataSet.lineWidth = 1f//线条宽度

        val dataSet2 = LineDataSet(entries2, "2") // add entries to dataset
        dataSet2.mode = LineDataSet.Mode.HORIZONTAL_BEZIER//设置折线图显示模式
        dataSet2.setDrawCircles(true)//设置是否画圆
        dataSet2.setDrawCircleHole(false)//设置是否空心圆
        dataSet2.color = ContextCompat.getColor(activity!!, R.color.line_color2)//线条颜色
        dataSet2.setCircleColor(ContextCompat.getColor(activity!!, R.color.line_color2))//圆点颜色
        dataSet2.lineWidth = 1f//线条宽度


        mLineChart2!!.setScaleEnabled(true)//设置是否可以缩放
        mLineChart2!!.isScaleXEnabled = true//设置x轴可以缩放
        mLineChart2!!.isScaleYEnabled = false//设置y轴不可以缩放
        if (mDataList.size > 60) {
            //            mLineChart2.zoomToCenter(3f, 1f);
            mLineChart2!!.zoom(3f, 1f, 0f, 0f)
            //mLineChart2.setScaleMinima(3f, 1f);
        } else if (mDataList.size > 30) {
            //            mLineChart2.zoomToCenter(1.6f, 1f);
            mLineChart2!!.zoom(1.6f, 1f, 0f, 0f)

            //mLineChart2.setScaleMinima(1.6f, 1f);
        } else if (mDataList.size > 15) {
            //            mLineChart2.zoomToCenter(1.3f, 1f);
            mLineChart2!!.zoom(1.3f, 1f, 0f, 0f)
            //mLineChart2.setScaleMinima(1.3f, 1f);
        }

        //         mLineChart2.getViewPortHandler().setMaximumScaleX(5);
        mLineChart2!!.viewPortHandler.setMaximumScaleY(1f)

        //mLineChart2.setGridBackgroundColor(R.color.colorAccent);//设置网格背景应与绘制的颜色。
        //mLineChart2.setDrawBorders(true);//启用/禁用绘制图表边框（chart周围的线）。
        //mLineChart2.setBorderColor(R.color.colorAccent);//设置chart边框线的颜色。


        // mLineChart2.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        //mLineChart2.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

        //mLineChart2.getLineData().getDataSets().get(0).setVisible(true);
        //设置样式
        val rightAxis = mLineChart2!!.axisRight
        //设置图表右边的y轴禁用
        rightAxis.isEnabled = false//设置y右边禁用
        rightAxis.axisMaximum = dataSet.yMax * 2

        val leftAxis = mLineChart2!!.axisLeft
        //     // 设置y轴数据偏移量
        //        leftAxis.setXOffset(30);
        //        leftAxis.setYOffset(-3);
        leftAxis.axisMaximum = dataSet.yMax * 1.4f
        leftAxis.axisMinimum = 0f
        if (dataSet.yMin < 0) {
            leftAxis.axisMinimum = dataSet.yMin * 1.4f
        }

        leftAxis.spaceBottom = 10f
        //设置图表左边的y轴禁用
        leftAxis.isEnabled = true

        //设置x轴
        val xAxis = mLineChart2!!.xAxis
        xAxis.textColor = Color.parseColor("#333333")
        xAxis.textSize = 11f
        xAxis.gridColor = R.color.colorPrimary
        xAxis.granularity = 0.1f//设置坐标轴间隔大小
        xAxis.axisMinimum = 0f
        xAxis.setDrawAxisLine(true)//是否绘制轴线
        xAxis.setDrawGridLines(true)//设置x轴上每个点对应的线
        //        xAxis.setGridColor(ContextCompat.getColor(getActivity(),R.color.divide_line));
        //        xAxis.setGridLineWidth(1f);
        xAxis.axisLineColor = ContextCompat.getColor(activity!!, R.color.yellow)
        xAxis.setDrawLabels(true)//绘制标签  指x轴上的对应数值
        xAxis.position = XAxis.XAxisPosition.BOTTOM//设置x轴的显示位置
        xAxis.granularity = 1f//禁止放大x轴标签重绘
        // 标签倾斜
        //xAxis.setLabelRotationAngle(45);
        val list = ArrayList<String>()


        for (map in mDataList) {
            val dateStr = map["acctime"]!!.toString() + ""
            if (dateStr.endsWith("01")) {
                val s = UtilTools.getStringFromSting2(map["acctime"]!!.toString() + "", "yyyyMMdd", "MM")
                list.add(s + "月")
            } else {
                val s = UtilTools.getStringFromSting2(map["acctime"]!!.toString() + "", "yyyyMMdd", "MM/dd")
                list.add(s)
            }
        }

        xAxis.valueFormatter = IndexAxisValueFormatter(list)

        //透明化图例
        val legend = mLineChart2!!.legend
        legend.form = Legend.LegendForm.NONE
        legend.textColor = Color.WHITE
        //legend.setYOffset(-2);

        //点击图表坐标监听
        mLineChart2!!.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                //查看覆盖物是否被回收
                if (mLineChart2!!.isMarkerAllNull) {
                    //重新绑定覆盖物
                    createMakerView2(list)
                    //并且手动高亮覆盖物
                    mLineChart2!!.highlightValue(h)
                }
            }

            override fun onNothingSelected() {

            }
        })

        //隐藏x轴描述
        val description = Description()
        description.isEnabled = false
        mLineChart2!!.description = description

        //创建覆盖物
        createMakerView2(list)

        //3.chart设置数据
        val lineData = LineData(dataSet, dataSet2)
        //是否绘制线条上的文字
        lineData.setDrawValues(false)
        mLineChart2!!.data = lineData


        //一个页面显示的数据太多了，都不看清楚，怎么样设置一个页面显示固定条数的数据，如果数据太多需要手动滑动看到
        //        mLineChart2.setVisibleXRangeMaximum(7.5f);

        mLineChart2!!.invalidate() // refresh

    }

    /**
     * 创建覆盖物
     */
    fun createMakerView(list: List<String>) {
        val detailsMarkerView = activity?.let { DetailsMarkerView(it) }
        detailsMarkerView!!.list = list
        detailsMarkerView!!.chartView = mLineChart
        detailsMarkerView?.let { mLineChart!!.setDetailsMarkerView(it) }
        activity?.let { PositionMarker(it) }?.let { mLineChart!!.setPositionMarker(it) }
        activity?.let { RoundMarker(it) }?.let { mLineChart!!.setRoundMarker(it) }
    }

    /**
     * 创建覆盖物
     */
    fun createMakerView2(list: List<String>) {
        val detailsMarkerView = activity?.let { DetailsMarkerView(it) }
        detailsMarkerView?.list = list
        detailsMarkerView?.chartView = mLineChart2
        detailsMarkerView?.let { mLineChart2!!.setDetailsMarkerView(it) }
        activity?.let { PositionMarker(it) }?.let { mLineChart2!!.setPositionMarker(it) }
        activity?.let { RoundMarker(it) }?.let { mLineChart2!!.setRoundMarker(it) }
    }

}
