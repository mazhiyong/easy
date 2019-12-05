package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.HangYeAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.OnSearchItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick

/**
 * 所属行业   界面
 */
class HangYeActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.search_edit)
    lateinit var mSearchEdit: EditText

    private var mRequestTag = ""


    private lateinit var mHangYeAdapter: HangYeAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mDataList: MutableList<MutableMap<String, Any>>? = ArrayList()
    private val mPage = 1

    private val mBankId = ""
    private val mCityCode = ""

    private var mType: String? = ""

    override val contentView: Int
        get() = R.layout.activity_bank_wd

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mType = bundle.getString("TYPE")
            if (mType == "1") {
                val ss = SelectDataUtil.getJson(this, "hangye_work.json")
                mTitleText!!.text = resources.getString(R.string.work_kind)
                mDataList = JSONUtil.instance.jsonToList(ss) as MutableList<MutableMap<String, Any>>?
            } else if (mType == "2") {
                mDataList = bundle.getSerializable("DATA") as MutableList<MutableMap<String, Any>>
                val name = bundle.getString("name")
                mTitleText!!.text = name

            }
        }

        initView()
        responseData()
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()
        val manager = LinearLayoutManager(this@HangYeActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager

        mRefreshListView!!.setOnRefreshListener {
            // bankWdListAction();
        }

        mRefreshListView!!.setOnLoadMoreListener {
            //bankWdListAction();
        }

        mRefreshListView!!.setOnNetWorkErrorListener { bankWdListAction() }

        mSearchEdit!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(sequence: CharSequence, i: Int, i1: Int, i2: Int) {
                mHangYeAdapter!!.filter.filter(sequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

    }

    private fun bankWdListAction() {

        mRequestTag = MethodUrl.bankWdList
        val map = HashMap<String, String>()
        map["opnbnkid"] = mBankId
        map["citycode"] = mCityCode
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.bankWdList, map)
    }


    private fun responseData() {
        if (mHangYeAdapter == null) {
            mHangYeAdapter = HangYeAdapter(this@HangYeActivity)
            mDataList?.let { mHangYeAdapter!!.addAll(it) }

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mHangYeAdapter)

            //            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
            //            mLRecyclerViewAdapter.addHeaderView(headerView);

            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView!!.setPullRefreshEnabled(false)
            mRefreshListView!!.setLoadMoreEnabled(false)


            val divider2 = DividerDecoration.Builder(this@HangYeActivity)
                    .setHeight(R.dimen.divide_hight)
                    //.setPadding(R.dimen.dp_10)
                    .setColorResource(R.color.divide_line)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)

        } else {
            if (mPage == 1) {
                mHangYeAdapter!!.clear()
            }
            mDataList?.let { mHangYeAdapter!!.addAll(it) }
            mHangYeAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }

        mHangYeAdapter!!.sourceList = this!!.mDataList!!
        mHangYeAdapter!!.onSearchItemClickListener = object : OnSearchItemClickListener {
            override fun OnSearchItemClickListener(mSelectData: MutableMap<String, Any>) {

                /*List<Map<String,Object>> mChildList = (List<Map<String,Object>>) mSelectData.get("typeList");
                if (mChildList == null || mChildList.size() == 0){*/
                val intent = Intent()
                intent.putExtra("DATA", mSelectData as Serializable)
                setResult(Activity.RESULT_OK, intent)
                finish()
                /*  }else {
                    Intent intent = new Intent(HangYeActivity.this,HangYeActivity.class);
                    intent.putExtra("DATA",(Serializable) mChildList);
                    intent.putExtra("TYPE","2");
                    intent.putExtra("name",mSelectData.get("name")+"");
                    startActivityForResult(intent,60);
                }*/


            }
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
        if (mDataList!!.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        mRefreshListView!!.refreshComplete(10)
        mHangYeAdapter!!.notifyDataSetChanged()
        if (mHangYeAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bundle: Bundle?
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                60 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        val mHangyeMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        val intent = Intent()
                        intent.putExtra("DATA", mHangyeMap as Serializable)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }


    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {

            MethodUrl.bankWdList//
            -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    val list = JSONUtil.instance.jsonToList(result)
                    responseData()
                } else {
                    val list = JSONUtil.instance.jsonToList(result)
                    if (list != null) {
                        mDataList!!.clear()
                        mDataList!!.addAll(list)
                        responseData()
                    }
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.bankWdList -> bankWdListAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.bankWdList -> if (mHangYeAdapter != null) {
                if (mHangYeAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { bankWdListAction() }
            } else {
                mPageView!!.showNetworkError()

            }
        }


        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        bankWdListAction()
    }
}
