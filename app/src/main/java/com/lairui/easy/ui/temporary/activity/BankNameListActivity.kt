package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.BankNameListAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.lairui.easy.mywidget.view.WaveSideBar
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick

/**
 * 开户行列表   界面
 */
class BankNameListActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.right_bar)
    lateinit var mSideBar: WaveSideBar

    @BindView(R.id.bank_image_view)
    lateinit var mBankImageView: ImageView
    @BindView(R.id.bank_name_tv)
    lateinit var mBankNameTv: TextView
    @BindView(R.id.head_bank_lay)
    lateinit var mHeadBankLay: LinearLayout


    private var mRequestTag = ""
    private lateinit var mBankNameListAdapter: BankNameListAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1


    override val contentView: Int
        get() = R.layout.activity_bank_name_list

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.chose_bank2)

        mRightImg!!.visibility = View.VISIBLE
        mRightImg!!.setImageResource(R.drawable.shuaixuan)
        mRightTextTv!!.visibility = View.VISIBLE
        mRightLay!!.visibility = View.GONE

        initView()
        showProgressDialog()
        bankNameAction()
        // mPageView.showLoading();
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        //mPageView.showEmpty();
        mPageView!!.reLoadingData = this
        val manager = LinearLayoutManager(this@BankNameListActivity)
        manager.orientation = RecyclerView.VERTICAL
        manager.isAutoMeasureEnabled = false
        mRefreshListView!!.layoutManager = manager

        mRefreshListView!!.setOnRefreshListener {
            mPage = 1
            bankNameAction()
        }

        mRefreshListView!!.setOnLoadMoreListener {
            mPage++
            bankNameAction()
        }

        //设置右侧SideBar触摸监听
        mSideBar!!.setOnTouchLetterChangeListener { letter ->
            if (mBankNameListAdapter != null) {
                //该字母首次出现的位置
                val position = mBankNameListAdapter!!.getPositionForSection(letter as String)
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position + 1, 0)
                }
            }
        }
    }


    private fun bankNameAction() {
        mRequestTag = MethodUrl.opnbnk
        val map = HashMap<String, String>()
        map["type"] = "UDK"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.opnbnk, map)
    }


    private fun responseData() {

        if (mBankNameListAdapter == null) {
            mBankNameListAdapter = BankNameListAdapter(this@BankNameListActivity)
            mBankNameListAdapter!!.addAll(mDataList)
            mBankNameListAdapter!!.simpleOrder()

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
                     adapter.setFirstOnly(false);
                     adapter.setDuration(500);
                     adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mBankNameListAdapter)


            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(false)
            val divider2 = DividerDecoration.Builder(this@BankNameListActivity)
                    .setHeight(2f)
                    .setPadding(0f)
                    .setColorResource(R.color.divide_line)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)

            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mBankNameListAdapter!!.dataList[position]
                val intent = Intent()
                intent.putExtra("DATA", item as Serializable)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }


        } else {
            if (mPage == 1) {
                mBankNameListAdapter!!.clear()
            }
            mBankNameListAdapter!!.addAll(mDataList)
            mBankNameListAdapter!!.simpleOrder()
            mBankNameListAdapter!!.notifyDataSetChanged()
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
        if (mBankNameListAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }


        var mHeadMap: MutableMap<String, Any> = HashMap()
        for (bankMap in mDataList) {
            val code = bankMap["opnbnkid"]!!.toString() + ""
            if (code == "3002") {
                mHeadMap = bankMap
                break
            }
        }

        if (!mHeadMap.isEmpty()) {
            mHeadBankLay!!.tag = mHeadMap
            mBankNameTv!!.text = mHeadMap["opnbnknm"]!!.toString() + ""
            GlideUtils.loadCircleImage(this@BankNameListActivity, mHeadMap["logopath"]!!.toString() + "", mBankImageView!!)
            mHeadBankLay!!.visibility = View.VISIBLE
        }

        mHeadBankLay!!.visibility = View.GONE

        dismissProgressDialog()

    }


    @OnClick(R.id.back_img, R.id.right_lay, R.id.left_back_lay, R.id.head_bank_lay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.head_bank_lay -> {
                val item = mHeadBankLay!!.tag as MutableMap<String, Any>
                intent = Intent()
                intent.putExtra("DATA", item as Serializable)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
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
        //        dismissProgressDialog();
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.opnbnk//
            -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    val list = JSONUtil.instance.jsonToList(result)
                } else {
                    val list = JSONUtil.instance.jsonToList(result)
                    if (list != null) {
                        mDataList.clear()
                        mDataList.addAll(list)
                    } else {

                    }
                }
                responseData()
                dismissProgressDialog()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                showProgressDialog()
                when (mRequestTag) {
                    MethodUrl.opnbnk -> bankNameAction()
                }
            }
        }


    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dismissProgressDialog()
        when (mType) {
            MethodUrl.opnbnk//
            ->

                if (mBankNameListAdapter != null) {
                    if (mBankNameListAdapter!!.dataList.size <= 0) {
                        mPageView!!.showNetworkError()
                    } else {
                        mPageView!!.showContent()
                    }
                    mRefreshListView!!.refreshComplete(10)
                    mRefreshListView!!.setOnNetWorkErrorListener { bankNameAction() }
                } else {
                    mPageView!!.showNetworkError()
                }
        }
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        showProgressDialog()
        bankNameAction()
    }
}
