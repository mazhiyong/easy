package com.lairui.easy.ui.module2.fragment

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.androidkun.xtablayout.XTabLayout
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module1.adapter.CoinInfoAdapter
import com.lairui.easy.ui.module2.activity.SearchListActivity
import com.lairui.easy.ui.module2.adapter.BuyAndSellAdapter
import com.lairui.easy.ui.module2.adapter.ChicangListAdapter
import com.lairui.easy.ui.module2.adapter.HangqingListAdapter
import com.lairui.easy.utils.tool.*
import kotlinx.android.synthetic.main.activity_buyandsell.*
import kotlinx.android.synthetic.main.fragment_hangqing.*
import java.util.*
import kotlin.collections.HashMap

class HangQingFragment : BasicFragment(), RequestView, ReLoadingData, SelectBackListener {
    @BindView(R.id.tab_layout)
    lateinit var mTabLayout: XTabLayout
    @BindView(R.id.right_img)
    lateinit var mSeacherIv: ImageView
    @BindView(R.id.rvHoriList)
    lateinit var mRvHoriList: RecyclerView
    @BindView(R.id.rg)
    lateinit var mRg: RadioGroup
    @BindView(R.id.zhangFuRb)
    lateinit var mZhangFuRb: RadioButton
    @BindView(R.id.dieFuRb)
    lateinit var mDieFuRb: RadioButton
    @BindView(R.id.huanHandRb)
    lateinit var mHuanHandRb: RadioButton
    @BindView(R.id.zhenFuRb)
    lateinit var mZhenFuRb: RadioButton
    @BindView(R.id.liangBiRb)
    lateinit var mLiangBiRb: RadioButton
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.title_bar_view)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.hScrollView)
    lateinit var hScrollView: HorizontalScrollView




    private lateinit var mLoadingWindow: LoadingWindow

    private var coinInfoAdapter: CoinInfoAdapter? = null

    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mHangqingAdapter: HangqingListAdapter? = null
    private var mRequestTag = ""

    private val mDataList = ArrayList<MutableMap<String, Any>>()



    private lateinit var mAnimUtil: AnimUtil

    private val listUp: MutableList<Map<String, Any>> = ArrayList()


    private lateinit var popView: View
    private lateinit var mConditionDialog: PopupWindow
    private var bright = false
    private var postion  = 0

    override val layoutId: Int
        get() = R.layout.fragment_hangqing



    override fun init() {

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, activity!!.resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(activity!!))
        mTitleBarView.layoutParams = layoutParams
        mTitleBarView.setPadding(0, UtilTools.getStatusHeight2(activity!!), 0, 0)

        mTabLayout.addTab(mTabLayout.newTab().setText("行情"))
        mTabLayout.addTab(mTabLayout.newTab().setText("自选"))
        mTabLayout.addOnTabSelectedListener(object :XTabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                when(tab!!.position){
                    0 ->{
                        rvHoriList.visibility = View.VISIBLE
                        hScrollView.visibility = View.VISIBLE

                        homeInfoAction()
                        when(postion){
                            0 -> listAction("new_all_changepercent_up")
                            1 -> listAction("new_all_changepercent_down")
                            2 -> listAction("new_all_turnoverrate")
                            3 -> zhenfuInfoAction()
                            4 -> liangbiInfoAction()
                        }

                    }

                    1 ->{
                        rvHoriList.visibility = View.GONE
                        hScrollView.visibility = View.GONE

                        getMyselfList()
                    }
                }
            }

        })





        mAnimUtil = AnimUtil()
        mLoadingWindow = LoadingWindow(activity!!, R.style.Dialog)
        mContent?.let { mPageView.setContentView(it) }
        mPageView.showLoading()
        mPageView.reLoadingData = this
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView.layoutManager = linearLayoutManager

        mRefreshListView.setOnRefreshListener {
            mLoadingWindow.showView()
            when(mTabLayout.selectedTabPosition){
                0 ->{
                    when(postion){
                        0 -> listAction("new_all_changepercent_up")
                        1 -> listAction("new_all_changepercent_down")
                        2 -> listAction("new_all_turnoverrate")
                        3 -> zhenfuInfoAction()
                        4 -> liangbiInfoAction()
                    }
                }
                1 ->{
                    getMyselfList()
                }

            }


        }
        mRefreshListView.setOnLoadMoreListener{
            mRefreshListView.setNoMore(true)
        }



      /*  for (index in 1..5){
            val map = HashMap<String,Any>()
            map["name"] = "上证指数"+index
            map["price"] = "18210.00"
            map["cny"] = "+22.22"
            map["ratio"] = "+11.09%"
            listUp.add(map)
        }*/
        coinInfoAdapter = CoinInfoAdapter(activity!!)
        mRvHoriList.adapter = coinInfoAdapter



        /*for (inde in 1..10){
            val map = HashMap<String,Any>()
            map["name"] = "莱瑞科技"
            map["price"] = "18210.00"
            map["ratio"] = "+11.09%"
            map["type"] = "SZ"
            map["number"] = "10086"
            mDataList.add(map)
        }

        mPageView.showContent()
        responseData()*/


        setBarTextColor()

        //mLoadingWindow.showView()
        listAction("new_all_changepercent_up")
        homeInfoAction()

    }

    //我的自选列表
    private fun getMyselfList() {
        val map = java.util.HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CONCERN_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[activity!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = java.util.HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CONCERN_LIST, map)
    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }

    private fun listAction(params :String)  {

        mRequestTag = params
        val map = HashMap<String, String>()
        map["list"] = params
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.HANGQING_SERVER_URL, map)
    }

    //获取详情
    private fun getDetialDataAction(code:String) {
        val map = HashMap<String, String>()
        map["q"] = code
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.DETIAL_SERVER_URL, map)
    }


    private fun homeInfoAction()  {
        val map = HashMap<String, String>()
        map["stock_index"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.HOME_SERVER_URL, map)
    }

    private fun zhenfuInfoAction()  {
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.ZHENFU_SERVER_URL, map)
    }


    private fun liangbiInfoAction()  {
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.LIANGBI_SERVER_URL, map)
    }


    @OnClick(R.id.right_img,R.id.zhangFuRb,R.id.dieFuRb,R.id.huanHandRb,R.id.zhenFuRb,R.id.liangBiRb)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.right_img -> {
                val  intent = Intent(activity,SearchListActivity::class.java)
                startActivity(intent)
            }
            R.id.zhangFuRb -> {
                postion = 0
                listAction("new_all_changepercent_up")
            }
            R.id.dieFuRb -> {
                postion = 1
                listAction("new_all_changepercent_down")
            }
            R.id.huanHandRb -> {
                postion = 2
                listAction("new_all_turnoverrate")
            }
            R.id.zhenFuRb -> {
                postion = 3
                zhenfuInfoAction()
            }
            R.id.liangBiRb -> {
                postion = 4
                liangbiInfoAction()
            }

        }
    }


    private fun responseData() {
        if (mHangqingAdapter == null) {
            mHangqingAdapter = HangqingListAdapter(activity!!)
            mHangqingAdapter!!.addAll(mDataList)

       /*     val adapter1 = ScaleInAnimationAdapter(mHangqingAdapter)
            adapter1.setFirstOnly(false)
            adapter1.setDuration(400)
            adapter1.setInterpolator(OvershootInterpolator(0.8f))


            val adapter = AlphaInAnimationAdapter(adapter1)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(1f))*/

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/
            val view: View = LayoutInflater.from(activity).inflate(R.layout.item_hangqing_header, mRefreshListView, false)
            //View view = LayoutInflater.from(this).inflate(R.layout.item_bank_bind, null);
            //View view = LayoutInflater.from(this).inflate(R.layout.item_bank_bind, null);
            mHangqingAdapter!!.addHeaderView(view)


            mLRecyclerViewAdapter = LRecyclerViewAdapter(mHangqingAdapter)
            //SampleHeader headerView = new SampleHeader(getActivity(), R.layout.fragment_home_head_view);
            //mLRecyclerViewAdapter.addHeaderView(headerView);
            mRefreshListView.adapter = mLRecyclerViewAdapter
            mRefreshListView.itemAnimator = DefaultItemAnimator()
            mRefreshListView.setHasFixedSize(true)
            mRefreshListView.isNestedScrollingEnabled = false

            mRefreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
            mRefreshListView.setPullRefreshEnabled(true)
            mRefreshListView.setLoadMoreEnabled(true)

            mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            //int spacing = getResources().getDimensionPixelSize(R.dimen.divide_hight);
            //mRefreshListView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, gridLayoutManager.getSpanCount(), Color.GRAY));
            //根据需要选择使用GridItemDecoration还是SpacesItemDecoration
            val divider = GridItemDecoration.Builder(activity!!)
                    .setHorizontal(R.dimen.divide_hight)
                    .setVertical(R.dimen.divide_hight)
                    .setColorResource(R.color.divide_line)
                    .build()
            //mRefreshListView.addItemDecoration(divider);

           /* val divider2 = DividerDecoration.Builder(activity!!)
                    .setHeight(R.dimen.dp_10)
                    .setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.body_bg)
                    .build()
            mRefreshListView.addItemDecoration(divider2)*/

            mLRecyclerViewAdapter.setOnItemClickListener { view, position ->
                val item = mHangqingAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {

            /* if (mPage == 1) {
                mRepaymentAdapter.clear();
            }*/
            mHangqingAdapter!!.clear()
            mHangqingAdapter!!.addAll(mDataList)
            mHangqingAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter.notifyDataSetChanged()//必须调用此方法
        }

        mRefreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView.setNoMore(true)
        } else {
            mRefreshListView.setNoMore(false)
        }

        if (mHangqingAdapter!!.dataList.isEmpty()) {
            mPageView.showEmpty()
        } else {
            mPageView.showContent()
        }
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow.cancleView()
        when (mType) {
            MethodUrl.CONCERN_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (!UtilTools.empty(tData["data"].toString())){
                        val listStr =JSONUtil.instance.jsonToListStr(tData["data"].toString())
                        if (listStr!!.isNotEmpty()){
                            var paramCode = ""
                            for (item in listStr){
                                paramCode = paramCode+item+","
                            }
                            getDetialDataAction(paramCode)

                        }else{
                            mPageView.showEmpty()
                        }
                    }else{
                        mPageView.showEmpty()
                    }


                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    activity!!.finish()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MbsConstans.DETIAL_SERVER_URL-> {
               //列表数据
                    val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    mPageView.showEmpty()
                } else {
                    handInfoDataList(result)
                    if (mDataList.size> 0){
                        responseData()
                        mRefreshListView.refreshComplete(10)
                    }else{
                        mPageView.showEmpty()
                    }
                }
            }



            MbsConstans.HANGQING_SERVER_URL -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    mPageView.showEmpty()
                } else {
                    handleDate(result)
                    if (mDataList.size> 0){
                        responseData()
                        mRefreshListView.refreshComplete(10)
                    }else{
                        mPageView.showEmpty()
                    }
                }
            }
            MbsConstans.HOME_SERVER_URL -> {
                val result = tData["result"]!!.toString() + ""
                if (!UtilTools.empty(result)) {
                    handleData2(result)
                    if (listUp.size > 0){
                        coinInfoAdapter!!.setList(listUp)
                    }
                }
            }

            MbsConstans.ZHENFU_SERVER_URL -> {
                val result = tData["result"]!!.toString() + ""
                if (!UtilTools.empty(result)) {
                    handleData3(result)
                    if (mDataList.size> 0){
                        responseData()
                        mRefreshListView.refreshComplete(10)
                    }else{
                        mPageView.showEmpty()
                    }
                }
            }

            MbsConstans.LIANGBI_SERVER_URL -> {
                val result = tData["result"]!!.toString() + ""
                if (!UtilTools.empty(result)) {
                    handleData3(result)
                    if (mDataList.size> 0){
                        responseData()
                        mRefreshListView.refreshComplete(10)
                    }else{
                        mPageView.showEmpty()
                    }
                }
            }


        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MbsConstans.ZHENFU_SERVER_URL -> if (mHangqingAdapter != null) {
                if (mHangqingAdapter!!.dataList.isEmpty()) {
                    mPageView.showNetworkError()
                } else {
                    mPageView.showContent()
                }
                mRefreshListView.refreshComplete(10)
                mRefreshListView.setOnNetWorkErrorListener {
                    zhenfuInfoAction()
                }
            } else {
                mPageView.showNetworkError()
            }

            MbsConstans.LIANGBI_SERVER_URL -> if (mHangqingAdapter != null) {
                if (mHangqingAdapter!!.dataList.isEmpty()) {
                    mPageView.showNetworkError()
                } else {
                    mPageView.showContent()
                }
                mRefreshListView.refreshComplete(10)
                mRefreshListView.setOnNetWorkErrorListener {
                    liangbiInfoAction()
                }
            } else {
                mPageView.showNetworkError()
            }

            MbsConstans.HANGQING_SERVER_URL -> if (mHangqingAdapter != null) {
                if (mHangqingAdapter!!.dataList.isEmpty()) {
                    mPageView.showNetworkError()
                } else {
                    mPageView.showContent()
                }
                mRefreshListView.refreshComplete(10)
                mRefreshListView.setOnNetWorkErrorListener {  if (!UtilTools.empty(mRequestTag)){
                    listAction(mRequestTag)
                } }
            } else {
                mPageView.showNetworkError()
            }


            MbsConstans.HOME_SERVER_URL -> if (mHangqingAdapter != null) {
                if (mHangqingAdapter!!.dataList.isEmpty()) {
                    mPageView.showNetworkError()
                } else {
                    mPageView.showContent()
                }
                mRefreshListView.refreshComplete(10)
                mRefreshListView.setOnNetWorkErrorListener {  if (!UtilTools.empty(mRequestTag)){
                    listAction(mRequestTag)
                } }
            } else {
                mPageView.showNetworkError()
            }

        }


        mLoadingWindow.cancleView()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        mLoadingWindow.showView()
        if (!UtilTools.empty(mRequestTag)){
            listAction(mRequestTag)
        }
    }


        fun handleDate(body: String) {
        val substring = body.substring(body.indexOf("=\"")+2, body.indexOf("]\"")+1)
        val split = substring.replace("'","\"")
        val  jsonStr = JSONUtil.instance.jsonToListStr2(split)
            if (jsonStr != null) {
                mDataList.clear()
                for (s in jsonStr){
                    val map = HashMap<String,Any>()
                    map["code"] = s[0]
                    map["name"] = s[1]
                    map["price"] = s[2]
                    map["rise"] = s[3]
                    mDataList.add(map)
                }
            }
    }


    private fun handInfoDataList(result: String){
        val infoDataList : MutableList<MutableMap<String,Any?>> = ArrayList()
        if (!TextUtils.isEmpty(result) && result.contains("~")) {
            val stockArray = result.split(";\n").toTypedArray()
            mDataList.clear()
            for (stockInfo in stockArray) {
                val split = stockInfo.split("~").toTypedArray()
                val map = HashMap<String,Any>()
                map["code"] = split[0].substring(2,10)
                map["name"] = split[1]
                map["price"] =split[3]
                map["rise"] = split[32]
                mDataList.add(map)
            }

        }

    }





    fun handleData2(result: String) {
        if (!TextUtils.isEmpty(result) && result.contains("~")) {
            listUp.clear()
            val stockArray: Array<String> = result.split(";\n").toTypedArray()
            for (stockInfo in stockArray) {
                val map = HashMap<String,Any>()
                val split = stockInfo.split("~").toTypedArray()
                map["name"] = split[1]
                map["price"] = split[3]
                map["amount"] = split[31]
                map["ratio"] = split[32]
                listUp.add(map)
            }

        }

    }

    fun handleData3(result: String) {
        if (!TextUtils.isEmpty(result)) {
            mDataList.clear()
            val resultStr = result.substring(result.indexOf("HqData:")+7,result.length-2)
            LogUtil.i("show","resultStr:"+resultStr)
            val stockArray = JSONUtil.instance.jsonToListStr2(resultStr)
            for (item in stockArray!!) {
                    val map = HashMap<String,Any>()
                    map["code"] = item[0]
                    map["name"] = item[2]
                    map["price"] = item[3]
                    map["rise"] = item[7]
                    mDataList.add(map)

            }

        }

    }





    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            21 -> {

            }
            22 -> {

            }
        }
    }
}
