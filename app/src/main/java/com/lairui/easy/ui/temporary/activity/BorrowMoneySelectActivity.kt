package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.view.View
import android.view.animation.OvershootInterpolator
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
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.temporary.adapter.BorrowSelectAdapter
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.MainActivity
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

/**
 * 我要借款 （应收账款池  信用融资）
 */
class BorrowMoneySelectActivity : BasicActivity(), RequestView, ReLoadingData {
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
    @BindView(R.id.brorow_tip_tv)
    lateinit var mTipTv: TextView
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout


    lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    lateinit var mBorrowSelectAdapter: BorrowSelectAdapter
    var mDataList: MutableList<MutableMap<String, Any>> = ArrayList()

    private var mRequestTag = ""


    override val contentView: Int
        get() = R.layout.activity_brorow_money_select


    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.apply_brorow)

        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = linearLayoutManager
        mRefreshListView!!.setOnRefreshListener { jiekuanCheck() }

        showProgressDialog()
        jiekuanCheck()
    }


    //借款前的配置信息获取   借款前选择的合作方   合作方列表
    private fun jiekuanCheck() {
        if (MbsConstans.USER_MAP != null) {//是否完善信息（1：已完善 0：未完善）
            val ss = MbsConstans.USER_MAP!!["cmpl_info"]!!.toString() + ""
            if (ss == "1") {
            } else {
                val intent = Intent(this, PerfectInfoActivity::class.java)
                startActivity(intent)
            }

        } else {
            MainActivity.mInstance.getUserInfoAction()
            TipsToast.showToastMsg("获取用户基本信息失败,请重新获取")
            return
        }

        mRequestTag = MethodUrl.jiekuanSxList
        val map = HashMap<String, String>()
        map["creditstate"] = "1"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.jiekuanSxList, map)
    }


    @OnClick(R.id.left_back_lay)
    fun onViewClicked() {
        finish()
    }


    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {
        //dismissProgressDialog();
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

        when (mType) {
            MethodUrl.jiekuanSxList -> {
                val jkSxList = tData["creditList"] as List<MutableMap<String, Any>>?
                if (jkSxList != null) {
                    mDataList.clear()
                    mDataList.addAll(jkSxList)
                    responseData()
                } else {
                    TipsToast.showToastMsg(resources.getString(R.string.exception_info))
                }

                mRefreshListView!!.refreshComplete(10)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.jiekuanSxList -> jiekuanCheck()
                }
            }
        }

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.jiekuanSxList -> if (mBorrowSelectAdapter != null) {
                if (mBorrowSelectAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener {
                    jiekuanCheck()
                    showProgressDialog()
                }

            } else {
                mPageView!!.showNetworkError()
            }
        }


        dealFailInfo(map, mType)
    }

    private fun responseData() {
        if (mBorrowSelectAdapter == null) {
            mBorrowSelectAdapter = BorrowSelectAdapter(this)
            mBorrowSelectAdapter!!.addAll(mDataList)

            val adapter1 = ScaleInAnimationAdapter(mBorrowSelectAdapter)
            adapter1.setFirstOnly(false)
            adapter1.setDuration(400)
            adapter1.setInterpolator(OvershootInterpolator(0.8f))


            val adapter = AlphaInAnimationAdapter(adapter1)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(1f))


            mLRecyclerViewAdapter = LRecyclerViewAdapter(mBorrowSelectAdapter)
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
                    .setHeight(R.dimen.dp_10)
                    .setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.body_bg)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)

            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position -> }


        } else {

            /* if (mPage == 1) {
                mRepaymentAdapter.clear();
            }*/
            mBorrowSelectAdapter!!.clear()
            mBorrowSelectAdapter!!.addAll(mDataList)
            mBorrowSelectAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        if (mBorrowSelectAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()

        } else {
            mPageView!!.showContent()
        }
    }


    override fun reLoadingData() {
        jiekuanCheck()
        showProgress()
    }


}
