package com.lairui.easy.ui.temporary.fragment

import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.TestActivity
import com.lairui.easy.ui.temporary.adapter.HomeAdapter
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.MyRefreshHeader

import java.util.ArrayList
import java.util.HashMap

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

class IndexChildFragment : Fragment(), View.OnClickListener, RequestView {

    private var rootView: View? = null
    private var mRecyclerView: LRecyclerView? = null


    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private var mDataAdapter: HomeAdapter? = null

    /**  对象定义   */
    private val mRequestPresenterImp: RequestPresenterImp? = null

    private var mPage = 1
    internal var list: List<MutableMap<String, Any>>? = ArrayList()


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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_index_child, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        mRecyclerView = rootView!!.findViewById(R.id.refresh_list_view)

        mRecyclerView!!.setRefreshHeader(MyRefreshHeader(context!!.applicationContext))

        mRecyclerView!!.setLScrollListener(mLScrollListener)
        mRecyclerView!!.setOnRefreshListener {
            mPage = 1
            initData()
        }
        //        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOut); //设置下拉刷新Progress的样式
        //        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        //        //设置头部加载颜色
        //        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.red ,R.color.white);
        ////设置底部加载颜色
        //        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);
        mRecyclerView!!.setOnLoadMoreListener { initData() }

        mRecyclerView!!.setOnNetWorkErrorListener { initData() }
        initData()
    }

    private fun initData() {


        val map = HashMap<String, Any>()
        map["unit_id"] = 1

        val mHeaderMap = HashMap<String, String>()

        //  mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.indexAction, map);
    }


    private fun responseData() {

        /*if (map == null){
            mPageView.showNetworkError();
            return;
        }else {
            mPageView.showContent();
        }*/


        if (mDataAdapter == null) {
            mDataAdapter = HomeAdapter(activity!!)
            mDataAdapter!!.addAll(list!!)

            val adapter = ScaleInAnimationAdapter(mDataAdapter)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(.5f))

            mLRecyclerViewAdapter = LRecyclerViewAdapter(adapter)
            mRecyclerView!!.adapter = mLRecyclerViewAdapter
            mRecyclerView!!.itemAnimator = DefaultItemAnimator()
            mRecyclerView!!.setHasFixedSize(true)
            mRecyclerView!!.isNestedScrollingEnabled = false
            mRecyclerView!!.layoutManager = LinearLayoutManager(activity)

            mRecyclerView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRecyclerView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRecyclerView!!.setLoadMoreEnabled(false)



            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mDataAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {
            mDataAdapter!!.clear()
            mDataAdapter!!.addAll(list!!)
            mDataAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }
        /*  //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black ,android.R.color.white);*/


        if (list == null || list!!.size < 10) {
            mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        } else {
            mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        }

        mRecyclerView!!.refreshComplete(10)
        mDataAdapter!!.notifyDataSetChanged()
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

    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        val msg = map["msg"]!!.toString() + ""
    }
}// Required empty public constructor
