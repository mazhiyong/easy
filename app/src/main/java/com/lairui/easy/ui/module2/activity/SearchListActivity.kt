package com.lairui.easy.ui.module2.activity

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module2.adapter.SearchListAdapter
import com.lairui.easy.ui.module4.adapter.RecordListAdapter
import com.lairui.easy.utils.tool.*
import java.util.regex.Pattern

/**
 * 搜索 界面
 */
class SearchListActivity : BasicActivity(), RequestView, ReLoadingData {


    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.inputEt)
    lateinit var inputEt: EditText
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    private var mRequestTag = "0"

    private var mDataMap: MutableMap<String, Any>? = null

    private var mRecordAdapter: SearchListAdapter? = null
    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1

    override val contentView: Int
        get() = R.layout.activity_search_list

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        initView()

        inputEt.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()){
                    mRequestTag = "0"
                    searchListAction(s.toString())
                }else{
                    mPageView.showEmpty()
                }
            }

        })

    }


    private fun initView() {
        mContent?.let { mPageView.setContentView(it) }
        mPageView.reLoadingData = this
        mPageView.showLoading()
        val manager = LinearLayoutManager(this@SearchListActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView.layoutManager = manager


    }

    private fun searchListAction(keyWord:String) {
        val map = HashMap<String, String>()
        map["q"] = keyWord
        map["t"] = "gp"
        map["c"] = "1"
        map["v"] = "1"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap,MbsConstans.QUERY_SERVER_URL, map)
    }


    private fun responseData() {
        if (mRecordAdapter == null) {
            mRecordAdapter = SearchListAdapter( this@SearchListActivity)
            mRecordAdapter!!.addAll(mDataList)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mRecordAdapter)

            //            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
            //            mLRecyclerViewAdapter.addHeaderView(headerView);

            mRefreshListView.adapter = mLRecyclerViewAdapter
            mRefreshListView.itemAnimator = DefaultItemAnimator()
            mRefreshListView.setHasFixedSize(true)
            mRefreshListView.isNestedScrollingEnabled = false


            mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView.setPullRefreshEnabled(false)
            mRefreshListView.setLoadMoreEnabled(false)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->

            }


        } else {
            if (mPage == 1) {
                mRecordAdapter!!.clear()
            }
            mRecordAdapter!!.addAll(mDataList)
            mRecordAdapter!!.notifyDataSetChanged()
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

        mRefreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList.size < 10) {
            mRefreshListView.setNoMore(false)
        } else {
            mRefreshListView.setNoMore(false)
        }

        mRefreshListView.refreshComplete(10)
        mRecordAdapter!!.notifyDataSetChanged()
        if (mRecordAdapter!!.dataList.isEmpty()) {
            mPageView.showEmpty()
        } else {
            mPageView.showContent()
        }

    }

    @OnClick(R.id.back_img,R.id.right_img)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.right_img -> { //搜索
                if (TextUtils.isEmpty(inputEt.text)){
                    showToastMsg("请输入检索关键字")
                    return
                }
                val regEx = "[^0-9]"
                val p = Pattern.compile(regEx)
                val m = p.matcher(inputEt.text.toString())
                if (m.replaceAll("").trim().isEmpty()){
                    mRequestTag = "1"
                    searchListAction(inputEt.text.toString())
                }else{
                    mRequestTag = "1"
                    searchListAction(m.replaceAll("").trim())
                }


            }

        }
    }

    override fun showProgress() {
        //showProgressDialog()
    }

    override fun disimissProgress() {
        //dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {
            MbsConstans.QUERY_SERVER_URL -> {
                var result = tData["result"]!!.toString() + ""
                result = result.substring(result.indexOf("=")+1).replace("\"","")
                LogUtil.i("show","搜索结果"+result)

                if (result == "N;"){
                    if (mRequestTag == "1"){
                        showToastMsg("未检索到相关数据")
                    }
                    mPageView.showEmpty()
                    return
                }

                val array = result.split("^")
                if (array.isNotEmpty()){ //多条数据
                    mDataList.clear()
                    if (mRecordAdapter != null){
                        mRecordAdapter!!.clear()
                    }
                    for (item in array){
                        val arrayChild = item.split("~")
                        val map = HashMap<String,Any>()
                        map["jc"] = arrayChild[0]
                        map["code"] = arrayChild[1]
                        map["name"] =UicodeBackslashU.unicodeToCn(arrayChild[2])
                        mDataList.add(map)
                    }
                    responseData()

                }else{
                    showToastMsg("未检索到相关数据")
                    mPageView.showEmpty()
                }


            }


        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {


        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {

    }


}
