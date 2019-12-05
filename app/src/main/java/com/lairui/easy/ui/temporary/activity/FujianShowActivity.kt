package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.ModifyShowImageAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.DataHolder
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import java.io.Serializable

import butterknife.BindView
import butterknife.OnClick

/**
 * 附件展示信息   界面
 */
class FujianShowActivity : BasicActivity(), RequestView, ReLoadingData {

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
    lateinit var mRefreshListView: RecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    private val mRequestTag = ""


    private lateinit var mDataList: List<MutableMap<String, Any>> //获取图片信息


    private lateinit var mModifyFileAdapter: ModifyShowImageAdapter

    override val contentView: Int
        get() = R.layout.activity_fujian_file

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.borrow_file)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            // mDataList = (List<Map<String,Object>>) bundle.getSerializable("DATA");
        }
        mDataList = (DataHolder.instance!!.retrieve("fileList") as List<MutableMap<String, Any>>?)!!
        mRightImg!!.visibility = View.VISIBLE
        mRightImg!!.setImageResource(R.drawable.shuaixuan)
        mRightTextTv!!.visibility = View.VISIBLE
        mRightLay!!.visibility = View.GONE
        initView()
        initList()
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        //mPageView.showEmpty();
        mPageView!!.showContent()
        mPageView!!.reLoadingData = this
        val manager = GridLayoutManager(this@FujianShowActivity, 4)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager


    }

    private fun initList() {

        mModifyFileAdapter = mDataList?.let { ModifyShowImageAdapter(this@FujianShowActivity, it) }
        mModifyFileAdapter!!.onMyItemClickListener = object : OnMyItemClickListener {
            override fun OnMyItemClickListener(view: View, position: Int) {
                val intent = Intent(this@FujianShowActivity, ShowDetailPictrue::class.java)
                intent.putExtra("position", position)
                intent.putExtra("DATA", mDataList as Serializable?)
                startActivity(intent)
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout)

            }
        }
        mRefreshListView!!.adapter = mModifyFileAdapter

        if (mDataList == null || mDataList!!.size == 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
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
