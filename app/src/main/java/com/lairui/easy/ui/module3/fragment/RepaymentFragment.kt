package com.lairui.easy.ui.module3.fragment

import android.content.Intent

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.RepaymentAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.Date
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

/**
 * 首页还款记录的fragment
 */
class RepaymentFragment : BasicFragment(), RequestView, ReLoadingData, SelectBackListener {

    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.start_time_value_tv)
    lateinit var mStartTimeValueTv: TextView
    @BindView(R.id.end_time_value_tv)
    lateinit var mEndTimeValueTv: TextView
    @BindView(R.id.search_view)
    lateinit var mSearchView: ImageView

    private lateinit var mLoadingWindow: LoadingWindow
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mRepaymentAdapter: RepaymentAdapter? = null


    private var mRequestTag = ""

    private val mDataList = ArrayList<MutableMap<String, Any>>()

    private var mStartTime = ""
    private var mEndTime = ""

    private var mSelectStartTime = ""
    private var mSelectEndTime = ""
    private lateinit var mySelectDialog: DateSelectDialog
    private lateinit var mySelectDialog2: DateSelectDialog

    private var mPage = 1
    override val layoutId: Int
        get() = R.layout.fragment_repayment

    override fun init() {
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, activity!!.resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(activity!!))
        mTitleBarView!!.layoutParams = layoutParams
        mTitleBarView!!.setPadding(0, UtilTools.getStatusHeight2(activity!!), 0, 0)
        mTitleText!!.text = resources.getString(R.string.return_history)
        mLeftBackLay!!.visibility = View.GONE
        mLoadingWindow = LoadingWindow(activity!!, R.style.Dialog)
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this
        mySelectDialog = DateSelectDialog(activity!!, true, "选择日期", 21)
        mySelectDialog!!.selectBackListener = this
        mySelectDialog2 = DateSelectDialog(activity!!, true, "选择日期", 22)
        mySelectDialog2!!.selectBackListener = this

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = linearLayoutManager

        mRefreshListView!!.setOnRefreshListener {
            mPage = 1
            repaymentListAction()
            //                mRefreshListView.refreshComplete(10);
            //                mLRecyclerViewAdapter.notifyDataSetChanged();
        }

        mRefreshListView!!.setOnLoadMoreListener { repaymentListAction() }

        val sTime = UtilTools.getFirstDayOfMonthByDate(Date())


        val eTime = UtilTools.getStringFromDate(Date(), "yyyyMMdd")

        //        mSelectStartTime = sTime;
        //        mSelectEndTime = eTime;
        //        mStartTime = mSelectStartTime;
        //        mEndTime = mSelectEndTime;
        //
        //        mStartTimeValueTv.setText(UtilTools.getStringFromSting2(mStartTime,"yyyyMMdd","yyyy年MM月dd日"));
        //        mEndTimeValueTv.setText(UtilTools.getStringFromSting2(mEndTime,"yyyyMMdd","yyyy年MM月dd日"));


        mStartTimeValueTv!!.hint = "请选择开始日期"
        mEndTimeValueTv!!.hint = "请选择结束日期"

        mLoadingWindow!!.showView()
        repaymentListAction()
        setBarTextColor()
    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }

    private fun repaymentListAction() {


        if (!UtilTools.empty(mStartTime) && !UtilTools.empty(mEndTime)) {
            if (UtilTools.isDateOneBigger(mStartTime, mEndTime, "yyyyMMdd")) {
                mRefreshListView!!.refreshComplete(10)
                mLoadingWindow!!.cancleView()
                TipsToast.showToastMsg("开始时间不能大于结束时间")
                return
            }
        } else if (UtilTools.empty(mStartTime) && UtilTools.empty(mEndTime)) {

        } else if (UtilTools.empty(mStartTime)) {
            TipsToast.showToastMsg("请选择开始日期")
            mRefreshListView!!.refreshComplete(10)
            mLoadingWindow!!.cancleView()
            return
        } else if (UtilTools.empty(mEndTime)) {
            TipsToast.showToastMsg("请选择结束日期")
            mRefreshListView!!.refreshComplete(10)
            mLoadingWindow!!.cancleView()
            return
        }


        mRequestPresenterImp = RequestPresenterImp(this, activity!!)
        mRequestTag = MethodUrl.repaymentList
        val map = HashMap<String, String>()
        map["loansqid"] = ""//借款申请编号
        map["startdate"] = mStartTime//格式：yyyymmdd
        map["enddate"] = mEndTime//格式：yyyymmdd
        map["checkstate"] = ""//还款状态(1:已申请,2:还款成功,3:还款失败)
        map["pagenumber"] = mPage.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.repaymentList, map)
    }


    private fun responseData() {
        if (mRepaymentAdapter == null) {
            mRepaymentAdapter = RepaymentAdapter(activity!!)
            mRepaymentAdapter!!.addAll(mDataList)

            val adapter = ScaleInAnimationAdapter(mRepaymentAdapter)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(.5f))

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mRepaymentAdapter)
            //SampleHeader headerView = new SampleHeader(getActivity(), R.layout.fragment_home_head_view);
            //mLRecyclerViewAdapter.addHeaderView(headerView);
            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(true)

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
                val item = mRepaymentAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {

            if (mPage == 1) {
                mRepaymentAdapter!!.clear()
            }
            mRepaymentAdapter!!.addAll(mDataList)
            mRepaymentAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")

        if (mDataList.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
            mPage++
        }
        mRefreshListView!!.refreshComplete(10)
        mRepaymentAdapter!!.notifyDataSetChanged()

        if (mRepaymentAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }
    }


    override fun showProgress() {
        //mLoadingWindow.showView();
    }

    override fun disimissProgress() {
        mLoadingWindow!!.cancleView()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mPageView!!.showNetworkError()
        val intent: Intent
        when (mType) {
            MethodUrl.repaymentList//
            -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    val list = JSONUtil.instance.jsonToList(result)
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
                mLoadingWindow!!.showView()
                when (mRequestTag) {
                    MethodUrl.repaymentList -> repaymentListAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.repaymentList -> if (mRepaymentAdapter != null) {
                if (mRepaymentAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { repaymentListAction() }
            } else {
                mPageView!!.showNetworkError()
            }
        }
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        mStartTime = mSelectStartTime
        mEndTime = mSelectEndTime
        mLoadingWindow!!.showView()
        repaymentListAction()
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {

        when (type) {
            21 -> {
                mSelectStartTime = map["date"]!!.toString() + ""
                mStartTimeValueTv!!.text = map["year"].toString() + "年" + map["month"] + "月" + map["day"] + "日"
                mStartTime = mSelectStartTime
            }
            22 -> {
                mSelectEndTime = map["date"]!!.toString() + ""
                mEndTimeValueTv!!.text = map["year"].toString() + "年" + map["month"] + "月" + map["day"] + "日"
                mEndTime = mSelectEndTime
            }
        }
    }


    @OnClick(R.id.start_time_value_tv, R.id.end_time_value_tv, R.id.search_view)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.start_time_value_tv -> mySelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.end_time_value_tv -> mySelectDialog2!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.search_view -> {
                mStartTime = mSelectStartTime
                mEndTime = mSelectEndTime
                mLoadingWindow!!.showView()
                repaymentListAction()
            }
        }
    }
}// Required empty public constructor
