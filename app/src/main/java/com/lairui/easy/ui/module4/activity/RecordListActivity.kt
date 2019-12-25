package com.lairui.easy.ui.module4.activity

import android.content.Intent
import android.view.Gravity
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
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module4.adapter.RecordListAdapter
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.SPUtils
import kotlinx.android.synthetic.main.activity_records_list.*

/**
 * 记录列表 界面
 */
class RecordListActivity : BasicActivity(), RequestView, ReLoadingData, SelectBackListener {



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

    private var mRecordAdapter: RecordListAdapter? = null
    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1

    private var type: String =""
    private var mark: String = ""

    private lateinit var mySelectDialog: DateSelectDialog
    private lateinit var mySelectDialog2: DateSelectDialog

    private var mStartTime = ""
    private var mEndTime = ""


    override val contentView: Int
        get() = R.layout.activity_records_list

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val  bundle = intent.extras
        if (bundle != null){
            type = bundle["TYPE"].toString()
            when(type){
                "1"->{
                    mTitleText.text = "交易记录"
                    tradeListAction()
                }
                "2"->{
                    mTitleText.text = "追加保证金记录"
                    mark = bundle.getString("mark")
                    bondMoneyListAction(mark)
                }
                "3"->{
                    mTitleText.text = "扩大配资记录"
                    mark = bundle.getString("mark")
                    extendMoneyListAction(mark)
                }
                "4"->{
                    mTitleText.text = "提取收益记录"
                    mark = bundle.getString("mark")
                    shouyiListAction(mark)
                }
                "5"->{
                    mTitleText.text = "支付利息记录"
                    mark = bundle.getString("mark")
                    lixiListAction(mark)
                }
                "6"->{
                    mTitleText.text = "资金流水"
                    mark = bundle.getString("mark")
                    zijinListAction(mark)
                    dateLay.visibility = View.VISIBLE
                    mySelectDialog = DateSelectDialog(this, true, "选择日期", 21)
                    mySelectDialog.selectBackListener = this
                    mySelectDialog2 = DateSelectDialog(this, true, "选择日期", 22)
                    mySelectDialog2.selectBackListener = this


                }

            }
        }else{
            finish()
        }


        mRightLay.visibility = View.GONE
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
        val manager = LinearLayoutManager(this@RecordListActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView.layoutManager = manager

        mRefreshListView.setOnRefreshListener {
            //mPage = 1
            when(type){
                "1"->{
                    tradeListAction()
                }
                "2"->{
                    bondMoneyListAction(mark)
                }
                "3"->{
                    extendMoneyListAction(mark)
                }
                "4"->{
                    shouyiListAction(mark)
                }
                "5"->{
                    lixiListAction(mark)
                }
                "6"->{
                    zijinListAction(mark)
                }

            }

        }

        mRefreshListView.setOnLoadMoreListener {
          /*  mPage++
            tradeListAction()*/
            mRefreshListView.setNoMore(true)
        }
    }

    private fun zijinListAction(mark :String) {

        mRequestTag = MethodUrl.TRADEFLOW_LIST
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.TRADEFLOW_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@RecordListActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        map["start"] = mStartTime
        map["end"] = mEndTime
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TRADEFLOW_LIST, map)
    }


    private fun lixiListAction(mark :String) {

        mRequestTag = MethodUrl.LIXI_LIST
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.LIXI_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@RecordListActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.LIXI_LIST, map)
    }


    private fun shouyiListAction(mark :String) {

        mRequestTag = MethodUrl.PROFIT_LIST
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PROFIT_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@RecordListActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PROFIT_LIST, map)
    }


    private fun extendMoneyListAction(mark :String) {

        mRequestTag = MethodUrl.CAPITAL_LIST
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CAPITAL_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@RecordListActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CAPITAL_LIST, map)
    }


    private fun bondMoneyListAction(mark :String) {

        mRequestTag = MethodUrl.BOND_LIST
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.BOND_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@RecordListActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.BOND_LIST, map)
    }

    private fun tradeListAction() {

        mRequestTag = MethodUrl.TRADE_LIST
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.TRADE_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@RecordListActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TRADE_LIST, map)
    }


    private fun responseData() {
        if (mRecordAdapter == null) {
            mRecordAdapter = RecordListAdapter( this@RecordListActivity)
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
            mPageView.showEmpty()
        } else {
            mPageView.showContent()
        }

    }

    @OnClick(R.id.left_back_lay,R.id.start_time_value_tv,R.id.end_time_value_tv,R.id.queryIv)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> {
                finish()
            }
            R.id.start_time_value_tv -> {
                mySelectDialog.showAtLocation(Gravity.BOTTOM, 0, 0)
            }
            R.id.end_time_value_tv -> {
                mySelectDialog2.showAtLocation(Gravity.BOTTOM, 0, 0)
            }
            R.id.queryIv -> {
                zijinListAction(mark)
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
            MethodUrl.TRADEFLOW_LIST,MethodUrl.LIXI_LIST,MethodUrl.PROFIT_LIST,MethodUrl.BOND_LIST ,MethodUrl.CAPITAL_LIST,MethodUrl.TRADE_LIST ->{
                when (tData["code"].toString() + "") {
                    "1" -> {
                        if (!UtilTools.empty(tData["data"])){
                            val result = tData["data"]!!.toString()
                            if (UtilTools.empty(result)) {
                                mPageView.showEmpty()
                            } else {
                                val list = tData["data"] as  MutableList<MutableMap<String,Any>>
                                if (list.isNotEmpty()) {
                                    mDataList.clear()
                                    mDataList.addAll(list)
                                    responseData()
                                    mRefreshListView.refreshComplete(10)
                                }else{
                                    mPageView.showEmpty()
                                }
                            }

                        }
                    }
                    "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                    "-1" -> {
                        closeAllActivity()
                        val intent = Intent(this@RecordListActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.hetongList//
            -> if (mRecordAdapter != null) {
                if (mRecordAdapter!!.dataList.isEmpty()) {
                    mPageView.showNetworkError()
                } else {
                    mPageView.showContent()
                }
                mRefreshListView.refreshComplete(10)
                mRefreshListView.setOnNetWorkErrorListener { tradeListAction() }
            } else {
                mPageView.showNetworkError()
            }
        }

        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        tradeListAction()
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            21 -> {
                mStartTime =map["year"].toString() + "-" + map["month"] + "-" + map["day"]
                start_time_value_tv.text = map["year"].toString() + "-" + map["month"] + "-" + map["day"]
            }
            22 -> {
                mEndTime = map["year"].toString() + "-" + map["month"] + "-" + map["day"]
                end_time_value_tv.text = map["year"].toString() + "-" + map["month"] + "-" + map["day"]

            }
        }
    }


}
