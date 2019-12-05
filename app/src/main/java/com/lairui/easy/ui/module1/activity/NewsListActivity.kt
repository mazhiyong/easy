package com.lairui.easy.ui.module1.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.HeTongListAdapter
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
import com.lairui.easy.ui.module1.adapter.NewsListAdapter

/**
 * 新闻列表 界面
 */
class NewsListActivity : BasicActivity(), RequestView, ReLoadingData {


    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
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

    private var mNewsAdapter: NewsListAdapter? = null
    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1

    override val contentView: Int
        get() = R.layout.activity_news_list

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        mTitleText.text = "新闻资讯"
        mRightLay.visibility = View.GONE
        initView()
        for (index in 1..6){
            val map = HashMap<String,Any>()
            map["title"] = "外交部召见美驻华使馆负责人 就美国会众议院通过涉疆法案提出严正交涉和强烈抗议"
            map["content"] = "12月4日，中国外交部副部长秦刚召见美国驻华使馆负责人柯有为，就美国会众议院审议通过“2019年维吾尔人权政策法案”提出严正交涉和强烈抗议，敦促美方立即纠正错误，停止借涉疆问题干涉中国内政。"
            map["url"] = "https://pics7.baidu.com/feed/9345d688d43f8794bbb8b935c6d1d7f11ad53ab1.jpeg?token=8bb505873039703b2500e78125e257ed&s=569139C47448935D0A512F9503005084"
            mDataList.add(map)
        }
        responseData()

        //heTongAction()
    }


    private fun initView() {
        mContent?.let { mPageView.setContentView(it) }
        mPageView.reLoadingData = this
        mPageView.showLoading()
        val manager = LinearLayoutManager(this@NewsListActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView.layoutManager = manager

        mRefreshListView.setOnRefreshListener {
            mPage = 1
            heTongAction()
        }

        mRefreshListView.setOnLoadMoreListener {
            mPage++
            heTongAction()
        }
    }

    private fun heTongAction() {

        mRequestTag = MethodUrl.hetongList
        val map = HashMap<String, String>()
        map["creditfile"] = mDataMap!!["creditfile"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.hetongList, map)
    }


    private fun responseData() {
        if (mNewsAdapter == null) {
            mNewsAdapter = NewsListAdapter(this@NewsListActivity)
            mNewsAdapter!!.addAll(mDataList)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mNewsAdapter)

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
                //val item = mNewsAdapter!!.dataList[position]
                val intent = Intent(this@NewsListActivity, NewsItemActivity::class.java)
                //intent.putExtra("id", item["filefullname"]!!.toString() + "")
                startActivity(intent)
            }


        } else {
            if (mPage == 1) {
                mNewsAdapter!!.clear()
            }
            mNewsAdapter!!.addAll(mDataList)
            mNewsAdapter!!.notifyDataSetChanged()
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

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        mRefreshListView!!.refreshComplete(10)
        mNewsAdapter!!.notifyDataSetChanged()
        if (mNewsAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }

    @OnClick(R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {

            R.id.left_back_lay -> {
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
            MethodUrl.hetongList//
            -> {
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
                mIsRefreshToken = false
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.hetongList -> heTongAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.hetongList//
            -> if (mNewsAdapter != null) {
                if (mNewsAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { heTongAction() }
            } else {
                mPageView!!.showNetworkError()
            }
        }

        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        heTongAction()
    }


}
