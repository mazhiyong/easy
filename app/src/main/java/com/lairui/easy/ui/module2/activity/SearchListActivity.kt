package com.lairui.easy.ui.module2.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module4.adapter.RecordListAdapter
import com.lairui.easy.utils.tool.SPUtils

/**
 * 搜索 界面
 */
class SearchListActivity : BasicActivity(), RequestView, ReLoadingData {


    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.inputEt)
    lateinit var inputEt: EditText
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    private var mRequestTag = ""

    private var mDataMap: MutableMap<String, Any>? = null

    private var mRecordAdapter: RecordListAdapter? = null
    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1

    override val contentView: Int
        get() = R.layout.activity_search_list

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        initView()
      /*  for (index in 1..6){
            val map = HashMap<String,Any>()
            map["title"] = "支付利息"
            map["time"] = "2019.11.10 12:56:10"
            map["money"] = "-100.00"
            mDataList.add(map)
        }
        responseData()*/
    }


    private fun initView() {
        mContent?.let { mPageView.setContentView(it) }
        mPageView.reLoadingData = this
        mPageView.showLoading()
        val manager = LinearLayoutManager(this@SearchListActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView.layoutManager = manager

        mRefreshListView.setOnRefreshListener {
            mPage = 1
            tradeListAction()
        }

        mRefreshListView.setOnLoadMoreListener {
            mPage++
            tradeListAction()
        }
    }

    private fun tradeListAction() {

        mRequestTag = MethodUrl.TRADE_LIST
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.TRADE_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@SearchListActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TRADE_LIST, map)
    }


    private fun responseData() {
        if (mRecordAdapter == null) {
            mRecordAdapter = RecordListAdapter( this@SearchListActivity)
            mRecordAdapter!!.addAll(mDataList)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mRecordAdapter)

            //            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
            //            mLRecyclerViewAdapter.addHeaderView(headerView);

            mRefreshListView.adapter = mLRecyclerViewAdapter
            mRefreshListView.itemAnimator = DefaultItemAnimator()
            mRefreshListView.setHasFixedSize(true)
            mRefreshListView.isNestedScrollingEnabled = false


            mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView.setPullRefreshEnabled(true)
            mRefreshListView.setLoadMoreEnabled(false)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->

            }


        } else {
            if (mPage == 1) {
                mRecordAdapter!!.clear()
            }
            mRecordAdapter!!.addAll(mDataList)
            mRecordAdapter!!.notifyDataSetChanged()
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

        mRefreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView.setNoMore(true)
        } else {
            mRefreshListView.setNoMore(false)
        }

        mRefreshListView!!.refreshComplete(10)
        mRecordAdapter!!.notifyDataSetChanged()
        if (mRecordAdapter!!.dataList.isEmpty()) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }

    @OnClick(R.id.back_img,R.id.right_img)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.right_img -> {

            }

        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {
            MethodUrl.TRADE_LIST -> {
                when (tData["code"].toString() + "") {
                    "1" -> {
                        if (!UtilTools.empty(tData["data"])){
                            val result = tData["data"]!!.toString() + ""
                            if (UtilTools.empty(result)) {
                                responseData()
                            } else {
                                val list = JSONUtil.instance.jsonToList(result)
                                if (list != null) {
                                    mDataList.clear()
                                    mDataList.addAll(list)
                                    responseData()
                                }
                            }
                            mRefreshListView!!.refreshComplete(10)
                        }
                    }
                    "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                    "-1" -> {
                        closeAllActivity()
                        val intent = Intent(this@SearchListActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

            }

            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                mIsRefreshToken = false
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.hetongList -> tradeListAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.hetongList//
            -> if (mRecordAdapter != null) {
                if (mRecordAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { tradeListAction() }
            } else {
                mPageView!!.showNetworkError()
            }
        }

        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        tradeListAction()
    }


}
