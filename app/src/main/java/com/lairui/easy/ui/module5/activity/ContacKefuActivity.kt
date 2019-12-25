package com.lairui.easy.ui.module5.activity

import android.content.Intent
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module1.adapter.NoticeListAdapter
import com.lairui.easy.ui.module5.adapter.ContactKefuListAdapter
import com.lairui.easy.ui.module5.adapter.PayWayListAdapter
import com.lairui.easy.ui.temporary.adapter.TradeDialogAdapter
import com.lairui.easy.utils.tool.AnimUtil
import com.lairui.easy.utils.tool.SPUtils.get
import com.lairui.easy.utils.tool.UtilTools
import com.lairui.easy.utils.tool.UtilTools.Companion.empty
import java.util.*

/**
 * 联系客服 界面
 */
class ContacKefuActivity : BasicActivity(), RequestView, ReLoadingData {
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
    private var mListAdapter: ContactKefuListAdapter? = null
    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private val mDataList: MutableList<MutableMap<String, Any>> = ArrayList()
    private var mPage = 1
    private var mAnimUtil: AnimUtil? = null
    override val contentView: Int
        get() = R.layout.activity_payway_list

    override fun init() { //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mAnimUtil = AnimUtil()
        mTitleText.text = "联系客服"
        mTitleText.setCompoundDrawables(null, null, null, null)
        mRightImg.visibility = View.GONE
        mRightImg.setImageResource(R.drawable.shuaixuan)
        mRightTextTv.visibility = View.GONE
        mRightTextTv.setTextColor(ContextCompat.getColor(this, R.color.btn_login_normal))
        initView()
        showProgressDialog()
        traderListAction()

    }

    private fun initView() {
        mPageView.setContentView(mContent!!)
        mPageView.reLoadingData = this
        mPageView.showLoading()
        val manager = LinearLayoutManager(this@ContacKefuActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView.layoutManager = manager
        mRefreshListView.setOnRefreshListener {
            showProgressDialog()
            traderListAction()
        }
    }

    //获取公告列表
    private fun traderListAction() {
        val map: MutableMap<String, Any> = HashMap()
        map["nozzle"] = MethodUrl.CONTACT_INFO
        if (empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = get(this@ContacKefuActivity, MbsConstans.ACCESS_TOKEN, "").toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap:MutableMap<String, String> = HashMap()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CONTACT_INFO, map)
    }

    private fun responseData() {
        if (mListAdapter == null) {
            mListAdapter = ContactKefuListAdapter(this@ContacKefuActivity)
            mListAdapter!!.addAll(mDataList)
            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/mLRecyclerViewAdapter = LRecyclerViewAdapter(mListAdapter)
            //            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
//            mLRecyclerViewAdapter.addHeaderView(headerView);
            mRefreshListView.adapter = mLRecyclerViewAdapter
            mRefreshListView.itemAnimator = DefaultItemAnimator()
            mRefreshListView.setHasFixedSize(true)
            mRefreshListView.isNestedScrollingEnabled = false
            mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)
            mRefreshListView.setPullRefreshEnabled(true)
            mRefreshListView.setLoadMoreEnabled(true)
            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item: Map<String, Any> = mListAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                        intent.putExtra("jsonData",item.get("url")+"");
                        startActivity(intent);*/
            }
        } else {
            if (mPage == 1) {
                mListAdapter!!.clear()
            }
            mListAdapter!!.addAll(mDataList)
            mListAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged() //必须调用此方法
        }
        /*   //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOut); //设置下拉刷新Progress的样式
        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);
//设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);*/mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView.setNoMore(true)
        } else {
            mRefreshListView.setNoMore(false)
            mPage++
        }
        mRefreshListView.refreshComplete(10)
        mListAdapter!!.notifyDataSetChanged()
        if (mListAdapter!!.dataList.size <= 0) {
            mPageView.showEmpty()
        } else {
            mPageView.showContent()
        }
    }


    @OnClick(R.id.back_img, R.id.right_lay, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent? = null
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun showProgress() { //showProgressDialog();
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.CONTACT_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    val list = tData["data"] as MutableList<MutableMap<String, Any>>?
                    if (empty(list)) {
                        mPageView.showEmpty()
                    } else {
                        mDataList.clear()
                        mDataList.addAll(list!!)
                        responseData()
                    }

                }
                "0" -> {
                    closeAllActivity()
                    intent = Intent(this@ContacKefuActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                "-1" -> {
                    mPageView.showNetworkError()
                    showToastMsg(tData["msg"].toString() + "")
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.tradeList -> if (mListAdapter != null) {
                if (mListAdapter!!.dataList.isEmpty()) {
                    mPageView.showNetworkError()
                } else {
                    mPageView.showContent()
                }
                mRefreshListView.refreshComplete(10)
                mRefreshListView.setOnNetWorkErrorListener { traderListAction() }
            } else {
                mPageView!!.showNetworkError()
            }
        }
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        showProgressDialog()
        traderListAction()
    }


}