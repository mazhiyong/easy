package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.ui.temporary.adapter.HomeAdapter

import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

class UserIdeaActivity : BasicActivity(), ReLoadingData {


    @BindView(R.id.refresh_list_view)
    lateinit var mRecyclerView: LRecyclerView
    @BindView(R.id.editTextBodyLl)
    lateinit var mEditTextBodyLl: LinearLayout
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private lateinit var mDataAdapter: HomeAdapter
    private var mPage = 1

    override val contentView: Int
        get() = R.layout.activity_user_idea

    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            list!!.clear()
            for (i in 0..9) {
                val map = HashMap<String, Any>()
                list!!.add(map)
            }
            responseData()
        }
    }
    var list: MutableList<MutableMap<String, Any>>? = ArrayList()


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

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        mRecyclerView!!.setLScrollListener(mLScrollListener)
        mRecyclerView!!.setOnRefreshListener {
            mPage = 1
            requestData()
        }

        mRecyclerView!!.setOnLoadMoreListener { requestData() }

        mRecyclerView!!.setOnNetWorkErrorListener { requestData() }
        handler.sendEmptyMessageDelayed(1, 0)


        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        mPageView!!.subscribRefreshEvent(this)
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()
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
            mDataAdapter = HomeAdapter(this)
            list?.let { mDataAdapter!!.addAll(it) }

            val adapter = ScaleInAnimationAdapter(mDataAdapter)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(.5f))

            mLRecyclerViewAdapter = LRecyclerViewAdapter(adapter)
            mRecyclerView!!.adapter = mLRecyclerViewAdapter
            mRecyclerView!!.itemAnimator = DefaultItemAnimator()
            mRecyclerView!!.setHasFixedSize(true)
            mRecyclerView!!.isNestedScrollingEnabled = false
            mRecyclerView!!.layoutManager = LinearLayoutManager(this)

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
            list?.let { mDataAdapter!!.addAll(it) }
            mDataAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }
        //设置底部加载颜色
        mRecyclerView!!.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white)
        if (list == null || list!!.size < 10) {
            mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        } else {
            mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        }
        mPageView!!.showContent()
        mRecyclerView!!.refreshComplete(10)
        mDataAdapter!!.notifyDataSetChanged()
    }

    override fun reLoadingData() {

    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {

    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {

    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }
}
