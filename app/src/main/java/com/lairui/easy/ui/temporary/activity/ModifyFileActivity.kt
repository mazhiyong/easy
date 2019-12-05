package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.ModifyFileAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.DataHolder
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick

/**
 * 附件信息 展示附件信息   界面
 */
class ModifyFileActivity : BasicActivity(), RequestView, ReLoadingData {

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

    private val mRequestTag = ""


    private lateinit var mDataList: List<MutableMap<String, Any>> //获取图片信息
    private lateinit var mModifyFileAdapter: ModifyFileAdapter
    private lateinit var mModifyLRecyclerViewAdapter: LRecyclerViewAdapter

    override val contentView: Int
        get() = R.layout.activity_look_file

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.has_upload_fujian)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            // mDataList = (List<Map<String,Object>>) bundle.getSerializable("DATA");
        }

        mDataList = (DataHolder.instance!!.retrieve("fileList") as List<MutableMap<String, Any>>?)!!

        if (mDataList == null) {
            mDataList = ArrayList()
        }

        mRightImg!!.visibility = View.VISIBLE
        mRightImg!!.setImageResource(R.drawable.shuaixuan)
        mRightTextTv!!.visibility = View.VISIBLE
        mRightLay!!.visibility = View.GONE


        initView()
        initList()
        //getSelectPic();
        // traderListAction();
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        //mPageView.showEmpty();
        mPageView!!.showContent()
        mPageView!!.reLoadingData = this
        val manager = LinearLayoutManager(this@ModifyFileActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager
        mRefreshListView!!.setOnRefreshListener { initList() }

        mRefreshListView!!.setOnLoadMoreListener { }

    }

    private fun initList() {
        mPageView!!.showContent()

        if (mModifyFileAdapter == null) {
            mModifyFileAdapter = ModifyFileAdapter(this@ModifyFileActivity)
            mDataList?.let { mModifyFileAdapter!!.addAll(it) }
            mModifyLRecyclerViewAdapter = LRecyclerViewAdapter(mModifyFileAdapter)
            mRefreshListView!!.adapter = mModifyLRecyclerViewAdapter
            mRefreshListView!!.setPullRefreshEnabled(false)
            mRefreshListView!!.setLoadMoreEnabled(false)

            val divider2 = DividerDecoration.Builder(this@ModifyFileActivity)
                    .setHeight(R.dimen.divide_hight)
                    .setColorResource(R.color.divide_line)
                    .build()
            mRefreshListView!!.addItemDecoration(divider2)
        } else {
            mModifyFileAdapter!!.notifyDataSetChanged()
            mModifyLRecyclerViewAdapter!!.notifyDataSetChanged()
        }

        if (mModifyFileAdapter!!.dataList != null && mModifyFileAdapter!!.dataList.size == 0) {
            mPageView!!.showEmpty()
        }


        mRefreshListView!!.refreshComplete(10)

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
            MethodUrl.creUploadFile//
            -> {
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.creUploadFile -> {
                    }
                }//                        uploadFile();
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.creUploadFile//
            -> {
            }
        }

        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {}


    override fun onDestroy() {
        super.onDestroy()
    }
}
