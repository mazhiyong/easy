package com.lairui.easy.ui.temporary.fragment

import android.animation.Animator
import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView

import com.flyco.dialog.utils.CornerUtils
import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.temporary.activity.MyShouMoneyActivity
import com.lairui.easy.ui.temporary.adapter.ShouMoneyListAdapter
import com.lairui.easy.ui.temporary.adapter.TradeDialogAdapter
import com.lairui.easy.utils.tool.AnimUtil
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.Date
import java.util.HashMap

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

/**
 * 列表统计  应收账款
 */
class ListDataFragment : BasicFragment(), RequestView, ReLoadingData, SelectBackListener {

    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.shuaixuan_lay)
    lateinit var mShuaiXuanLay: LinearLayout
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.date_tv)
    lateinit var mDateTv: TextView

    private lateinit var mLoadingWindow: LoadingWindow
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private lateinit var mAdapter: ShouMoneyListAdapter


    private var mRequestTag = ""

    private val mDataList = ArrayList<MutableMap<String, Any>>()

    //    private String mStartTime="";
    //    private String mEndTime = "";
    private var mJieKuanStatus = ""

    private var mKeyword = ""

    private var mSelectStartTime = ""
    private var mSelectEndTime = ""
    private var mSelectType = ""


    private var mDataType = ""

    private lateinit var mAnimUtil: AnimUtil

    override val layoutId: Int
        get() = R.layout.fragment_listdata


    private lateinit var mTypeRecyclerView: RecyclerView
    private lateinit var mTradeDialogAdapter: TradeDialogAdapter
    private lateinit var mOneWeekTv: TextView
    private lateinit var mOneTv: TextView
    private lateinit var mThreeTv: TextView
    private lateinit var mSetTimeTv: TextView
    private lateinit var mStartTimeTv: TextView
    private lateinit var mEndTimeTv: TextView
    private lateinit var mResetBut: Button
    private lateinit var mSureBut: Button
    private lateinit var mKeywordEt: EditText
    private lateinit var mKeywordLay: LinearLayout
    private lateinit var mLayout: LinearLayout
    private lateinit var mLayout1: LinearLayout
    private lateinit var mySelectDialog: DateSelectDialog
    private lateinit var mySelectDialog2: DateSelectDialog

    private lateinit var popView: View
    private lateinit var mConditionDialog: PopupWindow
    private var bright = false


    override fun init() {
        mLoadingWindow = activity?.let { LoadingWindow(it, R.style.Dialog) }!!
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this

        mAnimUtil = AnimUtil()

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = linearLayoutManager

        mRefreshListView!!.setOnRefreshListener { getShouMoneyInfoList() }


        setBarTextColor()

        //默认查询最近一周

        mDataType = "1"

        val sTime = UtilTools.getWeekAgo(Date(), -6)
        val eTime = UtilTools.getStringFromDate(Date(), "yyyyMMdd")

        mSelectStartTime = sTime
        mSelectEndTime = eTime

        // initData();
        // responseData();
        mLoadingWindow!!.showView()
        getShouMoneyInfoList()

        mDateTv!!.text = UtilTools.dateTypeTo(mSelectStartTime) + " - " + UtilTools.dateTypeTo(mSelectEndTime) + "应收账款"
    }


    //查询应收账款池图示信息
    fun getShouMoneyInfoList() {
        val mapKehu = (activity as MyShouMoneyActivity).selectKehuMap
        if (mapKehu == null) {
            (activity as MyShouMoneyActivity).showToastMsg("请选择付款方")
            mLoadingWindow!!.cancleView()
            return
        }
        mRequestTag = MethodUrl.shoumoneyList
        val map = HashMap<String, String>()
        map["paycustid"] = mapKehu["paycustid"]!!.toString() + "" //付款方客户号
        map["status"] = mJieKuanStatus //应收凭证状态(1正常，2已融资，3已核销，4已到期)
        map["datetype"] = mDataType //付款截止日查询类型（1近一周，2近一个月，3近三个月，4自定义）
        if (mDataType == "4") {
            map["begindate"] = mSelectStartTime //开始时间
            map["enddate"] = mSelectEndTime  //结束时间
        }

        map["keyword"] = mKeyword //关键字
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.shoumoneyList, map)
    }

    /* private void initData() {
        Map<String,Object> map = new HashMap<>();
        map.put("name","深圳市国投啥玩意有限责任公司");
        map.put("number","18218219719");
        map.put("date","2020-12-30");
        map.put("pingzheng_money","10000000");
        map.put("shou_money","800000");
        map.put("should_money","20000");
        map.put("state","正常");
        map.put("person","江苏苏宁银行有限责任公司");

        for (int i = 0; i <10 ; i++) {
            mDataList.add(map);
        }

    }
*/
    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }


    private fun responseData() {
        if (mAdapter == null) {
            mAdapter = ShouMoneyListAdapter(activity!!)
            mAdapter!!.addAll(mDataList)

            val adapter1 = ScaleInAnimationAdapter(mAdapter)
            adapter1.setFirstOnly(false)
            adapter1.setDuration(400)
            adapter1.setInterpolator(OvershootInterpolator(0.8f))


            val adapter = AlphaInAnimationAdapter(adapter1)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(1f))



            mLRecyclerViewAdapter = LRecyclerViewAdapter(adapter)
            //SampleHeader headerView = new SampleHeader(getActivity(), R.layout.fragment_home_head_view);
            //mLRecyclerViewAdapter.addHeaderView(headerView);
            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(false)

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            //int spacing = getResources().getDimensionPixelSize(R.dimen.divide_hight);
            //mRefreshListView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, gridLayoutManager.getSpanCount(), Color.GRAY));
            //根据需要选择使用GridItemDecoration还是SpacesItemDecoration
            val divider = GridItemDecoration.Builder(activity!!)
                    .setHorizontal(R.dimen.divide_hight)
                    .setVertical(R.dimen.divide_hight)
                    .setColorResource(R.color.divide_line)
                    .build()
            //mRefreshListView.addItemDecoration(divider);

            val divider2 = DividerDecoration.Builder(activity!!)
                    .setHeight(R.dimen.dp_10)
                    .setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.body_bg)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)

            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {

            /* if (mPage == 1) {
                mRepaymentAdapter.clear();
            }*/
            mAdapter!!.clear()
            mAdapter!!.addAll(mDataList)
            mAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        if (mAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }
    }


    override fun showProgress() {
        mLoadingWindow!!.showView()
    }

    override fun disimissProgress() {
        mLoadingWindow!!.cancleView()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow!!.cancleView()
        when (mType) {
            MethodUrl.shoumoneyList -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    responseData()
                } else {
                    val list = JSONUtil.instance.jsonToList(result)
                    if (list != null) {
                        mDataList.clear()
                        mDataList.addAll(list)
                        responseData()
                    } else {

                    }
                }
                mRefreshListView!!.refreshComplete(10)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.shoumoneyList -> getShouMoneyInfoList()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.borrowList -> if (mAdapter != null) {
                if (mAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { }
            } else {
                mPageView!!.showNetworkError()
            }
        }


        mLoadingWindow!!.cancleView()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        mLoadingWindow!!.showView()
        getShouMoneyInfoList()

    }


    @OnClick(R.id.shuaixuan_lay)
    fun onViewClicked() {
        showDialog()
    }

    fun showDialog() {
        initPopupWindow()
    }

    private fun initPopupWindow() {//城市列表的显示
        if (mConditionDialog == null) {

            mySelectDialog = activity?.let { DateSelectDialog(it, true, "选择日期", 21) }!!
            mySelectDialog!!.selectBackListener = this
            mySelectDialog2 = activity?.let { DateSelectDialog(it, true, "选择日期", 22) }!!
            mySelectDialog2!!.selectBackListener = this

            popView = LayoutInflater.from(activity).inflate(R.layout.dialog_trade_condition, null)
            mConditionDialog = PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            initConditionDialog(popView!!)
            val screenWidth = UtilTools.getScreenWidth(activity)
            val screenHeight = UtilTools.getScreenHeight(activity)
            mConditionDialog!!.width = (screenWidth * 0.8).toInt()
            mConditionDialog!!.height = WindowManager.LayoutParams.MATCH_PARENT

            //设置background后在外点击才会消失
            mConditionDialog!!.setBackgroundDrawable(CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), UtilTools.dip2px(activity!!, 5).toFloat()))
            //mConditionDialog.setOutsideTouchable(true);// 设置可允许在外点击消失
            //自定义动画
            mConditionDialog!!.animationStyle = R.style.PopupAnimation
            //            mConditionDialog.setAnimationStyle(android.R.style.Animation_Activity);//使用系统动画
            mConditionDialog!!.update()
            mConditionDialog!!.isTouchable = true
            mConditionDialog!!.isFocusable = true
            //popView.requestFocus();//pop设置不setBackgroundDrawable情况，把焦点给popView，添加popView.setOnKeyListener。可实现点击外部不消失，点击反键才消失
            //			mConditionDialog.showAtLocation(mCityTv, Gravity.TOP|Gravity.RIGHT, 0, 0); //设置layout在PopupWindow中显示的位置
            mConditionDialog!!.showAtLocation(activity!!.window.decorView, Gravity.TOP or Gravity.RIGHT, 0, 0)
            toggleBright()
            mConditionDialog!!.setOnDismissListener { toggleBright() }
        } else {
            mConditionDialog!!.showAtLocation(activity!!.window.decorView, Gravity.TOP or Gravity.RIGHT, 0, 0)
            toggleBright()
        }
    }


    private fun toggleBright() {
        //三个参数分别为： 起始值 结束值 时长 那么整个动画回调过来的值就是从0.5f--1f的
        mAnimUtil!!.setValueAnimator(0.7f, 1f, 300)
        mAnimUtil!!.addUpdateListener(object : AnimUtil.UpdateListener {
            override fun progress(progress: Float) {
                //此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                val bgAlpha = if (bright) progress else 1.7f - progress//三目运算，应该挺好懂的。
                //bgAlpha = progress;//三目运算，应该挺好懂的。
                bgAlpha(bgAlpha)//在此处改变背景，这样就不用通过Handler去刷新了。
            }
        })
        mAnimUtil!!.addEndListner(object : AnimUtil.EndListener {
            override fun endUpdate(animator: Animator) {
                //在一次动画结束的时候，翻转状态
                bright = !bright
            }
        })
        mAnimUtil!!.startAnimator()
    }

    private fun bgAlpha(alpha: Float) {
        val lp = (activity as Activity).window.attributes
        lp.alpha = alpha// 0.0-1.0
        activity!!.window.attributes = lp
    }


    private fun initConditionDialog(view: View) {
        mLayout = view.findViewById(R.id.lay)
        mLayout1 = view.findViewById(R.id.lay1)

        mLayout!!.visibility = View.GONE
        mLayout1!!.visibility = View.VISIBLE

        mTypeRecyclerView = view.findViewById(R.id.type_recycleview)

        mOneWeekTv = view.findViewById(R.id.one_week_tv)
        mOneWeekTv!!.visibility = View.VISIBLE

        mOneTv = view.findViewById(R.id.one_month_tv1)
        mThreeTv = view.findViewById(R.id.three_month_tv1)
        mSetTimeTv = view.findViewById(R.id.set_time_tv1)
        mStartTimeTv = view.findViewById(R.id.start_time_value_tv)
        mEndTimeTv = view.findViewById(R.id.end_time_value_tv)
        mResetBut = view.findViewById(R.id.reset_but)
        mSureBut = view.findViewById(R.id.sure_but)
        mKeywordEt = view.findViewById(R.id.et_keyword)
        mKeywordLay = view.findViewById(R.id.keyword_lay)
        mKeywordLay!!.visibility = View.VISIBLE


        val onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.one_week_tv -> {
                    mOneWeekTv!!.isSelected = true
                    mOneTv!!.isSelected = false
                    mThreeTv!!.isSelected = false
                    mSetTimeTv!!.isSelected = false


                    val startOne0 = UtilTools.getWeekAgo(Date(), -6)
                    val endOne0 = UtilTools.getStringFromDate(Date(), "yyyyMMdd")


                    mSelectStartTime = startOne0
                    mSelectEndTime = endOne0

                    val startShow0 = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
                    val endShow0 = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
                    mStartTimeTv!!.text = startShow0
                    mEndTimeTv!!.text = endShow0

                    mStartTimeTv!!.isEnabled = false
                    mEndTimeTv!!.isEnabled = false

                    mDataType = "1"
                }
                R.id.one_month_tv1 -> {
                    mOneWeekTv!!.isSelected = false
                    mOneTv!!.isSelected = true
                    mThreeTv!!.isSelected = false
                    mSetTimeTv!!.isSelected = false

                    val startOne = UtilTools.getMonthAgo(Date(), -1)
                    val endOne = UtilTools.getStringFromDate(Date(), "yyyyMMdd")


                    mSelectStartTime = startOne
                    mSelectEndTime = endOne

                    val startShow = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
                    val endShow = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
                    mStartTimeTv!!.text = startShow
                    mEndTimeTv!!.text = endShow

                    mStartTimeTv!!.isEnabled = false
                    mEndTimeTv!!.isEnabled = false
                    mDataType = "2"
                }
                R.id.three_month_tv1 -> {
                    mOneWeekTv!!.isSelected = false
                    mOneTv!!.isSelected = false
                    mThreeTv!!.isSelected = true
                    mSetTimeTv!!.isSelected = false

                    val startThree = UtilTools.getMonthAgo(Date(), -3)
                    val endThree = UtilTools.getStringFromDate(Date(), "yyyyMMdd")

                    mSelectStartTime = startThree
                    mSelectEndTime = endThree

                    val startShow3 = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
                    val endShow3 = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
                    mStartTimeTv!!.text = startShow3
                    mEndTimeTv!!.text = endShow3

                    LogUtil.i("时间日期信息", "$mSelectStartTime    $mSelectEndTime")
                    mStartTimeTv!!.isEnabled = false
                    mEndTimeTv!!.isEnabled = false

                    mDataType = "3"
                }
                R.id.set_time_tv1 -> {
                    mOneWeekTv!!.isSelected = false
                    mOneTv!!.isSelected = false
                    mThreeTv!!.isSelected = false
                    mSetTimeTv!!.isSelected = true

                    mStartTimeTv!!.isEnabled = true
                    mEndTimeTv!!.isEnabled = true

                    mDataType = "4"
                }
                R.id.start_time_value_tv -> showDateDialog()
                R.id.end_time_value_tv -> showDateDialog2()
                R.id.reset_but -> resetCondition()
                R.id.sure_but -> getSelectCondition()
            }
        }

        mOneWeekTv!!.setOnClickListener(onClickListener)
        mOneTv!!.setOnClickListener(onClickListener)
        mThreeTv!!.setOnClickListener(onClickListener)
        mSetTimeTv!!.setOnClickListener(onClickListener)
        mSureBut!!.setOnClickListener(onClickListener)
        mResetBut!!.setOnClickListener(onClickListener)
        mStartTimeTv!!.setOnClickListener(onClickListener)
        mEndTimeTv!!.setOnClickListener(onClickListener)


        val maps = SelectDataUtil.pingZhengStatus()

        val linearLayoutManager = GridLayoutManager(activity, 3)
        mTypeRecyclerView!!.layoutManager = linearLayoutManager
        mTradeDialogAdapter = TradeDialogAdapter(activity!!, maps)
        //第一次设置默认值
        mSelectType = "1"
        mDataType = "1"
        mTradeDialogAdapter!!.selectItme = 0

        mOneWeekTv!!.isSelected = true

        val startOne = UtilTools.getWeekAgo(Date(), -6)
        val endOne = UtilTools.getStringFromDate(Date(), "yyyyMMdd")
        mSelectStartTime = startOne
        mSelectEndTime = endOne

        val startShow = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
        val endShow = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
        mStartTimeTv!!.text = startShow
        mEndTimeTv!!.text = endShow

        mTradeDialogAdapter!!.onItemClickListener = object : OnMyItemClickListener {
            override fun OnMyItemClickListener(view: View, position: Int) {
                val itemMap = mTradeDialogAdapter!!.datas!![position]
                mSelectType = itemMap["code"]!!.toString() + ""
            }
        }
        mTypeRecyclerView!!.adapter = mTradeDialogAdapter
        mStartTimeTv!!.isEnabled = false
        mEndTimeTv!!.isEnabled = false
    }

    private fun showDateDialog() {
        mySelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showDateDialog2() {
        mySelectDialog2!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }


    private fun getSelectCondition() {
        //        mStartTime = mSelectStartTime;
        //        mEndTime = mSelectEndTime;
        mJieKuanStatus = mSelectType

        if (UtilTools.isDateOneBigger(mSelectStartTime, mSelectEndTime, "yyyyMMdd")) {
            mRefreshListView!!.refreshComplete(10)
            TipsToast.showToastMsg("开始时间不能大于结束时间")
            return
        }
        mConditionDialog!!.dismiss()

        mKeyword = mKeywordEt!!.text.toString()

        mDateTv!!.text = UtilTools.dateTypeTo(mSelectStartTime) + " - " + UtilTools.dateTypeTo(mSelectEndTime) + "应收账款"

        mLoadingWindow!!.showView()
        getShouMoneyInfoList()

    }

    private fun resetCondition() {
        mOneWeekTv!!.isSelected = true
        mOneTv!!.isSelected = false
        mThreeTv!!.isSelected = false
        mSetTimeTv!!.isSelected = false

        //第一次设置默认值
        mSelectType = "1"
        mDataType = "1"
        mTradeDialogAdapter!!.selectItme = 0
        mTradeDialogAdapter!!.notifyDataSetChanged()

        val startOne = UtilTools.getWeekAgo(Date(), -6)
        val endOne = UtilTools.getStringFromDate(Date(), "yyyyMMdd")
        mSelectStartTime = startOne
        mSelectEndTime = endOne


        val startShow = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
        val endShow = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
        mStartTimeTv!!.text = startShow
        mEndTimeTv!!.text = endShow

        //        mStartTime = mSelectStartTime;
        //        mEndTime = mSelectEndTime;
        mJieKuanStatus = mSelectType
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            21 -> {
                mSelectStartTime = map["date"]!!.toString() + ""
                val startShow = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
                mStartTimeTv!!.text = startShow
            }
            22 -> {
                mSelectEndTime = map["date"]!!.toString() + ""
                val endShow = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
                mEndTimeTv!!.text = endShow
            }
        }
    }
}// Required empty public constructor
