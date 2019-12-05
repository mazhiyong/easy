package com.lairui.easy.ui.temporary.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.TestActivity
import com.lairui.easy.ui.temporary.adapter.OrderAdapter
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.NetworkUtils

import java.util.ArrayList
import java.util.HashMap

class OrderChildFragment : Fragment, View.OnClickListener, RequestView, ReLoadingData {

    internal lateinit var mRecyclerView: LRecyclerView
    internal lateinit var mContent: LinearLayout
    internal lateinit var mPageView: PageView

    private val mRootView: View? = null

    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private var mDataAdapter: OrderAdapter? = null

    private var mPage = 1

    private val mType: Int = 0

    /**
     * 对象定义
     */
    private var mRequestPresenterImp: RequestPresenterImp? = null


    internal var list: List<MutableMap<String, Any>> = ArrayList()


    private val mLScrollListener = object : LRecyclerView.LScrollListener {
        override fun onScrollUp() {

        }

        override fun onScrollDown() {

        }

        override fun onScrolled(distanceX: Int, distanceY: Int) {

        }

        override fun onScrollStateChanged(state: Int) {

        }
    }

    constructor() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    constructor(mType: Int) {
        var mType = mType
        mType = mType
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        if(savedInstanceState == null){
            super.onCreate(Bundle())
        }else{
            super.onCreate(savedInstanceState)
        }

        val inflater = activity!!.layoutInflater
        //mRootView = inflater.inflate(R.layout.fragment_order_child, (ViewGroup) getActivity().findViewById(R.id.order_manager_page), false);
        initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val p = mRootView!!.parent as ViewGroup
        p?.removeAllViewsInLayout()
        return mRootView
    }


    private fun initView() {

        mContent = mRootView!!.findViewById(R.id.content)
        mPageView = mRootView.findViewById(R.id.page_view)

        mPageView.setContentView(mContent)
        mPageView.showLoading()
        mPageView.subscribRefreshEvent(this)
        mPageView.reLoadingData = this

        mRecyclerView = mRootView.findViewById(R.id.refresh_list_view)

        mRecyclerView.setLScrollListener(mLScrollListener)
        mRecyclerView.setOnRefreshListener {
            mPage = 1
            initData()
        }

        mRecyclerView.setOnLoadMoreListener {
            mPage++
            initData()
        }


        initData()
    }

    private fun initData() {
        //模拟一下网络请求失败的情况
        if (NetworkUtils.isNetAvailable(activity!!)) {
        } else {
            mRecyclerView.setOnNetWorkErrorListener { initData() }
            return
        }

        /*  "page": 0,
                "tab_index": 0,
                "unit_id": 0*/
        val map = HashMap<String, Any>()
        map["unit_id"] = 1
        map["tab_index"] = mType
        map["page"] = mPage

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp = RequestPresenterImp(this, activity!!)
        //   mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.orderList, map);
    }

    private fun responseData() {

        if (mDataAdapter == null) {
            mDataAdapter = OrderAdapter(activity!!)
            mDataAdapter!!.addAll(list)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mDataAdapter)
            mRecyclerView.adapter = mLRecyclerViewAdapter
            mRecyclerView.itemAnimator = DefaultItemAnimator()
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.isNestedScrollingEnabled = false
            mRecyclerView.layoutManager = LinearLayoutManager(activity)

            mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            // mRecyclerView.setLoadMoreEnabled(false);

            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mDataAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {
            if (mPage == 1) {
                mDataAdapter!!.clear()
            }
            mDataAdapter!!.addAll(list)
            mDataAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }
        /*   //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOut); //设置下拉刷新Progress的样式
        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);
//设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);*/

        mRecyclerView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (list.size < 10) {
            mRecyclerView.setNoMore(true)
        } else {
            mRecyclerView.setNoMore(false)
        }

        mRecyclerView.refreshComplete(10)
        mDataAdapter!!.notifyDataSetChanged()
        if (mDataAdapter!!.dataList.size <= 0) {
            mPageView.showEmpty()
        } else {
            mPageView.showContent()
        }

    }

    override fun onClick(view: View) {
        val intent = Intent(activity, TestActivity::class.java)
        startActivity(intent)
    }

    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        list = (tData["rows"] as List<MutableMap<String, Any>>?)!!
        responseData()
    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        val msg = map["msg"]!!.toString() + ""
        mRecyclerView.refreshComplete(10)
        mRecyclerView.setOnNetWorkErrorListener {
            mPage++
            initData()
        }
        if (mPage == 1) {
            if (mDataAdapter != null && mDataAdapter!!.dataList.size > 0) {

            } else {
                mPageView.showNetworkError()
            }
        } else {
            mPage--
        }
        Log.e("请求异常", msg)
        //        mRecyclerView.refreshComplete(10);
    }

    override fun reLoadingData() {
        initData()
    }
}
