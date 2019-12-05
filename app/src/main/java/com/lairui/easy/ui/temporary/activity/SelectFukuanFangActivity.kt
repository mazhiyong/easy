package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.view.View
import android.view.animation.OvershootInterpolator
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
import com.lairui.easy.ui.temporary.adapter.FukuanFangSelectAdapter
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

/**
 * 选择付款方
 */
class SelectFukuanFangActivity : BasicActivity(), RequestView, ReLoadingData {
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
    @BindView(R.id.date_tv)
    lateinit var mDateTv: TextView
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.btn_next)
    lateinit var mBtnNext: Button
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    lateinit var mAdapter: FukuanFangSelectAdapter
    var mDataList: MutableList<MutableMap<String, Any>> = ArrayList()

    private var mRequestTag = ""

    private var mSxMap: MutableMap<String, Any> = HashMap()


    override val contentView: Int
        get() = R.layout.activity_select_fukuan_fang

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mSxMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }

        mTitleText!!.text = resources.getString(R.string.shouldshou_money)

        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = linearLayoutManager
        mRefreshListView!!.setOnRefreshListener { payList() }

        showProgressDialog()
        payList()
    }

    private fun payList() {
        mRequestTag = MethodUrl.payCompanyList
        val map = HashMap<String, String>()
        map["flowdate"] = mSxMap["flowdate"]!!.toString() + ""
        map["flowid"] = mSxMap["flowid"]!!.toString() + ""
        map["autoid"] = mSxMap["autoid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.payCompanyList, map)
    }


    @OnClick(R.id.left_back_lay)
    fun onViewClicked() {
        finish()
    }


    override fun reLoadingData() {
        payList()
        showProgressDialog()

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
        when (mType) {
            MethodUrl.payCompanyList -> {
                val list = tData["payFirmInfoList"] as List<MutableMap<String, Any>>?
                if (list != null) {
                    mDataList.clear()
                    mDataList.addAll(list)
                    responseData()
                }

                mRefreshListView!!.refreshComplete(10)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.payCompanyList -> payList()
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
            MethodUrl.payCompanyList -> if (mAdapter != null) {
                if (mAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener {
                    payList()
                    showProgressDialog()
                }

            } else {
                mPageView!!.showNetworkError()
            }
        }

        dealFailInfo(map, mType)
    }

    private fun responseData() {
        if (mAdapter == null) {
            mAdapter = FukuanFangSelectAdapter(this)
            mAdapter!!.addAll(mDataList)

            val adapter1 = ScaleInAnimationAdapter(mAdapter)
            adapter1.setFirstOnly(false)
            adapter1.setDuration(400)
            adapter1.setInterpolator(OvershootInterpolator(0.8f))


            val adapter = AlphaInAnimationAdapter(adapter1)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(1f))


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
                    // .setHeight(R.dimen.dp_10)
                    //.setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.body_bg)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)


            mAdapter!!.onItemClickListener = object : com.lairui.easy.listener.OnItemClickListener {
                override fun onItemClickListener(view: View, position: Int, map: MutableMap<String, Any>) {
                    /*    Intent intent = new Intent(SelectFukuanFangActivity.this, ShouldShouMoneyActivity.class);
                    intent.putExtra("payfirmname",map.get("payfirmname")+"");
                    intent.putExtra("paycustid",map.get("paycustid")+"");
                    intent.putExtra("DATA",(Serializable) mSxMap);
                    startActivity(intent);*/
                    val intent = Intent(this@SelectFukuanFangActivity, BorrowMoneyActivity::class.java)
                    intent.putExtra("payfirmname", map["payfirmname"]!!.toString() + "")
                    intent.putExtra("paycustid", map["paycustid"]!!.toString() + "")
                    intent.putExtra("DATA", mSxMap as Serializable)
                    startActivity(intent)
                }
            }


        } else {

            /* if (mPage == 1) {
                mRepaymentAdapter.clear();
            }*/
            mAdapter!!.clear()
            mAdapter!!.addAll(mDataList)
            mAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        if (mAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()

        } else {
            mPageView!!.showContent()
        }
    }


    @OnClick(R.id.left_back_lay, R.id.btn_next, R.id.content, R.id.page_view)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> {
            }
            R.id.btn_next -> {
            }
            R.id.content -> {
            }
            R.id.page_view -> {
            }
        }
    }


}
