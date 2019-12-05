package com.lairui.easy.ui.temporary.activity

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
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.OnChildClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.ui.temporary.adapter.PingZhengSelectAdapter
import com.lairui.easy.utils.tool.LogUtil
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
 * 选择关联应收凭证
 */
class SelectPingZhengActivity : BasicActivity(), RequestView, ReLoadingData {

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

    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private lateinit var mAdapter: PingZhengSelectAdapter
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private val mBooleanList = ArrayList<MutableMap<String, Any>>()
    private val mSelectList = ArrayList<MutableMap<String, Any>>()


    private lateinit var mHeTongMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_select_ping_zheng


    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mRightTextTv!!.text = "全选"
        mRightTextTv!!.visibility = View.VISIBLE
        mRightImg!!.visibility = View.VISIBLE
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()

        mTitleText!!.text = "选择应收凭证"

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = linearLayoutManager


        mRefreshListView!!.setOnRefreshListener {
            init()
            responseData()
            mRefreshListView!!.refreshComplete(10)
        }

        val bundle = intent.extras
        if (bundle != null) {
            mHeTongMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }


        initData()

        responseData()


    }


    @OnClick(R.id.left_back_lay)
    fun onViewClicked() {
        finish()
    }

    private fun initData() {
        for (i in 0..9) {

            val map = HashMap<String, Any>()
            map["flowdate"] = ""
            map["flowid"] = ""
            map["sgndt"] = ""
            map["name"] = "深圳市国有兴头投资那个公司"
            map["money"] = "57690000"
            map["mumber"] = "128192$i"
            mDataList.add(map)
        }
    }


    override fun reLoadingData() {

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

    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }


    private fun responseData() {
        for (m in mDataList) {
            val map = HashMap<String, Any>()
            map["value"] = m
            map["selected"] = false
            mBooleanList.add(map)
        }


        if (mAdapter == null) {
            mAdapter = mHeTongMap?.let { PingZhengSelectAdapter(this, it, 0) }
            mAdapter!!.booleanList = mBooleanList
            mAdapter!!.addAll(mDataList)

            /*AnimationAdapter adapter1 = new ScaleInAnimationAdapter(mAdapter);
            adapter1.setFirstOnly(false);
            adapter1.setDuration(400);
            adapter1.setInterpolator(new OvershootInterpolator(0.8f));


            AnimationAdapter adapter = new AlphaInAnimationAdapter(adapter1);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(1f));*/


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

            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position -> }

            mAdapter!!.setOnChildClickListener(object : OnChildClickListener {
                override fun onChildClickListener(view: View, position: Int, mParentMap: MutableMap<String, Any>) {
                    mRefreshListView!!.post {
                        mAdapter!!.notifyDataSetChanged()
                        mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
                    }
                }
            })


        } else {

            /* if (mPage == 1) {
                mRepaymentAdapter.clear();
            }*/
            mAdapter!!.booleanList = mBooleanList
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


    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }


    @OnClick(R.id.left_back_lay, R.id.right_lay, R.id.btn_next)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.right_lay //全选
            -> if (mAdapter != null) {
                if (mRightTextTv!!.text.toString() == "全选") {
                    mAdapter!!.selectAll()
                    mRightTextTv!!.text = "取消"
                } else {
                    mAdapter!!.cancelSelectAll()
                    mRightTextTv!!.text = "全选"
                }
            }
            R.id.btn_next -> {
                val list = mAdapter!!.booleanList
                mSelectList.clear()
                for (map in list!!) {
                    val b = (map["selected"] as Boolean?)!!
                    val mSelectMap = map["value"] as MutableMap<String, Any>?
                    if (b) {
                        mSelectMap?.let { mSelectList.add(it) }
                    }
                }


                LogUtil.i("show", "size:" + mSelectList.size)
            }
        }//关联合同
        //startActivity(new Intent(SelectPingZhengActivity.this, HeTongSelectActivity.class));
    }


}
