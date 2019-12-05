package com.lairui.easy.ui.temporary.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.FoodAdapter

import java.util.ArrayList
import java.util.HashMap

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

class FoodFragment : Fragment() {

    private val rootView: View? = null
    private var mRecyclerView: LRecyclerView? = null

    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private var mDataAdapter: FoodAdapter? = null
    private var mRootView: View? = null
    private val list = ArrayList<MutableMap<String, Any>>()

    private val mPage = 1
    internal var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            list.clear()
            for (i in 0..39) {
                val map = HashMap<String, Any>()
                list.add(map)
            }
            responseData()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        if(savedInstanceState == null){
            super.onCreate(Bundle())
        }else{
            super.onCreate(savedInstanceState)
        }

        val inflater = activity!!.layoutInflater
        mRootView = inflater.inflate(R.layout.fragment_food, activity!!.findViewById<View>(R.id.food_manager_page) as ViewGroup, false)
        initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val p = mRootView!!.parent as ViewGroup
        p?.removeAllViewsInLayout()
        return mRootView
    }

    fun initView() {
        mRecyclerView = mRootView!!.findViewById<View>(R.id.refresh_list_view) as LRecyclerView
        //setLayoutManager must before setAdapter
        val manager = GridLayoutManager(activity, 4)
        mRecyclerView!!.layoutManager = manager

        mRecyclerView!!.setLScrollListener(object : LRecyclerView.LScrollListener {
            override fun onScrollUp() {

            }

            override fun onScrollDown() {

            }

            override fun onScrolled(distanceX: Int, distanceY: Int) {

            }

            override fun onScrollStateChanged(state: Int) {

            }
        })

        mRecyclerView!!.setOnRefreshListener { requestData() }

        requestData()
    }

    private fun requestData() {

        handler.sendEmptyMessageDelayed(1, 1000)

    }

    private fun responseData() {

        /*if (map == null){
            mPageView.showNetworkError();
            return;
        }else {
            mPageView.showContent();
        }*/


        if (mDataAdapter == null) {
            mDataAdapter = FoodAdapter(activity!!)
            mDataAdapter!!.addAll(list)

            val adapter = ScaleInAnimationAdapter(mDataAdapter)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(.5f))

            mLRecyclerViewAdapter = LRecyclerViewAdapter(adapter)
            mRecyclerView!!.adapter = mLRecyclerViewAdapter
            mRecyclerView!!.itemAnimator = DefaultItemAnimator()
            mRecyclerView!!.setHasFixedSize(true)
            mRecyclerView!!.isNestedScrollingEnabled = false
            // mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mRecyclerView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRecyclerView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRecyclerView!!.setLoadMoreEnabled(false)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mDataAdapter!!.dataList[position]
                //                    Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                //                    intent.putExtra("jsonData",item.get("url")+"");
                //                    startActivity(intent);
            }


        } else {
            mDataAdapter!!.clear()
            mDataAdapter!!.addAll(list)
            mDataAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }
        //设置底部加载颜色
        mRecyclerView!!.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white)
        if (list == null || list.size < 10) {
            mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        } else {
            mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        }

        mRecyclerView!!.refreshComplete(20)
        mDataAdapter!!.notifyDataSetChanged()
    }
}// Required empty public constructor
