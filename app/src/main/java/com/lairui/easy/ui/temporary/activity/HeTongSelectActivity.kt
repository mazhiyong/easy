package com.lairui.easy.ui.temporary.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.ui.temporary.adapter.HeTongSelectAdapter
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.ParseTextUtil
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.temporary.adapter.HeTongSelectAdapter.*

/**
 * 选择合同
 */
class HeTongSelectActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.btn_next)
    lateinit var mBtnNext: Button
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.all_money_tv)
    lateinit var mAllMoneyTv: TextView

    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private lateinit var mAdapter: HeTongSelectAdapter
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private val mBooleanList = ArrayList<MutableMap<String, Any>>()
    private var mSelectMap: MutableMap<String, Any> = HashMap()


    //选择合同下的凭证列表信息
    private val mPingZhengList = ArrayList<MutableMap<String, Any>>()
    private val mSelectPingZhengList = ArrayList<MutableMap<String, Any>>()
    private var mBooleanPingZhengList: List<MutableMap<String, Any>> = ArrayList()

    private var mHeTongMap: MutableMap<String, Any> = HashMap()


    private var mRequestTag = ""
    private var mPayCompayName: String? = ""
    private var mPaycustid: String? = ""

    private var mSxMap: MutableMap<String, Any> = HashMap()


    override val contentView: Int
        get() = R.layout.activity_select_hetong


    private var mResponseType = ""


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val b = intent.extras
            if (MbsConstans.BroadcastReceiverAction.HTONGUPDATE == action) {
                showProgressDialog()
                htList()
            }
        }
    }

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.hetong_title)


        val filter = IntentFilter()
        filter.addAction(MbsConstans.BroadcastReceiverAction.HTONGUPDATE)
        registerReceiver(receiver, filter)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mPayCompayName = bundle.getString("payfirmname")
            mPaycustid = bundle.getString("paycustid")
            mSxMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }


        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = linearLayoutManager

        mRefreshListView!!.setOnRefreshListener { htList() }

        mRefreshListView!!.setOnLoadMoreListener {
            //payHistoryAction();
        }

        mRefreshListView!!.setOnNetWorkErrorListener { }

        showProgressDialog()
        htList()
    }

    private fun htList() {
        mRequestTag = MethodUrl.hetongInfo
        val map = HashMap<String, String>()
        map["paycustid"] = mPaycustid!!
        map["payfirmname"] = mPayCompayName!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.hetongInfo, map)
    }

    private fun ruchiAction() {
        //List<Map<String,Object>> mapList = (List<Map<String, Object>>) DataHolder.getInstance().retrieve("moneySelect");
        val list = ArrayList<MutableMap<String, Any>>()
        for (mm in mSelectPingZhengList) {
            val moneyMap = HashMap<String, Any>()
            moneyMap["flowdate"] = mm["flowdate"]!!.toString() + ""
            moneyMap["flowid"] = mm["flowid"]!!.toString() + ""
            moneyMap["sgndt"] = mm["sgndt"]!!.toString() + ""

            //入池状态：0：未入池  2：已入池
            val status = mm["poolsta"]!!.toString() + ""
            if (status == "0") {
                list.add(moneyMap)
            }
        }
        mRequestTag = MethodUrl.ruchiAction
        val map = HashMap<String, Any>()
        map["contno"] = mHeTongMap["contno"]!!.toString() + ""//贸易合同编号
        map["settid"] = mHeTongMap["settid"]!!.toString() + ""//贸易合同业务主键
        map["receivables"] = list//应收账款列表
        map["vchtrdtype"] = "0" //账款类型(0:应收账款 1:发票 )
        val mHeaderMap = HashMap<String, String>()
        LogUtil.i("入池操作传递的参数", map)
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.ruchiAction, map)
    }


    private fun yszkList() {
        mRequestTag = MethodUrl.yszkList
        val map = HashMap<String, String>()
        map["flowdate"] = mSxMap["flowdate"]!!.toString() + ""
        map["flowid"] = mSxMap["flowid"]!!.toString() + ""
        map["autoid"] = mSxMap["autoid"]!!.toString() + ""
        map["settid"] = mHeTongMap["settid"]!!.toString() + ""
        map["contno"] = mHeTongMap["contno"]!!.toString() + ""
        map["paycustid"] = mPaycustid!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.yszkList, map)
    }

    private fun initData() {

        val mm = "合计:500000元"

        val parseTextUtil = ParseTextUtil(this)

        val ms = parseTextUtil.getTextSpan(mm, ":")
        mAllMoneyTv!!.text = ms
    }


    override fun reLoadingData() {
        showProgress()
        htList()
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
        dismissProgressDialog()
    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.ruchiAction //申请入池
            -> {

                showToastMsg(tData["result"]!!.toString() + "")
                mBtnNext!!.isEnabled = true

                backTo(BorrowMoneyActivity::class.java, true)
            }
            MethodUrl.hetongInfo //合同列表
            -> {
                val list = tData["tradecontList"] as List<MutableMap<String, Any>>?

                if (list != null) {
                    mDataList.clear()
                    mDataList.addAll(list)
                }
                mResponseType = "1"
                responseData()
                mRefreshListView!!.refreshComplete(10)
            }

            MethodUrl.yszkList //应收账款列表
            -> {
                val listData = tData["yszkInfoList"] as List<MutableMap<String, Any>>?
                /* Map<String,Object> map = new HashMap<>();
                map.put("flowdate","20190511");
                map.put("poolsta","0");
                map.put("paymoney","10000");
                map.put("billid","201596276");
                map.put("paycustid","1905713000000191");
                map.put("payfirmname","测试");
                map.put("flowid","22");
                map.put("paydate","20190811");
                map.put("sgndt","20190511");
                listData.add(map);


                Map<String,Object> map1 = new HashMap<>();
                map1.put("flowdate","20190511");
                map1.put("poolsta","0");
                map1.put("paymoney","10000");
                map1.put("billid","201596276");
                map1.put("paycustid","1905713000000191");
                map1.put("payfirmname","测试222");
                map1.put("flowid","22");
                map1.put("paydate","20190811");
                map1.put("sgndt","20190511");
                listData.add(map1);*/
                if (listData != null) {
                    mPingZhengList.clear()
                    mPingZhengList.addAll(listData)
                }
                mResponseType = "2"
                responseData()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                showProgressDialog()
                when (mRequestTag) {
                    MethodUrl.hetongInfo -> htList()
                    MethodUrl.ruchiAction -> ruchiAction()
                    MethodUrl.yszkList -> yszkList()
                }
            }
        }/*intent = new Intent(HeTongSelectActivity.this, BorrowMoneyActivity.class);
                intent.putExtra("DATA",(Serializable)mSxMap);
                startActivity(intent);*/
    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.ruchiAction -> {
                mBtnNext!!.isEnabled = true
                if (mAdapter != null) {
                    if (mDataList.size <= 0) {
                        mPageView!!.showNetworkError()
                    } else {
                        mPageView!!.showContent()
                    }
                    mRefreshListView!!.refreshComplete(10)
                    mRefreshListView!!.setOnNetWorkErrorListener {
                        showProgressDialog()
                        htList()
                    }

                } else {
                    mPageView!!.showNetworkError()
                }
            }
            MethodUrl.hetongInfo//
            -> if (mAdapter != null) {
                if (mDataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener {
                    showProgressDialog()
                    htList()
                }
            } else {
                mPageView!!.showNetworkError()
            }
        }
        dealFailInfo(map, mType)
    }


    private fun responseData() {
        Log.i("show", "mPingZheng:" + mPingZhengList.size)

        mBooleanList?.clear()

        if (mResponseType == "2") { //请求凭证列表
            for (m in mDataList) {
                val map = HashMap<String, Any>()
                map["value"] = m
                if (m == mHeTongMap) {
                    map["list"] = mPingZhengList
                    map["selected"] = true
                } else {
                    val mPZList = ArrayList<MutableMap<String, Any>>()
                    map["selected"] = false
                    map["list"] = mPZList

                }

                mBooleanList.add(map)
            }
        } else {

            for (m in mDataList) {
                val map = HashMap<String, Any>()
                map["value"] = m
                val mPZList = ArrayList<MutableMap<String, Any>>()
                map["selected"] = false
                map["list"] = mPZList

                mBooleanList.add(map)
            }
        }



        if (mAdapter == null) {
            mAdapter = HeTongSelectAdapter(this, mDataList,object : OnChangeBankCardListener {
                override fun onButClickListener(type: String, map: MutableMap<String, Any>) {
                    mRefreshListView!!.smoothScrollToPosition(0)
                    val intent: Intent
                    when (type) {
                        "1" -> {
                            intent = Intent(this@HeTongSelectActivity, HeTongAddActivity::class.java)
                            intent.putExtra("payfirmname", mPayCompayName)
                            intent.putExtra("paycustid", mPaycustid)
                            startActivity(intent)
                        }
                        "2" -> {
                            //请求当前合同下应收账款列表
                            mHeTongMap = map
                            showProgressDialog()
                            yszkList()
                            val list = mAdapter!!.booleanList
                            for (mm in list) {
                                val b = (mm["selected"] as Boolean?)!!
                                mSelectMap = (mm["value"] as MutableMap<String, Any>?)!!
                            }

                            mRefreshListView!!.post {
                                mAdapter!!.notifyDataSetChanged()
                                mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
                            }
                        }
                    }/* intent = new Intent(HeTongSelectActivity.this, BorrowMoneyActivity.class);
                                        intent.putExtra("payfirmname",mPayCompayName);
                                        intent.putExtra("paycustid",mPaycustid);
                                        startActivity(intent);*/
                }
            })

            mAdapter!!.booleanList = mBooleanList
            val view = LayoutInflater.from(this).inflate(R.layout.item_hetong_add, mRefreshListView, false)
            //View view = LayoutInflater.from(this).inflate(R.layout.item_bank_bind, null);

            mAdapter!!.addFooterView(view)

            mAdapter!!.setClickListener { list, mParentMap ->
                mBooleanPingZhengList = list as List<MutableMap<String, Any>>
                mHeTongMap = mParentMap as MutableMap<String, Any>
            }

            /*mAdapter.setBooleanList(mBooleanList);
            mAdapter.addAll(mDataList);*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mAdapter)
            //SampleHeader headerView = new SampleHeader(getActivity(), R.layout.fragment_home_head_view);
            //mLRecyclerViewAdapter.addHeaderView(headerView);
            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(false)

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView!!.isFocusableInTouchMode = false

            //int spacing = getResources().getDimensionPixelSize(R.dimen.divide_hight);
            //mRefreshListView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, gridLayoutManager.getSpanCount(), Color.GRAY));
            //根据需要选择使用GridItemDecoration还是SpacesItemDecoration
            val divider = GridItemDecoration.Builder(this)
                    .setHorizontal(R.dimen.divide_hight)
                    .setVertical(R.dimen.divide_hight)
                    .setColorResource(R.color.divide_line)
                    .build()
            //mRefreshListView.addItemDecoration(divider);

            val divider2 = DividerDecoration.Builder(this)
                    .setHeight(R.dimen.divide_hight)
                    //.setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.divide_line)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)


        } else {
            /* if (mPage == 1) {
                mRepaymentAdapter.clear();
            }*/
            mAdapter!!.booleanList = mBooleanList
            mAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        if (mDataList.size <= 0) {
            mPageView!!.showEmpty()

        } else {
            mPageView!!.showContent()
        }
    }


    @OnClick(R.id.left_back_lay, R.id.btn_next)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.btn_next -> {
                Log.i("show", "total_size:" + mBooleanPingZhengList.size)
                mSelectPingZhengList.clear()
                if (mBooleanPingZhengList.size == 0) { //默认全选状态 用户无点击取消勾选操作
                    for (map in mPingZhengList) {
                        if (map["poolsta"]!!.toString() + "" == "0") {
                            mSelectPingZhengList.add(map)
                        }
                    }
                } else {
                    for (map in mBooleanPingZhengList) {
                        val b = (map["selected"] as Boolean?)!!
                        val mSelectMap = map["value"] as MutableMap<String, Any>?
                        if (b && mSelectMap!!["poolsta"]!!.toString() + "" == "0") {
                            mSelectPingZhengList.add(mSelectMap)
                        }
                    }
                }

                Log.i("show", "select_size:" + mSelectPingZhengList.size)
                if (mSelectPingZhengList.size > 0) {
                    mBtnNext!!.isEnabled = false
                    ruchiAction()
                } else {
                    showToastMsg("请先选择收款凭证")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}
