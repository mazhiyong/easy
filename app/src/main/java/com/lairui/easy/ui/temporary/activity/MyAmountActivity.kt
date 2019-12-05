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
import com.lairui.easy.ui.temporary.adapter.MyAmountAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.mywidget.view.SampleHeader
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.ParseTextUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick

/**
 * 我的---我的额度   界面
 */
class MyAmountActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.empty_view)
    lateinit var mEmptyView: LinearLayout
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    private lateinit var mUserfulMoneyTv: TextView
    private lateinit var mTotleMoneyTv: TextView

    private var mRequestTag = ""


    private lateinit var mMyAmountAdapter: MyAmountAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1

    private var mMoneyMap: MutableMap<String, Any> = HashMap()

    override val contentView: Int
        get() = R.layout.activity_my_amount

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.my_larger_num)

        mRightImg!!.visibility = View.VISIBLE
        mRightImg!!.setImageResource(R.drawable.shuaixuan)
        mRightTextTv!!.visibility = View.VISIBLE
        mRightTextTv!!.text = "筛选"
        mRightLay!!.visibility = View.GONE

        initView()
        showProgressDialog()
        totleMoney()
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this
        val manager = LinearLayoutManager(this@MyAmountActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager

        mRefreshListView!!.setOnRefreshListener {
            mPage = 1
            totleMoney()
        }

        mRefreshListView!!.setOnLoadMoreListener {
            mPage++
            sxListAction()
        }
    }


    /**
     * 授信列表请求
     */
    private fun sxListAction() {

        mRequestTag = MethodUrl.jiekuanSxList
        val map = HashMap<String, String>()
        //map.put("creditstate","");
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.jiekuanSxList, map)
    }

    private fun totleMoney() {

        mRequestTag = MethodUrl.totleMoney
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.totleMoney, map)
    }


    private fun responseData() {
        if (mMyAmountAdapter == null) {
            mMyAmountAdapter = MyAmountAdapter(this@MyAmountActivity)
            mMyAmountAdapter!!.addAll(mDataList)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mMyAmountAdapter)

            val headerView = SampleHeader(this@MyAmountActivity, R.layout.header_my_amount)
            mUserfulMoneyTv = headerView.findViewById(R.id.use_money_tv)
            mTotleMoneyTv = headerView.findViewById(R.id.totle_value_tv)
            headerViewValue()

            mLRecyclerViewAdapter!!.addHeaderView(headerView)

            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(false)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mMyAmountAdapter!!.dataList[position]
                var intent: Intent

                val type = mMyAmountAdapter!!.getItemViewType(position)
                when (type) {
                    MyAmountAdapter.TYPE_FIRST -> {
                        intent = Intent(this@MyAmountActivity, ShouxinPreDetailActivity::class.java)
                        intent.putExtra("DATA", item as Serializable)
                        startActivity(intent)
                    }
                    MyAmountAdapter.TYPE_NORMAL -> {
                        intent = Intent(this@MyAmountActivity, ShouxinDetailActivity::class.java)
                        intent.putExtra("DATA", item as Serializable)
                        startActivity(intent)
                    }
                }
            }


        } else {
            headerViewValue()
            if (mPage == 1) {
                mMyAmountAdapter!!.clear()
            }
            mMyAmountAdapter!!.addAll(mDataList)
            mMyAmountAdapter!!.notifyDataSetChanged()
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
        mMyAmountAdapter!!.notifyDataSetChanged()
        if (mMyAmountAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
            //mRefreshListView.setEmptyView( LayoutInflater.from(this).inflate(R.layout.page_view_empty, null));
        } else {
            mPageView!!.showContent()
        }

        if (!mMoneyMap.isEmpty()) {
            mPageView!!.showContent()
        }
        //mRefreshListView.setEmptyView( mEmptyView);
    }


    private fun headerViewValue() {
        if (mUserfulMoneyTv != null && !mMoneyMap.isEmpty()) {
            val mm = UtilTools.getRMBMoney(mMoneyMap["leftmoney"]!!.toString() + "")
            val m = ParseTextUtil(this@MyAmountActivity)
            val reuslt = m.getDianType(mm)
            mUserfulMoneyTv!!.text = reuslt

            val tmm = UtilTools.getRMBMoney(mMoneyMap["totalmoney"]!!.toString() + "")
            mTotleMoneyTv!!.text = tmm
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
            MethodUrl.jiekuanSxList//
            -> {

                val preSXMap = tData["preCredit"] as MutableMap<String, Any>?
                val list = tData["creditList"] as List<MutableMap<String, Any>>?
                if (list != null) {
                    mDataList.clear()
                    mDataList.addAll(list)
                }

                //判断预授信信息是否为空   为空的话列表就不要显示了
                if (preSXMap != null && !preSXMap.isEmpty()) {
                    preSXMap.put("viewType", MyAmountAdapter.TYPE_FIRST)
                    mDataList.add(0, preSXMap)
                }
                responseData()
                mRefreshListView!!.refreshComplete(10)
            }
            MethodUrl.totleMoney -> {
                mMoneyMap = tData
                sxListAction()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                showProgressDialog()
                when (mRequestTag) {
                    MethodUrl.jiekuanSxList -> sxListAction()
                    MethodUrl.totleMoney -> totleMoney()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.jiekuanSxList//
            -> {
                if (mMyAmountAdapter != null) {
                    if (mMyAmountAdapter!!.dataList.size <= 0) {
                        mPageView!!.showNetworkError()
                    } else {
                        mPageView!!.showContent()
                    }
                    mRefreshListView!!.refreshComplete(10)
                    mRefreshListView!!.setOnNetWorkErrorListener { sxListAction() }
                } else {

                }
                responseData()
            }
            MethodUrl.totleMoney -> if (mMyAmountAdapter != null) {

            } else {
                mPageView!!.showNetworkError()

            }
        }
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        showProgressDialog()
        totleMoney()
    }
}
