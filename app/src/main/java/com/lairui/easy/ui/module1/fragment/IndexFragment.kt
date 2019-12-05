package com.lairui.easy.ui.module1.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
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
import com.lairui.easy.ui.module1.activity.NewsListActivity
import com.lairui.easy.ui.module1.adapter.CoinInfoAdapter
import com.lairui.easy.ui.module1.adapter.MainCoinAdapter
import com.lairui.easy.ui.module1.adapter.NewsListAdapter
import com.lairui.easy.ui.temporary.activity.ApplyAmountActivity
import com.lairui.easy.ui.temporary.activity.BankTiXianModifyActivity
import com.lairui.easy.ui.temporary.activity.BorrowMoneyActivity
import com.lairui.easy.ui.temporary.activity.ChongZhiCardAddActivity
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.fragment_circle_view2.*
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



    private var coinInfoAdapter: CoinInfoAdapter? = null
    private var mainCoinAdapter: MainCoinAdapter? = null
    private val listUp: MutableList<Map<String, Any>> = ArrayList()


    private var newsAdapter: NewsListAdapter? = null
    private val listNews: MutableList<MutableMap<String, Any>> = ArrayList()


    private lateinit var mZhengshuMap: MutableMap<String, Any>
    private lateinit var mHezuoMap: MutableMap<String, Any>
    private lateinit var mJieKuanMap: MutableMap<String, Any>

    private lateinit var mLoadingWindow: LoadingWindow

    override val layoutId: Int
        get() = R.layout.fragment_circle_view2





    override fun init() {
        initView()
    }

    override fun onResume() {
        super.onResume()

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



        for (index in 1..5){
            val map = HashMap<String,Any>()
            map["name"] = "上证指数"+index
            map["price"] = "18210.00"
            map["cny"] = "+22.22"
            map["ratio"] = "+11.09%"
            listUp.add(map)
        }
        mainCoinAdapter = MainCoinAdapter(activity!!, listUp)
        vpQuotesInfo.adapter = mainCoinAdapter
        mainCoinAdapter!!.setData(listUp,vpQuotesInfo.currentItem)

        coinInfoAdapter = CoinInfoAdapter(activity)
        rvHoriList.adapter = coinInfoAdapter
        //rvHoriList.addItemDecoration(SpaceItemDecoration(UtilTools.dip2px(activity!!,10),3))
        coinInfoAdapter!!.setList(listUp)


        for (index in 1..3){
            val map = HashMap<String,Any>()
            map["title"] = "外交部召见美驻华使馆负责人 就美国会众议院通过涉疆法案提出严正交涉和强烈抗议"
            map["content"] = "12月4日，中国外交部副部长秦刚召见美国驻华使馆负责人柯有为，就美国会众议院审议通过“2019年维吾尔人权政策法案”提出严正交涉和强烈抗议，敦促美方立即纠正错误，停止借涉疆问题干涉中国内政。"
            map["url"] = "https://pics7.baidu.com/feed/9345d688d43f8794bbb8b935c6d1d7f11ad53ab1.jpeg?token=8bb505873039703b2500e78125e257ed&s=569139C47448935D0A512F9503005084"
            listNews.add(map)
        }


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

        pageView.showContent()
        newsAdapter = NewsListAdapter(activity!!)
        newsAdapter!!.addAll(listNews)
        rvList.adapter = newsAdapter

    }
    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }






    @OnClick(  R.id.moreNewIv, R.id.moreNewsTv)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.moreNewIv ->{
                intent = Intent(activity,NewsListActivity::class.java)
                startActivity(intent)
            }
            R.id.moreNewsTv -> {
               intent = Intent(activity,NewsListActivity::class.java)
               startActivity(intent)
            }
        }
    }

    override fun showProgress() {
        //mLoadingWindow.showView();
    }

    override fun disimissProgress() {
        // mLoadingWindow.cancleView();

    }



    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow!!.cancleView()

        when (mType) {
            MethodUrl.isInstallCer//{verify_type=FACE, state=0}//安装证书信息
            -> mZhengshuMap = tData
            MethodUrl.erleiHuList//二类户列表
            -> {

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
                LogUtil.i("打印log日志", mJieKuanMap!!)
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()

    }

    inner class SpaceItemDecoration(private val space: Int, private val column: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val mod = parent.getChildAdapterPosition(view) % column
            outRect.left = space * mod
        }

    }

    override fun reLoadingData() {

    }


}