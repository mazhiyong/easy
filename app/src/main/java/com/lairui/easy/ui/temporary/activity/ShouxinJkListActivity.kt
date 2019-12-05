package com.lairui.easy.ui.temporary.activity

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
import com.lairui.easy.ui.temporary.adapter.ShouxinJkListAdapter
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

/**
 * 我的--我的额度--列表详情--借款信息item点击界面   界面
 */
class ShouxinJkListActivity : BasicActivity(), RequestView, ReLoadingData {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    private var mRequestTag = ""

    private lateinit var mDataMap: MutableMap<String, Any>
    private lateinit var mShouxinJkListAdapter: ShouxinJkListAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1

    override val contentView: Int
        get() = R.layout.activity_shouxin_jik_list

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }


        mTitleText!!.text = resources.getString(R.string.borrow_info)
        mRightLay!!.visibility = View.GONE

        initView()
        showProgressDialog()
        borrowListAction()
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this
        val manager = LinearLayoutManager(this@ShouxinJkListActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager

        mRefreshListView!!.setOnRefreshListener {
            mPage = 1
            borrowListAction()
        }

        mRefreshListView!!.setOnLoadMoreListener {
            mPage++
            borrowListAction()
        }
    }

    private fun borrowListAction() {
        mRequestTag = MethodUrl.borrowList
        val map = HashMap<String, String>()
        map["loansqid"] = ""
        map["startdate"] = ""
        map["enddate"] = ""
        map["loanstate"] = ""
        map["creditfile"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.borrowList, map)
    }


    private fun responseData() {
        if (mShouxinJkListAdapter == null) {
            mShouxinJkListAdapter = ShouxinJkListAdapter(this@ShouxinJkListActivity)
            mShouxinJkListAdapter!!.addAll(mDataList)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mShouxinJkListAdapter)

            //            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
            //            mLRecyclerViewAdapter.addHeaderView(headerView);

            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(false)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mShouxinJkListAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {
            if (mPage == 1) {
                mShouxinJkListAdapter!!.clear()
            }
            mShouxinJkListAdapter!!.addAll(mDataList)
            mShouxinJkListAdapter!!.notifyDataSetChanged()
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
        mShouxinJkListAdapter!!.notifyDataSetChanged()
        if (mShouxinJkListAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }

    @OnClick(R.id.back_img, R.id.right_lay, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.right_lay -> {
            }
        }
    }

    override fun showProgress() {
        //showProgressDialog();
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {
            MethodUrl.borrowList//
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
                when (mRequestTag) {
                    MethodUrl.borrowList -> borrowListAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.hetongList//
            -> if (mShouxinJkListAdapter != null) {
                if (mShouxinJkListAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { borrowListAction() }
            } else {
                mPageView!!.showNetworkError()
            }
        }

        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        showProgressDialog()
        borrowListAction()
    }

}
