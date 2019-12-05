package com.lairui.easy.ui.temporary.activity

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
import com.lairui.easy.ui.temporary.adapter.ServiceRemindInfoAdapter
import com.lairui.easy.ui.temporary.adapter.SwipeMenuAdapter
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
 * 服务提醒消息列表
 */
class ServicesRemindInfoActivity : BasicActivity(), RequestView, ReLoadingData {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.service_remind_list_view)
    lateinit var mLRecyclerView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    private lateinit var mAdapter: LRecyclerViewAdapter
    private var mServiceRemindInfoAdapter: ServiceRemindInfoAdapter? = null
    private var mRequestTag = ""
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    override val contentView: Int
        get() = R.layout.activity_services_remind_info

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.service_remind)
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this
        val manager = LinearLayoutManager(this)
        manager.orientation = RecyclerView.VERTICAL
        mLRecyclerView!!.layoutManager = manager
        mLRecyclerView!!.setOnRefreshListener {
            //刷新请求数据(待办列表项数据测试)
            serviceListAction()
        }
        serviceListAction()
    }

    private fun serviceListAction() {
        mRequestTag = MethodUrl.workList

        val mHeaderMap = HashMap<String, String>()
        val map = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.workList, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.workList -> {
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

                    }

                    //授信签署列表
                    val list2 = mm["creditSignList"] as List<MutableMap<String, Any>>?
                    if (list2 != null && list2.size > 0) {
                        for (map in list2) {
                            map.put("type", "2")
                        }
                        mDataList.addAll(list2)

                    } else {

                    }

                    //借款进度列表
                    val list3 = mm["loanPlanList"] as List<MutableMap<String, Any>>?
                    if (list3 != null && list3.size > 0) {
                        for (map in list3) {
                            map.put("type", "3")
                        }
                        mDataList.addAll(list3)
                    } else {

                    }

                    //待还款列表
                    val list4 = mm["replayList"] as List<MutableMap<String, Any>>?
                    if (list4 != null && list4.size > 0) {
                        for (map in list4) {
                            map.put("type", "4")
                        }
                        mDataList.addAll(list4)
                    } else {

                    }

                    //共同借款人审核列表
                    val list5 = mm["gtPreList"] as List<MutableMap<String, Any>>?
                    if (list5 != null && list5.size > 0) {
                        for (map in list5) {
                            map.put("type", "5")
                        }
                        mDataList.addAll(list5)
                    } else {

                    }

                    responseData()
                }
                mLRecyclerView!!.refreshComplete(10)
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.workList -> serviceListAction()
                }
            }
        }
    }

    private fun responseData() {
        if (mServiceRemindInfoAdapter == null) {
            mServiceRemindInfoAdapter = ServiceRemindInfoAdapter(this)
            mServiceRemindInfoAdapter!!.addAll(mDataList)
            mServiceRemindInfoAdapter!!.setOnSwipeListener(object : SwipeMenuAdapter.onSwipeListener {
                override fun onDel(pos: Int) {
                    //删除

                    /*//RecyclerView关于notifyItemRemoved的那点小事 参考：http://blog.csdn.net/jdsjlzx/article/details/52131528
                mSwipeMenuAdapter.getDataList().remove(pos);
                mSwipeMenuAdapter.notifyItemRemoved(pos);//推荐用这个
                if(pos != (mSwipeMenuAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
                    mSwipeMenuAdapter.notifyItemRangeChanged(pos, mSwipeMenuAdapter.getDataList().size() - pos);
                }
                mSwipeMenuAdapter.notifyDataSetChanged();*/

                    mDataList.removeAt(pos)
                    responseData()

                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();

                }

                override fun onTop(pos: Int) {
                    //置顶
                }
            })



            val adapter = ScaleInAnimationAdapter(mServiceRemindInfoAdapter)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(5f))

            mAdapter = LRecyclerViewAdapter(adapter)
            mLRecyclerView!!.adapter = mAdapter
            mLRecyclerView!!.itemAnimator = DefaultItemAnimator()
            mLRecyclerView!!.setHasFixedSize(true)
            mLRecyclerView!!.isNestedScrollingEnabled = false
            mLRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
            mLRecyclerView!!.setPullRefreshEnabled(true)
            mLRecyclerView!!.setLoadMoreEnabled(false)

            mLRecyclerView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mLRecyclerView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)


            val divider2 = DividerDecoration.Builder(this)
                    .setHeight(R.dimen.divide_hight)
                    .setPadding(R.dimen.dp_14)
                    .setColorResource(R.color.divide_line)
                    .build()
            mLRecyclerView!!.addItemDecoration(divider2)


        } else {

            mServiceRemindInfoAdapter!!.clear()
            mServiceRemindInfoAdapter!!.addAll(mDataList)
            mServiceRemindInfoAdapter!!.notifyDataSetChanged()
            mAdapter!!.notifyDataSetChanged()
        }
        //添加尾部布局
        mLRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mLRecyclerView!!.setNoMore(true)
        } else {
            mLRecyclerView!!.setNoMore(false)
        }

        if (mServiceRemindInfoAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        serviceListAction()
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

}
