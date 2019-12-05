package com.lairui.easy.ui.temporary.activity

import android.animation.Animator
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView

import com.flyco.dialog.utils.CornerUtils
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.TradeDialogAdapter
import com.lairui.easy.ui.temporary.adapter.TradeListAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.AnimUtil
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.Date
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 交易明细  界面
 */
class TradeListActivity : BasicActivity(), RequestView, ReLoadingData, SelectBackListener {

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

    private var mRequestTag = ""

    private var mStartTime = ""
    private var mEndTime = ""
    private var mBusiType = ""

    private var mSelectStartTime = ""
    private var mSelectEndTime = ""
    private var mSelectType = ""


    private lateinit var mTradeListAdapter: TradeListAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1

    private lateinit var mAnimUtil: AnimUtil

    override val contentView: Int
        get() = R.layout.activity_trade_list
    private lateinit var mTypeRecyclerView: RecyclerView
    private lateinit var mTradeDialogAdapter: TradeDialogAdapter
    private lateinit var mOneTv: TextView
    private lateinit var mThreeTv: TextView
    private lateinit var mSetTimeTv: TextView
    private lateinit var mStartTimeTv: TextView
    private lateinit var mEndTimeTv: TextView
    private lateinit var mResetBut: Button
    private lateinit var mSureBut: Button
    private lateinit var mySelectDialog: DateSelectDialog
    private lateinit var mySelectDialog2: DateSelectDialog


    private lateinit var popView: View
    private lateinit var mConditionDialog: PopupWindow
    private var bright = false

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        mAnimUtil = AnimUtil()

        mTitleText!!.text = resources.getString(R.string.order_detail)

        mRightImg!!.visibility = View.VISIBLE
        mRightImg!!.setImageResource(R.drawable.shuaixuan)
        mRightTextTv!!.visibility = View.VISIBLE
        mRightTextTv!!.text = "筛选"
        mRightTextTv!!.setTextColor(ContextCompat.getColor(this, R.color.btn_login_normal))

        val sTime = UtilTools.getFirstDayOfMonthByDate(Date())


        val eTime = UtilTools.getStringFromDate(Date(), "yyyyMMdd")

        mSelectStartTime = sTime
        mSelectEndTime = eTime
        mStartTime = mSelectStartTime
        mEndTime = mSelectEndTime


        initView()
        showProgressDialog()
        traderListAction()
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()
        val manager = LinearLayoutManager(this@TradeListActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager

        mRefreshListView!!.setOnRefreshListener {
            mPage = 1
            traderListAction()
        }

        mRefreshListView!!.setOnLoadMoreListener { traderListAction() }
    }

    private fun traderListAction() {

        mRequestTag = MethodUrl.tradeList
        val map = HashMap<String, String>()
        map["current_page"] = mPage.toString() + ""
        map["ptncode"] = "" //合作方编号  ，默认所有
        map["start_time"] = mStartTime
        map["end_time"] = mEndTime
        map["busi_type"] = mBusiType
        LogUtil.i("打印log日志", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@$map")
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.tradeList, map)
    }


    private fun responseData() {
        if (mTradeListAdapter == null) {
            mTradeListAdapter = TradeListAdapter(this@TradeListActivity)
            mTradeListAdapter!!.addAll(mDataList)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mTradeListAdapter)

            //            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
            //            mLRecyclerViewAdapter.addHeaderView(headerView);

            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(true)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mTradeListAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }


        } else {
            if (mPage == 1) {
                mTradeListAdapter!!.clear()
            }
            mTradeListAdapter!!.addAll(mDataList)
            mTradeListAdapter!!.notifyDataSetChanged()
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
            mPage++
        }

        mRefreshListView!!.refreshComplete(10)
        mTradeListAdapter!!.notifyDataSetChanged()
        if (mTradeListAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }

    fun showDialog() {
        initPopupWindow()
    }

    private fun initPopupWindow() {//城市列表的显示
        if (mConditionDialog == null) {
            mySelectDialog = DateSelectDialog(this, true, "选择日期", 21)
            mySelectDialog!!.selectBackListener = this
            mySelectDialog2 = DateSelectDialog(this, true, "选择日期", 22)
            mySelectDialog2!!.selectBackListener = this

            popView = LayoutInflater.from(this@TradeListActivity).inflate(R.layout.dialog_trade_condition, null)
            mConditionDialog = PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            initConditionDialog(popView!!)
            val screenWidth = UtilTools.getScreenWidth(this@TradeListActivity)
            val screenHeight = UtilTools.getScreenHeight(this@TradeListActivity)
            mConditionDialog!!.width = (screenWidth * 0.8).toInt()
            mConditionDialog!!.height = WindowManager.LayoutParams.MATCH_PARENT

            //设置background后在外点击才会消失
            mConditionDialog!!.setBackgroundDrawable(CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), UtilTools.dip2px(this@TradeListActivity, 5).toFloat()))
            //mConditionDialog.setOutsideTouchable(true);// 设置可允许在外点击消失
            //自定义动画
            mConditionDialog!!.animationStyle = R.style.PopupAnimation
            //            mConditionDialog.setAnimationStyle(android.R.style.Animation_Activity);//使用系统动画
            mConditionDialog!!.update()
            mConditionDialog!!.isTouchable = true
            mConditionDialog!!.isFocusable = true
            //popView.requestFocus();//pop设置不setBackgroundDrawable情况，把焦点给popView，添加popView.setOnKeyListener。可实现点击外部不消失，点击反键才消失
            //			mConditionDialog.showAtLocation(mCityTv, Gravity.TOP|Gravity.RIGHT, 0, 0); //设置layout在PopupWindow中显示的位置
            mConditionDialog!!.showAtLocation(this@TradeListActivity.window.decorView, Gravity.TOP or Gravity.RIGHT, 0, 0)
            toggleBright()
            mConditionDialog!!.setOnDismissListener { toggleBright() }
        } else {
            mConditionDialog!!.showAtLocation(this@TradeListActivity.window.decorView, Gravity.TOP or Gravity.RIGHT, 0, 0)
            toggleBright()
        }
    }


    private fun toggleBright() {
        //三个参数分别为： 起始值 结束值 时长 那么整个动画回调过来的值就是从0.5f--1f的
        mAnimUtil!!.setValueAnimator(0.7f, 1f, 300)
        mAnimUtil!!.addUpdateListener(object : AnimUtil.UpdateListener {
            override fun progress(progress: Float) {
                //此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                val bgAlpha = if (bright) progress else 1.7f - progress//三目运算，应该挺好懂的。
                //bgAlpha = progress;//三目运算，应该挺好懂的。
                bgAlpha(bgAlpha)//在此处改变背景，这样就不用通过Handler去刷新了。
            }
        })
        mAnimUtil!!.addEndListner(object : AnimUtil.EndListener {
            override fun endUpdate(animator: Animator) {
                //在一次动画结束的时候，翻转状态
                bright = !bright
            }
        })
        mAnimUtil!!.startAnimator()
    }

    private fun bgAlpha(alpha: Float) {
        val lp = window.attributes
        lp.alpha = alpha// 0.0-1.0
        window.attributes = lp
    }


    private fun initConditionDialog(view: View) {

        mTypeRecyclerView = view.findViewById(R.id.type_recycleview)

        mOneTv = view.findViewById(R.id.one_month_tv)
        mThreeTv = view.findViewById(R.id.three_month_tv)
        mSetTimeTv = view.findViewById(R.id.set_time_tv)
        mStartTimeTv = view.findViewById(R.id.start_time_value_tv)
        mEndTimeTv = view.findViewById(R.id.end_time_value_tv)
        mResetBut = view.findViewById(R.id.reset_but)
        mSureBut = view.findViewById(R.id.sure_but)

        val onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.one_month_tv -> {
                    mOneTv!!.isSelected = true
                    mThreeTv!!.isSelected = false
                    mSetTimeTv!!.isSelected = false

                    val startOne = UtilTools.getMonthAgo(Date(), -1)
                    val endOne = UtilTools.getStringFromDate(Date(), "yyyyMMdd")

                    mSelectStartTime = startOne
                    mSelectEndTime = endOne

                    mStartTimeTv!!.text = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
                    mEndTimeTv!!.text = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
                }
                R.id.three_month_tv -> {
                    mOneTv!!.isSelected = false
                    mThreeTv!!.isSelected = true
                    mSetTimeTv!!.isSelected = false

                    val startThree = UtilTools.getMonthAgo(Date(), -3)
                    val endThree = UtilTools.getStringFromDate(Date(), "yyyyMMdd")

                    mSelectStartTime = startThree
                    mSelectEndTime = endThree

                    mStartTimeTv!!.text = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
                    mEndTimeTv!!.text = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
                }
                R.id.set_time_tv -> {
                    mOneTv!!.isSelected = false
                    mThreeTv!!.isSelected = false
                    mSetTimeTv!!.isSelected = true
                }
                R.id.start_time_value_tv -> showDateDialog()
                R.id.end_time_value_tv -> showDateDialog2()
                R.id.reset_but -> resetCondition()
                R.id.sure_but -> {
                    showProgressDialog()
                    getSelectCondition()
                    mConditionDialog!!.dismiss()
                }
            }
        }

        mOneTv!!.setOnClickListener(onClickListener)
        mThreeTv!!.setOnClickListener(onClickListener)
        mSetTimeTv!!.setOnClickListener(onClickListener)
        mSureBut!!.setOnClickListener(onClickListener)
        mResetBut!!.setOnClickListener(onClickListener)
        mStartTimeTv!!.setOnClickListener(onClickListener)
        mEndTimeTv!!.setOnClickListener(onClickListener)


        val maps = SelectDataUtil.condition

        val linearLayoutManager = GridLayoutManager(this@TradeListActivity, 3)
        mTypeRecyclerView!!.layoutManager = linearLayoutManager
        mTradeDialogAdapter = TradeDialogAdapter(this@TradeListActivity, maps)
        //第一次设置默认值
        mSelectType = "borrow"
        mTradeDialogAdapter!!.selectItme = 0

        mOneTv!!.isSelected = true

        val startOne = UtilTools.getMonthAgo(Date(), -1)
        val endOne = UtilTools.getStringFromDate(Date(), "yyyyMMdd")
        mSelectStartTime = startOne
        mSelectEndTime = endOne

        mStartTimeTv!!.text = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
        mEndTimeTv!!.text = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")

        mTradeDialogAdapter!!.onItemClickListener = object : OnMyItemClickListener {
            override fun OnMyItemClickListener(view: View, position: Int) {
                val itemMap = mTradeDialogAdapter!!.datas?.get(position)
                mSelectType = itemMap?.get("code")!!.toString() + ""
            }
        }
        mTypeRecyclerView!!.adapter = mTradeDialogAdapter
    }

    private fun showDateDialog() {
        mySelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showDateDialog2() {
        mySelectDialog2!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }


    private fun getSelectCondition() {
        mStartTime = mSelectStartTime
        mEndTime = mSelectEndTime
        mBusiType = mSelectType
        mPage = 1
        traderListAction()
    }

    private fun resetCondition() {
        mOneTv!!.isSelected = true
        mThreeTv!!.isSelected = false
        mSetTimeTv!!.isSelected = false

        //设置默认值
        mSelectType = "borrow"
        mTradeDialogAdapter!!.selectItme = 0
        mTradeDialogAdapter!!.notifyDataSetChanged()

        val startOne = UtilTools.getMonthAgo(Date(), -1)
        val endOne = UtilTools.getStringFromDate(Date(), "yyyyMMdd")
        mSelectStartTime = startOne
        mSelectEndTime = endOne

        mStartTimeTv!!.text = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
        mEndTimeTv!!.text = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")

    }


    @OnClick(R.id.back_img, R.id.right_lay, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.right_lay -> showDialog()
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
            MethodUrl.tradeList//
            -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    responseData()
                } else {
                    val list = JSONUtil.instance.jsonToList(result)
                    if (list != null) {
                        mDataList.clear()
                        mDataList.addAll(list)
                        responseData()
                    } else {

                    }
                }
                mRefreshListView!!.refreshComplete(10)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.tradeList -> traderListAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.tradeList//
            -> if (mTradeListAdapter != null) {
                if (mTradeListAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { traderListAction() }
            } else {
                mPageView!!.showNetworkError()
            }
        }

        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        showProgressDialog()
        traderListAction()
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            21 -> {
                mSelectStartTime = map["date"]!!.toString() + ""
                mStartTimeTv!!.text = mSelectStartTime
                mStartTimeTv!!.text = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
            }
            22 -> {
                mSelectEndTime = map["date"]!!.toString() + ""
                mEndTimeTv!!.text = mSelectEndTime
                mEndTimeTv!!.text = UtilTools.getStringFromSting2(mSelectEndTime, "yyyyMMdd", "yyyy-MM-dd")
            }
        }

    }
}
