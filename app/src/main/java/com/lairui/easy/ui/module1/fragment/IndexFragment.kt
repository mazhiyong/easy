package com.lairui.easy.ui.module1.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.OnClick
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.MyRefreshHeader
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.mywidget.view.PullScrollView
import com.lairui.easy.mywidget.view.TipsToast.Companion.showToastMsg
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module.activity.MainActivity
import com.lairui.easy.ui.module1.activity.NewsListActivity
import com.lairui.easy.ui.module1.activity.NoticeDetialActivity
import com.lairui.easy.ui.module1.activity.NoticeListActivity
import com.lairui.easy.ui.module1.adapter.CoinInfoAdapter
import com.lairui.easy.ui.module1.adapter.MainCoinAdapter
import com.lairui.easy.ui.module1.adapter.NewsListAdapter
import com.lairui.easy.ui.module5.activity.FreeActivity
import com.lairui.easy.ui.temporary.activity.ApplyAmountActivity
import com.lairui.easy.ui.temporary.activity.BankTiXianModifyActivity
import com.lairui.easy.ui.temporary.activity.BorrowMoneyActivity
import com.lairui.easy.ui.temporary.activity.ChongZhiCardAddActivity
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import com.sunfusheng.marqueeview.MarqueeView
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

@SuppressLint("ValidFragment")
class IndexFragment : BasicFragment(), RequestView, SelectBackListener,ReLoadingData {

    @BindView(R.id.refresh_layout)
    lateinit var refreshLayout: PullScrollView
    @BindView(R.id.vpQuotesInfo)
    lateinit var vpQuotesInfo: ViewPager
    @BindView(R.id.rvHoriList)
    lateinit var rvHoriList: RecyclerView
    @BindView(R.id.pageView)
    lateinit var pageView: PageView
    @BindView(R.id.contentLay)
    lateinit var contentLay: LinearLayout
    @BindView(R.id.rvList)
    lateinit var rvList: RecyclerView
    @BindView(R.id.marqueeView)
    lateinit var marqueeView: MarqueeView<Any>
    @BindView(R.id.peiziLay)
    lateinit var peiziLay: CardView
    @BindView(R.id.dayLay)
    lateinit var dayLay: LinearLayout
    @BindView(R.id.monthLay)
    lateinit var monthLay: LinearLayout
    @BindView(R.id.lixiLay)
    lateinit var lixiLay: LinearLayout
    @BindView(R.id.freeLay)
    lateinit var freeLay: LinearLayout






    private var noticeList: MutableList<MutableMap<String, Any>?>? = null

    private var coinInfoAdapter: CoinInfoAdapter? = null
    private var mainCoinAdapter: MainCoinAdapter? = null
    private val listUp: MutableList<Map<String, Any>> = ArrayList()


    private var newsAdapter: NewsListAdapter? = null
    private var listNews: MutableList<MutableMap<String, Any>> = ArrayList()


    private lateinit var mZhengshuMap: MutableMap<String, Any>
    private lateinit var mHezuoMap: MutableMap<String, Any>
    private lateinit var mJieKuanMap: MutableMap<String, Any>

    private lateinit var mLoadingWindow: LoadingWindow

    private var handler = android.os.Handler()

    override val layoutId: Int
        get() = R.layout.fragment_circle_view2


    override fun init() {
        initView()
    }


    private fun initView() {

        mLoadingWindow = LoadingWindow(activity!!, R.style.Dialog)
        mLoadingWindow.setOnDismissListener {
            refreshLayout.setRefreshCompleted()
        }



        refreshLayout = mRootView!!.findViewById<View>(R.id.refresh_layout) as PullScrollView
        //设置头部加载颜色
        refreshLayout.setRefreshHeader(MyRefreshHeader(activity!!))

        refreshLayout.setRefreshListener(object:PullScrollView.RefreshListener {
            override fun onRefresh() {
                refreshLayout.setRefreshCompleted()
                mLoadingWindow.cancleView()
            }

        })

        mLoadingWindow.showView()
        setBarTextColor()






      /*  for (index in 1..5){
            val map = HashMap<String,Any>()
            map["name"] = "上证指数"+index
            map["price"] = "18210.00"
            map["cny"] = "+22.22"
            map["ratio"] = "+11.09%"
            listUp.add(map)
        }*/
        mainCoinAdapter = MainCoinAdapter(activity!!, listUp)
        vpQuotesInfo.adapter = mainCoinAdapter
        mainCoinAdapter!!.setData(listUp,vpQuotesInfo.currentItem)

        coinInfoAdapter = CoinInfoAdapter(activity!!)
        rvHoriList.adapter = coinInfoAdapter
        //rvHoriList.addItemDecoration(SpaceItemDecoration(UtilTools.dip2px(activity!!,10),3))
        coinInfoAdapter!!.setList(listUp)

/*
        for (index in 1..3){
            val map = HashMap<String,Any>()
            map["title"] = "外交部召见美驻华使馆负责人 就美国会众议院通过涉疆法案提出严正交涉和强烈抗议"
            map["content"] = "12月4日，中国外交部副部长秦刚召见美国驻华使馆负责人柯有为，就美国会众议院审议通过“2019年维吾尔人权政策法案”提出严正交涉和强烈抗议，敦促美方立即纠正错误，停止借涉疆问题干涉中国内政。"
            map["url"] = "https://pics7.baidu.com/feed/9345d688d43f8794bbb8b935c6d1d7f11ad53ab1.jpeg?token=8bb505873039703b2500e78125e257ed&s=569139C47448935D0A512F9503005084"
            listNews.add(map)
        }*/


        //        Resources resources = getActivity().getResources();
//        Drawable drawable = resources.getDrawable(R.drawable.icon0_logo);
//        titleText.setCompoundDrawables(drawable,null,null,null);
        pageView.setContentView(contentLay)
        pageView.reLoadingData
        pageView.showLoading()
        pageView.showEmpty()
        val manager = LinearLayoutManager(activity)
        manager.orientation = RecyclerView.VERTICAL
        rvList.layoutManager = manager




        marqueeView.setOnItemClickListener { position, textView ->
            if (noticeList!!.isNotEmpty()){
                val intent = Intent(activity, NoticeDetialActivity::class.java)
                intent.putExtra("DATA", noticeList!![position] as Serializable)
                startActivity(intent)
            }
        }



        getNoticeListAction()
        getNewsListAction()

    }
    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }

    private fun homeInfoAction()  {
        val map = HashMap<String, String>()
        map["stock_index"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.HOME_SERVER_URL, map)
    }




    fun getNewsListAction() {
        val map = java.util.HashMap<String, Any>()
        map["nozzle"] = MethodUrl.NEWS_LIST
        val mHeaderMap = java.util.HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.NEWS_LIST, map)
    }

    fun getNoticeListAction() {
        val map = java.util.HashMap<String, Any>()
        map["nozzle"] = MethodUrl.NOTICE_LIST
        val mHeaderMap = java.util.HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.NOTICE_LIST, map)
    }



    @OnClick(  R.id.moreNewIv, R.id.moreNewsTv,R.id.dayLay,R.id.monthLay,R.id.lixiLay,R.id.freeLay,R.id.moreNoticeIv)
    fun onViewClicked(view: View) {
        var intent: Intent
        val activity: MainActivity? = activity as MainActivity?
        when (view.id) {
            R.id.moreNewIv ->{
                intent = Intent(activity,NewsListActivity::class.java)
                startActivity(intent)
            }
            R.id.moreNewsTv -> {
               intent = Intent(activity,NewsListActivity::class.java)
               startActivity(intent)
            }
            R.id.dayLay -> {
                activity?.toCeLueFragment(0)
            }
            R.id.monthLay -> {
                activity?.toCeLueFragment(1)
            }
            R.id.lixiLay -> {
                activity?.toCeLueFragment(2)
            }
            R.id.freeLay -> {
                intent = Intent(activity,FreeActivity::class.java)
                startActivity(intent)
            }
            R.id.moreNoticeIv ->{
                intent = Intent(activity,NoticeListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun showProgress() {
        //mLoadingWindow.showView()
    }

    override fun disimissProgress() {
         //mLoadingWindow.cancleView()

    }



    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow.cancleView()
        when (mType) {
            MethodUrl.NOTICE_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (!UtilTools.empty(tData["data"].toString())){
                        noticeList = tData["data"] as MutableList<MutableMap<String, Any>?>?
                        if (noticeList!!.isNotEmpty()){
                            val list1: MutableList<String> = ArrayList()
                            for (map in noticeList!!) {
                                list1.add(map?.get("name").toString() + "")
                            }
                            marqueeView.startWithList(list1 as List<Any>?)
                        }
                    }


                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1"->{
                     activity!!.finish()
                     val intent = Intent(activity, LoginActivity::class.java)
                     startActivity(intent)

                }
            }

            MethodUrl.NEWS_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (UtilTools.empty(tData["data"].toString())){
                        pageView.showEmpty()
                    }else{
                        val mapData = tData["data"] as MutableMap<String,Any>
                        if (UtilTools.empty(mapData["data"].toString())){
                            pageView.showEmpty()
                        }else{
                            listNews = mapData["data"] as MutableList<MutableMap<String, Any>>
                            var showList: MutableList<MutableMap<String, Any>> = ArrayList()
                            if ( listNews.size > 0){
                                showList.clear()
                                if (listNews.size > 3){
                                    showList.add(listNews[0])
                                    showList.add(listNews[1])
                                    showList.add(listNews[2])
                                }else{
                                    showList = listNews
                                }
                                pageView.showContent()
                                newsAdapter = NewsListAdapter(activity!!)
                                newsAdapter!!.clear()
                                newsAdapter!!.addAll(showList)
                                rvList.adapter = newsAdapter
                            }else{
                                pageView.showEmpty()
                            }


                        }
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1"->{
                    activity!!.finish()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)

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






        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mLoadingWindow.cancleView()

        when (mType) {
            MethodUrl.erleiHuList//二类户列表
            -> {

            }
        }
        dealFailInfo(map, mType)
    }



    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        val intent: Intent
        when (type) {
            200 -> {
                mHezuoMap = map
                val accid = mHezuoMap!!["accid"]!!.toString() + ""
                val secstatus = mHezuoMap!!["secstatus"]!!.toString() + ""

                if (UtilTools.empty(accid)) {

                    val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
                    if (kind == "1") {
                        intent = Intent(activity, BankTiXianModifyActivity::class.java)
                        intent.putExtra("DATA", mHezuoMap as Serializable?)
                        startActivity(intent)
                    } else {
                        intent = Intent(activity, ChongZhiCardAddActivity::class.java)
                        intent.putExtra("backtype", "100")
                        startActivity(intent)
                    }
                    //erLeiHuList();
                } else {
                    /*if (secstatus.equals("2")){
                        intent = new Intent(getActivity(), BankQianyueActivity.class);
                        intent.putExtra("DATA", (Serializable) mHezuoMap);
                        startActivity(intent);
                    }else {*/
                    intent = Intent(activity, ApplyAmountActivity::class.java)
                    intent.putExtra("DATA", mHezuoMap as Serializable?)
                    startActivity(intent)
                    //                    }

                }
            }
            210 -> {
                mJieKuanMap = map
                intent = Intent(activity, BorrowMoneyActivity::class.java)
                intent.putExtra("DATA", mJieKuanMap as Serializable?)
                startActivity(intent)
                LogUtil.i("show", mJieKuanMap!!)
            }
        }
    }





    override fun reLoadingData() {
        getNewsListAction()
    }

    override fun onPause() {
        super.onPause()
        // if (getDefault().isConnect) getDefault().disConnect()
        handler.removeCallbacks(cnyRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.post(cnyRunnable)
    }

    private val cnyRunnable = object : Runnable {
        override fun run() {
            homeInfoAction()
            handler.postDelayed(this, 5 * 1000)

        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            //不可见
            handler.removeCallbacks(cnyRunnable)
        } else {
            //可见
            handler.post(cnyRunnable)
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




}
