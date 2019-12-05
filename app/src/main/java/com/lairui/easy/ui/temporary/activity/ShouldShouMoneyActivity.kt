package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.PopuTipView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.ui.temporary.adapter.ShouldShouMoneyAdapter
import com.lairui.easy.utils.tool.DataHolder
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
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

/**
 * 应收账款
 */
class ShouldShouMoneyActivity : BasicActivity(), RequestView, ReLoadingData {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit  var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.tv_message)
    lateinit var mTvMessage: TextView
    @BindView(R.id.empty_lay)
    lateinit var mEmptyLay: LinearLayout

    lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    lateinit var mAdapter: ShouldShouMoneyAdapter

    @BindView(R.id.totall_shoumoney_tv)
    lateinit var mTotallShoumoneyTv: TextView
    @BindView(R.id.max_jiemoney_tv)
    lateinit var mMaxJiemoneyTv: TextView
    @BindView(R.id.date_tv)
    lateinit var mDateTv: TextView
    @BindView(R.id.btn_next)
    lateinit var mBtnNext: Button
    @BindView(R.id.tip_iv)
    lateinit var mTipIv: ImageView

    private val mBooleanList = ArrayList<MutableMap<String, Any>>()
    private val mSelectList = ArrayList<MutableMap<String, Any>>()
    private val mDataList = ArrayList<MutableMap<String, Any>>()


    private var mRequestTag = ""

    private var mSxMap: MutableMap<String, Any> = HashMap()
    private var mPayCompayName: String? = ""
    private var mPaycustid: String? = ""


    override val contentView: Int
        get() = R.layout.activity_should_shou_money


    override fun init() {

        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.shouldshou_money)

        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mSxMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            mPayCompayName = bundle.getString("payfirmname")
            mPaycustid = bundle.getString("paycustid")
        }


        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = linearLayoutManager

        mRefreshListView!!.setOnRefreshListener {
            // showProgressDialog();
            yszkList()
        }

        mRefreshListView!!.setOnLoadMoreListener {
            //payHistoryAction();
        }


        showProgressDialog()
        yszkList()
    }


    private fun yszkList() {
        mRequestTag = MethodUrl.yszkList
        val map = HashMap<String, String>()
        map["flowdate"] = mSxMap["flowdate"]!!.toString() + ""
        map["flowid"] = mSxMap["flowid"]!!.toString() + ""
        map["autoid"] = mSxMap["autoid"]!!.toString() + ""
        map["payfirmname"] = mPayCompayName!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.yszkList, map)
    }


    @OnClick(R.id.left_back_lay, R.id.btn_next, R.id.right_text_tv, R.id.tip_iv)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.right_text_tv -> {
                intent = Intent(this@ShouldShouMoneyActivity, InvoiceActivity::class.java)
                startActivity(intent)
            }
            R.id.left_back_lay -> finish()
            R.id.btn_next -> {
                val list = mAdapter!!.booleanList
                mSelectList.clear()
                var isChi = true
                if (list != null) {
                    for (map in list) {
                        val b = (map["selected"] as Boolean?)!!
                        val mSelectMap = map["value"] as MutableMap<String, Any>?
                        if (b) {
                            mSelectMap?.let { mSelectList.add(it) }
                            val status = mSelectMap!!["poolsta"]!!.toString() + ""
                            if (status == "0") {//入池状态：0：未入池  2：已入池
                                isChi = false
                            }
                        }
                    }
                }
                LogUtil.i("选择的应收账款信息", mSelectList)
                if (isChi) {
                    intent = Intent(this@ShouldShouMoneyActivity, BorrowMoneyActivity::class.java)
                } else {
                    intent = Intent(this@ShouldShouMoneyActivity, HeTongSelectActivity::class.java)
                }
                intent.putExtra("DATA", mSxMap as Serializable)
                DataHolder.instance!!.save("moneySelect", mSelectList)
                intent.putExtra("payfirmname", mPayCompayName)
                intent.putExtra("paycustid", mPaycustid)
                startActivity(intent)
            }
            R.id.tip_iv -> {
                /*    View inflate2 = View.inflate(ShouldShouMoneyActivity.this, R.layout.popup_bubble_text, null);
                TextView mTextView2 = inflate2.findViewById(R.id.tv_bubble);
                mTextView2.setText("注：截止2018年10月17日，您到期的\n" +
                        "应收账款为490,000.00元。最大可借款\n" +
                        "金额等于应收账款金额合计乘以最大\n" +
                        "比率，与授信可用额度之间的最小值。\n" +
                        "即490,000.00元×70.00%与97,400.00元\n" +
                        "之间的最小值。");*/
                val s = "注：截止2018年10月17日，您到期的\n" +
                        "应收账款为490,000.00元。最大可借款\n" +
                        "金额等于应收账款金额合计乘以最大\n" +
                        "比率，与授信可用额度之间的最小值。\n" +
                        "即490,000.00元×70.00%与97,400.00元\n" +
                        "之间的最小值。"

                val mp = PopuTipView(this@ShouldShouMoneyActivity, s, R.layout.popu_lay_bottom)
                mTipIv?.let { mp.show(it, 4) }
            }
        }/* new BubblePopup(ShouldShouMoneyActivity.this, inflate2)
                        .anchorView(mTipIv)
                        .bubbleColor(Color.parseColor("#A6000000"))
                        .gravity(Gravity.BOTTOM)
                        //箭头宽度 高度
                        .triangleWidth(5)
                        .triangleHeight(5)
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();*/
    }


    override fun reLoadingData() {
        showProgressDialog()
        yszkList()
    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {}

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
            MethodUrl.yszkList -> {

                val t = tData["vchtrdtype"]!!.toString() + ""
                if (t == "0") {//应收账款
                    mRightTextTv!!.visibility = View.GONE
                    mRightImg!!.visibility = View.GONE
                } else if (t == "1") {//发票
                    mRightTextTv!!.visibility = View.VISIBLE
                    mRightTextTv!!.text = "导入发票"
                    mRightImg!!.visibility = View.VISIBLE
                }

                val mm = tData["totalmny"]!!.toString() + ""
                mTotallShoumoneyTv!!.text = UtilTools.getMoney(mm)
                val mm2 = tData["maxcanloan"]!!.toString() + ""
                mMaxJiemoneyTv!!.text = UtilTools.getMoney(mm2)

                val list = tData["yszkInfoList"] as List<MutableMap<String, Any>>?
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
                    MethodUrl.yszkList -> yszkList()
                }
            }
        }
    }

    private fun responseData() {

        for (m in mDataList) {
            val map = HashMap<String, Any>()
            map["value"] = m
            map["selected"] = false
            mBooleanList.add(map)
        }


        if (mAdapter == null) {
            mAdapter = ShouldShouMoneyAdapter(this)
            mAdapter!!.booleanList = mBooleanList
            mAdapter!!.addAll(mDataList)

            /*    AnimationAdapter adapter1 = new ScaleInAnimationAdapter(mAdapter);
            adapter1.setFirstOnly(false);
            adapter1.setDuration(400);
            adapter1.setInterpolator(new OvershootInterpolator(0.8f));


            AnimationAdapter adapter = new AlphaInAnimationAdapter(adapter1);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(1f));
*/

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
                    //  .setHeight(R.dimen.dp_10)
                    //  .setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.body_bg)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)

            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position -> }

            mAdapter!!.setOnMyItemClickListener(object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {

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

        when (mType) {
            MethodUrl.yszkList -> if (mAdapter != null) {
                if (mAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener {
                    showProgressDialog()
                    yszkList()
                }

            } else {
                mPageView!!.showNetworkError()
            }
        }




        dealFailInfo(map, mType)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1234 -> {
                Toast.makeText(this@ShouldShouMoneyActivity, R.string.message_setting_comeback, Toast.LENGTH_SHORT).show()
            }
        }

    }


}
