package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.db.FaPiaoData
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.ui.temporary.adapter.InvoiceListAdapter
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick

/**
 * 已经导入的发票列表   界面
 */
class InvoiceListActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.tv_message)
   lateinit var mTvMessage: TextView
    @BindView(R.id.tv_message2)
   lateinit var mTvMessage2: TextView


    private var mRequestTag = ""

    private lateinit var mInvoiceListAdapter: InvoiceListAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mDataList: MutableList<MutableMap<String, Any>> = ArrayList()
    private var mPage = 1


    override val contentView: Int
        get() = R.layout.activity_invoice_list


    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            responseData()
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.sao_result)

        mRightImg!!.visibility = View.VISIBLE
        mRightImg!!.setImageResource(R.drawable.shuaixuan)
        mRightTextTv!!.visibility = View.VISIBLE
        mRightLay!!.visibility = View.GONE

        initView()

        //获取数据库发票信息
        mDataList = FaPiaoData.instance.selectDB() as MutableList<MutableMap<String, Any>>
        responseData()
        //        showProgressDialog();
        //        samePeopleList();
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent != null) {
            val bundle = intent.extras
            if (bundle != null) {

            }
        }
        super.onNewIntent(intent)
    }


    override fun onResume() {
        super.onResume()
        //获取数据库发票信息
        mDataList = FaPiaoData.instance.selectDB() as MutableList<MutableMap<String, Any>>
        responseData()
    }

    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this
        val manager = LinearLayoutManager(this@InvoiceListActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager

        mRefreshListView!!.setOnRefreshListener {
            mPage = 1
            mDataList.clear()
            mDataList = FaPiaoData.instance.selectDB() as MutableList<MutableMap<String, Any>>
            responseData()
        }

        mRefreshListView!!.setOnLoadMoreListener {
            mPage++
            mHandler.sendEmptyMessageDelayed(1, 3000)
        }
    }


    private fun samePeopleList() {

        mRequestTag = MethodUrl.peopleList
        val map = HashMap<String, String>()
        map["creditstate"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.peopleList, map)
    }


    private fun responseData() {

        /* for (int i = 0;i<10;i++){
            mDataList.add(new HashMap<>());
        }
*/
        mTvMessage!!.text = "已成功扫描 " + mDataList.size + " 张发票"
        mTvMessage2!!.text = "已成功扫描 " + mDataList.size + " 张发票"

        if (mInvoiceListAdapter == null) {
            mInvoiceListAdapter = InvoiceListAdapter(this@InvoiceListActivity)
            mInvoiceListAdapter!!.addAll(mDataList)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mInvoiceListAdapter)


            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(true)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mInvoiceListAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {
            if (mPage == 1) {
                mInvoiceListAdapter!!.clear()
            }
            mInvoiceListAdapter!!.addAll(mDataList)
            mInvoiceListAdapter!!.notifyDataSetChanged()
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
        mInvoiceListAdapter!!.notifyDataSetChanged()
        if (mInvoiceListAdapter!!.dataList.size < 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }


    @OnClick(R.id.back_img, R.id.right_lay, R.id.left_back_lay, R.id.save_tv, R.id.next_tv)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.right_lay -> {
            }
            R.id.next_tv -> {
                intent = Intent(this, TestScanActivity::class.java)
                intent.putExtra("type", "2")
                startActivity(intent)
            }
            R.id.save_tv -> {
                intent = Intent(this, InvoiceImportActivity::class.java)
                intent.putExtra("type", "2")
                startActivity(intent)

                //清空数据库信息
                FaPiaoData.instance.clearData()
            }
        }
    }

    override fun showProgress() {
        // showProgressDialog();
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {
            MethodUrl.peopleList//
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
                    MethodUrl.peopleList -> samePeopleList()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.peopleList//
            ->

                if (mInvoiceListAdapter != null) {
                    if (mInvoiceListAdapter!!.dataList.size <= 0) {
                        mPageView!!.showNetworkError()
                    } else {
                        mPageView!!.showContent()
                    }
                    mRefreshListView!!.refreshComplete(10)
                    mRefreshListView!!.setOnNetWorkErrorListener { samePeopleList() }
                } else {
                    mPageView!!.showNetworkError()
                }
        }
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        showProgressDialog()
        samePeopleList()
    }

}
