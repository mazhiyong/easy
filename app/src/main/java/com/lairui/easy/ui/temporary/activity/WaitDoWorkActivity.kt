package com.lairui.easy.ui.temporary.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.WaitDoWorkAdapter
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
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

/**
 * 待办事项列表
 */
class WaitDoWorkActivity : BasicActivity(), RequestView, ReLoadingData {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.dowork_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mRequestTag = ""
    private val mPage = 1

    private lateinit var mWaitDoWorkAdapter: WaitDoWorkAdapter
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    override val contentView: Int
        get() = R.layout.activity_wait_do_work

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.DAIBAN_INFO_UPDATE) {
                doworkListAction()
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.wait_do_work)


        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.DAIBAN_INFO_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)

        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = linearLayoutManager

        mRefreshListView!!.setOnRefreshListener {
            doworkListAction()
            //                mRefreshListView.refreshComplete(10);
            //                mLRecyclerViewAdapter.notifyDataSetChanged();
        }
        showProgressDialog()
        doworkListAction()
    }

    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    private fun doworkListAction() {
        mRequestTag = MethodUrl.workList

        val mHeaderMap = HashMap<String, String>()
        val map = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.workList, map)
    }

    private fun responseData() {
        if (mWaitDoWorkAdapter == null) {
            mWaitDoWorkAdapter = WaitDoWorkAdapter(this)
            mWaitDoWorkAdapter!!.addAll(mDataList)

            val adapter = ScaleInAnimationAdapter(mWaitDoWorkAdapter)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(.5f))

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mWaitDoWorkAdapter)
            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false
            mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(false)

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            val divider2 = DividerDecoration.Builder(this)
                    .setHeight(R.dimen.divide_hight)
                    //.setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.divide_line)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)

        } else {

            mWaitDoWorkAdapter!!.clear()
            mWaitDoWorkAdapter!!.addAll(mDataList)
            mWaitDoWorkAdapter!!.notifyDataSetChanged()

            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }
        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        if (mWaitDoWorkAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }

    /*
      预加载数据
     */
    override fun reLoadingData() {
        showProgressDialog()
        doworkListAction()
    }

    override fun showProgress() {
        //showProgressDialog();
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.workList//
            -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    responseData()
                } else {
                    mDataList.clear()
                    val mm = JSONUtil.instance.jsonMap(result)
                    //预授信回退列表
                    val list1 = mm!!["prebackList"] as List<MutableMap<String, Any>>?
                    if (list1 != null && list1.size > 0) {
                        for (map in list1) {
                            map.put("type", "1")
                        }
                        mDataList.addAll(list1)

                    } else {
                        // Toast.makeText(this,"预授信列表为空",Toast.LENGTH_SHORT).show();
                    }

                    //授信签署列表
                    val list2 = mm["creditSignList"] as List<MutableMap<String, Any>>?
                    if (list2 != null && list2.size > 0) {
                        for (map in list2) {
                            map.put("type", "2")
                        }
                        mDataList.addAll(list2)

                    } else {
                        // Toast.makeText(this,"授信签署列表为空",Toast.LENGTH_SHORT).show();
                    }

                    //借款进度列表
                    val list3 = mm["loanPlanList"] as List<MutableMap<String, Any>>?
                    if (list3 != null && list3.size > 0) {
                        for (map in list3) {
                            map.put("type", "3")
                        }
                        mDataList.addAll(list3)
                    } else {
                        // Toast.makeText(this,"借款进度列表为空",Toast.LENGTH_SHORT).show();
                    }

                    //待还款列表
                    val list4 = mm["replayList"] as List<MutableMap<String, Any>>?
                    if (list4 != null && list4.size > 0) {
                        for (map in list4) {
                            map.put("type", "4")
                        }
                        mDataList.addAll(list4)
                    } else {
                        // Toast.makeText(this,"待还款列表为空",Toast.LENGTH_SHORT).show();
                    }

                    //共同借款人审核列表
                    val list5 = mm["gtPreList"] as List<MutableMap<String, Any>>?
                    if (list5 != null && list5.size > 0) {
                        for (map in list5) {
                            map.put("type", "5")
                        }
                        mDataList.addAll(list5)
                    } else {
                        // Toast.makeText(this,"共同借款人列表为空",Toast.LENGTH_SHORT).show();
                    }

                    responseData()

                }
                mRefreshListView!!.refreshComplete(10)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.workList -> doworkListAction()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.workList -> if (mWaitDoWorkAdapter != null) {
                if (mWaitDoWorkAdapter!!.dataList.size < 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { doworkListAction() }
            } else {
                mPageView!!.showNetworkError()
            }
        }


        //根据处理错误类型
        dealFailInfo(map, mType)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }
}
