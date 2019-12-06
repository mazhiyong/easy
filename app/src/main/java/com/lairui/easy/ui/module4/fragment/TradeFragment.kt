package com.lairui.easy.ui.module4.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.androidkun.xtablayout.XTabLayout
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.ui.module4.adapter.TradeListAdapter
import com.lairui.easy.utils.tool.AnimUtil
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.UtilTools
import java.util.*

class TradeFragment : BasicFragment(), RequestView, ReLoadingData, SelectBackListener {
    @BindView(R.id.tab_layout)
    lateinit var mTabLayout: XTabLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.title_bar_view)
    lateinit var mTitleBarView: LinearLayout


    private lateinit var mLoadingWindow: LoadingWindow

    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mTradeListAdapter: TradeListAdapter? = null
    private var mRequestTag = ""

    private val mDataList = ArrayList<MutableMap<String, Any>>()

    private var mStartTime = ""
    private var mEndTime = ""
    private var mJieKuanStatus = ""

    private lateinit var mAnimUtil: AnimUtil

    private val listUp: MutableList<Map<String, Any>> = ArrayList()


    private lateinit var popView: View
    private lateinit var mConditionDialog: PopupWindow
    private var bright = false

    override val layoutId: Int
        get() = R.layout.fragment_trade


    override fun init() {

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, activity!!.resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(activity!!))
        mTitleBarView.layoutParams = layoutParams
        mTitleBarView.setPadding(0, UtilTools.getStatusHeight2(activity!!), 0, 0)

        mTabLayout.addTab(mTabLayout.newTab().setText("全部"))
        mTabLayout.addTab(mTabLayout.newTab().setText("操盘中"))
        mTabLayout.addTab(mTabLayout.newTab().setText("已结算"))
        mTabLayout.addOnTabSelectedListener(object :XTabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: XTabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {

            }

            override fun onTabSelected(tab: XTabLayout.Tab?) {

            }

        })


        mAnimUtil = AnimUtil()
        mLoadingWindow = LoadingWindow(activity!!, R.style.Dialog)
        mContent?.let { mPageView.setContentView(it) }
        mPageView.showLoading()
        mPageView.reLoadingData = this
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView.layoutManager = linearLayoutManager

        mRefreshListView.setOnRefreshListener {
            borrowListAction()
        }

        mLoadingWindow.showView()
        //borrowListAction()


        for (index in 1..10){
            val map = HashMap<String,Any>()
            if(index % 2 == 0 ) {
                map["status"] = "1"
            }else{
                map["status"] = "2"
            }

            map["title"] = "HT2001"
            map["time"] = "2019.10.15-2019.11.15"
            map["a"] = "100.00元"
            map["b"] = "100.00元"
            mDataList.add(map)
        }

        mPageView.setContentView(mContent)
        mPageView.reLoadingData
        mPageView.showLoading()
     
        mRefreshListView.layoutManager = linearLayoutManager
        mRefreshListView.setOnRefreshListener {
            //getMyTreamInfoAction()

        }

        mRefreshListView.setOnLoadMoreListener {
            mRefreshListView.setNoMore(true)
        }


        mPageView.showContent()
        responseData()


        setBarTextColor()
    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }

    private fun borrowListAction() {
        mRequestPresenterImp = RequestPresenterImp(this, activity!!)
        mRequestTag = MethodUrl.borrowList
        val map = HashMap<String, String>()
        map["loansqid"] = ""
        map["startdate"] = mStartTime
        map["enddate"] = mEndTime
        map["loanstate"] = mJieKuanStatus
        map["creditfile"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.borrowList, map)
    }


    private fun responseData() {
        if (mTradeListAdapter == null) {
            mTradeListAdapter = TradeListAdapter(activity!!)
            mTradeListAdapter!!.addAll(mDataList)

       /*     val adapter1 = ScaleInAnimationAdapter(mHangqingAdapter)
            adapter1.setFirstOnly(false)
            adapter1.setDuration(400)
            adapter1.setInterpolator(OvershootInterpolator(0.8f))


            val adapter = AlphaInAnimationAdapter(adapter1)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(1f))*/

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/



            mLRecyclerViewAdapter = LRecyclerViewAdapter(mTradeListAdapter)
            //SampleHeader headerView = new SampleHeader(getActivity(), R.layout.fragment_home_head_view);
            //mLRecyclerViewAdapter.addHeaderView(headerView);
            mRefreshListView.adapter = mLRecyclerViewAdapter
            mRefreshListView.itemAnimator = DefaultItemAnimator()
            mRefreshListView.setHasFixedSize(true)
            mRefreshListView.isNestedScrollingEnabled = false

            mRefreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
            mRefreshListView.setPullRefreshEnabled(true)
            mRefreshListView.setLoadMoreEnabled(false)

            mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            //int spacing = getResources().getDimensionPixelSize(R.dimen.divide_hight);
            //mRefreshListView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, gridLayoutManager.getSpanCount(), Color.GRAY));
            //根据需要选择使用GridItemDecoration还是SpacesItemDecoration
            val divider = GridItemDecoration.Builder(activity!!)
                    .setHorizontal(R.dimen.divide_hight)
                    .setVertical(R.dimen.divide_hight)
                    .setColorResource(R.color.divide_line)
                    .build()
            //mRefreshListView.addItemDecoration(divider);

           /* val divider2 = DividerDecoration.Builder(activity!!)
                    .setHeight(R.dimen.dp_10)
                    .setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.body_bg)
                    .build()
            mRefreshListView.addItemDecoration(divider2)*/

            mLRecyclerViewAdapter.setOnItemClickListener { view, position ->
                val item = mTradeListAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {

            /* if (mPage == 1) {
                mRepaymentAdapter.clear();
            }*/
            mTradeListAdapter!!.clear()
            mTradeListAdapter!!.addAll(mDataList)
            mTradeListAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        if (mTradeListAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow!!.cancleView()
        val intent: Intent
        when (mType) {
            MethodUrl.borrowList//
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
                when (mRequestTag) {
                    MethodUrl.borrowList -> borrowListAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.borrowList -> if (mTradeListAdapter != null) {
                if (mTradeListAdapter!!.dataList.size <= 0) {
                    mPageView.showNetworkError()
                } else {
                    mPageView.showContent()
                }
                mRefreshListView.refreshComplete(10)
                mRefreshListView.setOnNetWorkErrorListener { borrowListAction() }
            } else {
                mPageView!!.showNetworkError()
            }
        }


        mLoadingWindow!!.cancleView()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        mLoadingWindow.showView()
        borrowListAction()
    }










    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            21 -> {

            }
            22 -> {

            }
        }
    }
}
