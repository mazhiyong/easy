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
import com.lairui.easy.mywidget.view.TipsToast.Companion.showToastMsg
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module4.adapter.TradeListAdapter
import com.lairui.easy.utils.tool.*
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

    private var mDataList = ArrayList<MutableMap<String, Any>>()


    private lateinit var mAnimUtil: AnimUtil

    private val listUp: MutableList<Map<String, Any>> = ArrayList()


    private lateinit var popView: View
    private lateinit var mConditionDialog: PopupWindow
    private var bright = false

    private var handler = android.os.Handler()

    override val layoutId: Int
        get() = R.layout.fragment_trade


    override fun init() {

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, activity!!.resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(activity!!))
        mTitleBarView.layoutParams = layoutParams
        mTitleBarView.setPadding(0, UtilTools.getStatusHeight2(activity!!), 0, 0)

        //mTabLayout.addTab(mTabLayout.newTab().setText("全部"))
        mTabLayout.addTab(mTabLayout.newTab().setText("操盘中"))
        mTabLayout.addTab(mTabLayout.newTab().setText("已结算"))
        mTabLayout.addOnTabSelectedListener(object :XTabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                when(mTabLayout.selectedTabPosition){
                    0 ->{
                        mLoadingWindow.showView()
                        borrowListAction()
                    }

                    1 ->{
                        mLoadingWindow.showView()
                        historyListAction()
                    }
                }
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
            mLoadingWindow.showView()
            when(mTabLayout.selectedTabPosition){
                0 ->{
                    borrowListAction()
                }

                1 ->{
                    historyListAction()
                }
            }
        }

        mLoadingWindow.showView()
        borrowListAction()



      /*  for (index in 1..10){
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
        mPageView.showContent()
        responseData()*/


        setBarTextColor()
    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }

    private fun borrowListAction() {
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[activity!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_LIST, map)
    }

    private fun historyListAction() {
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.HISTORY_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[activity!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.HISTORY_LIST, map)
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

        mRefreshListView.refreshComplete(10)
        mRefreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView.setNoMore(true)
        } else {
            mRefreshListView.setNoMore(false)
        }

        if (mTradeListAdapter!!.dataList.isEmpty()) {
            mPageView.showEmpty()
        } else {
            mPageView.showContent()
        }
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow.cancleView()
        val intent: Intent
        when (mType) {
            MethodUrl.PEIZI_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (UtilTools.empty(tData["data"])){
                        mPageView.showEmpty()
                    }else{
                        mDataList = tData["data"] as ArrayList<MutableMap<String, Any>>
                        if(!UtilTools.empty(mDataList) && mDataList.size>0){
                            for (item in mDataList){
                                item["status"] = "1"
                            }
                            responseData()
                        }else{
                            mPageView.showEmpty()
                        }
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    activity!!.finish()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


            MethodUrl.HISTORY_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (UtilTools.empty(tData["data"])){
                        mPageView.showEmpty()
                    }else{
                        mDataList = tData["data"] as ArrayList<MutableMap<String, Any>>
                        if(!UtilTools.empty(mDataList) && mDataList.size>0){
                            for (item in mDataList){
                                item["status"] = "2"
                            }
                            responseData()
                        }else{
                            mPageView.showEmpty()
                        }
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    activity!!.finish()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }



        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.borrowList -> if (mTradeListAdapter != null) {
                if (mTradeListAdapter!!.dataList.isEmpty()) {
                    mPageView.showNetworkError()
                } else {
                    mPageView.showContent()
                }
                mRefreshListView.refreshComplete(10)
                mRefreshListView.setOnNetWorkErrorListener { borrowListAction() }
            } else {
                mPageView.showNetworkError()
            }
        }


        mLoadingWindow.cancleView()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        when(mTabLayout.selectedTabPosition){
            0 ->{
                mLoadingWindow.showView()
                borrowListAction()
            }

            1 ->{
                mLoadingWindow.showView()
                historyListAction()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // if (getDefault().isConnect) getDefault().disConnect()
        handler.removeCallbacks(cnyRunnable)
    }

    override fun onResume() {
        super.onResume()
        //if (!getDefault().isConnect) getDefault().reconnect()
        handler.post(cnyRunnable)
    }

    private val cnyRunnable = object : Runnable {
        override fun run() {
            when(mTabLayout.selectedTabPosition){
                0 ->{
                    borrowListAction()
                }
            }
            handler.postDelayed(this, 5 * 1000)

        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
           //不可见
            handler.removeCallbacks(cnyRunnable)
        } else {
           //可见
            handler.post(cnyRunnable)
        }
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
